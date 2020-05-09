package com.jiebai.qqsk.live.provider.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.jiebai.framework.common.cglib.BaseBeanCopier;
import com.jiebai.framework.common.cglib.SimpleBeanCopier;
import com.jiebai.qqsk.discover.remote.RemoteUserFollowService;
import com.jiebai.qqsk.live.constant.DeleteFlagEnum;
import com.jiebai.qqsk.live.dto.LiveStarDTO;
import com.jiebai.qqsk.live.dto.LiveStarRoomDTO;
import com.jiebai.qqsk.live.model.TbLiveRoomDO;
import com.jiebai.qqsk.live.model.TbLiveStarDO;
import com.jiebai.qqsk.live.model.TbUserDO;
import com.jiebai.qqsk.live.remote.RemoteLiveTeachService;
import com.jiebai.qqsk.live.service.QiNiuService;
import com.jiebai.qqsk.live.service.TbLiveRoomService;
import com.jiebai.qqsk.live.service.TbLiveStarService;
import com.jiebai.qqsk.live.service.TbLiveVisitorService;
import com.jiebai.qqsk.live.utils.VisitorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : eyestarrysky
 * @date : Created in 2020/2/28
 */
@Slf4j
@Component
@Service(interfaceClass = RemoteLiveTeachService.class, version = "${provider.live.version}",
        validation = "false", retries = 0, timeout = 5000)
public class RemoteLiveTeachServiceImpl implements RemoteLiveTeachService {

    private static BaseBeanCopier<TbLiveRoomDO, LiveStarRoomDTO> ROOM_STAR_DO2DTO_COPIER =
            new SimpleBeanCopier<>(TbLiveRoomDO.class, LiveStarRoomDTO.class);

    @Resource
    private TbLiveStarService tbLiveStarService;

    @Resource
    private TbLiveRoomService tbLiveRoomService;

    @Resource
    private QiNiuService qiNiuService;

    @Resource
    private TbLiveVisitorService tbLiveVisitorService;

    @Reference(version = "${consumer.discover.version}", validation = "false")
    private RemoteUserFollowService remoteUserFollowService;

    @NacosValue(value = "${visitor_rule:}", autoRefreshed = true)
    private String visitor_rule;

    @Override
    public List<LiveStarDTO> findLiveStarList(Integer userId) {
        List<LiveStarDTO> liveStarDTOList = Lists.newArrayList();
        Condition condition = new Condition(TbLiveStarDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("isDeleted", DeleteFlagEnum.NOT_DELETED.getCode());
        condition.orderBy("gmtModified").desc();
        List<TbLiveStarDO> starDOList = tbLiveStarService.listByCondition(condition);
        if (CollectionUtils.isEmpty(starDOList)) {
            return liveStarDTOList;
        }
        return starDOList.stream().map(m -> {
            //封装主播基本信息
            LiveStarDTO liveStarDTO = new LiveStarDTO();
            liveStarDTO.setUserId(m.getUserId());
            liveStarDTO.setUserTags(m.getUserTags());
            TbUserDO tbUserDO = tbLiveVisitorService.getTbUserByUserId(m.getUserId());
            Optional.ofNullable(tbUserDO).ifPresent(s -> {
                //设置头像
                liveStarDTO.setHeadImgUrl(s.getHeadimgurl());
                liveStarDTO.setShopName(StringUtils.isBlank(s.getShopName()) ? s.getNickname() : s.getShopName());
            });
            List<TbLiveRoomDO> tbLiveRoomDOList = tbLiveRoomService.findPublicLiveRoomByUserId(m.getUserId());
            //查询直播间信息
            if (CollectionUtils.isEmpty(tbLiveRoomDOList)) {
                liveStarDTO.setLiveStarRoomDTOList(Lists.newArrayList());
                return liveStarDTO;
            }
            //封装单个直播间基本信息---start
            liveStarDTO.setLiveStarRoomDTOList(tbLiveRoomDOList.stream().map(s -> {
                LiveStarRoomDTO liveStarRoomDTO = ROOM_STAR_DO2DTO_COPIER.copy(s);
                //设置回放时长
                if (StringUtils.isNotBlank(s.getStreamFname())) {
                    if (StringUtils.isNotBlank(s.getBackTime())) {
                        liveStarRoomDTO.setLiveTime(s.getBackTime());
                    } else {
                        String timeStr = VisitorUtils.getBackTime(qiNiuService.getBackUrl(s.getStreamFname()));
                        if (StringUtils.isNotBlank(timeStr)) {
                            s.setBackTime(timeStr);
                            liveStarRoomDTO.setLiveTime(timeStr);
                            tbLiveRoomService.updateById(s);
                        }
                    }
                }
                liveStarRoomDTO.setVisitorNum(VisitorUtils.getVisitors(s.getVisitorNum(), visitor_rule));
                //回放地址
                liveStarRoomDTO.setStreamBackUrl(qiNiuService.getBackUrl(s.getStreamFname()));
                //flv回放地址
                liveStarRoomDTO.setStreamFlvBackUrl(qiNiuService.getFlvBackUrl(s.getStreamFlvFname()));
                //播放地址
                liveStarRoomDTO.setHLSPlayURL(qiNiuService.getHLSPlayURL(s.getStreamKey()));
                liveStarRoomDTO.setRTMPPlayURL(qiNiuService.getRTMPPlayURL(s.getStreamKey()));
                liveStarRoomDTO.setHDLPlayURL(qiNiuService.getHDLPlayURL(s.getStreamKey()));
                liveStarRoomDTO.setIfConcern(
                        remoteUserFollowService.ifFollowOfFollow(userId, s.getLiveUserId()));
                return liveStarRoomDTO;
            }).collect(Collectors.toList()));
            //--- 封装单个直播间基本信息 end
            return liveStarDTO;
        }).collect(Collectors.toList());
    }
}

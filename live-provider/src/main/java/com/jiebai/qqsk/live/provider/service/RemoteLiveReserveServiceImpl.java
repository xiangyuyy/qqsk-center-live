package com.jiebai.qqsk.live.provider.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.jiebai.framework.common.cglib.BaseBeanCopier;
import com.jiebai.framework.common.cglib.SimpleBeanCopier;
import com.jiebai.qqsk.live.constant.LiveRoomPublicStatusEnum;
import com.jiebai.qqsk.live.constant.LiveStatusEnum;
import com.jiebai.qqsk.live.dto.LiveRoomDTO;
import com.jiebai.qqsk.live.dto.LiveRoomSubscriberDTO;
import com.jiebai.qqsk.live.dto.LiveSubscribersDTO;
import com.jiebai.qqsk.live.dto.MyLiveDTO;
import com.jiebai.qqsk.live.exception.RemoteLiveServiceErrorCodeEnum;
import com.jiebai.qqsk.live.exception.RemoteLiveServiceException;
import com.jiebai.qqsk.live.model.TbLiveCategoryDO;
import com.jiebai.qqsk.live.model.TbLiveRoomDO;
import com.jiebai.qqsk.live.model.TbLiveSubscribersDO;
import com.jiebai.qqsk.live.remote.RemoteLiveReserveService;
import com.jiebai.qqsk.live.service.QiNiuService;
import com.jiebai.qqsk.live.service.TbLiveCategoryService;
import com.jiebai.qqsk.live.service.TbLiveRoomService;
import com.jiebai.qqsk.live.service.TbLiveSubscribersService;
import com.jiebai.qqsk.live.utils.VisitorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 预约直播间相关服务
 * @author lichenguang
 * 2019/11/13
 */
@Slf4j
@Component
@Service(interfaceClass = RemoteLiveReserveService.class, version = "${provider.live.version}", validation = "false",
        retries = 0, timeout = 3000)
public class RemoteLiveReserveServiceImpl implements RemoteLiveReserveService {

    private static BaseBeanCopier<LiveRoomDTO, TbLiveRoomDO> LIVE_ROOM_DTO2DO_COPIER =
            new SimpleBeanCopier<>(LiveRoomDTO.class, TbLiveRoomDO.class);

    private static BaseBeanCopier<TbLiveRoomDO, MyLiveDTO> LIVE_ROOM_DO2MYLIVEDTO_COPIER =
            new SimpleBeanCopier<>(TbLiveRoomDO.class, MyLiveDTO.class);

    private static BaseBeanCopier<TbLiveRoomDO, LiveRoomSubscriberDTO> LIVE_ROOM_DO2SUBSCRIBEDTO_COPIER =
            new SimpleBeanCopier<>(TbLiveRoomDO.class, LiveRoomSubscriberDTO.class);

    private static BaseBeanCopier<LiveSubscribersDTO, TbLiveSubscribersDO> SUBSCRIBE_DTO2DO_COPIER =
            new SimpleBeanCopier<>(LiveSubscribersDTO.class, TbLiveSubscribersDO.class);

    private static BaseBeanCopier<TbLiveRoomDO, LiveRoomDTO> LIVE_ROOM_DO2DTO_COPIER =
            new SimpleBeanCopier<>(TbLiveRoomDO.class, LiveRoomDTO.class);

    @NacosValue(value = "${visitor_rule:}", autoRefreshed = true)
    private String visitor_rule;

    @Resource
    private TbLiveRoomService tbLiveRoomService;

    @Resource
    private TbLiveSubscribersService tbLiveSubscribersService;

    @Resource
    private QiNiuService qiNiuService;

    @Resource
    private TbLiveCategoryService tbLiveCategoryService;

    @NacosValue(value = "${live_rank_when_living:50,100,300,800-2,3,4,5}", autoRefreshed = true)
    private String liveRankWhenLiving;

    @NacosValue(value = "${live_rank_when_reserved:50,100,300,800-2,3,4,5}", autoRefreshed = true)
    private String liveRankWhenReserved;

    @NacosValue(value = "${live_rank_when_over:100,300,800-2,3,4}", autoRefreshed = true)
    private String liveRankWhenOver;

    @NacosValue(value = "${LIVER_RESERVE_VISITORS:2}", autoRefreshed = true)
    private Integer liver_reserve_visitors;

    @Override
    public int createLiveRoom(LiveRoomDTO liveRoomDTO) {
        //获取用户未结束的直播间的数量
        int count = tbLiveRoomService.getLiveRoomNotOverCount(liveRoomDTO.getLiveUserId());
        if (count > 0) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.EXISET_ROOM_NOT_OVER);
        }
        return tbLiveRoomService.insertSelective(liveRoomDTO);
    }

    @Override
    public int updateLiveInfo(LiveRoomDTO liveRoomDTO) {
        Integer roomId = liveRoomDTO.getId();
        Integer liveUserId = liveRoomDTO.getLiveUserId();
        if (null == roomId) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.ROOM_ID_NOT_NULL);
        }
        TbLiveRoomDO beforeUpdateLiveRoom = tbLiveRoomService.getById(roomId);
        if (Objects.nonNull(beforeUpdateLiveRoom) && !liveUserId.equals(beforeUpdateLiveRoom.getLiveUserId())) {
            log.error("【直播模块-更新直播间出现错误】, 用户userId = {}, 进行违规调用！", liveUserId);
            throw new RemoteLiveServiceException("用户错误操作!");
        }
        TbLiveRoomDO tbLiveRoomDO = LIVE_ROOM_DTO2DO_COPIER.copy(liveRoomDTO);
        return tbLiveRoomService.updateByPrimaryKeySelective(tbLiveRoomDO);
    }

    @Override
    public MyLiveDTO getByIdAndUserId(Integer roomId, Integer userId) {
        MyLiveDTO myLiveDTO = new MyLiveDTO();
        TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getByIdAndUserId(roomId, userId);
        if (Objects.nonNull(tbLiveRoomDO)) {
            myLiveDTO = LIVE_ROOM_DO2MYLIVEDTO_COPIER.copy(tbLiveRoomDO);
            String streamKey = tbLiveRoomDO.getStreamKey();
            //回放地址
            myLiveDTO.setStreamBackUrl(qiNiuService.getBackUrl(tbLiveRoomDO.getStreamFname()));
            //播放地址
            myLiveDTO.setRTMPPlayURL(qiNiuService.getRTMPPlayURL(streamKey));
            myLiveDTO.setHDLPlayURL(qiNiuService.getHDLPlayURL(streamKey));
            myLiveDTO.setHLSPlayURL(qiNiuService.getHLSPlayURL(streamKey));
            //查询分类
            TbLiveCategoryDO tbLiveCategoryDO = tbLiveCategoryService.getByName(tbLiveRoomDO.getCategory());
            if (tbLiveCategoryDO != null) {
                myLiveDTO.setCategory(tbLiveCategoryDO.getMark());
            }


        }
        return myLiveDTO;
    }

    @Override
    public int reserveLive(LiveSubscribersDTO liveSubscribersDTO) {
        TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getById(liveSubscribersDTO.getRoomId());
        if (Objects.nonNull(tbLiveRoomDO)) {
            Integer state = tbLiveRoomDO.getState();
            if (LiveStatusEnum.LIVE_OVER.getStatus().equals(state)) {
                throw new RemoteLiveServiceException("直播已经结束, 预约失败");
            } else if (LiveStatusEnum.LIVE_STARTING.getStatus().equals(state)) {
                throw new RemoteLiveServiceException("直播已经开始, 预约失败");
            }
        }
        TbLiveSubscribersDO tbLiveSubscribersDO =
                tbLiveSubscribersService.getByRoomIdAndUserId(liveSubscribersDTO.getRoomId(),
                        liveSubscribersDTO.getUserId());
        //如果已经预约过了，直接返回处理失败
        if (Objects.nonNull(tbLiveSubscribersDO)) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.LIVE_EXIST_SUBSCRIBE);
        }
        //默认未发送消息
        TbLiveSubscribersDO subscribersDO = SUBSCRIBE_DTO2DO_COPIER.copy(liveSubscribersDTO);
        try {
            if (ObjectUtils.isEmpty(liver_reserve_visitors) || liver_reserve_visitors <= 0) {
                throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.LIVE_ROOM_NOT_VISITORS);
            }
            if (Objects.isNull(tbLiveRoomDO)) {
                throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.ROOM_ENTITY_NOT_NULL);
            }
            tbLiveRoomDO.setVisitorNum(tbLiveRoomDO.getVisitorNum() + VisitorUtils.getBackVisitors(liver_reserve_visitors, visitor_rule));
            tbLiveRoomService.updateById(tbLiveRoomDO);
            return tbLiveSubscribersService.insertSelective(subscribersDO);
        } catch (Exception e) {
            log.error("预约直播发生异常， message = {}", e.getMessage());
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.BUSINESS_HANDLING_FAIL);
        }
    }

    @Override
    public LiveRoomSubscriberDTO getReserveInfoByRoomIdAndUserId(Integer roomId, Integer userId) {
        LiveRoomSubscriberDTO liveRoomSubscriberDTO = new LiveRoomSubscriberDTO();
        TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.selectNotDeleteLiveRoomById(roomId);
        //找不到直播间，直接返回
        if (Objects.isNull(tbLiveRoomDO)) {
            return liveRoomSubscriberDTO;
        }
        liveRoomSubscriberDTO = LIVE_ROOM_DO2SUBSCRIBEDTO_COPIER.copy(tbLiveRoomDO);
        TbLiveSubscribersDO tbLiveSubscribersDO = tbLiveSubscribersService.getByRoomIdAndUserId(roomId, userId);
        //差不到订阅信息
        if (Objects.isNull(tbLiveSubscribersDO)) {
            //未预约
            liveRoomSubscriberDTO.setSubscribeStatus(false);
        } else {
            liveRoomSubscriberDTO.setSubscribeStatus(true);
        }
        return liveRoomSubscriberDTO;
    }

    @Override
    public int makeLiveRoomPublicOrPrivate(Integer roomId, Integer userId) {
        TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getByIdAndUserId(roomId, userId);
        if (null == tbLiveRoomDO) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.LIVE_ROOM_NOT_EXIST);
        }
        Integer isPublic = LiveRoomPublicStatusEnum.IS_PUBLIC.getStatus();
        TbLiveRoomDO record = new TbLiveRoomDO();
        record.setId(roomId);
        record.setGmtModified(new Date());
        //如果原先为私密，就变成公开，原先是公开就变成私密
        record.setIsPublic(isPublic.equals(tbLiveRoomDO.getIsPublic()) ?
                LiveRoomPublicStatusEnum.IS_PRIVATE.getStatus() : isPublic);
        return tbLiveRoomService.updateByPrimaryKeySelective(record);
    }

    @Override
    public LiveRoomDTO getUserLastLiveInfo(Integer userId) {
        TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getUserLastLiveInfo(userId);
        if (Objects.isNull(tbLiveRoomDO)) {
            return null;
        }
        TbLiveCategoryDO tbLiveCategoryDO = tbLiveCategoryService.getByName(tbLiveRoomDO.getCategory());
        if (tbLiveCategoryDO != null) {
            tbLiveRoomDO.setCategory(tbLiveCategoryDO.getMark());
        }
        return LIVE_ROOM_DO2DTO_COPIER.copy(tbLiveRoomDO);
    }

    @Override
    public void closeNotStartInTimeLiveRoom() {
        List<TbLiveRoomDO> liveRoomDOList = tbLiveRoomService.selectDelayTaskLiveRoomList();
        if (CollectionUtils.isEmpty(liveRoomDOList)) {
            return;
        }
        for (TbLiveRoomDO tbLiveRoomDO : liveRoomDOList) {
            Integer roomId = tbLiveRoomDO.getId();
            log.info("超出预约时间10分钟以上未开播，直播间关闭, roomId = {}", roomId);
            Integer res = tbLiveRoomService.closeLiveRoom(roomId);
            if (res > 0) {
                qiNiuService.stop(tbLiveRoomDO.getStreamKey());
            }
        }
    }

    @Override
    public void scheduleChangeLiveRank() {
        //查询直播和预约中的
        List<TbLiveRoomDO> tbLiveRoomDOList = tbLiveRoomService.findLiveRoomIsLiving();
        //直播中/预约中的
        if (!CollectionUtils.isEmpty(tbLiveRoomDOList)) {
            List<TbLiveRoomDO> listWhenReserved =
                    tbLiveRoomDOList.stream().filter(f -> LiveStatusEnum.LIVE_NOT_START.getStatus().equals(f.getState()))
                            .collect(Collectors.toList());
            List<TbLiveRoomDO> listWhenLiving =
                    tbLiveRoomDOList.stream().filter(f -> LiveStatusEnum.LIVE_STARTING.getStatus().equals(f.getState()))
                            .collect(Collectors.toList());
            //处理预约中
            handleRankWhenLivingOrReserved(listWhenReserved, liveRankWhenReserved.split("-"));
            //处理直播中
            handleRankWhenLivingOrReserved(listWhenLiving, liveRankWhenLiving.split("-"));
        }
        //结束0 - 24小时的且时长
        List<TbLiveRoomDO> endLiveRoomList = tbLiveRoomService.selectLessOneDayAndEndLiveRoom();
        if (!CollectionUtils.isEmpty(endLiveRoomList)) {
            String[] str = liveRankWhenOver.split("-");
            String[] heatStrArr = str[0].split(",");
            String[] rankStrArr = str[1].split(",");
            //100-300人气
            List<Integer> roomIdList1 = Lists.newArrayList();
            //300-800人气
            List<Integer> roomIdList2 = Lists.newArrayList();
            //800以上人气
            List<Integer> roomIdList3 = Lists.newArrayList();
            for (TbLiveRoomDO tbLiveRoomDO : endLiveRoomList) {
                Integer roomId = tbLiveRoomDO.getId();
                Integer visitorNum = tbLiveRoomDO.getVisitorNum();
                //热度
                int heatCount = VisitorUtils.getVisitors(visitorNum, visitor_rule);
                if (heatCount >= Integer.parseInt(heatStrArr[0]) && heatCount < Integer.parseInt(heatStrArr[1])) {
                    roomIdList1.add(roomId);
                } else if (heatCount >= Integer.parseInt(heatStrArr[1]) && heatCount < Integer.parseInt(heatStrArr[2])) {
                    roomIdList2.add(roomId);
                } else if (heatCount >= Integer.parseInt(heatStrArr[2])) {
                    roomIdList3.add(roomId);
                }
            }
            tbLiveRoomService.updateRankByIds(roomIdList1, Integer.parseInt(rankStrArr[0]));
            tbLiveRoomService.updateRankByIds(roomIdList2, Integer.parseInt(rankStrArr[1]));
            tbLiveRoomService.updateRankByIds(roomIdList3, Integer.parseInt(rankStrArr[2]));
        }
        //结束0 - 24 && (时长<5分钟 || 人气<100）
        int backVisitorsNum = VisitorUtils.getBackVisitors(50, visitor_rule);
        tbLiveRoomService.updateLessOneDayLiveTimeLess5(backVisitorsNum);
        //结束24-48小时的
        List<TbLiveRoomDO> roomDOList = tbLiveRoomService.findEndLiveRoomMoreOneDayByDay(-1, -2);
        if (!CollectionUtils.isEmpty(roomDOList)) {
            tbLiveRoomService.updateRankByIds(roomDOList.stream()
                    .map(TbLiveRoomDO::getId).collect(Collectors.toList()), 0);
        }
        //结束48-72小时的
        List<TbLiveRoomDO> list = tbLiveRoomService.findEndLiveRoomMoreOneDayByDay(-2, -3);
        if (!CollectionUtils.isEmpty(list)) {
            tbLiveRoomService.updateRankByIds(list.stream()
                    .map(TbLiveRoomDO::getId).collect(Collectors.toList()), -1);
        }
    }

    /**
     * 处理预约中和直播中的直播间权重提升
     * @param tbLiveRoomDOList tbLiveRoomDOList
     * @param heatRanStrArray heatRanStrArray
     */
    private void handleRankWhenLivingOrReserved(List<TbLiveRoomDO> tbLiveRoomDOList, String[] heatRanStrArray) {
        String[] heatStrArr = heatRanStrArray[0].split(",");
        String[] rankStrArr = heatRanStrArray[1].split(",");
        log.info("【定时任务处理直播/预约中的权重】");
        for (TbLiveRoomDO tbLiveRoomDO : tbLiveRoomDOList) {
            int rank = 0;
            Integer visitorNum = tbLiveRoomDO.getVisitorNum();
            //热度
            int heatCount = VisitorUtils.getVisitors(visitorNum, visitor_rule);
            if (heatCount >= Integer.parseInt(heatStrArr[0]) && heatCount < Integer.parseInt(heatStrArr[1])) {
                rank = Integer.parseInt(rankStrArr[0]);
            } else if (heatCount >= Integer.parseInt(heatStrArr[1]) && heatCount < Integer.parseInt(heatStrArr[2])) {
                rank = Integer.parseInt(rankStrArr[1]);
            } else if (heatCount >= Integer.parseInt(heatStrArr[2]) && heatCount < Integer.parseInt(heatStrArr[3])) {
                rank = Integer.parseInt(rankStrArr[2]);
            } else if (heatCount >= Integer.parseInt(heatStrArr[3])){
                rank = Integer.parseInt(rankStrArr[3]);
            }
            if (rank > 0) {
                tbLiveRoomService.updateRankById(tbLiveRoomDO.getId(), rank);
            }
        }
    }

}

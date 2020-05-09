package com.jiebai.qqsk.live.provider.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jiebai.framework.common.cglib.BaseBeanCopier;
import com.jiebai.framework.common.cglib.SimpleBeanCopier;
import com.jiebai.qqsk.discover.dto.UserFollowDTO;
import com.jiebai.qqsk.discover.remote.RemoteUserFollowService;
import com.jiebai.qqsk.goods.dto.SpuManageShowDTO;
import com.jiebai.qqsk.goods.dto.TbSpuManageParamDTO;
import com.jiebai.qqsk.goods.remote.RemoteGoodsService;
import com.jiebai.qqsk.live.constant.LiveStatusEnum;
import com.jiebai.qqsk.live.constant.UserMemberRoleEnum;
import com.jiebai.qqsk.live.dto.*;
import com.jiebai.qqsk.live.model.TbLivePopShopDO;
import com.jiebai.qqsk.live.model.TbLiveRoomDO;
import com.jiebai.qqsk.live.model.TbLiveSubscribersDO;
import com.jiebai.qqsk.live.model.TbUserDO;
import com.jiebai.qqsk.live.remote.RemoteLiveDiscoverService;
import com.jiebai.qqsk.live.service.*;
import com.jiebai.qqsk.live.utils.VisitorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.jiebai.qqsk.live.constant.LiveConstant.HEAD_IMAGE_COUNT;
import static com.jiebai.qqsk.live.constant.LiveConstant.LiVEROOM_SPU_SHOW_MAX_COUNT;

/**
 * @author lichenguang
 * @date 2020/1/10
 */
@Slf4j
@Component
@Service(interfaceClass = RemoteLiveDiscoverService.class, version = "${provider.live.version}", validation = "false", retries = 0, timeout = 3000)
public class RemoteLiveDiscoverServiceImpl implements RemoteLiveDiscoverService {

    @Resource
    private TbLiveRoomService tbLiveRoomService;

    @Resource
    private TbLivePopShopService tbLivePopShopService;

    @Resource
    private TbLiveVisitorService tbLiveVisitorService;

    @Resource
    private QiNiuService qiNiuService;

    @Resource
    private TbLiveSubscribersService tbLiveSubscribersService;

    @Resource
    private TbLiveGoodsService tbLiveGoodsService;

    @NacosValue(value = "${visitor_rule:}", autoRefreshed = true)
    private String visitor_rule;

    @Reference(version = "${consumer.discover.version}", validation = "false")
    private RemoteUserFollowService remoteUserFollowService;

    @NacosValue(value = "${follow_page_num:1}", autoRefreshed = true)
    private Integer followPageNum;

    @NacosValue(value = "${follow_page_size:1000}", autoRefreshed = true)
    private Integer followPageSize;

    @Reference(version = "${consumer.goods.version}", validation = "false")
    private RemoteGoodsService remoteGoodsService;

    private static BaseBeanCopier<TbLiveRoomDO, LiveHomeListDTO> ROOM_DO2DTO_LIVEHOME =
        new SimpleBeanCopier<>(TbLiveRoomDO.class, LiveHomeListDTO.class);

    private static BaseBeanCopier<SpuManageShowDTO, LiveHomeGoodDTO> SPU2GOODS_COPIER =
        new SimpleBeanCopier<>(SpuManageShowDTO.class, LiveHomeGoodDTO.class);

    private static BaseBeanCopier<LiveHomeListDTO, DiscoverPersonalLiveDTO> DISCOVER_DTO2DTO_COPIER =
        new SimpleBeanCopier<>(LiveHomeListDTO.class, DiscoverPersonalLiveDTO.class);

    @Override
    public Set<Integer> getDiscoverLivingUserIdList(List<Integer> userIdList) {
        List<TbLiveRoomDO> liveRoomList = tbLiveRoomService.getDiscoverLivingUserList(userIdList);
        if (CollectionUtils.isEmpty(liveRoomList)) {
            return Sets.newHashSet();
        }
        return liveRoomList.stream().map(TbLiveRoomDO::getLiveUserId).collect(Collectors.toSet());
    }

    @Override
    public DiscoverLivePageDTO getDiscoverLiveListByUserId(LiveParamDTO liveParamDTO) {
        //初始化分页
        DiscoverLivePageDTO discoverLivePageDTO = new DiscoverLivePageDTO();
        List<String> headImageUrls = Lists.newArrayList();
        PageInfo<LiveHomeListDTO> pageInfo = new PageInfo<>();
        discoverLivePageDTO.setPageInfo(pageInfo);
        discoverLivePageDTO.setHeadImageUrls(headImageUrls);
        Integer userId = liveParamDTO.getUserId();
        Integer pageNum = liveParamDTO.getPageNum();
        Integer pageSize = liveParamDTO.getPageSize();
        PageInfo<UserFollowDTO> userFollowDTOPageInfo =
            remoteUserFollowService.listUserFollow(userId, followPageNum, followPageSize);
        List<UserFollowDTO> userFollowDTOList = userFollowDTOPageInfo.getList();
        //如果关注列表为空，直播直接冒得
        if (CollectionUtils.isEmpty(userFollowDTOList)) {
            return discoverLivePageDTO;
        }
        //先拿到关注人userIdList
        List<Integer> followUserList =
            userFollowDTOList.stream().map(UserFollowDTO::getFollowUserId).collect(Collectors.toList());
        List<TbLivePopShopDO> discoverLiveList = tbLivePopShopService.getDiscoverLiveList(followUserList);
        //如果关注的人都没开通直播，直接冒得
        if (CollectionUtils.isEmpty(discoverLiveList)) {
            return discoverLivePageDTO;
        }
        //关注人中开通了直播的userIdList
        List<Integer> liveUserIdList =
            discoverLiveList.stream().map(TbLivePopShopDO::getUserId).collect(Collectors.toList());
        Map<Integer, String> userIdFollowMap =
            userFollowDTOList.stream().collect(Collectors.toMap(UserFollowDTO::getFollowUserId, userFollowDTO -> {
                String headimgurl = userFollowDTO.getHeadimgurl();
                return StringUtils.isBlank(headimgurl) ? "" : headimgurl;
            }));
        //开通了直播的人数
        int openLiveUserCount = liveUserIdList.size();
        //使List乱序
        Collections.shuffle(liveUserIdList);
        for (int i = 0; i < Math.min(openLiveUserCount, HEAD_IMAGE_COUNT); i++) {
            Integer popUserId = liveUserIdList.get(i);
            //从事先的map中取出图片url
            headImageUrls.add(userIdFollowMap.get(popUserId));
        }
        discoverLivePageDTO.setHeadImageUrls(headImageUrls);
        //分页查询关注的主播正在直播的列表
        Page<TbLiveRoomDO> page = PageHelper.startPage(pageNum, pageSize);
        tbLiveRoomService.getDiscoverLivingUserList(liveUserIdList);
        //封装直播数据
        List<LiveHomeListDTO> liveHomeListDTOList =
            page.stream().map(this::handleLiveData).collect(Collectors.toList());
        wrapperPageInfoData(pageInfo, page, liveHomeListDTOList);
        discoverLivePageDTO.setPageInfo(pageInfo);
        return discoverLivePageDTO;
    }

    @Override
    public PageInfo<LiveHomeListDTO> getFollowedLiveList(LiveParamDTO liveParamDTO) {
        //初始化分页
        PageInfo<LiveHomeListDTO> pageInfo = new PageInfo<>();
        Integer userId = liveParamDTO.getUserId();
        Integer pageNum = liveParamDTO.getPageNum();
        Integer pageSize = liveParamDTO.getPageSize();
        List<UserFollowDTO> userFollowDTOList = initPageData(userId);
        //如果关注列表为空，直播直接冒得
        if (CollectionUtils.isEmpty(userFollowDTOList)) {
            return pageInfo;
        }
        //先拿到关注人userIdList
        List<Integer> followUserList =
            userFollowDTOList.stream().map(UserFollowDTO::getFollowUserId).collect(Collectors.toList());
        Page<TbLiveRoomDO> page = PageHelper.startPage(pageNum, pageSize);
        tbLiveRoomService.getDiscoverListByUserList(followUserList);
        List<LiveHomeListDTO> liveHomeListDTOList = page.stream().map(m -> {
            Integer roomId = m.getId();
            LiveHomeListDTO liveHomeListDTO = handleLiveData(m);
            //是否预约
            TbLiveSubscribersDO tbLiveSubscribersDO = tbLiveSubscribersService.getByRoomIdAndUserId(roomId, userId);
            if (Objects.nonNull(tbLiveSubscribersDO)) {
                liveHomeListDTO.setSubscribeStatus(true);
            } else {
                liveHomeListDTO.setSubscribeStatus(false);
            }
            List<String> spuCodeList = tbLiveGoodsService.getGoodsByRoomIdAndLimit(roomId, LiVEROOM_SPU_SHOW_MAX_COUNT);
            //封装商品信息
            if (CollectionUtils.isEmpty(spuCodeList)) {
                return liveHomeListDTO;
            }
            List<LiveHomeGoodDTO> liveHomeGoodDTOList = spuCodeList.stream().map(spuCode -> {
                LiveHomeGoodDTO homeGoodDTO = new LiveHomeGoodDTO();
                TbSpuManageParamDTO tbSpuManageParamDTO = new TbSpuManageParamDTO();
                tbSpuManageParamDTO.setSpuCode(spuCode);
                tbSpuManageParamDTO.setUserMemberRole(UserMemberRoleEnum.GUEST.getRole());
                //获得商品信息
                SpuManageShowDTO spuManageShowDTO = remoteGoodsService.selectOneSpuShowByParam(tbSpuManageParamDTO);
                if (Objects.nonNull(spuManageShowDTO)) {
                    homeGoodDTO = SPU2GOODS_COPIER.copy(spuManageShowDTO);
                    homeGoodDTO.setSpu(spuCode);
                }
                return homeGoodDTO;
            }).collect(Collectors.toList());
            liveHomeListDTO.setGoods(liveHomeGoodDTOList);
            return liveHomeListDTO;
        }).collect(Collectors.toList());
        wrapperPageInfoData(pageInfo, page, liveHomeListDTOList);
        return pageInfo;
    }

    @Override
    public DiscoverPersonalLiveDTO getFollowPersonalLive(Integer userId) {
        DiscoverPersonalLiveDTO discoverPersonalLiveDTO = new DiscoverPersonalLiveDTO();
        TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getFollowUserLastLiveInfo(userId);
        if (Objects.isNull(tbLiveRoomDO)) {
            return null;
        }
        LiveHomeListDTO liveHomeListDTO = handleLiveData(tbLiveRoomDO);
        discoverPersonalLiveDTO = DISCOVER_DTO2DTO_COPIER.copy(liveHomeListDTO);
        //是否预约
        TbLiveSubscribersDO tbLiveSubscribersDO =
            tbLiveSubscribersService.getByRoomIdAndUserId(tbLiveRoomDO.getId(), userId);
        if (Objects.nonNull(tbLiveSubscribersDO)) {
            discoverPersonalLiveDTO.setSubscribeStatus(true);
        } else {
            discoverPersonalLiveDTO.setSubscribeStatus(false);
        }
        //如果直播尚未开始，拼接提示信息
        if (LiveStatusEnum.LIVE_NOT_START.getStatus().equals(tbLiveRoomDO.getState())) {
            Date appointStart = discoverPersonalLiveDTO.getAppointStart();
            long startTime = appointStart.getTime();
            long countDownTime = (startTime - System.currentTimeMillis()) / 1000;
            if (countDownTime > 60) {
                long minute = countDownTime / 60;
                //超过1小时
                long hour = minute / 60;
                //超过1天
                long day = hour / 24;
                if (minute >= 60) {
                    if (hour >= 24) {
                        long hourNum = minute / 60 - day * 24;
                        long minuteNum = minute - (day * 24 * 60 + hourNum * 60);
                        discoverPersonalLiveDTO.setAppointPrompt(day + "天" + hourNum + "小时" + minuteNum + "分钟后开始");
                    } else {
                        String minuteStr = String.valueOf(countDownTime / 60 - hour * 60);
                        discoverPersonalLiveDTO.setAppointPrompt(hour + "小时" + minuteStr + "分钟后开始");
                    }
                } else {
                    //不超过1小时
                    discoverPersonalLiveDTO.setAppointPrompt(minute + "分钟后开始");
                }
            } else {
                discoverPersonalLiveDTO.setAppointPrompt("直播即将开始");
            }
        }
        return discoverPersonalLiveDTO;
    }

    /**
     * 初始化分页
     * @param userId   用户id
     * @return List<UserFollowDTO>
     */
    private List<UserFollowDTO> initPageData(Integer userId) {
        //先查出用户的关注列表
        PageInfo<UserFollowDTO> userFollowDTOPageInfo =
            remoteUserFollowService.listUserFollow(userId, followPageNum, followPageSize);
        return userFollowDTOPageInfo.getList();
    }

    private LiveHomeListDTO handleLiveData(TbLiveRoomDO tbLiveRoomDO) {
        LiveHomeListDTO liveHomeListDTO = ROOM_DO2DTO_LIVEHOME.copy(tbLiveRoomDO);
        //设置回放时长
        if (StringUtils.isNotBlank(tbLiveRoomDO.getStreamFname())) {
            if (StringUtils.isNotBlank(tbLiveRoomDO.getBackTime())) {
                liveHomeListDTO.setLiveTime(tbLiveRoomDO.getBackTime());
            } else {
                String timeStr = VisitorUtils.getBackTime(qiNiuService.getBackUrl(tbLiveRoomDO.getStreamFname()));
                if (StringUtils.isNotBlank(timeStr)) {
                    tbLiveRoomDO.setBackTime(timeStr);
                    liveHomeListDTO.setLiveTime(timeStr);
                    tbLiveRoomService.updateById(tbLiveRoomDO);
                }
            }
        }
        liveHomeListDTO.setVisitorNum(VisitorUtils.getVisitors(tbLiveRoomDO.getVisitorNum(), visitor_rule));
        //回放地址
        liveHomeListDTO.setStreamBackUrl(qiNiuService.getBackUrl(tbLiveRoomDO.getStreamFname()));
        //flv回放地址
        liveHomeListDTO.setStreamFlvBackUrl(qiNiuService.getFlvBackUrl(tbLiveRoomDO.getStreamFlvFname()));
        //播放地址
        liveHomeListDTO.setHLSPlayURL(qiNiuService.getHLSPlayURL(tbLiveRoomDO.getStreamKey()));
        liveHomeListDTO.setRTMPPlayURL(qiNiuService.getRTMPPlayURL(tbLiveRoomDO.getStreamKey()));
        liveHomeListDTO.setHDLPlayURL(qiNiuService.getHDLPlayURL(tbLiveRoomDO.getStreamKey()));
        liveHomeListDTO.setIfConcern(true);
        TbUserDO tbUserDO = tbLiveVisitorService.getTbUserByUserId(tbLiveRoomDO.getLiveUserId());
        Optional.ofNullable(tbUserDO).ifPresent(s -> {
            //设置头像
            liveHomeListDTO.setHeadimgurl(s.getHeadimgurl());
            liveHomeListDTO.setShopName(StringUtils.isBlank(s.getShopName()) ? s.getNickname() : s.getShopName());
        });
        //封装商品数量
        int goodsCount =
            tbLiveGoodsService.selectCountByUserIdAndRoomId(tbLiveRoomDO.getLiveUserId(), tbLiveRoomDO.getId());
        liveHomeListDTO.setGoodsCount(goodsCount);
        return liveHomeListDTO;
    }

    /**
     * 封装分页数据
     *
     * @param pageInfo            分页信息
     * @param page                分页数据
     * @param liveHomeListDTOList 数据
     */
    private void wrapperPageInfoData(PageInfo<LiveHomeListDTO> pageInfo, Page<TbLiveRoomDO> page,
        List<LiveHomeListDTO> liveHomeListDTOList) {
        pageInfo.setList(liveHomeListDTOList);
        pageInfo.setHasNextPage(page.getPages() > page.getPageNum());
        pageInfo.setPages(page.getPages());
        pageInfo.setTotal(page.getTotal());
    }

}

package com.jiebai.qqsk.live.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Sets;
import com.jiebai.framework.common.cglib.BaseBeanCopier;
import com.jiebai.framework.common.cglib.SimpleBeanCopier;
import com.jiebai.framework.service.AbstractService;
import com.jiebai.qqsk.live.constant.*;
import com.jiebai.qqsk.live.dao.TbLiveGoodsMapper;
import com.jiebai.qqsk.live.dao.TbLiveRoomMapper;
import com.jiebai.qqsk.live.dao.TbLiveSubscribersMapper;
import com.jiebai.qqsk.live.dto.LiveRoomDTO;
import com.jiebai.qqsk.live.dto.SmallProgramLiveStartParamDTO;
import com.jiebai.qqsk.live.exception.RemoteLiveServiceErrorCodeEnum;
import com.jiebai.qqsk.live.exception.RemoteLiveServiceException;
import com.jiebai.qqsk.live.model.TbLiveGoodsDO;
import com.jiebai.qqsk.live.model.TbLiveRoomDO;
import com.jiebai.qqsk.live.model.TbLiveSubscribersDO;
import com.jiebai.qqsk.live.service.QiNiuService;
import com.jiebai.qqsk.live.service.RongYunService;
import com.jiebai.qqsk.live.service.TbLiveRoomService;
import com.jiebai.qqsk.live.utils.DateUtils;
import com.qiniu.pili.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lichenguang
 * @version v1.0.0
 * @date 2019/11/13 19:11:15
 */
@Slf4j
@Service
public class TbLiveRoomServiceImpl extends AbstractService<TbLiveRoomDO> implements TbLiveRoomService {

    private static BaseBeanCopier<LiveRoomDTO, TbLiveRoomDO> LIVE_ROOM_DTO2DO_COPIER =
            new SimpleBeanCopier<>(LiveRoomDTO.class, TbLiveRoomDO.class);

    @Value("${live.mq.xingemessage.topic}")
    private String xingeMQDestination;

    @Value("${live.mq.smallprogram.topic}")
    private String smallProgramDestination;

    @NacosValue(value = "${live.delayCloseTime:-10}", autoRefreshed = true)
    private Integer delayCloseTime;

    @Resource
    private TbLiveRoomMapper tbLiveRoomMapper;

    @Resource
    private TbLiveGoodsMapper tbLiveGoodsMapper;

    @Resource
    private QiNiuService qiNiuService;

    @Resource
    private RongYunService rongYunService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private TbLiveSubscribersMapper tbLiveSubscribersMapper;

    @Override
    public void updateById(TbLiveRoomDO model) {
        super.updateById(model);
    }

    @Override
    public TbLiveRoomDO getById(Integer id) {
        return super.getById(id);
    }

    @Override
    public void removeById(Integer id) {
        super.removeById(id);
    }

    @Override
    public TbLiveRoomDO getByRoomId(Integer roomId) {
        return tbLiveRoomMapper.getByRoomId(roomId);
    }

    @Override
    public List<TbLiveRoomDO> getAllRoom(String category,Integer visitorNum) {
        return tbLiveRoomMapper.getAllRoom(category,visitorNum);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertSelective(LiveRoomDTO liveRoomDTO) {
        Integer liveUserId = liveRoomDTO.getLiveUserId();
        TbLiveRoomDO liveRoomDO = LIVE_ROOM_DTO2DO_COPIER.copy(liveRoomDTO);
        //生成steamKey
        String streamKey = qiNiuService.getKey(liveUserId);
        //获取推流地址
        Stream stream = qiNiuService.create(streamKey);
        if (null == stream) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.CREATE_STREAM_FAIL);
        }
        String streamUrl = qiNiuService.getRTMPPublishURL(streamKey, Integer.MAX_VALUE);
        if (StringUtils.isBlank(streamUrl)) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.QINIU_GET_STREAM_URL_FAILURE);
        }
        liveRoomDO.setStreamKey(streamKey);
        liveRoomDO.setStreamUrl(streamUrl);
        //添加直播间信息
        int res = tbLiveRoomMapper.insertAndReturnId(liveRoomDO);
        if (res < 1) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.BUSINESS_HANDLING_FAIL);
        }
        Integer roomId = liveRoomDO.getId();
        List<String> spuCodeList = liveRoomDTO.getSpuCodeList();
        List<TbLiveGoodsDO> liveGoodsDOList = spuCodeList.stream().map(spuCode -> {
            TbLiveGoodsDO tbLiveGoodsDO = new TbLiveGoodsDO();
            tbLiveGoodsDO.setSpuCode(spuCode);
            tbLiveGoodsDO.setUserId(liveUserId);
            tbLiveGoodsDO.setShowState(0);//默认值
            tbLiveGoodsDO.setRoomId(roomId);
            return tbLiveGoodsDO;
        }).collect(Collectors.toList());
        //倒序排列橱窗商品
        Collections.reverse(liveGoodsDOList);
        //添加橱窗商品信息
        tbLiveGoodsMapper.insertList(liveGoodsDOList);
        return roomId;
    }

    @Override
    public int updateByPrimaryKeySelective(TbLiveRoomDO liveRoomDO) {
        return tbLiveRoomMapper.updateByPrimaryKeySelective(liveRoomDO);
    }

    @Override
    public int getPraisesByLiveUserId(Integer userId) {
        return tbLiveRoomMapper.getPraisesByLiveUserId(userId);
    }

    @Override
    public List<TbLiveRoomDO> getLiveRoomByUserId(Integer userId) {
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("liveUserId", userId);
        criteria.andEqualTo("isDeleted", DeleteFlagEnum.NOT_DELETED.getCode());
        //按直播状态降序排，进行中，预约中，已结束
        condition.orderBy("state").desc();
        condition.orderBy("gmtCreate").desc();
        return tbLiveRoomMapper.selectByCondition(condition);
    }

    @Override
    public int getLiveRoomNotOverCount(Integer userId) {
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("liveUserId", userId);
        criteria.andNotEqualTo("state", LiveStatusEnum.LIVE_OVER.getStatus());
        criteria.andEqualTo("isDeleted", DeleteFlagEnum.NOT_DELETED.getCode());
        return tbLiveRoomMapper.selectCountByCondition(condition);
    }

    @Override
    public TbLiveRoomDO getByIdAndUserId(Integer roomId, Integer userId) {
        TbLiveRoomDO tbLiveRoomDO = new TbLiveRoomDO();
        tbLiveRoomDO.setId(roomId);
        tbLiveRoomDO.setLiveUserId(userId);
        tbLiveRoomDO.setIsDeleted(DeleteFlagEnum.NOT_DELETED.getCode());
        return tbLiveRoomMapper.selectOne(tbLiveRoomDO);
    }

    @Override
    public String updateLiveStatusByIdAndUserId(Integer roomId, Integer userId) {
        TbLiveRoomDO tbLiveRoomDO = new TbLiveRoomDO();
        //获取融云imId
        String imId = rongYunService.getImId(userId);
        if (StringUtils.isBlank(imId)) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.GET_IMID_FAIL);
        }
        tbLiveRoomDO.setImId(imId);
        //将直播状态改为进行中
        tbLiveRoomDO.setState(LiveStatusEnum.LIVE_STARTING.getStatus());
        //记录开播时间
        tbLiveRoomDO.setLiveStart(new Date());
        int res = tbLiveRoomMapper.updateByConditionSelective(tbLiveRoomDO, getRoomIdAndUserIdCondition(roomId, userId));
        if (res < 1) {
            throw new RemoteLiveServiceException("开播失败!");
        }
        //主播开播成功给预约了直播的用户推送消息
        TbLiveSubscribersDO tbLiveSubscribersDO = new TbLiveSubscribersDO();
        tbLiveSubscribersDO.setRoomId(roomId);
        //如果没有预约直播的人，直接返回
        List<TbLiveSubscribersDO> subscribersDOList = tbLiveSubscribersMapper.select(tbLiveSubscribersDO);
        if (CollectionUtils.isEmpty(subscribersDOList)) {
            return imId;
        }
        //筛选需要推送信鸽的用户列表
        List<Integer> xingeUserIdList = subscribersDOList.stream().filter(f -> StringUtils.isBlank(f.getFormId()))
                .map(TbLiveSubscribersDO::getUserId).collect(Collectors.toList());
        //筛选需要推送小程序服务通知的用户列表
        List<Integer> miniProgramUserIdList =
                subscribersDOList.stream().filter(f -> StringUtils.isNotBlank(f.getFormId()))
                        .map(TbLiveSubscribersDO::getUserId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(xingeUserIdList)) {
            handleXingeMessage(xingeUserIdList);
        }
        if (!CollectionUtils.isEmpty(miniProgramUserIdList)) {
            handleMiniProgramMessage(miniProgramUserIdList, roomId);
        }
        TbLiveSubscribersDO updateState = new TbLiveSubscribersDO();
        Condition subscribeCondition = new Condition(TbLiveSubscribersDO.class);
        Example.Criteria subscribeCriteria = subscribeCondition.createCriteria();
        subscribeCriteria.andEqualTo("roomId", roomId);
        //变成已经发送
        updateState.setSendState(SendStatusEnum.IS_SEND.getStatus());
        updateState.setSendTime(new Date());
        tbLiveSubscribersMapper.updateByConditionSelective(updateState, subscribeCondition);
        return imId;
    }

    /**
     * 处理信鸽消息通知
     *
     * @param userIdList 用户列表
     */
    private void handleXingeMessage(List<Integer> userIdList) {
        for (Integer userId : userIdList) {
            StringBuilder paramsBuilder = new StringBuilder();
            paramsBuilder.append(userId);
            paramsBuilder.append("-");
            //content
            paramsBuilder.append("您预约的直播开播啦～赶紧搬上小板凳过来围观吧!");
            paramsBuilder.append("-");
            //title
            paramsBuilder.append("主播开播通知");
            paramsBuilder.append("-");
            //orderNo
            paramsBuilder.append("无订单");
            rocketMQTemplate.convertAndSend(xingeMQDestination, paramsBuilder.toString());
        }
    }

    /**
     * 处理小程序服务通知
     *
     * @param userIdList userIdList
     * @param roomId     房间号
     */
    private void handleMiniProgramMessage(List<Integer> userIdList, Integer roomId) {
        List<String> toUserList =
                tbLiveSubscribersMapper.getUserOpenIdBySubscriberUserIdList(userIdList, AppTypeEnum.WXMA_KXSC.getAppId());
        SmallProgramLiveStartParamDTO liveStartParamDTO = new SmallProgramLiveStartParamDTO();
        liveStartParamDTO.setToUserList(toUserList);
        TbLiveRoomDO roomDO = tbLiveRoomMapper.selectByPrimaryKey(roomId);
        liveStartParamDTO.setAppointStart(DateUtils.liveDateToStringWithoutSecond(roomDO.getAppointStart()));
        liveStartParamDTO.setRoomId(roomId);
        rocketMQTemplate.convertAndSend(smallProgramDestination, JSON.toJSONString(liveStartParamDTO));
    }

    @Override
    public Integer closeLiveRoom(Integer roomId) {
        TbLiveRoomDO tbLiveRoomDO = new TbLiveRoomDO();
        tbLiveRoomDO.setId(roomId);
        tbLiveRoomDO.setState(LiveStatusEnum.LIVE_OVER.getStatus());
        tbLiveRoomDO.setIsDeleted(DeleteFlagEnum.IS_DELETED.getCode());
        tbLiveRoomDO.setGmtModified(new Date());
        return tbLiveRoomMapper.updateByPrimaryKeySelective(tbLiveRoomDO);
    }

    @Override
    public void closeYunServices(String streamKey, String imId) {
        qiNiuService.stop(streamKey);
        rongYunService.destroy(imId);
    }

    @Override
    public TbLiveRoomDO selectNotDeleteLiveRoomById(Integer roomId) {
        TbLiveRoomDO tbLiveRoomDO = new TbLiveRoomDO();
        tbLiveRoomDO.setId(roomId);
        tbLiveRoomDO.setIsDeleted(DeleteFlagEnum.NOT_DELETED.getCode());
        return tbLiveRoomMapper.selectOne(tbLiveRoomDO);
    }

    @Override
    public List<TbLiveRoomDO> selectDelayTaskLiveRoomList() {
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("isDeleted", DeleteFlagEnum.NOT_DELETED.getCode());
        criteria.andEqualTo("state", LiveStatusEnum.LIVE_NOT_START.getStatus());
        criteria.andLessThanOrEqualTo("appointStart", DateUtils.getAfterMinutesDate(new Date(), delayCloseTime));
        return tbLiveRoomMapper.selectByCondition(condition);
    }

    @Override
    public int deleteLiveRoomById(Integer roomId) {
        TbLiveRoomDO tbLiveRoomDO = new TbLiveRoomDO();
        tbLiveRoomDO.setIsDeleted(DeleteFlagEnum.IS_DELETED.getCode());
        tbLiveRoomDO.setId(roomId);
        return tbLiveRoomMapper.updateByPrimaryKeySelective(tbLiveRoomDO);
    }

    @Override
    public int updateByIdAndUserId(Integer roomId, Integer userId, TbLiveRoomDO tbLiveRoomDO) {
        return tbLiveRoomMapper.updateByConditionSelective(tbLiveRoomDO, getRoomIdAndUserIdCondition(roomId, userId));
    }


    private Condition getRoomIdAndUserIdCondition(Integer roomId, Integer userId) {
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("id", roomId);
        criteria.andEqualTo("liveUserId", userId);
        return condition;
    }

    @Override
    public int getRoomVisitorNum(Integer userId) {
        return tbLiveRoomMapper.getRoomVisitorNum(userId);
    }

    @Override
    public TbLiveRoomDO getUserLastLiveInfo(Integer userId) {
        return tbLiveRoomMapper.selectUserLastLiveInfo(userId);
    }

    @Override
    public List<TbLiveRoomDO> getNotDeleteRoomList(String category) {
        return tbLiveRoomMapper.getNotDeleteRoomList(category);
    }

    @Override
    public List<TbLiveRoomDO> getDiscoverLivingUserList(List<Integer> userIdList) {
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andIn("liveUserId", userIdList);
        criteria.andEqualTo("isPublic", LiveRoomPublicStatusEnum.IS_PUBLIC.getStatus());
        criteria.andEqualTo("isDeleted", DeleteFlagEnum.NOT_DELETED.getCode());
        criteria.andEqualTo("state", LiveStatusEnum.LIVE_STARTING.getStatus());
        return tbLiveRoomMapper.selectByCondition(condition);
    }

    @Override
    public List<TbLiveRoomDO> getDiscoverListByUserList(List<Integer> followUserList) {
        return tbLiveRoomMapper.selectDiscoverListByUserList(followUserList);
    }

    @Override
    public TbLiveRoomDO getFollowUserLastLiveInfo(Integer userId) {
        return tbLiveRoomMapper.selectFollowUserLastLiveInfo(userId);
    }

    @Override
    public List<TbLiveRoomDO> getRoomListForManager(TbLiveRoomDO liveRoomDO) {
        return tbLiveRoomMapper.getRoomListForManager(liveRoomDO);
    }

    @Override
    public List<TbLiveRoomDO> getHeadSearchList(String search) {
        return tbLiveRoomMapper.getHeadSearchList(search);
    }

    @Override
    public List<TbLiveRoomDO> getRecommendList() {
        return tbLiveRoomMapper.getRecommendList();
    }

    @Override
    public Integer getLiveRoomByCategryId(Integer id) {
        return tbLiveRoomMapper.getLiveRoomByCategryId(id);
    }

    @Override
    public int getOpenLivesByUserId(Integer userId) {
        return tbLiveRoomMapper.getOpenLivesByUserId(userId);
    }

    @Override
    public void scheduleChangeLiveRank() {
        Date now = new Date();
        Date beforeDate = org.apache.commons.lang3.time.DateUtils.addMinutes(now, -5);
        tbLiveRoomMapper.updateRank15BackLive(beforeDate);
    }

    @Override
    public List<TbLiveRoomDO> findLiveRoomIsLiving() {
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andNotEqualTo("state", LiveStatusEnum.LIVE_OVER.getStatus());
        criteria.andEqualTo("isDeleted", DeleteFlagEnum.NOT_DELETED.getCode());
        return tbLiveRoomMapper.selectByCondition(condition);
    }

    @Override
    public void updateRankById(Integer id, int rank) {
        tbLiveRoomMapper.updateRankById(id, rank);
    }

    @Override
    public List<TbLiveRoomDO> selectLessOneDayAndEndLiveRoom() {
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andNotEqualTo("rank", -2);
        criteria.andEqualTo("state", LiveStatusEnum.LIVE_OVER.getStatus());
        criteria.andEqualTo("isDeleted", DeleteFlagEnum.NOT_DELETED.getCode());
        criteria.andGreaterThanOrEqualTo("liveEnd", org.apache.commons.lang3.time.DateUtils.addDays(new Date(), -1));
        return tbLiveRoomMapper.selectByCondition(condition);
    }

    @Override
    public void updateRankByIds(List<Integer> roomIdList, int rank) {
        if (roomIdList.size() < 1) {
            return;
        }
        TbLiveRoomDO record = new TbLiveRoomDO();
        record.setRank(rank);
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("isDeleted", DeleteFlagEnum.NOT_DELETED.getCode());
        criteria.andIn("id", roomIdList);
        tbLiveRoomMapper.updateByConditionSelective(record, condition);
        log.info("【定时任务处理结束的直播间】, roomIdList = {}, rank = {}", roomIdList, rank);
    }

    @Override
    public List<TbLiveRoomDO> findEndLiveRoomMoreOneDayByDay(int start, int end) {
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("state", LiveStatusEnum.LIVE_OVER.getStatus());
        criteria.andEqualTo("isDeleted", DeleteFlagEnum.NOT_DELETED.getCode());
        criteria.andNotEqualTo("rank", -2);
        criteria.andLessThan("liveEnd", org.apache.commons.lang3.time.DateUtils.addDays(new Date(), start));
        criteria.andGreaterThanOrEqualTo("liveEnd", org.apache.commons.lang3.time.DateUtils.addDays(new Date(), end));
        return tbLiveRoomMapper.selectByCondition(condition);
    }

    @Override
    public void updateLessOneDayLiveTimeLess5(int backNum) {
        TbLiveRoomDO record = new TbLiveRoomDO();
        record.setRank(-2);
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("state", LiveStatusEnum.LIVE_OVER.getStatus());
        criteria.andEqualTo("isDeleted", DeleteFlagEnum.NOT_DELETED.getCode());
        criteria.andNotEqualTo("rank", -2);
        Example.Criteria newCriteria = condition.createCriteria();
        newCriteria.andLessThanOrEqualTo("backTime", "00:05:00")
                .orLessThanOrEqualTo("visitorNum", backNum);
        condition.and(newCriteria);
        log.info("改变时长小5分钟或人气<50的直播间的rank---start");
        tbLiveRoomMapper.updateByConditionSelective(record, condition);
        log.info("改变时长小5分钟或人气<50的直播间的rank---end");
    }

    @Override
    public List<TbLiveRoomDO> findPublicLiveRoomByUserId(Integer userId) {
        return tbLiveRoomMapper.selectPublicLiveRoomByUserId(userId);
    }

}

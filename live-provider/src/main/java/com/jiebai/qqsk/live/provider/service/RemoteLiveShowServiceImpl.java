package com.jiebai.qqsk.live.provider.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.esotericsoftware.minlog.Log;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jiebai.framework.common.cglib.BaseBeanCopier;
import com.jiebai.framework.common.cglib.SimpleBeanCopier;
import com.jiebai.qqsk.discover.dto.UserFollowDTO;
import com.jiebai.qqsk.discover.remote.RemoteUserFollowService;
import com.jiebai.qqsk.goods.dto.SpuManageShowDTO;
import com.jiebai.qqsk.goods.dto.TbSpuManageParamDTO;
import com.jiebai.qqsk.goods.remote.RemoteGoodsService;
import com.jiebai.qqsk.live.config.YunConfig;
import com.jiebai.qqsk.live.constant.*;
import com.jiebai.qqsk.live.dto.*;
import com.jiebai.qqsk.live.event.BusinessEventMessage;
import com.jiebai.qqsk.live.event.BusinessEventQueue;
import com.jiebai.qqsk.live.exception.RemoteLiveServiceErrorCodeEnum;
import com.jiebai.qqsk.live.exception.RemoteLiveServiceException;
import com.jiebai.qqsk.live.model.*;
import com.jiebai.qqsk.live.remote.RemoteLiveShowService;
import com.jiebai.qqsk.live.service.*;
import com.jiebai.qqsk.live.utils.Base16;
import com.jiebai.qqsk.live.utils.ConvertorTimeUtils;
import com.jiebai.qqsk.live.utils.VisitorUtils;
import com.jiebai.qqsk.member.dto.UserDTO;
import com.jiebai.qqsk.member.remote.RemoteUserService;
import com.jiebai.qqsk.member.utils.DateUtils;
import com.jiebai.qqsk.message.dto.LiveRoomFansPushDTO;
import com.jiebai.qqsk.message.remote.RemoteXingeAppService;
import com.qiniu.pili.Stream;
import io.rong.models.chatroom.ChatroomMember;
import io.rong.models.response.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.jiebai.qqsk.live.constant.LiveConstant.DELAY_SECOND;
import static com.jiebai.qqsk.live.constant.LiveConstant.LiVEROOM_SPU_SHOW_MAX_COUNT;

/**
 * 观看直播相关服务
 *
 * @author cxy
 */
@Slf4j
@Component
@Service(interfaceClass = RemoteLiveShowService.class, version = "${provider.live.version}", validation = "false", retries = 0, timeout = 10000)
public class RemoteLiveShowServiceImpl implements RemoteLiveShowService {

    private static BaseBeanCopier<LiveRoomDTO, TbLiveRoomDO> LIVEROOMDTO2DO_COPIER =
            new SimpleBeanCopier<>(LiveRoomDTO.class, TbLiveRoomDO.class);

    private static BaseBeanCopier<TbLiveRoomDO, MyLiveDTO> LIVEROOMDO2MYLIVEDO_COPIER =
            new SimpleBeanCopier<>(TbLiveRoomDO.class, MyLiveDTO.class);

    private static BaseBeanCopier<SpuManageShowDTO, LiveHomeGoodDTO> SPU2GOODS_COPIER =
            new SimpleBeanCopier<>(SpuManageShowDTO.class, LiveHomeGoodDTO.class);

    private static BaseBeanCopier<TbLiveRoomDO, LiveRoomManagerDTO> LIVEROOMDO2MANAGERDTO_COPIER =
            new SimpleBeanCopier<>(TbLiveRoomDO.class, LiveRoomManagerDTO.class);

    private static BaseBeanCopier<LiveRoomManagerQueryDTO, TbLiveRoomDO> LIVEROOMMANAGERQUERYDTO2ROOMDO_COPIER =
            new SimpleBeanCopier<>(LiveRoomManagerQueryDTO.class, TbLiveRoomDO.class);

    private static BaseBeanCopier<TbLiveCategoryDO, LiveCategoryDTO> LIVE_CATEGORY_DO2DTO =
            new SimpleBeanCopier<>(TbLiveCategoryDO.class, LiveCategoryDTO.class);

    private static BaseBeanCopier<LiveCategoryDTO, TbLiveCategoryDO> LIVE_CATEGORY_DTO2DO =
            new SimpleBeanCopier<>(LiveCategoryDTO.class, TbLiveCategoryDO.class);

    @Reference(version = "${consumer.goods.version}", validation = "false")
    private RemoteGoodsService remoteGoodsService;

    @Reference(version = "${consumer.discover.version}", validation = "false")
    private RemoteUserFollowService remoteUserFollowService;

    @Reference(version = "${consumer.message.version}", validation = "false")
    private RemoteXingeAppService remoteXingeAppService;

    @Reference(version = "${consumer.member.version}", validation = "false")
    private RemoteUserService remoteUserService;
    @Resource
    private TbLiveRoomService tbLiveRoomService;

    @Resource
    private TbLiveImTokenService tbLiveImTokenService;

    @Resource
    private TbLiveVisitorService tbLiveVisitorService;

    @Resource
    private TbLiveSubscribersService tbLiveSubscribersService;

    @Resource
    private TbLiveGoodsService tbLiveGoodsService;

    @Resource
    private QiNiuService qiNiuService;

    @Resource
    private RongYunService rongYunService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    BusinessEventQueue businessEventQueue;

    @Resource
    private TbLiveCategoryService tbLiveCategoryService;

    @Autowired
    private TimeChatRoomService timeChatRoomService;

    @Autowired
    private EnterRoomDelayService enterRoomDelayService;

    @NacosValue(value = "${max_live_time_hour:2}", autoRefreshed = true)
    private Integer max_live_time_hour;

    @NacosValue(value = "${im_max_live_join_room:49000}", autoRefreshed = true)
    private Integer im_max_live_join_room;

    @NacosValue(value = "${update_total_today_visitor_time:60}", autoRefreshed = true)
    private Integer update_total_today_visitor_time;

    @NacosValue(value = "${visitor_rule:}", autoRefreshed = true)
    private String visitor_rule;

    @NacosValue(value = "${show_live_min_visitor_num:35}", autoRefreshed = true)
    private Integer show_live_min_visitor_num;

    @NacosValue(value = "${live_super_manager:20}", autoRefreshed = true)
    private List<Integer> liveSuperManager;

    @NacosValue(value = "${SEND_LIVE_GOLD_ONE:100}", autoRefreshed = true)
    private Integer SEND_LIVE_GOLD_ONE;

    @NacosValue(value = "${SEND_LIVE_GOLD_TWO:200}", autoRefreshed = true)
    private Integer SEND_LIVE_GOLD_TWO;


    @NacosValue(value = "${NORMAL_LIVER_OPEN_VISITORS:100}", autoRefreshed = true)
    private Integer NORMAL_LIVER_OPEN_VISITORS; //普通用户开直播打底人气值

    @NacosValue(value = "${FAMOUS_LIVER_OPEN_VISITORS:1000}", autoRefreshed = true)
    private Integer FAMOUS_LIVER_OPEN_VISITORS; //知名用户开直播打底人气值

    @NacosValue(value = "${FAMOUS_LIVERS:}", autoRefreshed = true)
    private String FAMOUS_LIVERS; //知名用户列表

    @NacosValue(value = "${LIVER_SHARE_VISITORS_LOW:30}", autoRefreshed = true)
    private Integer LIVER_SHARE_VISITORS_LOW; //用户分享直播间增加人气值(低)

    @NacosValue(value = "${LIVER_SHARE_ADDPRAISE_FOLLOW_LOW:20}", autoRefreshed = true)
    private Integer LIVER_SHARE_ADDPRAISE_FOLLOW_LOW; //用户点赞或者被关注增加人气值(低)

    @NacosValue(value = "${LIVER_BULLET_CHAT_LOW:5}", autoRefreshed = true)
    private Integer LIVER_BULLET_CHAT_LOW;//用户弹幕增加人气值(低)

    @NacosValue(value = "${LIVER_SHARE_VISITORS_HIGH:15}", autoRefreshed = true)
    private Integer LIVER_SHARE_VISITORS_HIGH; //用户分享直播间增加人气值(高)

    @NacosValue(value = "${LIVER_SHARE_ADDPRAISE_FOLLOW_HIGH:10}", autoRefreshed = true)
    private Integer LIVER_SHARE_ADDPRAISE_FOLLOW_HIGH; //用户点赞或者被关注增加人气值(高)

    @NacosValue(value = "${LIVER_BULLET_CHAT_HIGH:5}", autoRefreshed = true)
    private Integer LIVER_BULLET_CHAT_HIGH; //用户弹幕增加人气值(高)

    @NacosValue(value = "${LIVER_VISITORS_BORDER:500}", autoRefreshed = true)
    private Integer LIVER_VISITORS_BORDER; //高低的边界


    @Override
    public PageInfo<LiveHomeListDTO> getLiveHomeList(LiveParamDTO liveParamDTO) {
        Integer userId = liveParamDTO.getUserId();
        if (Objects.isNull(liveParamDTO.getPageSize()) || liveParamDTO.getPageSize() <= 0) {
            liveParamDTO.setPageSize(Integer.valueOf(1));
        }
        if (Objects.isNull(liveParamDTO.getPageNum()) || liveParamDTO.getPageNum() < 0) {
            liveParamDTO.setPageNum(Integer.valueOf(1));
        }
        Page<TbLiveRoomDO> page = PageHelper.startPage(liveParamDTO.getPageNum(), liveParamDTO.getPageSize());
        if (!CollectionUtils.isEmpty(liveSuperManager) && liveSuperManager.contains(userId)) {
            //如果是超级管理，默认查所有直播（包括私密）
            tbLiveRoomService.getNotDeleteRoomList(liveParamDTO.getCategory());
        } else {
            tbLiveRoomService.getAllRoom(liveParamDTO.getCategory(), show_live_min_visitor_num);//显示需要的最小观众数
        }
        ArrayList<LiveHomeListDTO> list = Lists.newArrayList();
        PageInfo<LiveHomeListDTO> pageInfo = new PageInfo<>(list);
        if (page != null && page.getTotal() > 0) {
            for (TbLiveRoomDO tbLiveRoomDO : page) {
                //直播间信息
                LiveHomeListDTO liveHomeListDTO = new LiveHomeListDTO();
                if (!StringUtils.isEmpty(tbLiveRoomDO.getStreamFname())) {
                    if (!StringUtils.isEmpty(tbLiveRoomDO.getBackTime())) {
                        liveHomeListDTO.setLiveTime(tbLiveRoomDO.getBackTime());
                    } else {
                        String timeStr = VisitorUtils.getBackTime(qiNiuService.getBackUrl(tbLiveRoomDO.getStreamFname()));
                        if (!StringUtils.isEmpty(timeStr)) {
                            tbLiveRoomDO.setBackTime(timeStr);
                            tbLiveRoomService.updateById(tbLiveRoomDO);
                            liveHomeListDTO.setLiveTime(timeStr);
                        }
                    }
                }
                liveHomeListDTO.setCover(tbLiveRoomDO.getCover());
                liveHomeListDTO.setId(tbLiveRoomDO.getId());
                liveHomeListDTO.setState(tbLiveRoomDO.getState());
                liveHomeListDTO.setTitle(tbLiveRoomDO.getTitle());
                liveHomeListDTO.setVisitorNum(VisitorUtils.getVisitors(tbLiveRoomDO.getVisitorNum(), visitor_rule));
                liveHomeListDTO.setImId(tbLiveRoomDO.getImId());
                liveHomeListDTO.setLiveUserId(tbLiveRoomDO.getLiveUserId());
                liveHomeListDTO.setStreamUrl(tbLiveRoomDO.getStreamUrl());//推流地址
                liveHomeListDTO.setStreamBackUrl(qiNiuService.getBackUrl(tbLiveRoomDO.getStreamFname()));//回放地址
                liveHomeListDTO.setStreamFlvBackUrl(qiNiuService.getFlvBackUrl(tbLiveRoomDO.getStreamFlvFname()));//flv回放地址
                //播放地址
                liveHomeListDTO.setRTMPPlayURL(qiNiuService.getRTMPPlayURL(tbLiveRoomDO.getStreamKey()));
                liveHomeListDTO.setHDLPlayURL(qiNiuService.getHDLPlayURL(tbLiveRoomDO.getStreamKey()));
                liveHomeListDTO.setHLSPlayURL(qiNiuService.getHLSPlayURL(tbLiveRoomDO.getStreamKey()));
                TbUserDO tbUserDO = tbLiveVisitorService.getTbUserByUserId(tbLiveRoomDO.getLiveUserId());
                //主播信息
                if (Objects.nonNull(tbUserDO)) {
                    if (StringUtils.isEmpty(tbUserDO.getShopName())) {
                        liveHomeListDTO.setShopName(tbUserDO.getNickname());
                    } else {
                        liveHomeListDTO.setShopName(tbUserDO.getShopName());
                    }
                    liveHomeListDTO.setHeadimgurl(tbUserDO.getHeadimgurl());
                }
                //是否关注
                liveHomeListDTO.setIfConcern(
                        remoteUserFollowService.ifFollowOfFollow(userId, tbLiveRoomDO.getLiveUserId()));
                //展示前面3个商品信息getLiveHomeGoodList
                List<LiveHomeGoodDTO> liveHomeGoodDTOList = Lists.newArrayList();
                //橱窗中的商品
                List<TbLiveGoodsDO> liveGoodsDOList = tbLiveGoodsService.getGoodsByRoomId(tbLiveRoomDO.getId());
                liveHomeListDTO.setGoodsCount(liveGoodsDOList.size());
                if (liveGoodsDOList.size() >= 3) {
                    liveGoodsDOList = liveGoodsDOList.subList(0, 3);
                }
                //是否预约
                TbLiveSubscribersDO tbLiveSubscribersDO =
                        tbLiveSubscribersService.getByRoomIdAndUserId(tbLiveRoomDO.getId(), userId);
                //查不到订阅信息
                if (Objects.isNull(tbLiveSubscribersDO)) {
                    //未预约
                    liveHomeListDTO.setSubscribeStatus(false);
                } else {
                    liveHomeListDTO.setSubscribeStatus(true);
                }
                liveHomeListDTO.setAppointStart(tbLiveRoomDO.getAppointStart());
                for (TbLiveGoodsDO tbLiveGoodsDO : liveGoodsDOList) {
                    TbSpuManageParamDTO param = new TbSpuManageParamDTO();
                    param.setSpuCode(tbLiveGoodsDO.getSpuCode());
                    param.setUserMemberRole(UserMemberRoleEnum.GUEST.getRole());
                    //获得商品信息
                    SpuManageShowDTO spuManageShowDTO = remoteGoodsService.selectOneSpuShowByParam(param);
                    if (Objects.nonNull(spuManageShowDTO)) {
                        LiveHomeGoodDTO liveHomeGoodDTO = new LiveHomeGoodDTO();
                        liveHomeGoodDTO.setSpu(tbLiveGoodsDO.getSpuCode());
                        liveHomeGoodDTO.setPrice(spuManageShowDTO.getPrice());
                        liveHomeGoodDTO.setSpuImage(spuManageShowDTO.getSpuImage());
                        liveHomeGoodDTO.setSpuTitle(spuManageShowDTO.getSpuTitle());
                        liveHomeGoodDTO.setSpuId(spuManageShowDTO.getSpuId());
                        liveHomeGoodDTO.setSpuCode(spuManageShowDTO.getSpucode());
                        liveHomeGoodDTOList.add(liveHomeGoodDTO);
                    }
                }
                liveHomeListDTO.setGoods(liveHomeGoodDTOList);
                list.add(liveHomeListDTO);
            }
            pageInfo.setHasNextPage(page.getPages() > page.getPageNum());
            pageInfo.setTotal(page.getTotal());
            pageInfo.setPages(page.getPages());
        }
        return pageInfo;
    }

    @Override
    public PageInfo<LiveHomeGoodDTO> getLiveHomeGoodList(LiveParamDTO liveParamDTO) {
        if (Objects.isNull(liveParamDTO.getPageSize()) || liveParamDTO.getPageSize() <= 0) {
            liveParamDTO.setPageSize(Integer.valueOf(1));
        }
        if (Objects.isNull(liveParamDTO.getPageNum()) || liveParamDTO.getPageNum() < 0) {
            liveParamDTO.setPageNum(Integer.valueOf(1));
        }
        Page<TbLiveGoodsDO> page = PageHelper.startPage(liveParamDTO.getPageNum(), liveParamDTO.getPageSize());
        tbLiveGoodsService.getGoodsByRoomId(liveParamDTO.getRoomId());
        ArrayList<LiveHomeGoodDTO> list = Lists.newArrayList();
        for (TbLiveGoodsDO tbLiveGoodsDO : page) {
            LiveHomeGoodDTO liveHomeGoodDTO = new LiveHomeGoodDTO();
            liveHomeGoodDTO.setSpu(tbLiveGoodsDO.getSpuCode());
            TbSpuManageParamDTO param = new TbSpuManageParamDTO();
            param.setUserMemberRole(UserMemberRoleEnum.GUEST.getRole());
            param.setSpuCode(tbLiveGoodsDO.getSpuCode());
            //获得商品信息
            SpuManageShowDTO spuManageShowDTO = remoteGoodsService.selectOneSpuShowByParam(param);
            if (Objects.nonNull(spuManageShowDTO)) {
                liveHomeGoodDTO.setPrice(spuManageShowDTO.getPrice());
                liveHomeGoodDTO.setSpuImage(spuManageShowDTO.getSpuImage());
                liveHomeGoodDTO.setSpuTitle(spuManageShowDTO.getSpuTitle());
                liveHomeGoodDTO.setSpuId(spuManageShowDTO.getSpuId());
                liveHomeGoodDTO.setShowState(tbLiveGoodsDO.getShowState());
                liveHomeGoodDTO.setSpuCode(spuManageShowDTO.getSpucode());
            }
            list.add(liveHomeGoodDTO);
        }
        PageInfo<LiveHomeGoodDTO> pageInfo = new PageInfo<>(list);
        pageInfo.setHasNextPage(page.getPages() > page.getPageNum());
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }

    @Override
    public LiveShowRoomDTO getLiveShowRoom(Integer roomId, Integer userId) {
        return getLiveShowRoom(roomId, userId, false);
    }

    @Override
    public LiveShowRoomDTO getLiveShowRoomForScan(Integer roomId, Integer userId) {
        return getLiveShowRoom(roomId, userId, true);
    }

    /**
     * @param roomId
     * @param userId
     * @param Ifscan 是否是扫描过来的
     * @return
     */
    private LiveShowRoomDTO getLiveShowRoom(Integer roomId, Integer userId, Boolean Ifscan) {
        LiveShowRoomDTO liveShowRoomDTO = new LiveShowRoomDTO();
        liveShowRoomDTO.setRoomId(roomId);
        TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getByRoomId(roomId);
        if (Objects.isNull(tbLiveRoomDO)) {
            Log.info("-----------直播间找不到数据--------" + roomId);
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.ROOM_ENTITY_NOT_NULL);
        }
        liveShowRoomDTO.setLiveUserId(tbLiveRoomDO.getLiveUserId());
        liveShowRoomDTO.setVisitorNum(VisitorUtils.getVisitors(tbLiveRoomDO.getVisitorNum(), visitor_rule));
        liveShowRoomDTO.setIfRegisterIm(true);
        //获得今天进入直播间观众总数
        int visitors = 0;
        String nowDateStr = LocalDate.now().toString();
        String totalStr = stringRedisTemplate.opsForValue().get("QQSK:TOTALTODAYVISITOR" + nowDateStr);
        if (StringUtils.isEmpty(totalStr)) {
            int total = tbLiveVisitorService.getTodayVisitors() + tbLiveImTokenService.getTodayTokenCount();
            stringRedisTemplate.opsForValue()
                    .set("QQSK:TOTALTODAYVISITOR" + nowDateStr, String.valueOf(total), update_total_today_visitor_time,
                            TimeUnit.SECONDS);
            visitors = total;
        } else {
            visitors = Integer.parseInt(totalStr);
        }
        if (visitors >= im_max_live_join_room.intValue()) {
            liveShowRoomDTO.setIfRegisterIm(false);
        }
        //缓存橱窗总数
        String goodsStr = stringRedisTemplate.opsForValue().get(RedisKeyConstant.LIVE_ROOM_GOODS_COUNT_PREFIX + roomId);
        if (!StringUtils.isEmpty(goodsStr)) {
            liveShowRoomDTO.setGoodsCount(Integer.parseInt(goodsStr));
        } else {
            int goods = tbLiveGoodsService.getGoodsByRoomId(roomId).size();
            liveShowRoomDTO.setGoodsCount(goods);
            stringRedisTemplate.opsForValue()
                    .set(RedisKeyConstant.LIVE_ROOM_GOODS_COUNT_PREFIX + roomId, goods + "", 60 * 60 * 2, TimeUnit.SECONDS);
        }
        liveShowRoomDTO.setImId(tbLiveRoomDO.getImId());

        //缓存主播信息
        TbUserDO tbUserDO = null;
        String liveStr = stringRedisTemplate.opsForValue().get("QQSK:LIVEINFOR" + tbLiveRoomDO.getLiveUserId());
        if (StringUtils.isEmpty(liveStr)) {
            tbUserDO = tbLiveVisitorService.getTbUserByUserId(tbLiveRoomDO.getLiveUserId());
            if (Objects.isNull(tbUserDO)) {
                throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.LIVE_USER_ENTITY_NOT_NULL);
            }
            stringRedisTemplate.opsForValue()
                    .set("QQSK:LIVEINFOR" + tbLiveRoomDO.getLiveUserId(), JSONObject.toJSONString(tbUserDO),
                            (max_live_time_hour + 1) * 60 * 60, TimeUnit.SECONDS);
        } else {
            tbUserDO = JSONObject.parseObject(liveStr, TbUserDO.class);
        }
        //主播信息
        liveShowRoomDTO.setNickname(tbUserDO.getNickname());
        liveShowRoomDTO.setHeadimgurl(getHttpsUrl(tbUserDO.getHeadimgurl()));
        if (StringUtils.isEmpty(tbUserDO.getShopName())) {
            liveShowRoomDTO.setShopName(tbUserDO.getNickname());
        } else {
            liveShowRoomDTO.setShopName(tbUserDO.getShopName());
        }
        //是否关注 无用 默认false
        liveShowRoomDTO.setIfConcern(false);
        //缓存前面3个观众头像
        List<String> stringList = Lists.newArrayList();
        String liveVisitorListStr = stringRedisTemplate.opsForValue().get("QQSK:THREELIVEVISITOR" + roomId);
        if (!StringUtils.isEmpty(liveVisitorListStr)) {
            stringList = JSONObject.parseArray(liveVisitorListStr, String.class);
        } else {
            List<TbLiveVisitorDO> liveVisitorDOList = tbLiveVisitorService.getVisitorByRoomId(roomId);
            if (liveVisitorDOList.size() >= 3) {
                liveVisitorDOList = liveVisitorDOList.subList(0, 3);
            }
            //展示最新的3个观众头像
            for (TbLiveVisitorDO tbLiveVisitorDO : liveVisitorDOList) {
                TbUserDO tbUser = tbLiveVisitorService.getTbUserByUserId(tbLiveVisitorDO.getUserId());
                //观众头像
                if (Objects.nonNull(tbUser)) {
                    stringList.add(tbUser.getHeadimgurl());
                }
            }
            stringRedisTemplate.opsForValue()
                    .set("QQSK:THREELIVEVISITOR" + roomId, JSONObject.toJSONString(stringList), 60 * 60, TimeUnit.SECONDS);
        }
        liveShowRoomDTO.setVisitors(stringList);
        if (Objects.nonNull(tbLiveRoomDO.getLiveStart())) {
            //sign 防盗时间戳暂时没用
            long signTime = tbLiveRoomDO.getLiveStart().getTime() + (max_live_time_hour + 1) * 60 * 60 * 1000;
            signTime = signTime / 1000;
            String s = Base16.intToHex((int) signTime);
            String key = YunConfig.qiniu_signkey;
            String path = "/" + YunConfig.qiniu_hubName + "/" + tbLiveRoomDO.getStreamKey();
            String sign = Base16.getQiNiuSign(key + path + s);
            liveShowRoomDTO.setPlaySign(sign);

            long time = tbLiveRoomDO.getLiveStart().getTime() + max_live_time_hour * 60 * 60 * 1000;
            int liveCountDown = (int) ((time - new Date().getTime()) / 1000);
            liveShowRoomDTO.setLiveCountDown(liveCountDown);
        }
        if (tbLiveRoomDO.getState().equals(LiveStatusEnum.LIVE_OVER.getStatus())) {
            liveShowRoomDTO.setIfOver(true);
            if (Objects.nonNull(tbLiveRoomDO.getLiveEnd())) {
                int liveTime = (int) (tbLiveRoomDO.getLiveEnd().getTime() - tbLiveRoomDO.getLiveStart().getTime());
                String timeStr = ConvertorTimeUtils.msecToTime(liveTime).substring(0, 8);//直播时间
                liveShowRoomDTO.setLiveTime(timeStr);
            }
        } else {
            liveShowRoomDTO.setIfOver(false);
        }
        liveShowRoomDTO.setAppointStart(tbLiveRoomDO.getAppointStart());
        liveShowRoomDTO.setLiveState(tbLiveRoomDO.getState());
        liveShowRoomDTO.setCover(tbLiveRoomDO.getCover());
        liveShowRoomDTO.setTitle(tbLiveRoomDO.getTitle());
        liveShowRoomDTO.setStreamUrl(tbLiveRoomDO.getStreamUrl());//推流地址
        liveShowRoomDTO.setStreamBackUrl(qiNiuService.getBackUrl(tbLiveRoomDO.getStreamFname()));//回放地址
        liveShowRoomDTO.setRTMPPlayURL(qiNiuService.getRTMPPlayURL(tbLiveRoomDO.getStreamKey()));//播放地址
        if (Ifscan) {
            //是否关注
            liveShowRoomDTO
                    .setIfConcern(remoteUserFollowService.ifFollowOfFollow(userId, tbLiveRoomDO.getLiveUserId()));
        }
        if (tbLiveRoomDO.getState().equals(LiveStatusEnum.LIVE_STARTING.getStatus())) {
            //是否正常推流
            Stream.LiveStatus liveStatus = qiNiuService.getiveStatus(tbLiveRoomDO.getStreamKey());
            if (Objects.isNull(liveStatus)) { // 没有推流
                liveShowRoomDTO.setIfNormalPush(false);
            } else {
                liveShowRoomDTO.setIfNormalPush(true);
            }
        }
        liveShowRoomDTO.setShowGoodsList(getShowGoods(roomId));//展示的上屏商品信息
        return liveShowRoomDTO;
    }

    @Override
    public LiveShowRoomDTO getTimeLiveShowRoom(Integer roomId, Integer userId) {
        LiveShowRoomDTO liveShowRoomDTO = new LiveShowRoomDTO();
        liveShowRoomDTO.setRoomId(roomId);
        TbLiveRoomDO tbLiveRoomDO = null;
        String str = stringRedisTemplate.opsForValue().get("QQSK:GETTIMELIVESHOWROOM" + roomId);
        if (StringUtils.isEmpty(str)) {
            tbLiveRoomDO = tbLiveRoomService.getByRoomId(roomId);
            if (Objects.isNull(tbLiveRoomDO)) {
                throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.ROOM_ENTITY_NOT_NULL);
            }
            stringRedisTemplate.opsForValue()
                    .set("QQSK:GETTIMELIVESHOWROOM" + roomId, JSONObject.toJSONString(tbLiveRoomDO),
                            (max_live_time_hour + 1) * 60 * 60, TimeUnit.SECONDS);
        } else {
            tbLiveRoomDO = (TbLiveRoomDO) JSONObject.parse(str);
        }
        liveShowRoomDTO.setLiveUserId(tbLiveRoomDO.getLiveUserId());
        liveShowRoomDTO.setVisitorNum(VisitorUtils.getVisitors(tbLiveRoomDO.getVisitorNum(), visitor_rule));
        liveShowRoomDTO.setIfRegisterIm(true);

        //缓存橱窗总数
        String goodsStr = stringRedisTemplate.opsForValue().get(RedisKeyConstant.LIVE_ROOM_GOODS_COUNT_PREFIX + roomId);
        if (!StringUtils.isEmpty(goodsStr)) {
            liveShowRoomDTO.setGoodsCount(Integer.parseInt(goodsStr));
        } else {
            int goods = tbLiveGoodsService.getGoodsByRoomId(roomId).size();
            liveShowRoomDTO.setGoodsCount(goods);
            stringRedisTemplate.opsForValue()
                    .set(RedisKeyConstant.LIVE_ROOM_GOODS_COUNT_PREFIX + roomId, goods + "", 60 * 60 * 2, TimeUnit.SECONDS);
        }
        //缓存前面3个观众头像
        List<String> stringList = Lists.newArrayList();
        String liveVisitorListStr = stringRedisTemplate.opsForValue().get("QQSK:THREELIVEVISITOR" + roomId);
        if (!StringUtils.isEmpty(liveVisitorListStr)) {
            stringList = JSONObject.parseArray(liveVisitorListStr, String.class);
        } else {
            List<TbLiveVisitorDO> liveVisitorDOList = tbLiveVisitorService.getVisitorByRoomId(roomId);
            if (liveVisitorDOList.size() >= 3) {
                liveVisitorDOList = liveVisitorDOList.subList(0, 3);
            }
            //展示最新的3个观众头像
            for (TbLiveVisitorDO tbLiveVisitorDO : liveVisitorDOList) {
                TbUserDO tbUser = tbLiveVisitorService.getTbUserByUserId(tbLiveVisitorDO.getUserId());
                //观众头像
                if (Objects.nonNull(tbUser)) {
                    stringList.add(tbUser.getHeadimgurl());
                }
            }
            stringRedisTemplate.opsForValue()
                    .set("QQSK:THREELIVEVISITOR" + roomId, JSONObject.toJSONString(stringList), 60 * 60, TimeUnit.SECONDS);
        }
        liveShowRoomDTO.setVisitors(stringList);

        if (Objects.nonNull(tbLiveRoomDO.getLiveStart())) {
            long time = tbLiveRoomDO.getLiveStart().getTime() + max_live_time_hour * 60 * 60 * 1000;
            int liveCountDown = (int) ((time - new Date().getTime()) / 1000);
            liveShowRoomDTO.setLiveCountDown(liveCountDown);
        }
        if (tbLiveRoomDO.getState().equals(LiveStatusEnum.LIVE_OVER.getStatus())) {
            liveShowRoomDTO.setIfOver(true);
            if (Objects.nonNull(tbLiveRoomDO.getLiveEnd())) {
                int liveTime = (int) (tbLiveRoomDO.getLiveEnd().getTime() - tbLiveRoomDO.getLiveStart().getTime());
                String timeStr = ConvertorTimeUtils.msecToTime(liveTime).substring(0, 8);//直播时间
                liveShowRoomDTO.setLiveTime(timeStr);
            }
        } else {
            liveShowRoomDTO.setIfOver(false);
        }
        liveShowRoomDTO.setLiveState(tbLiveRoomDO.getState());
        if (tbLiveRoomDO.getState().equals(LiveStatusEnum.LIVE_STARTING.getStatus())) {
            //是否正常推流
            Stream.LiveStatus liveStatus = qiNiuService.getiveStatus(tbLiveRoomDO.getStreamKey());
            if (Objects.isNull(liveStatus)) { // 没有推流
                liveShowRoomDTO.setIfNormalPush(false);
            } else {
                liveShowRoomDTO.setIfNormalPush(true);
            }
        }
        return liveShowRoomDTO;
    }

    @Override
    public PageInfo<LiverVisitorDTO> getVisitorList(LiveParamDTO liveParamDTO) {
        if (Objects.isNull(liveParamDTO.getPageSize()) || liveParamDTO.getPageSize() <= 0) {
            liveParamDTO.setPageSize(Integer.valueOf(1));
        }
        if (Objects.isNull(liveParamDTO.getPageNum()) || liveParamDTO.getPageNum() < 0) {
            liveParamDTO.setPageNum(Integer.valueOf(1));
        }
        Page<TbLiveVisitorDO> page = PageHelper.startPage(liveParamDTO.getPageNum(), liveParamDTO.getPageSize());
        tbLiveVisitorService.getVisitorByRoomId(liveParamDTO.getRoomId());
        ArrayList<LiverVisitorDTO> list = Lists.newArrayList();
        for (TbLiveVisitorDO tbLiveVisitorDO : page) {
            LiverVisitorDTO liverVisitorDTO = new LiverVisitorDTO();
            TbUserDO tbUserDO = tbLiveVisitorService.getTbUserByUserId(tbLiveVisitorDO.getUserId());
            //观众信息
            if (Objects.nonNull(tbUserDO)) {
                liverVisitorDTO.setNickname(tbUserDO.getNickname());
                liverVisitorDTO.setHeadimgurl(getHttpsUrl(tbUserDO.getHeadimgurl()));
                liverVisitorDTO.setUserId(tbUserDO.getUserId());
                list.add(liverVisitorDTO);
            }
        }
        PageInfo<LiverVisitorDTO> pageInfo = new PageInfo<>(list);
        pageInfo.setHasNextPage(page.getPages() > page.getPageNum());
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }

    /**
     * http替换为https 非http请求不替换
     *
     * @param httpUrl
     * @return httpsUrl
     */
    private String getHttpsUrl(String httpUrl) {
        String httpsUrl = httpUrl;
        if (org.apache.commons.lang3.StringUtils.isBlank(httpUrl)) {
            return httpsUrl;
        }
        if (httpUrl.startsWith("https")) {
            return httpsUrl;
        }
        if (httpUrl.startsWith("http")) {
            httpsUrl = httpUrl.replace("http", "https");
        }
        return httpsUrl;
    }

    @Override
    public LiverUserDTO getLiverUser(Integer userId, Integer toUserId) {
        LiverUserDTO liverVisitorDTO = new LiverUserDTO();
        TbUserDO tbUserDO = tbLiveVisitorService.getTbUserByUserId(toUserId);
        //被查看人信息
        if (Objects.nonNull(tbUserDO)) {
            liverVisitorDTO.setNickname(tbUserDO.getNickname());
            liverVisitorDTO.setHeadimgurl(tbUserDO.getHeadimgurl());
            liverVisitorDTO.setToUserId(toUserId);
            liverVisitorDTO.setUserId(userId);
            //粉丝总数
            int fans = remoteUserFollowService.selectFansIdOfUser(toUserId).size();
            //点赞数
            int praises = tbLiveRoomService.getPraisesByLiveUserId(toUserId);
            liverVisitorDTO.setPraises(praises);
            liverVisitorDTO.setFans(fans);
            //是否关注
            liverVisitorDTO.setIfConcern(remoteUserFollowService.ifFollowOfFollow(userId, toUserId));
        }
        return liverVisitorDTO;
    }

    @Override
    public String getImTokenByUserId(Integer userId) {
        String token = null;
        TbLiveImTokenDO tbLiveImTokenDO = tbLiveImTokenService.getImTokenByUserId(userId);
        if (Objects.nonNull(tbLiveImTokenDO)) {
            token = tbLiveImTokenDO.getImUserToken();
        } else {
            TbUserDO tbUser = tbLiveVisitorService.getTbUserByUserId(userId);
            if (Objects.nonNull(tbUser)) {
                TokenResult tokenResult = rongYunService
                        .Register(tbUser.getUserId().toString(), tbUser.getNickname(), tbUser.getHeadimgurl());
                if (Objects.nonNull(tokenResult)) {
                    if (tokenResult.getCode().equals(200)) {
                        token = tokenResult.getToken();
                        //Token保存本地
                        TbLiveImTokenDO tbLiveImToken = new TbLiveImTokenDO();
                        tbLiveImToken.setUserId(userId);
                        tbLiveImToken.setImUserToken(token);
                        tbLiveImToken.setGmtCreate(new Date());
                        tbLiveImTokenService.save(tbLiveImToken);
                    }
                }
            }
        }
        return token;
    }

    @Override
    public void addUserFollow(Integer userId, Integer followUserId) {
        if (userId.equals(followUserId)) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.NOT_FOLLOW_SELF);
        }
        remoteUserFollowService.addUserFollow(userId, followUserId);
    }

    @Override
    public void deleteUserFollow(Integer userId, Integer followUserId) {
        remoteUserFollowService.deleteUserFollow(userId, followUserId);
    }

    @Override
    public String closeLiveRoom(Integer roomId) {
        TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getByRoomId(roomId);
        if (Objects.isNull(tbLiveRoomDO)) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.ROOM_ENTITY_NOT_NULL);
        }
        String timeStr = null;
        if (Objects.nonNull(tbLiveRoomDO)) {
            Date now = new Date();
            tbLiveRoomDO.setState(LiveStatusEnum.LIVE_OVER.getStatus());//直播结束
            tbLiveRoomDO.setLiveEnd(now);
            //获得回放名称
            if (StringUtils.isEmpty(tbLiveRoomDO.getStreamFname())) {
                String fname = qiNiuService.getPlayBackFname(tbLiveRoomDO.getStreamKey());
                if (!StringUtils.isEmpty(fname)) {
                    String str = VisitorUtils.getBackTime(qiNiuService.getBackUrl(fname));
                    if (!StringUtils.isEmpty(str)) {
                        tbLiveRoomDO.setBackTime(str);
                    }
                }
                tbLiveRoomDO.setStreamFname(fname);
            }
            int time = (int) (now.getTime() - tbLiveRoomDO.getLiveStart().getTime());
            timeStr = ConvertorTimeUtils.msecToTime(time).substring(0, 8);//直播时间
            tbLiveRoomService.updateByPrimaryKeySelective(tbLiveRoomDO);
            stringRedisTemplate.opsForValue()
                    .set("QQSK:GETTIMELIVESHOWROOM" + roomId, JSONObject.toJSONString(tbLiveRoomDO),
                            (max_live_time_hour + 1) * 60 * 60, TimeUnit.SECONDS);
            closeYunServices(tbLiveRoomDO.getStreamKey(), tbLiveRoomDO.getImId());//位置可能是先关闭流再生产回放？
            enterRoomDelayService.quit(roomId);//关闭刷进入消息
        }
        return timeStr;
    }

    @Override
    public void timingCloseLiveRoom() {
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("state", LiveStatusEnum.LIVE_STARTING.getStatus());//直播中的
        List<TbLiveRoomDO> list = tbLiveRoomService.listByCondition(condition);
        for (TbLiveRoomDO tbLiveRoomDO : list) {
            long time = tbLiveRoomDO.getLiveStart().getTime() + max_live_time_hour * 60 * 60 * 1000;
            long nowTime = System.currentTimeMillis();
            if (nowTime > time) {  //超时了
                Date now = new Date();
                tbLiveRoomDO.setState(LiveStatusEnum.LIVE_OVER.getStatus());//直播结束
                tbLiveRoomDO.setLiveEnd(now);
                //获得回放名称
                if (StringUtils.isEmpty(tbLiveRoomDO.getStreamFname())) {
                    String fname = qiNiuService.getPlayBackFname(tbLiveRoomDO.getStreamKey());
                    if (!StringUtils.isEmpty(fname)) {
                        String str = VisitorUtils.getBackTime(qiNiuService.getBackUrl(fname));
                        if (!StringUtils.isEmpty(str)) {
                            tbLiveRoomDO.setBackTime(str);
                        }
                    }
                    tbLiveRoomDO.setStreamFname(fname);
                }
                tbLiveRoomService.updateByPrimaryKeySelective(tbLiveRoomDO);
                stringRedisTemplate.opsForValue()
                        .set("QQSK:GETTIMELIVESHOWROOM" + tbLiveRoomDO.getId(), JSONObject.toJSONString(tbLiveRoomDO),
                                (max_live_time_hour + 1) * 60 * 60, TimeUnit.SECONDS);
                closeYunServices(tbLiveRoomDO.getStreamKey(), tbLiveRoomDO.getImId());
                enterRoomDelayService.quit(tbLiveRoomDO.getId());//关闭刷进入消息
            }
        }
    }

    @Override
    public void timingScanCloseLiveRoom() {
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("state", LiveStatusEnum.LIVE_STARTING.getStatus());//直播中的
        List<TbLiveRoomDO> list = tbLiveRoomService.listByCondition(condition);
        for (TbLiveRoomDO tbLiveRoomDO : list) {
            Stream.LiveStatus liveStatus = qiNiuService.getiveStatus(tbLiveRoomDO.getStreamKey());
            String status = "1";//1 正常 0 没有推流
            if (Objects.isNull(liveStatus)) {
                status = "0";
            }
            String value = stringRedisTemplate.opsForValue().get("QQSK:LIVEROOMSTATUS:ROOMID" + tbLiveRoomDO.getId());
            if (StringUtils.isEmpty(value)) {
                stringRedisTemplate.opsForValue().set("QQSK:LIVEROOMSTATUS:ROOMID" + tbLiveRoomDO.getId(), status,
                        (max_live_time_hour + 1) * 60 * 60, TimeUnit.SECONDS);
            } else {
                if (status.equals("1")) {
                    stringRedisTemplate.opsForValue().set("QQSK:LIVEROOMSTATUS:ROOMID" + tbLiveRoomDO.getId(), status,
                            (max_live_time_hour + 1) * 60 * 60, TimeUnit.SECONDS);
                }
                if (status.equals("0")) {
                    if (value.equals("1")) {
                        stringRedisTemplate.opsForValue()
                                .set("QQSK:LIVEROOMSTATUS:ROOMID" + tbLiveRoomDO.getId(), status,
                                        (max_live_time_hour + 1) * 60 * 60, TimeUnit.SECONDS);
                    } else { //目前没有推流 和之前一次也没有推流 可能断开网络 关闭直播间
                        Date now = new Date();
                        tbLiveRoomDO.setState(LiveStatusEnum.LIVE_OVER.getStatus());//直播结束
                        tbLiveRoomDO.setLiveEnd(now);
                        //获得回放名称
                        if (StringUtils.isEmpty(tbLiveRoomDO.getStreamFname())) {
                            String fname = qiNiuService.getPlayBackFname(tbLiveRoomDO.getStreamKey());
                            if (!StringUtils.isEmpty(fname)) {
                                String str = VisitorUtils.getBackTime(qiNiuService.getBackUrl(fname));
                                if (!StringUtils.isEmpty(str)) {
                                    tbLiveRoomDO.setBackTime(str);
                                }
                            }
                            tbLiveRoomDO.setStreamFname(fname);
                        }
                        tbLiveRoomService.updateByPrimaryKeySelective(tbLiveRoomDO);
                        stringRedisTemplate.opsForValue().set("QQSK:GETTIMELIVESHOWROOM" + tbLiveRoomDO.getId(),
                                JSONObject.toJSONString(tbLiveRoomDO), (max_live_time_hour + 1) * 60 * 60, TimeUnit.SECONDS);
                        closeYunServices(tbLiveRoomDO.getStreamKey(), tbLiveRoomDO.getImId());
                        enterRoomDelayService.quit(tbLiveRoomDO.getId());//关闭刷进入消息
                    }
                }
            }
        }
    }

    @Override
    public void timingUpdatePraise() {
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("state", LiveStatusEnum.LIVE_STARTING.getStatus());//直播中的
        List<TbLiveRoomDO> list = tbLiveRoomService.listByCondition(condition);
        for (TbLiveRoomDO tbLiveRoomDO : list) {
            String value = stringRedisTemplate.opsForValue().get("QQSK:LIVEROOMPRAISE:ROOMID" + tbLiveRoomDO.getId());
            if (Strings.isNotBlank(value)) {
                int count = tbLiveRoomDO.getPraiseCount().intValue();
                int redisCount = Integer.parseInt(value);
                if (redisCount > count) { //redis 数量大于 更新
                    tbLiveRoomDO.setPraiseCount(redisCount);
                    tbLiveRoomService.updateById(tbLiveRoomDO);
                }
            }
        }
    }

    @Override
    public void timingGetBackFnameForFLV() {
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("state", LiveStatusEnum.LIVE_OVER.getStatus());//直播结束的
        criteria.andEqualTo("streamFlvFname", "");//没有flv
        criteria.andEqualTo("isDeleted", 0);//没有删除
        List<TbLiveRoomDO> list = tbLiveRoomService.listByCondition(condition);
        for (TbLiveRoomDO item : list) {
            if (Objects.nonNull(item.getLiveStart()) && Objects.nonNull(item.getLiveEnd())) {
                    String fname = qiNiuService.getPlayBackFname(item.getStreamKey(), item.getLiveStart().getTime() / 1000,
                            item.getLiveEnd().getTime() / 1000, "flv");
                    item.setStreamFlvFname(fname);
                    tbLiveRoomService.updateById(item);//更新
            }
        }
    }

    @Override
    public void timingGetBackFname() {
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("state", LiveStatusEnum.LIVE_OVER.getStatus());//直播结束的
        criteria.andEqualTo("streamFname", "");//没有回放
        criteria.andEqualTo("isDeleted", 0);//没有删除
        List<TbLiveRoomDO> list = tbLiveRoomService.listByCondition(condition);
        for (TbLiveRoomDO item : list) {
            if (Objects.nonNull(item.getLiveStart())) {
                    String fname = qiNiuService.getPlayBackFname(item.getStreamKey());
                    if (fname != null){
                        item.setStreamFname(fname);
                        String str = VisitorUtils.getBackTime(qiNiuService.getBackUrl(fname));
                        if (!StringUtils.isEmpty(str)) {
                            item.setBackTime(str);
                        }
                        tbLiveRoomService.updateById(item);//更新
                    }
            }
        }
    }

    @Override
    public void closeYunServices(String streamKey, String imId) {
        tbLiveRoomService.closeYunServices(streamKey, imId);
    }

    @Override
    public void addPraise(Integer roomId, Integer counts) {
        String value = stringRedisTemplate.opsForValue().get("QQSK:LIVEROOMPRAISE:ROOMID" + roomId);
        updateLiveVisitorsForKind(roomId, AddVisitorsKindEnum.ADDPRAISE_FOLLOW.getKind());//点赞增加人气值
        if (Strings.isBlank(value)) {//第一次或者redis挂了
            TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getById(roomId);
            if (Objects.nonNull(tbLiveRoomDO)) {
                int count = 0;
                if (Objects.nonNull(tbLiveRoomDO.getPraiseCount())) {
                    count = tbLiveRoomDO.getPraiseCount().intValue();
                }
                String countStr = String.valueOf(count);
                stringRedisTemplate.opsForValue().set("QQSK:LIVEROOMPRAISE:ROOMID" + roomId, countStr);
            } else {
                stringRedisTemplate.opsForValue().increment("QQSK:LIVEROOMPRAISE:ROOMID" + roomId, counts);
                return;
            }
        }
        stringRedisTemplate.opsForValue().increment("QQSK:LIVEROOMPRAISE:ROOMID" + roomId, counts);
    }

    @Override
    public void addVisitor(Integer roomId, Integer userId) {
        BusinessEventMessage businessEventMessage = new BusinessEventMessage();
        businessEventMessage.setRoomId(roomId);
        businessEventMessage.setUserId(userId);
        businessEventQueue.offer(businessEventMessage);
    }

    @Override
    public MyLivePageDTO getMyLiveList(Integer userId, Integer pageNum, Integer pageSize) {
        MyLivePageDTO myLivePageDTO = new MyLivePageDTO();
        int count = tbLiveRoomService.getLiveRoomNotOverCount(userId);
        if (count > 0) {
            myLivePageDTO.setLiveRoomContinue(false);
        } else {
            myLivePageDTO.setLiveRoomContinue(true);
        }
        Page<TbLiveRoomDO> page = PageHelper.startPage(pageNum, pageSize);
        List<TbLiveRoomDO> tbLiveRoomDOList = tbLiveRoomService.getLiveRoomByUserId(userId);
        if (CollectionUtils.isEmpty(tbLiveRoomDOList)) {
            PageInfo<MyLiveDTO> pageInfo = new PageInfo<>(Lists.newArrayList());
            myLivePageDTO.setPageInfo(pageInfo);
            return myLivePageDTO;
        }
        List<MyLiveDTO> liveDTOList = tbLiveRoomDOList.stream().map(m -> {
            Integer roomId = m.getId();
            //streamUrl已经复制进去
            MyLiveDTO myLiveDTO = LIVEROOMDO2MYLIVEDO_COPIER.copy(m);
            String streamKey = m.getStreamKey();
            if (!StringUtils.isEmpty(m.getStreamFname())) {
                if (!StringUtils.isEmpty(m.getBackTime())) {
                    myLiveDTO.setLiveTime(m.getBackTime());
                } else {
                    String timeStr = VisitorUtils.getBackTime(qiNiuService.getBackUrl(m.getStreamFname()));
                    if (!StringUtils.isEmpty(timeStr)) {
                        m.setBackTime(timeStr);
                        tbLiveRoomService.updateById(m);
                        myLiveDTO.setLiveTime(timeStr);
                    }
                }
            }
            myLiveDTO.setStreamBackUrl(qiNiuService.getBackUrl(m.getStreamFname()));//回放地址
            //播放地址
            myLiveDTO.setVisitorNum(VisitorUtils.getVisitors(m.getVisitorNum(), visitor_rule));
            myLiveDTO.setRTMPPlayURL(qiNiuService.getRTMPPlayURL(streamKey));
            myLiveDTO.setHDLPlayURL(qiNiuService.getHDLPlayURL(streamKey));
            myLiveDTO.setHLSPlayURL(qiNiuService.getHLSPlayURL(streamKey));
            int goodsCount = tbLiveGoodsService.selectCountByUserIdAndRoomId(userId, roomId);
            myLiveDTO.setGoodsCount(goodsCount);
            List<String> spuCodeList = tbLiveGoodsService.getGoodsByRoomIdAndLimit(roomId, LiVEROOM_SPU_SHOW_MAX_COUNT);
            //封装商品信息
            if (!CollectionUtils.isEmpty(spuCodeList)) {
                List<LiveHomeGoodDTO> liveHomeGoodDTOList = spuCodeList.stream().map(spuCode -> {
                    LiveHomeGoodDTO homeGoodDTO = new LiveHomeGoodDTO();
                    TbSpuManageParamDTO tbSpuManageParamDTO = new TbSpuManageParamDTO();
                    tbSpuManageParamDTO.setUserMemberRole(UserMemberRoleEnum.GUEST.getRole());
                    tbSpuManageParamDTO.setSpuCode(spuCode);
                    //获得商品信息
                    SpuManageShowDTO spuManageShowDTO = remoteGoodsService.selectOneSpuShowByParam(tbSpuManageParamDTO);
                    if (Objects.nonNull(spuManageShowDTO)) {
                        homeGoodDTO = SPU2GOODS_COPIER.copy(spuManageShowDTO);
                        homeGoodDTO.setSpu(spuCode);
                    }
                    return homeGoodDTO;
                }).collect(Collectors.toList());
                myLiveDTO.setGoods(liveHomeGoodDTOList);
            }
            return myLiveDTO;
        }).collect(Collectors.toList());
        PageInfo<MyLiveDTO> pageInfo = new PageInfo<>(liveDTOList);
        pageInfo.setHasNextPage(page.getPages() > page.getPageNum());
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPages(page.getPages());
        myLivePageDTO.setPageInfo(pageInfo);
        return myLivePageDTO;
    }

    @Override
    public Boolean addLiveBans(List<Integer> userIds, Integer minute) {
        ChatroomMember[] members = new ChatroomMember[userIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            ChatroomMember model = new ChatroomMember();
            model.setId(userIds.get(i).toString());
        }
        ResponseResult result = rongYunService.addBan(members, minute);
        if (Objects.nonNull(result) && result.getCode().equals(200)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean removeLiveBans(List<Integer> userIds) {
        ChatroomMember[] members = new ChatroomMember[userIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            ChatroomMember model = new ChatroomMember();
            model.setId(userIds.get(i).toString());
        }
        ResponseResult result = rongYunService.removeBan(members);
        if (Objects.nonNull(result) && result.getCode().equals(200)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> getBans() {
        ListGagChatroomUserResult result = rongYunService.getBanUsers();
        if (Objects.nonNull(result) && result.getCode().equals(200)) {
            if (Objects.nonNull(result.getMembers())) {
                return null;
            }
            List<String> list = Lists.newArrayList();
            for (ChatroomMember item : result.getMembers()) {
                list.add(item.getId());
            }
            return list;
        } else {
            return null;
        }
    }

    @Override
    public Boolean addKeepalive(String imId) {
        ResponseResult result = rongYunService.addKeepalive(imId);
        if (Objects.nonNull(result) && result.getCode().equals(200)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean removeKeepalive(String imId) {
        ResponseResult result = rongYunService.removeKeepalive(imId);
        if (Objects.nonNull(result) && result.getCode().equals(200)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> getKeepalives() {
        ChatroomKeepaliveResult result = rongYunService.getKeepalives();
        if (Objects.nonNull(result) && result.getCode().equals(200)) {
            if (Objects.nonNull(result.getChatrooms())) {
                return null;
            }
            List<String> list = Lists.newArrayList();
            for (String item : result.getChatrooms()) {
                list.add(item);
            }
            return list;
        } else {
            return null;
        }
    }

    @Override
    public String startLiveRoom(Integer roomId, Integer userId) {
        //查询未删除直播间
        TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.selectNotDeleteLiveRoomById(roomId);
        if (Objects.isNull(tbLiveRoomDO)) {
            log.error("【开播失败，直播间找不到数据】, 参数roomId = {}", roomId);
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.ROOM_ENTITY_NOT_NULL);
        }
        Date appointStart = tbLiveRoomDO.getAppointStart();
        //当前时间离预约时间秒数
        int second = (int) ((System.currentTimeMillis() - appointStart.getTime()) / 1000);
        //如果当前时间比预约时间大600秒以上
        if (second > DELAY_SECOND) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.LIVE_ROOM_IS_DELETED);
        }
        String imid = tbLiveRoomService.updateLiveStatusByIdAndUserId(roomId, userId);
        //公开的直播开播后给粉丝发推送
        if("1".equals(tbLiveRoomDO.getIsPublic().toString())){
            enterRoomDelayService.join(roomId);//直播开始 开启刷进入消息
            List<Integer> followList = remoteUserFollowService.selectFansIdOfUser(userId);
            List<Integer> appointmentList = tbLiveSubscribersService.getUserIdByRoomId(tbLiveRoomDO.getId());
            if ((followList != null && followList.size() > 0) || (appointmentList!=null && appointmentList.size()>0)) {
                UserDTO user = remoteUserService.getById(userId);
                LiveRoomFansPushDTO liveRoomFansPushDTO = new LiveRoomFansPushDTO();
                liveRoomFansPushDTO.setRoomId(tbLiveRoomDO.getId());
                liveRoomFansPushDTO.setRtmpplayURL(tbLiveRoomDO.getStreamUrl());
                liveRoomFansPushDTO.setLiveUserId(tbLiveRoomDO.getLiveUserId());
                liveRoomFansPushDTO.setImId(imid);
                liveRoomFansPushDTO.setCover(tbLiveRoomDO.getCover());
                liveRoomFansPushDTO.setTitle(tbLiveRoomDO.getTitle());
                if (tbLiveRoomDO.getAppointStart() != null) {
                    liveRoomFansPushDTO.setAppointStart(DateUtils.timeStampToString(tbLiveRoomDO.getAppointStart().getTime(), DateUtils.YYYYMMDDhhmmss));
                }

                liveRoomFansPushDTO.setHeadimgurl(user.getHeadimgurl());
                String shopName = user.getShopName();
                if (shopName == null || "".equals(shopName)) {
                    shopName = user.getNickname();
                }
                liveRoomFansPushDTO.setShopName(shopName);
                liveRoomFansPushDTO.setNickname(user.getNickname());
                Map<String,List<Integer>> map = new HashMap();
                map.put("followList",followList);
                map.put("appointmentList",appointmentList);
                remoteXingeAppService.startLiveRoomFansPush(map, liveRoomFansPushDTO);
            }
        }
        return imid;
    }

    @Override
    public Boolean addBlock(String imId, List<Integer> userIds, Integer minute) {
        ChatroomMember[] members = new ChatroomMember[userIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            ChatroomMember model = new ChatroomMember();
            model.setId(userIds.get(i).toString());
        }
        ResponseResult result = rongYunService.addBlock(imId, members, minute);
        if (Objects.nonNull(result) && result.getCode().equals(200)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean removeBlock(String imId, List<Integer> userIds) {
        ChatroomMember[] members = new ChatroomMember[userIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            ChatroomMember model = new ChatroomMember();
            model.setId(userIds.get(i).toString());
        }
        ResponseResult result = rongYunService.removeBlock(imId, members);
        if (Objects.nonNull(result) && result.getCode().equals(200)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> getBlockUsers(String imId) {
        ListBlockChatroomUserResult result = rongYunService.getBlockUsers(imId);
        if (Objects.nonNull(result) && result.getCode().equals(200)) {
            if (Objects.nonNull(result.getMembers())) {
                return null;
            }
            List<String> list = Lists.newArrayList();
            for (ChatroomMember item : result.getMembers()) {
                list.add(item.getId());
            }
            return list;
        } else {
            return null;
        }
    }

    @Override
    public Boolean addGag(String imId, List<Integer> userIds, Integer minute) {
        ChatroomMember[] members = new ChatroomMember[userIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            ChatroomMember model = new ChatroomMember();
            model.setId(userIds.get(i).toString());
        }
        ResponseResult result = rongYunService.addGag(imId, members, minute);
        if (Objects.nonNull(result) && result.getCode().equals(200)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean removeGag(String imId, List<Integer> userIds) {
        ChatroomMember[] members = new ChatroomMember[userIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            ChatroomMember model = new ChatroomMember();
            model.setId(userIds.get(i).toString());
        }
        ResponseResult result = rongYunService.removeGag(imId, members);
        if (Objects.nonNull(result) && result.getCode().equals(200)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> getGagUsers(String imId) {
        ListGagChatroomUserResult result = rongYunService.getGagUsers(imId);
        if (Objects.nonNull(result) && result.getCode().equals(200)) {
            if (Objects.nonNull(result.getMembers())) {
                return null;
            }
            List<String> list = Lists.newArrayList();
            for (ChatroomMember item : result.getMembers()) {
                list.add(item.getId());
            }
            return list;
        } else {
            return null;
        }
    }

    @Override
    public int deleteMyLiveRoom(Integer roomId, Integer userId) {
        //根据roomId和userId获取未删除的直播间
        TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getByIdAndUserId(roomId, userId);
        if (Objects.isNull(tbLiveRoomDO)) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.ROOM_ENTITY_NOT_NULL);
        }
        Integer state = tbLiveRoomDO.getState();
        //如果直播还在进行中，提示删除失败
        if (LiveStatusEnum.LIVE_STARTING.getStatus().equals(state)) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.LIVE_ROOM_NOT_END);
        }
        return tbLiveRoomService.deleteLiveRoomById(tbLiveRoomDO.getId());
    }

    @Override
    public List<Map<String, String>> getSpringRooms(Date begin, Date end, Integer level) {
        Condition condition = new Condition(TbLiveRoomDO.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("isPublic", LiveRoomPublicStatusEnum.IS_PUBLIC.getStatus());//公开的
        criteria.andEqualTo("isDeleted", DeleteFlagEnum.NOT_DELETED.getCode());//未删除的
        criteria.andBetween("gmtCreate", begin, end);
        List<TbLiveRoomDO> list = tbLiveRoomService.listByCondition(condition);
        List<Map<String, String>> result = new ArrayList<>();
        for (TbLiveRoomDO item : list) {
            Integer visitor = VisitorUtils.getVisitors(item.getVisitorNum(), visitor_rule);
            Integer count = 0;
            if (level == 1) {
                count = SEND_LIVE_GOLD_ONE;
            }
            if (level == 2) {
                count = SEND_LIVE_GOLD_TWO;
            }
            if (visitor >= count) {
                Map<String, String> map = new HashMap<>();
                map.put("userId", item.getLiveUserId().toString());
                map.put("roomId", item.getId().toString());
                map.put("title", item.getTitle());
                result.add(map);
            }
        }
        return result;
    }

    @Override
    public Boolean updateLiveVisitors(Integer roomId, Integer num) {
        if (ObjectUtils.isEmpty(num) || num <= 0) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.LIVE_ROOM_NOT_VISITORS);
        }
        TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getById(roomId);
        if (Objects.isNull(tbLiveRoomDO)) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.ROOM_ENTITY_NOT_NULL);
        }
        tbLiveRoomDO.setVisitorNum(VisitorUtils.getBackVisitors(num, visitor_rule));
        tbLiveRoomService.updateById(tbLiveRoomDO);
        return true;
    }

    @Override
    public PageInfo<LiveRoomManagerDTO> getRoomListForManager(LiveRoomManagerQueryDTO queryDTO) {
        log.info(JSONObject.toJSONString(queryDTO));
        if (Objects.isNull(queryDTO.getPageSize()) || queryDTO.getPageSize() <= 0) {
            queryDTO.setPageSize(Integer.valueOf(30));
        }
        if (Objects.isNull(queryDTO.getPageNum()) || queryDTO.getPageNum() < 0) {
            queryDTO.setPageNum(Integer.valueOf(1));
        }
        Page<TbLiveRoomDO> page = PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        TbLiveRoomDO tbLiveRoomQuery = LIVEROOMMANAGERQUERYDTO2ROOMDO_COPIER.copy(queryDTO);
        tbLiveRoomService.getRoomListForManager(tbLiveRoomQuery);
        ArrayList<LiveRoomManagerDTO> list = Lists.newArrayList();
        for (TbLiveRoomDO tbLiveRoomDO : page) {
            LiveRoomManagerDTO dto = LIVEROOMDO2MANAGERDTO_COPIER.copy(tbLiveRoomDO);
            dto.setVisitorNum(VisitorUtils.getVisitors(tbLiveRoomDO.getVisitorNum(), visitor_rule));
            list.add(dto);
        }
        PageInfo<LiveRoomManagerDTO> pageInfo = new PageInfo<>(list);
        pageInfo.setHasNextPage(page.getPages() > page.getPageNum());
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }

    @Override
    public PageInfo<LiveHomeListDTO> getHeadSearchList(LiveQueryParamDTO liveQueryParamDTO) {
        Integer userId = liveQueryParamDTO.getUserId();
        if (Objects.isNull(liveQueryParamDTO.getPageSize()) || liveQueryParamDTO.getPageSize() <= 0) {
            liveQueryParamDTO.setPageSize(Integer.valueOf(10));
        }
        if (Objects.isNull(liveQueryParamDTO.getPageNum()) || liveQueryParamDTO.getPageNum() < 0) {
            liveQueryParamDTO.setPageNum(Integer.valueOf(1));
        }
        Page<TbLiveRoomDO> page = PageHelper.startPage(liveQueryParamDTO.getPageNum(), liveQueryParamDTO.getPageSize());
        tbLiveRoomService.getHeadSearchList(liveQueryParamDTO.getSearch());
        List<LiveHomeListDTO> list = getLiveHomeListDTOByDO(page.getResult(), userId);
        PageInfo<LiveHomeListDTO> pageInfo = new PageInfo<>(list);
        pageInfo.setHasNextPage(page.getPages() > page.getPageNum());
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }

    @Override
    public PageInfo<LiveHomeListDTO> getRecommendList(LiveParamDTO liveParamDTO) {
        List<TbLiveRoomDO> result = Lists.newArrayList();
        Integer userId = liveParamDTO.getUserId();
        if (Objects.isNull(liveParamDTO.getPageSize()) || liveParamDTO.getPageSize() <= 0) {
            liveParamDTO.setPageSize(Integer.valueOf(10));
        }
        if (Objects.isNull(liveParamDTO.getPageNum()) || liveParamDTO.getPageNum() < 0) {
            liveParamDTO.setPageNum(Integer.valueOf(1));
        }
        PageInfo<UserFollowDTO> userFollowDTOPageInfo =
                remoteUserFollowService.listUserFollow(userId, 1, 100000);//写死
        List<UserFollowDTO> userFollowDTOList = userFollowDTOPageInfo.getList();
        if (!CollectionUtils.isEmpty(userFollowDTOList)) {
            //先拿到关注人userIdList
            List<Integer> followUserList =
                    userFollowDTOList.stream().map(UserFollowDTO::getFollowUserId).collect(Collectors.toList());
            List<TbLiveRoomDO> discoverList = tbLiveRoomService.getDiscoverListByUserList(followUserList);
            result.addAll(discoverList);
        }
        List<TbLiveRoomDO> list = tbLiveRoomService.getRecommendList();
        if (list.size() > 0) {
            result.addAll(list);
        }
        PageInfo<LiveHomeListDTO> pageInfo = new PageInfo<>();
        if (result.size() > 0) {
            result = result.stream().filter(distinctByKey(b -> b.getId())).collect(Collectors.toList());
            result.sort(Comparator.comparing(TbLiveRoomDO::getRank).reversed());
            result.sort(Comparator.comparing(TbLiveRoomDO::getState).reversed());
            int index = (liveParamDTO.getPageNum() - 1) * liveParamDTO.getPageSize();
            int pages = result.size() / liveParamDTO.getPageSize() + 1;
            pageInfo.setHasNextPage(pages > liveParamDTO.getPageNum());
            List<TbLiveRoomDO> pageList;
            if (pageInfo.isHasNextPage()) {
                pageList = result.subList(index, index + liveParamDTO.getPageSize());
            } else {
                pageList = result.subList(index, result.size());
            }
            List<LiveHomeListDTO> resultList = getLiveHomeListDTOByDO(pageList, userId);
            pageInfo.setList(resultList);
            pageInfo.setTotal(result.size());
            pageInfo.setPages(pages);
        }
        return pageInfo;
    }


    @Override
    public List<Map<String, String>> getLiveCategory() {
        List<Map<String, String>> list = new ArrayList<>();
        List<TbLiveCategoryDO> allEnableMark = tbLiveCategoryService.getAllEnableMark();
        if (allEnableMark != null && allEnableMark.size() > 0) {
            for (int i = 0; i < allEnableMark.size(); i++) {
                Map<String, String> map = new HashMap<>();
                map.put("name", allEnableMark.get(i).getMark());
                map.put("key", allEnableMark.get(i).getName());
                list.add(map);
            }
        }
        return list;
    }

    @Override
    public List<LiveCategoryDTO> getAllLiveCategoryDTO() {
        List<TbLiveCategoryDO> allMark = tbLiveCategoryService.getAllMark();
        List<LiveCategoryDTO> list = new ArrayList<>();
        for (int i = 0; i < allMark.size(); i++) {
            list.add(LIVE_CATEGORY_DO2DTO.copy(allMark.get(i)));
        }
        return list;
    }

    @Override
    public List<LiveCategoryDTO> getLiveCategoryDTOEnable() {
        List<TbLiveCategoryDO> allEnableMark = tbLiveCategoryService.getAllEnableMark();
        List<LiveCategoryDTO> list = new ArrayList<>();
        for (int i = 0; i < allEnableMark.size(); i++) {
            list.add(LIVE_CATEGORY_DO2DTO.copy(allEnableMark.get(i)));
        }
        return list;
    }

    @Override
    public String addLiveCategory(LiveCategoryDTO liveCategoryDTO) {
        if (liveCategoryDTO == null) {
            return "";
        }
        List<TbLiveCategoryDO> allMark = tbLiveCategoryService.getAllMark();
        if (allMark != null && allMark.size() > 0) {
            List<String> markList = new ArrayList<>();
            for (int i = 0; i < allMark.size(); i++) {
                markList.add(allMark.get(i).getMark());
            }
            for (int i = 0; i < markList.size(); i++) {
                if (markList.get(i).equals(liveCategoryDTO.getMark())) {
                    return "已存在相同的分类名称，请更换！";
                }
            }
        }
        TbLiveCategoryDO tbLiveCategoryDO = new TbLiveCategoryDO();
        tbLiveCategoryDO.setName(UUID.randomUUID().toString());
        tbLiveCategoryDO.setMark(liveCategoryDTO.getMark());
        tbLiveCategoryDO.setEnable(liveCategoryDTO.getEnable());
        tbLiveCategoryDO.setWeight(liveCategoryDTO.getWeight());
        tbLiveCategoryDO.setCreateTime(new Date());
        tbLiveCategoryDO.setUpdateTime(new Date());
        tbLiveCategoryService.save(tbLiveCategoryDO);
        return "成功";
    }


    @Override
    public String deleteLiveCategory(Integer id) {
        TbLiveCategoryDO tbLiveCategoryDO = tbLiveCategoryService.getById(id);
        if (id == null || tbLiveCategoryDO == null) {
            return "该分类不存在，无法删除";
        }
        //查询改标签有无创建的直播间
        Integer liveRoomByCategryId = tbLiveRoomService.getLiveRoomByCategryId(id);
        if (liveRoomByCategryId > 0) {
            return "该分类下有直播间，不能删除！";
        }
        tbLiveCategoryService.removeById(id);
        return "成功";
    }

    @Override
    public String updateLiveCategory(LiveCategoryDTO liveCategoryDTO) {
        List<TbLiveCategoryDO> liveCategorys = tbLiveCategoryService.getLiveCategory(liveCategoryDTO.getId());
        if (liveCategorys != null && liveCategorys.size() > 0) {
            List<String> markList = new ArrayList<>();
            for (int i = 0; i < liveCategorys.size(); i++) {
                markList.add(liveCategorys.get(i).getMark());
            }
            for (int i = 0; i < markList.size(); i++) {
                if (markList.get(i).equals(liveCategoryDTO.getMark())) {
                    return "已存在相同的分类名称，请更换！";
                }
            }

            TbLiveCategoryDO tbLiveCategoryDO = LIVE_CATEGORY_DTO2DO.copy(liveCategoryDTO);
            tbLiveCategoryDO.setUpdateTime(new Date());
            tbLiveCategoryService.updateById(tbLiveCategoryDO);
        }
        return "成功";
    }

    @Override
    public void updateLiveRoomByManager(UpdateLiveRoomDTO updateLiveRoomDTO) {
        TbLiveCategoryDO tbLiveCategoryDO = tbLiveCategoryService.getByName(updateLiveRoomDTO.getName());
        if (tbLiveCategoryDO != null) {
            TbLiveRoomDO model = new TbLiveRoomDO();
            model.setId(updateLiveRoomDTO.getId());
            model.setCategory(updateLiveRoomDTO.getName());
            model.setAppointStart(updateLiveRoomDTO.getAppointStart());
            model.setCover(updateLiveRoomDTO.getCover());
            model.setTitle(updateLiveRoomDTO.getTitle());
            model.setIsPublic(updateLiveRoomDTO.getIsPublic());
            tbLiveRoomService.updateById(model);
            return;
        } else {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.LIVE_ROOM_LIVE_CATEGORY_MORE);
        }
    }

    @Override
    public Boolean updateLiveVisitorsForKind(Integer roomId, Integer kind) {
        TbLiveRoomDO tbLiveRoomDO = tbLiveRoomService.getByRoomId(roomId);
        if (Objects.isNull(tbLiveRoomDO)) {
            Log.info("-----------直播间找不到数据--------" + roomId);
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.ROOM_ENTITY_NOT_NULL);
        }
        int add = 0;
        int counts = VisitorUtils.getVisitors(tbLiveRoomDO.getVisitorNum(), visitor_rule);//人气值
        Boolean isHigh = (counts >= LIVER_VISITORS_BORDER.intValue());//是否是高人气
        if (kind.equals(AddVisitorsKindEnum.OPEN_VISITORS.getKind())) {
            if (!StringUtils.isEmpty(FAMOUS_LIVERS) && FAMOUS_LIVERS.contains(tbLiveRoomDO.getLiveUserId().toString())) {
                //知名主播
                add = FAMOUS_LIVER_OPEN_VISITORS;
            } else {
                add = NORMAL_LIVER_OPEN_VISITORS;
            }
        }
        if (kind.equals(AddVisitorsKindEnum.SHARE.getKind())) {
            if (isHigh) {
                add = LIVER_SHARE_VISITORS_HIGH;
            } else {
                add = LIVER_SHARE_VISITORS_LOW;
            }
        }
        if (kind.equals(AddVisitorsKindEnum.ADDPRAISE_FOLLOW.getKind())) {
            if (isHigh) {
                add = LIVER_SHARE_ADDPRAISE_FOLLOW_HIGH;
            } else {
                add = LIVER_SHARE_ADDPRAISE_FOLLOW_LOW;
            }
        }
        if (kind.equals(AddVisitorsKindEnum.BULLET_CHAT.getKind())) {
            if (isHigh) {
                add = LIVER_BULLET_CHAT_HIGH;
            } else {
                add = LIVER_BULLET_CHAT_LOW;
            }
        }
        return updateLiveVisitors(roomId, counts + add);
    }

    @Override
    public void joinTimeWelcomeMessage(Integer roomId,String chatroomId, long period, int limit) {
        timeChatRoomService.join(roomId,chatroomId,period,limit);
    }

    @Override
    public void quitTimeWelcomeMessage(Integer roomId) {
        timeChatRoomService.quit(roomId);
    }

    @Override
    public void changeTimeWelcomeMessage(Integer roomId,String chatroomId, long period, int limit) {
        timeChatRoomService.change(roomId,chatroomId,period,limit);
    }

    @Override
    public void joinDelayWelcomeMessage(Integer roomId) {
        enterRoomDelayService.join(roomId);
    }

    @Override
    public void changeDelayWelcomeMessage(Integer roomId) {
        enterRoomDelayService.change(roomId);
    }

    @Override
    public void quitDelayWelcomeMessage(Integer roomId) {
        enterRoomDelayService.quit(roomId);
    }

    @Override
    public void updateLiveOpenType(Integer roomId, String openType) {
        TbLiveRoomDO tbLiveRoomDO = new TbLiveRoomDO();
        tbLiveRoomDO.setId(roomId);
        tbLiveRoomDO.setOpenType(openType);
        String[] rules = enterRoomDelayService.getTypeRule(openType);
        if (rules != null) {
            TbLiveRoomDO model = tbLiveRoomService.getById(roomId);
            if (model != null) {
                if (!model.getState().equals(LiveStatusEnum.LIVE_STARTING.getStatus())) { //直播中的不改人气
                    int vistor = enterRoomDelayService.getVistors(model.getVisitorNum(), openType);
                    tbLiveRoomDO.setVisitorNum(vistor);
                }
            }
        }
        tbLiveRoomService.updateById(tbLiveRoomDO);
    }

    @Override
    public List<LiveHomeGoodDTO> getShowGoods(Integer roomId) {
        List<LiveHomeGoodDTO> list = new ArrayList<>();
        List<TbLiveGoodsDO> result = tbLiveGoodsService.GetShowGoods(roomId);
        if (result.size() > 0) {
            for (TbLiveGoodsDO tbLiveGoodsDO : result) {
                LiveHomeGoodDTO liveHomeGoodDTO = new LiveHomeGoodDTO();
                liveHomeGoodDTO.setSpu(tbLiveGoodsDO.getSpuCode());
                TbSpuManageParamDTO param = new TbSpuManageParamDTO();
                param.setUserMemberRole(UserMemberRoleEnum.GUEST.getRole());
                param.setSpuCode(tbLiveGoodsDO.getSpuCode());
                //获得商品信息
                SpuManageShowDTO spuManageShowDTO = remoteGoodsService.selectOneSpuShowByParam(param);
                if (Objects.nonNull(spuManageShowDTO)) {
                    liveHomeGoodDTO.setPrice(spuManageShowDTO.getPrice());
                    liveHomeGoodDTO.setSpuImage(spuManageShowDTO.getSpuImage());
                    liveHomeGoodDTO.setSpuTitle(spuManageShowDTO.getSpuTitle());
                    liveHomeGoodDTO.setSpuId(spuManageShowDTO.getSpuId());
                    liveHomeGoodDTO.setShowState(tbLiveGoodsDO.getShowState());
                    liveHomeGoodDTO.setSpuCode(spuManageShowDTO.getSpucode());
                }
                list.add(liveHomeGoodDTO);
            }
        }
        return list;
    }

    @Override
    public UpAndDownGoodDTO upAndDownGood(Integer roomId, String spuCode, Integer type) {
        List<LiveHomeGoodDTO> list = new ArrayList<>();
        UpAndDownGoodDTO upAndDownGoodDTO = new UpAndDownGoodDTO();
        int result = tbLiveGoodsService.upAndDownGood(roomId, spuCode, type);
        upAndDownGoodDTO.setResult(result);
        upAndDownGoodDTO.setShowGoodsList(list);
        if (result > 0) {
            list = getShowGoods(roomId);
            upAndDownGoodDTO.setShowGoodsList(list);
        }
        return upAndDownGoodDTO;
    }
    /**
     * 去重
     *
     * @param keyExtractor
     * @param <T>
     * @return
     */
    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * DO 转 DTO
     *
     * @param page
     * @param userId
     * @return
     */
    private List<LiveHomeListDTO> getLiveHomeListDTOByDO(List<TbLiveRoomDO> page, Integer userId) {
        ArrayList<LiveHomeListDTO> list = Lists.newArrayList();
        for (TbLiveRoomDO tbLiveRoomDO : page) {
            //直播间信息
            LiveHomeListDTO liveHomeListDTO = new LiveHomeListDTO();
            if (!StringUtils.isEmpty(tbLiveRoomDO.getStreamFname())) {
                if (!StringUtils.isEmpty(tbLiveRoomDO.getBackTime())) {
                    liveHomeListDTO.setLiveTime(tbLiveRoomDO.getBackTime());
                } else {
                    String timeStr = VisitorUtils.getBackTime(qiNiuService.getBackUrl(tbLiveRoomDO.getStreamFname()));
                    if (!StringUtils.isEmpty(timeStr)) {
                        tbLiveRoomDO.setBackTime(timeStr);
                        tbLiveRoomService.updateById(tbLiveRoomDO);
                        liveHomeListDTO.setLiveTime(timeStr);
                    }
                }
            }
            liveHomeListDTO.setCover(tbLiveRoomDO.getCover());
            liveHomeListDTO.setId(tbLiveRoomDO.getId());
            liveHomeListDTO.setState(tbLiveRoomDO.getState());
            liveHomeListDTO.setTitle(tbLiveRoomDO.getTitle());
            liveHomeListDTO.setVisitorNum(VisitorUtils.getVisitors(tbLiveRoomDO.getVisitorNum(), visitor_rule));
            liveHomeListDTO.setImId(tbLiveRoomDO.getImId());
            liveHomeListDTO.setLiveUserId(tbLiveRoomDO.getLiveUserId());
            liveHomeListDTO.setStreamUrl(tbLiveRoomDO.getStreamUrl());//推流地址
            liveHomeListDTO.setStreamBackUrl(qiNiuService.getBackUrl(tbLiveRoomDO.getStreamFname()));//回放地址
            liveHomeListDTO.setStreamFlvBackUrl(qiNiuService.getFlvBackUrl(tbLiveRoomDO.getStreamFlvFname()));//flv回放地址
            //播放地址
            liveHomeListDTO.setRTMPPlayURL(qiNiuService.getRTMPPlayURL(tbLiveRoomDO.getStreamKey()));
            liveHomeListDTO.setHDLPlayURL(qiNiuService.getHDLPlayURL(tbLiveRoomDO.getStreamKey()));
            liveHomeListDTO.setHLSPlayURL(qiNiuService.getHLSPlayURL(tbLiveRoomDO.getStreamKey()));
            TbUserDO tbUserDO = tbLiveVisitorService.getTbUserByUserId(tbLiveRoomDO.getLiveUserId());
            //主播信息
            if (Objects.nonNull(tbUserDO)) {
                if (StringUtils.isEmpty(tbUserDO.getShopName())) {
                    liveHomeListDTO.setShopName(tbUserDO.getNickname());
                } else {
                    liveHomeListDTO.setShopName(tbUserDO.getShopName());
                }
                liveHomeListDTO.setHeadimgurl(tbUserDO.getHeadimgurl());
            }
            //是否关注
            liveHomeListDTO.setIfConcern(
                    remoteUserFollowService.ifFollowOfFollow(userId, tbLiveRoomDO.getLiveUserId()));
            //展示前面3个商品信息getLiveHomeGoodList
            List<LiveHomeGoodDTO> liveHomeGoodDTOList = Lists.newArrayList();
            //橱窗中的商品
            List<TbLiveGoodsDO> liveGoodsDOList = tbLiveGoodsService.getGoodsByRoomId(tbLiveRoomDO.getId());
            liveHomeListDTO.setGoodsCount(liveGoodsDOList.size());
            if (liveGoodsDOList.size() >= 3) {
                liveGoodsDOList = liveGoodsDOList.subList(0, 3);
            }
            //是否预约
            TbLiveSubscribersDO tbLiveSubscribersDO =
                    tbLiveSubscribersService.getByRoomIdAndUserId(tbLiveRoomDO.getId(), userId);
            //查不到订阅信息
            if (Objects.isNull(tbLiveSubscribersDO)) {
                //未预约
                liveHomeListDTO.setSubscribeStatus(false);
            } else {
                liveHomeListDTO.setSubscribeStatus(true);
            }
            liveHomeListDTO.setAppointStart(tbLiveRoomDO.getAppointStart());
            for (TbLiveGoodsDO tbLiveGoodsDO : liveGoodsDOList) {
                TbSpuManageParamDTO param = new TbSpuManageParamDTO();
                param.setSpuCode(tbLiveGoodsDO.getSpuCode());
                param.setUserMemberRole(UserMemberRoleEnum.GUEST.getRole());
                //获得商品信息
                SpuManageShowDTO spuManageShowDTO = remoteGoodsService.selectOneSpuShowByParam(param);
                if (Objects.nonNull(spuManageShowDTO)) {
                    LiveHomeGoodDTO liveHomeGoodDTO = new LiveHomeGoodDTO();
                    liveHomeGoodDTO.setSpu(tbLiveGoodsDO.getSpuCode());
                    liveHomeGoodDTO.setPrice(spuManageShowDTO.getPrice());
                    liveHomeGoodDTO.setSpuImage(spuManageShowDTO.getSpuImage());
                    liveHomeGoodDTO.setSpuTitle(spuManageShowDTO.getSpuTitle());
                    liveHomeGoodDTO.setSpuId(spuManageShowDTO.getSpuId());
                    liveHomeGoodDTO.setSpuCode(spuManageShowDTO.getSpucode());
                    liveHomeGoodDTOList.add(liveHomeGoodDTO);
                }
            }
            liveHomeListDTO.setGoods(liveHomeGoodDTOList);
            list.add(liveHomeListDTO);
        }
        return list;
    }
}

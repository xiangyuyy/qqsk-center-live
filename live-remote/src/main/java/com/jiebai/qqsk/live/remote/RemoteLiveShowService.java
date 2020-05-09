package com.jiebai.qqsk.live.remote;

import com.github.pagehelper.PageInfo;
import com.jiebai.qqsk.live.dto.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 观看直播相关服务
 *
 * @author cxy
 */
public interface RemoteLiveShowService {

    /**
     * 观众看到的所有直播列表
     *
     * @param liveParamDTO
     * @return
     */
    PageInfo<LiveHomeListDTO> getLiveHomeList(LiveParamDTO liveParamDTO);

    /**
     * 观众看到的直播间商品列表
     *
     * @param liveParamDTO
     * @return
     */
    PageInfo<LiveHomeGoodDTO> getLiveHomeGoodList(LiveParamDTO liveParamDTO);

    /**
     * 直播间展示信息
     *
     * @param roomId
     * @return
     */
    LiveShowRoomDTO getLiveShowRoom(Integer roomId, Integer userId);

    /**
     * 扫码进入直播间展示信息
     *
     * @param roomId
     * @return
     */
    LiveShowRoomDTO getLiveShowRoomForScan(Integer roomId, Integer userId);


    /**
     * 定时获得直播间展示信息
     *
     * @param roomId
     * @return
     */
    LiveShowRoomDTO getTimeLiveShowRoom(Integer roomId, Integer userId);


    /**
     * 直播间观众列表
     *
     * @param liveParamDTO
     * @return
     */
    PageInfo<LiverVisitorDTO> getVisitorList(LiveParamDTO liveParamDTO);

    /**
     * 获得直播间人员信息
     *
     * @param userId   本身userId
     * @param toUserId 被查看人userId
     * @return
     */
    LiverUserDTO getLiverUser(Integer userId, Integer toUserId);

    /**
     * 获得融云Im token
     *
     * @param userId
     * @return
     */
    String getImTokenByUserId(Integer userId);

    /**
     * 新增关注.
     *
     * @param userId       用户ID
     * @param followUserId 被关在用户ID
     */
    void addUserFollow(Integer userId, Integer followUserId);

    /**
     * 取消关注
     *
     * @param userId       用户ID
     * @param followUserId 用户的关注人ID
     */
    void deleteUserFollow(Integer userId, Integer followUserId);

    /**
     * 关闭直播间
     *
     * @param roomId
     * @return 直播时间
     */
    String closeLiveRoom(Integer roomId);

    /**
     * 定时关闭直播间
     */
    void timingCloseLiveRoom();

    /**
     * 定时扫描关闭直播间（中途断网的情况）
     */
    void timingScanCloseLiveRoom();

    /**
     * 定时同步房间点赞数
     */
    void timingUpdatePraise();

    /**
     * 定时同步flv格式回放
     */
    void timingGetBackFnameForFLV();

    /**
     * 定时同步mu38格式回放
     */
    void timingGetBackFname();

    /**
     * 关闭云服务
     *
     * @param streamKey 七牛key
     * @param imId      融云imid
     */
    void closeYunServices(String streamKey, String imId);

    /**
     * redis处理点赞
     *
     * @param roomId
     */
    void addPraise(Integer roomId, Integer counts);

    /**
     * 处理观众进入数
     *
     * @param roomId
     * @param userId
     */
    void addVisitor(Integer roomId, Integer userId);

    /**
     * 我的直播列表
     *
     * @param userId   用户id
     * @param pageNum  页码
     * @param pageSize 分页大小
     * @return MyLivePageDTO
     */
    MyLivePageDTO getMyLiveList(Integer userId, Integer pageNum, Integer pageSize);

    /**
     * 添加聊天室全局禁言成员
     *
     * @param userIds
     * @param minute
     */
    Boolean addLiveBans(List<Integer> userIds, Integer minute);

    /**
     * 删除聊天室全局禁言成员
     *
     * @param userIds
     */
    Boolean removeLiveBans(List<Integer> userIds);

    /**
     * 获取聊天室全局禁言列表
     *
     * @return
     */
    List<String> getBans();

    /**
     * 添加保活聊天室
     *
     * @param imId
     * @return
     */
    Boolean addKeepalive(String imId);

    /**
     * 删除保活聊天室
     *
     * @param imId
     * @return
     */
    Boolean removeKeepalive(String imId);

    /**
     * 获取保活聊天室列表
     *
     * @return
     */
    List<String> getKeepalives();

    /**
     * 开始直播, 更改直播状态
     *
     * @param roomId 直播间id
     * @param userId 用户id
     * @return String 返回融云Id
     */
    String startLiveRoom(Integer roomId, Integer userId);


    /**
     * 添加封禁聊天室成员
     *
     * @param userIds
     * @param minute
     */
    Boolean addBlock(String imId, List<Integer> userIds, Integer minute);

    /**
     * 删除封禁聊天室成员
     *
     * @param userIds
     */
    Boolean removeBlock(String imId, List<Integer> userIds);

    /**
     * 获取封禁聊天室成员列表
     *
     * @return
     */
    List<String> getBlockUsers(String imId);


    /**
     * 添加聊天室禁言成员
     *
     * @param userIds
     * @param minute
     */
    Boolean addGag(String imId, List<Integer> userIds, Integer minute);

    /**
     * 删除聊天室禁言成员
     *
     * @param userIds
     */
    Boolean removeGag(String imId, List<Integer> userIds);

    /**
     * 获取聊天室禁言列表
     *
     * @return
     */
    List<String> getGagUsers(String imId);

    /**
     * 删除我的直播间
     *
     * @param roomId 直播间id
     * @param userId 用户Id
     * @return int
     */
    int deleteMyLiveRoom(Integer roomId, Integer userId);


    /**
     * 春节直播人气
     *
     * @param begin 活动开始时间
     * @param end   活动结束时间
     * @param level 人气  1 -100 ,2 -200
     * @return
     */
    List<Map<String, String>> getSpringRooms(Date begin, Date end, Integer level);

    /**
     * 修改直播间人气
     *
     * @param roomId roomId
     * @param num    人气值
     * @return
     */
    Boolean updateLiveVisitors(Integer roomId, Integer num);

    /**
     * 直播间列表（管理后台）
     *
     * @param queryDTO
     * @return
     */
    PageInfo<LiveRoomManagerDTO> getRoomListForManager(LiveRoomManagerQueryDTO queryDTO);


    /**
     * 观众看到的直播列表头部搜索
     *
     * @param liveQueryParamDTO
     * @return
     */
    PageInfo<LiveHomeListDTO> getHeadSearchList(LiveQueryParamDTO liveQueryParamDTO);

    /**
     * 观众看到的直播列表推荐列表
     *
     * @param liveParamDTO
     * @return
     */
    PageInfo<LiveHomeListDTO> getRecommendList(LiveParamDTO liveParamDTO);

    /**
     * 获取直播类型标签
     *
     * @return
     */
    List<Map<String, String>> getLiveCategory();

    /**
     * 获取所有的直播分类标签（后台管理）
     *
     * @return
     */
    List<LiveCategoryDTO> getAllLiveCategoryDTO();

    /**
     * 获取未禁用的直播分类标签（后台管理）
     *
     * @return
     */
    List<LiveCategoryDTO> getLiveCategoryDTOEnable();

    /**
     * 添加直播分类标签（后台管理）
     *
     * @return
     */
    String addLiveCategory(LiveCategoryDTO liveCategoryDTO);

    /**
     * 删除直播分类标签（后台管理）
     *
     * @param id
     * @return
     */
    String deleteLiveCategory(Integer id);

    /**
     * 修改直播分类标签
     *
     * @param liveCategoryDTO
     * @return
     */
    String updateLiveCategory(LiveCategoryDTO liveCategoryDTO);

    /**
     * 后台修改直播间信息
     *
     * @param updateLiveRoomDTO
     * @return
     */
    void updateLiveRoomByManager(UpdateLiveRoomDTO updateLiveRoomDTO);

    /**
     * 各种情况修改直播间人气
     * @param roomId roomId
     * @param kind 类型
     * @return
     */
    Boolean updateLiveVisitorsForKind(Integer roomId,Integer kind);

    /**
     * 开播的直播间定时刷进入消息 （定时任务方案）
     *
     * @param roomId     roomId
     * @param chatroomId 融云房间号
     * @param period     每隔多少秒刷
     * @param limit      一次发送几个消息
     */
    void joinTimeWelcomeMessage(Integer roomId, String chatroomId, long period, int limit);

    /**
     * 直播间退出定时刷进入消息（定时任务方案）
     *
     * @param roomId roomId
     */
    void quitTimeWelcomeMessage(Integer roomId);

    /**
     * 改变直播间定时刷进入消息 （定时任务方案）
     *
     * @param roomId     roomId
     * @param chatroomId 融云房间号
     * @param period     每隔多少秒刷
     * @param limit      一次发送几个消息
     */
    void changeTimeWelcomeMessage(Integer roomId, String chatroomId, long period, int limit);

    /**
     * 开播的直播间定时刷进入消息 （延时任务方案）
     *
     * @param roomId  roomId
     */
    void joinDelayWelcomeMessage(Integer roomId);

    /**
     * 直播间退出定时刷进入消息（定时任务方案）
     *
     * @param roomId roomId
     */
    void changeDelayWelcomeMessage(Integer roomId);

    /**
     * 改变直播间定时刷进入消息 （定时任务方案）
     *
     * @param roomId  roomId
     */
    void quitDelayWelcomeMessage(Integer roomId);

    /**
     * 修改直播开播类型
     * @param roomId  roomId
     * @param openType  A.B.C..
     */
    void updateLiveOpenType(Integer roomId,String openType);

    /**
     *  获得展示上下屏的商品
     * @param roomId  roomId
     * @return
     */
    List<LiveHomeGoodDTO> getShowGoods(Integer roomId);

    /**
     * 上下屏操作
     * @param roomId roomId
     * @param spuCode spuCode
     * @param type  0 下屏 1上屏
     * @return
     */
    UpAndDownGoodDTO upAndDownGood(Integer roomId,String spuCode,Integer type);
}

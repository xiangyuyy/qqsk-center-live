package com.jiebai.qqsk.live.service;

import com.jiebai.framework.service.Service;
import com.jiebai.qqsk.live.dto.LiveRoomDTO;
import com.jiebai.qqsk.live.model.TbLiveRoomDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lichenguang
 * @version v1.0.0
 * @date 2019/11/13 19:11:15
 */
public interface TbLiveRoomService extends Service<TbLiveRoomDO> {

    TbLiveRoomDO getByRoomId(@Param("roomId") Integer roomId);

    /**
     * 获得所有直播间（排序）
     * @return
     */
    List<TbLiveRoomDO> getAllRoom(String category,Integer visitorNum);

    /**
     * 预约直播间（创建直播间和橱窗）
     * @param liveRoomDTO 直播间信息
     * @return 0为失败
     */
    int insertSelective(LiveRoomDTO liveRoomDTO);

    /**
     * 更新直播间向信息（更新直播间）
     * @param tbLiveRoomDO 直播间信息
     * @return 0为失败
     */
    int updateByPrimaryKeySelective(TbLiveRoomDO tbLiveRoomDO);

    /**
     * 获得用户点赞总数
     * @param userId
     * @return
     */
    int getPraisesByLiveUserId(Integer userId);

    /**
     * @param userId 用户id
     * @return List
     */
    List<TbLiveRoomDO> getLiveRoomByUserId(Integer userId);

    /**
     * 获取用户未结束的直播间的数量
     * @param userId 用户id
     * @return int
     */
    int getLiveRoomNotOverCount(Integer userId);

    /**
     * 获取直播间信息
     * @param roomId 直播间id
     * @param userId 用户id
     * @return TbLiveRoomDO
     */
    TbLiveRoomDO getByIdAndUserId(Integer roomId, Integer userId);

    /**
     * 开始直播, 更改直播状态
     * @param roomId 直播间id
     * @param userId 用户id
     * @return String 融云id
     */
    String updateLiveStatusByIdAndUserId(Integer roomId, Integer userId);

    /**
     * 关闭直播间
     * @param roomId 直播间id
     * @return Integer
     */
    Integer closeLiveRoom(Integer roomId);

    /**
     * 关闭云服务
     * @param streamKey 流key
     * @param imId      String
     */
    void closeYunServices(String streamKey, String imId);

    /**
     * 观众获取直播间订阅信息
     * @param roomId 直播间id
     * @return TbLiveRoomDO
     */
    TbLiveRoomDO selectNotDeleteLiveRoomById(Integer roomId);

    /**
     * 获取超过预约时间10分钟未开播，但是没有关闭的直播间
     * @return List
     */
    List<TbLiveRoomDO> selectDelayTaskLiveRoomList();

    /**
     * 删除我的直播间
     * @param roomId 直播间id
     * @return int
     */
    int deleteLiveRoomById(Integer roomId);

    /**
     * 根据用户id和id更新
     * @param roomId       直播间id
     * @param userId       用户id
     * @param tbLiveRoomDO 实体
     * @return int
     */
    int updateByIdAndUserId(Integer roomId, Integer userId, TbLiveRoomDO tbLiveRoomDO);

    /**
     * 获得房间观众数
     * @param userId
     * @return
     */
    int getRoomVisitorNum(Integer userId);

    /**
     * 获取用户上一次直播信息
     * @param userId 用户id
     * @return TbLiveRoomDO
     */
    TbLiveRoomDO getUserLastLiveInfo(Integer userId);

    List<TbLiveRoomDO> getNotDeleteRoomList(String category);

    /**
     * 获取关注人中正在直播的
     * @param userIdList 关注人userId
     * @return List<TbLiveRoomDO>
     */
    List<TbLiveRoomDO> getDiscoverLivingUserList(List<Integer> userIdList);

    /**
     * 获取关注人直播间列表
     * @param followUserList 关注用户list
     * @return List<TbLiveRoomDO>
     */
    List<TbLiveRoomDO> getDiscoverListByUserList(List<Integer> followUserList);

    /**
     * 获取关注人最近的一场直播
     * @param userId 用户id
     * @return TbLiveRoomDO
     */
    TbLiveRoomDO getFollowUserLastLiveInfo(Integer userId);

    /**
     * 直播间列表（后台管理）
     * @param liveRoomDO
     * @return
     */
    List<TbLiveRoomDO>  getRoomListForManager(TbLiveRoomDO liveRoomDO);

    /**
     * 观众看到的直播列表头部搜索
     * @param search
     * @return
     */
    List<TbLiveRoomDO> getHeadSearchList(String search);

    /**
     * 观众看到的直播列表推荐内容
     *
     * @return
     */
    List<TbLiveRoomDO> getRecommendList();

    /**
     * 查询该分类下有无直播间
     * @param id
     * @return
     */
    Integer getLiveRoomByCategryId(Integer id);

    /**
     * 定时任务改变live rank
     */
    void scheduleChangeLiveRank();

    /**
     * 查询正在直播的直播间
     * @return List
     */
    List<TbLiveRoomDO> findLiveRoomIsLiving();

    /**
     * 更新正在直播的直播间权重
     * @param id 主键Id
     * @param rank 权重
     */
    void updateRankById(Integer id, int rank);

    /**
     * 查询已结束且回放时长大于15分钟的
     * @return List
     */
    List<TbLiveRoomDO> selectLessOneDayAndEndLiveRoom();

    /**
     * 更新rank
     * @param roomIdList ids
     * @param rank 权重
     */
    void updateRankByIds(List<Integer> roomIdList, int rank);

    /**
     * 查询已结束且回放时长大于1天的
     * @param start 开始
     * @param end 结束
     * @return List
     */
    List<TbLiveRoomDO> findEndLiveRoomMoreOneDayByDay(int start, int end);

    /**
     * 更新0-24小时，时长小宇5分钟，人气小宇100
     * @param backVisitorsNum 后台实际人数
     */
    void updateLessOneDayLiveTimeLess5(int backVisitorsNum);

    /**
     * 获得主播直播场数
     * @param userId userId
     * @return
     */
    int getOpenLivesByUserId(Integer userId);

    /**
     * 查询公开的直播间
     * @param userId 用户id
     * @return List
     */
    List<TbLiveRoomDO> findPublicLiveRoomByUserId(Integer userId);
}

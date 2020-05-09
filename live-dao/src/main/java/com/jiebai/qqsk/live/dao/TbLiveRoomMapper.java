package com.jiebai.qqsk.live.dao;

import com.jiebai.framework.service.Mapper;
import com.jiebai.qqsk.live.model.TbLiveRoomDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TbLiveRoomMapper extends Mapper<TbLiveRoomDO> {

    TbLiveRoomDO getByRoomId(@Param("roomId") Integer roomId);

    /**
     * 获得所有直播间（排序）
     *
     * @return List
     */
    List<TbLiveRoomDO> getAllRoom(@Param("category") String category,@Param("visitorNum") Integer visitorNum);

    int insertAndReturnId(TbLiveRoomDO liveRoomDO);

    /**
     * 获得用户点赞总数
     *
     * @param userId
     * @return
     */
    int getPraisesByLiveUserId(@Param("userId") Integer userId);

    /**
     * 获得房间观众数
     *
     * @param userId
     * @return
     */
    int getRoomVisitorNum(@Param("userId") Integer userId);

    /**
     * @param userId 用户id
     * @return TbLiveRoomDO
     */
    TbLiveRoomDO selectUserLastLiveInfo(Integer userId);

    /**
     * 获取未删除的主播列表，包括私密直播
     *
     * @return List<TbLiveRoomDO>
     */
    List<TbLiveRoomDO> getNotDeleteRoomList(@Param("category") String category);

    /**
     * 获取关注人最近的一场直播
     *
     * @param userId 用户id
     * @return TbLiveRoomDO
     */
    TbLiveRoomDO selectFollowUserLastLiveInfo(Integer userId);

    /**
     * 根据userIdList查询关注人的直播列表
     *
     * @param userIdList 用户id
     * @return List
     */
    List<TbLiveRoomDO> selectDiscoverListByUserList(@Param(("userIdList")) List<Integer> userIdList);

    /**
     * 直播间列表（后台管理）
     *
     * @param liveRoomDO
     * @return
     */
    List<TbLiveRoomDO> getRoomListForManager(TbLiveRoomDO liveRoomDO);

    /**
     * 观众看到的直播列表头部搜索
     *
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
    Integer getLiveRoomByCategryId(@Param("id") Integer id);

    /**
     * 获得主播直播场数
     * @param userId userId
     * @return
     */
    int getOpenLivesByUserId(@Param("userId") Integer userId);

    /**
     * 查询公开的直播间
     * @param userId 用户id
     * @return List
     */
    List<TbLiveRoomDO> selectPublicLiveRoomByUserId(@Param("userId") Integer userId);
    /**
     * 更新5分钟以内结束，时长15分钟以内的
     * @param beforeDate 日期
     * @return int
     */
    int updateRank15BackLive(@Param("beforeDate") Date beforeDate);

    /**
     * 更新直播中国权重
     * @param roomId 主键
     * @param rank 权重
     * @return int
     */
    int updateRankById(@Param("roomId") Integer roomId, @Param("rank") int rank);
}
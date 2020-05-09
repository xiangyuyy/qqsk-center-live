package com.jiebai.qqsk.live.dao;

import com.jiebai.framework.service.Mapper;
import com.jiebai.qqsk.live.model.TbLiveVisitorDO;
import com.jiebai.qqsk.live.model.TbUserDO;
import org.apache.ibatis.annotations.Param;
import org.mvel2.util.Make;

import java.util.List;
import java.util.Map;

public interface TbLiveVisitorMapper extends Mapper<TbLiveVisitorDO> {

    /**
     * 获得TbUser基础信息
     *
     * @param userId
     * @return
     */
    TbUserDO getTbUserByUserId(@Param("userId") Integer userId);

    /**
     * 获得房间观众列表
     *
     * @param roomId
     * @return
     */
    List<TbLiveVisitorDO> getVisitorByRoomId(@Param("roomId") Integer roomId);

    /**
     * 获得今天进入直播间观众总数
     *
     * @return
     */
    int getTodayVisitors();

    /**
     * 获得一个随机数
     *
     * @return
     */
    int getRandForChatRomm();

    /**
     * 通过随机数随机获得n个数
     *
     * @param rand  随机数
     * @param limit n个数
     * @return
     */
    List<Map<String,Object>> getRandVisitors(@Param("rand") int rand, @Param("limit") int limit);

    /**
     * 获得所有观众信息
     * @return
     */
    List<Map<String,Object>> getAllRandVisitors();
}
package com.jiebai.qqsk.live.service;

import com.jiebai.framework.service.Service;
import com.jiebai.qqsk.live.model.TbLiveVisitorDO;
import com.jiebai.qqsk.live.model.TbUserDO;

import java.util.List;
import java.util.Map;

/**
 * @author lichenguang
 * @version v1.0.0
 * @date 2019/11/13 16:00:22
 */
public interface TbLiveVisitorService extends Service<TbLiveVisitorDO> {

    /**
     * 获得TbUser基础信息
     *
     * @param userId
     * @return
     */
    TbUserDO getTbUserByUserId(Integer userId);

    /**
     * 获得房间观众列表
     *
     * @param roomId
     * @return
     */
    List<TbLiveVisitorDO> getVisitorByRoomId(Integer roomId);

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
     * @param limit n个数
     * @return
     */
    List<Map<String,Object>> getRandVisitors(int limit);


    /**
     * 获得所有观众信息
     * @return
     */
    List<Map<String,Object>> getAllRandVisitors();
}

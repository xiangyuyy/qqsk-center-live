package com.jiebai.qqsk.live.service;

import com.jiebai.framework.service.Service;
import com.jiebai.qqsk.live.model.TbLiveSubscribersDO;

import java.util.List;

/**
 * @author lichenguang
 * @version v1.0.0
 * @date 2019/11/13 16:03:57
 */
public interface TbLiveSubscribersService extends Service<TbLiveSubscribersDO> {

    /**
     * 预约直播间接口
     * @param tbLiveSubscribersDO 预约直播间实体类
     * @return int
     */
    int insertSelective(TbLiveSubscribersDO tbLiveSubscribersDO);

    /**
     * 根据用户id和直播间id查询订阅信息
     * @param roomId 直播间id
     * @param userId 用户id
     * @return TbLiveSubscribersDO
     */
    TbLiveSubscribersDO getByRoomIdAndUserId(Integer roomId, Integer userId);

    /**
     * 获取预约直播间的所有用户ID
     * @param roomId
     * @return
     */
    List<Integer> getUserIdByRoomId(Integer roomId);
}

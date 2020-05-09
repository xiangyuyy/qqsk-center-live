package com.jiebai.qqsk.live.dao;

import com.jiebai.framework.service.Mapper;
import com.jiebai.qqsk.live.model.TbLiveSubscribersDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbLiveSubscribersMapper extends Mapper<TbLiveSubscribersDO> {

    /**
     * 根据订阅者的userId查询其openId
     * @param userIdList List
     * @return List
     */
    List<String> getUserOpenIdBySubscriberUserIdList(@Param("userIdList") List<Integer> userIdList,
        @Param("sourceId") String sourceId);

    /**
     * 获取预约直播间的所有用户ID
     * @param roomId
     * @return
     */
    List<Integer> getUserIdByRoomId(@Param("roomId") Integer roomId);
}
package com.jiebai.qqsk.live.remote;

import com.jiebai.qqsk.live.dto.LiveRoomDTO;
import com.jiebai.qqsk.live.dto.LiveRoomSubscriberDTO;
import com.jiebai.qqsk.live.dto.LiveSubscribersDTO;
import com.jiebai.qqsk.live.dto.MyLiveDTO;

/**
 * 直播预约相关服务
 * @author lichenguang
 * 2019/11/13
 */
public interface RemoteLiveReserveService {

    /**
     * 创建直播间
     * @param liveRoomDTO 直播间传输实体
     * @return 0为失败
     */
    int createLiveRoom(LiveRoomDTO liveRoomDTO);

    /**
     * 更新直播间信息
     * @param liveRoomDTO 直播间传输实体
     * @return 0为失败
     */
    int updateLiveInfo(LiveRoomDTO liveRoomDTO);

    /**
     * 主播获取直播间信息
     * @param roomId 直播间id
     * @param userId 用户id
     * @return LiveRoomDTO
     */
    MyLiveDTO getByIdAndUserId(Integer roomId, Integer userId);

    /**
     * 预约直播间接口
     * @param liveSubscribersDTO 预约直播间实体类
     * @return int
     */
    int reserveLive(LiveSubscribersDTO liveSubscribersDTO);

    /**
     * 观众获取直播间订阅信息
     * @param roomId 直播间id
     * @param userId 用户id
     * @return LiveRoomVisitorDTO
     */
    LiveRoomSubscriberDTO getReserveInfoByRoomIdAndUserId(Integer roomId, Integer userId);

    /**
     * 设置直播间私密，或者公开
     * @param roomId
     * @param userId
     * @return
     */
    int makeLiveRoomPublicOrPrivate(Integer roomId, Integer userId);

    /**
     * 获取主播上次开播的信息
     * @param userId 用户id
     * @return LiveRoomDTO
     */
    LiveRoomDTO getUserLastLiveInfo(Integer userId);

    /**
     * 关闭哪些未准时开播的直播间
     */
    void closeNotStartInTimeLiveRoom();

    void scheduleChangeLiveRank();

}

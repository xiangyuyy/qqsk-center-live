package com.jiebai.qqsk.live.event;

import lombok.Data;

/**
 * @author cxy
 * @description: 本地队列消息实体
 * @date 2019/11/169:25
 */
@Data
public class BusinessEventMessage {
    /**
     * 观众用户的user_id
     */
    private Integer userId;

    /**
     * 直播间id
     */
    private Integer roomId;

    /**
     * 是否真实 1 真实 0  不真实
     */
    private Integer IsTrue;

}

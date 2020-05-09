package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lichenguang
 * 2019/11/25
 */
@Data
public class LiveRoomSubscriberDTO implements Serializable {

    private static final long serialVersionUID = 349373506640522542L;

    private Integer id;
    /**
     * 封面地址
     */
    private String cover;

    /**
     * 直播状态（1，未开始，2，直播中, 0，已结束，）
     */
    private Integer state;

    /**
     * 标题
     */
    private String title;

    /**
     * 直播预约开始时间
     */
    private Date appointStart;

    /**
     * 直播预约结束时间
     */
    private Date appointEnd;

    /**
     * 用户是否已经预约
     */
    private Boolean subscribeStatus;

    /**
     * 主播的userId
     */
    private Integer liveUserId;
}

package com.jiebai.qqsk.live.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "tb_live_subscribers")
public class TbLiveSubscribersDO {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 预约人的userId
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 直播间id
     */
    @Column(name = "room_id")
    private Integer roomId;

    /**
     * 小程序模板消息formId
     */
    @Column(name = "form_id")
    private String formId;

    /**
     * 预约创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 发送时间
     */
    @Column(name = "send_time")
    private Date sendTime;

    /**
     * 服务消息发送状态（0，未发送，1，已发送）
     */
    @Column(name = "send_state")
    private Integer sendState;

}
package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LiveSubscribersDTO implements Serializable {

    private static final long serialVersionUID = 6176614106966415521L;
    /**
     * 自增主键
     */
    private Integer id;

    /**
     * 预约人的userId
     */
    private Integer userId;

    /**
     * 直播间id
     */
    private Integer roomId;

    /**
     * 小程序模板消息formId
     */
    private String formId;

    /**
     * 预约创建时间
     */
    private Date gmtCreate;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 服务消息发送状态（0，未发送，1，已发送）
     */
    private Integer sendState;

}
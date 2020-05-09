package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 后台直播列表
 *
 * @author lichenguang
 * 2019/11/13
 */
@Data
public class LiveRoomManagerDTO implements Serializable {

    private static final long serialVersionUID = -7837903326144313341L;
    /**
     * 直播间id,自增主键
     */
    private Integer id;


    /**
     * 封面地址
     */
    private String cover;


    /**
     * 直播状态（1，未开始，2，直播中， 0，已结束）, 默认为1
     */
    private Integer state;

    /**
     * 直播预约开始时间
     */
    private Date appointStart;

    /**
     * 直播预约结束时间
     */
    private Date appointEnd;

    /**
     * 直播开始时间
     */
    private Date liveStart;

    /**
     * 直播结束时间
     */
    private Date liveEnd;

    /**
     * 标题
     */
    private String title;

    /**
     * 预留，分类名称
     */
    //private String category;

    /**
     * 观看人数
     */
    private Integer visitorNum;

    /**
     * 主播的userId
     */
    private Integer liveUserId;

    /**
     * 主播的昵称
     */
    private String liveUserNickName;

    /**
     * 点赞数
     */
    private Integer praiseCount;


    /**
     * 是否展示在列表中，1为展示，0为不展示，默认为1
     */
    private Integer isPublic;

    /**
     * 是否删除：0为否，1为是
     */
    private Integer isDeleted;

    /**
     * 直播分类标签
     */
    private String mark;

    /**
     * 直播开播类型
     */
    private String openType;
}

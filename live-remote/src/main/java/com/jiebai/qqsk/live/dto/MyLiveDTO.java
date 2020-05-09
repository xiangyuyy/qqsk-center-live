package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 我的直播间列表
 * @author lichenguang
 */
@Data
public class MyLiveDTO implements Serializable {

    private static final long serialVersionUID = 3918167730961758413L;
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
     * 标题
     */
    private String title;

    /**
     * 七牛云回放地地址
     */
    private String streamBackUrl;

    /**
     * 七牛云推流url
     */
    private String streamUrl;

    /**
     * 直播预约开始时间
     */
    private Date appointStart;

    /**
     * 直播预约结束时间
     */
    private Date appointEnd;

    /**
     * 直播间私密或公开，0为私密，1为公开
     */
    private Integer isPublic;

    /**
     * RTMP直播地址
     */
    private String RTMPPlayURL;

    /**
     * HLS直播地址
     */
    private String HLSPlayURL;

    /**
     * HDL直播地址
     */
    private String HDLPlayURL;


    /**
     * IMID（融云聊天室id）
     */
    private String imId;

    /**
     * 观看人数
     */
    private Integer visitorNum;

    /**
     * 直播间商品总数
     */
    private Integer goodsCount;

    /**
     * 展示的3个商品列表
     */
    private List<LiveHomeGoodDTO> goods;

    /**
     * 直播时长
     */
    private String liveTime;

    /**
     * 直播分类
     */
    private String category;
}

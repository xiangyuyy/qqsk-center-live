package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 发现页个人直播类
 * @author lichenguang
 * date 2020/1/13
 */
@Data
public class DiscoverPersonalLiveDTO implements Serializable {

    private static final long serialVersionUID = 4542409390059129124L;
    /**
     * 直播间id,自增主键
     */
    private Integer id;

    /**
     * 七牛云回放地地址
     */
    private String streamBackUrl;

    /**
     * 七牛云FLV回放地地址
     */
    private String streamFlvBackUrl;

    /**
     * 七牛云推流url
     */
    private String streamUrl;

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
     * 主播的userId
     */
    private Integer liveUserId;

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
     * 观看人数
     */
    private Integer visitorNum;

    /**
     * 头像路径
     */
    private String headimgurl;
    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 直播间商品总数
     */
    private Integer goodsCount;

    /**
     * 直播预约开始时间
     */
    private Date appointStart;

    /**
     * 用户是否已经预约
     */
    private Boolean subscribeStatus;

    /**
     * 是否关注 0未关注,1关注
     */
    private Boolean ifConcern;

    /**
     * 直播时长
     */
    private String liveTime;

    /**
     * 预约时间提示
     */
    private String appointPrompt;
}

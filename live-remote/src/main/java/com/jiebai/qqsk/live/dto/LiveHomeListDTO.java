package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 直播首页列表DTO
 *
 * @author cxy
 */
@Data
public class LiveHomeListDTO implements Serializable {

    private static final long serialVersionUID = -7837903326144313341L;
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
     * 展示的3个商品列表
     */
    private List<LiveHomeGoodDTO> goods;

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

}

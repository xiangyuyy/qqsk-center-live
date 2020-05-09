package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 观众进入展示直播间信息DTO
 *
 * @author cxy
 */
@Data
public class LiveShowRoomDTO implements Serializable {

    private static final long serialVersionUID = -7837903326144313341L;
    /**
     * 直播间id
     */
    private Integer roomId;

    /**
     * 主播userId
     */
    private Integer liveUserId;
    /**
     * 主播头像路径
     */
    private String headimgurl;

    /**
     * 主播微信名
     */
    private String nickname;

    /**
     * 主播店铺名称
     */
    private String shopName;

    /**
     * 观看人数
     */
    private Integer visitorNum;

    /**
     * 前3位观众人数头像
     */
    private List<String> visitors;

    /**
     * 是否关注 false未关注,true关注
     */
    private Boolean ifConcern;

    /**
     * 直播倒计时(秒数)
     */
    private Integer liveCountDown;
    /**
     * IMID（融云聊天室id）
     */
    private String imId;

    /**
     * 是否注册Im
     */
    private Boolean ifRegisterIm;

    /**
     * 商品总数
     */
    private Integer goodsCount;

    /**
     * 防盗sign
     */
    private String playSign;

    /**
     * 是否结束直播
     */
    private Boolean ifOver;

    /**
     * 直播时长
     */
    private String liveTime;

    /**
     * 直播状态（1，未开始，2，直播中， 0，已结束）, 默认为1
     */
    private Integer liveState;

    /**
     * 七牛云回放地地址
     */
    private String streamBackUrl;

    /**
     * 七牛云推流url
     */
    private String streamUrl;

    /**
     * RTMP直播地址
     */
    private String RTMPPlayURL;

    /**
     * 封面地址
     */
    private String cover;

    /**
     * 封面标题
     */
    private String title;


    /**
     * 是否正常推流
     */
    private Boolean ifNormalPush;

    /**
     * 直播预约开始时间
     */
    private Date appointStart;

    /**
     * 展示的上屏商品信息
     */
    private List<LiveHomeGoodDTO> showGoodsList;
}

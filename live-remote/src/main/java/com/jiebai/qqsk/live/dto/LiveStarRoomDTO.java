package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : eyestarrysky
 * date : Created in 2020/3/1
 */
@Data
public class LiveStarRoomDTO implements Serializable {

    private static final long serialVersionUID = 2579663025950691881L;

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
     * 观看人数
     */
    private Integer visitorNum;

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
     * 直播时长
     */
    private String liveTime;

    /**
     * 直播预约开始时间
     */
    private Date appointStart;

    /**
     * 是否关注 0未关注,1关注
     */
    private Boolean ifConcern;
}

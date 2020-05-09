package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author lichenguang
 * 2019/11/13
 */
@Data
public class LiveRoomDTO implements Serializable {

    private static final long serialVersionUID = -7837903326144313341L;
    /**
     * 直播间id,自增主键
     */
    private Integer id;

    /**
     * 七牛云推流key
     */
    private String streamKey;

    /**
     * 七牛云回放名称
     */
    private String streamFname;

    /**
     * 七牛云推流url
     */
    private String streamUrl;

    /**
     * IMID（融云聊天室id）
     */
    private String imId;

    /**
     * 封面地址
     */
    private String cover;

    /**
     * 我的橱窗商品
     */
    private List<String> spuCodeList;

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
    private String category;

    /**
     * 观看人数
     */
    private Integer visitorNum;

    /**
     * 主播的userId
     */
    private Integer liveUserId;

    /**
     * 点赞数
     */
    private Integer praiseCount;

    /**
     * 扩展字段json
     */
    private String ext;

    /**
     * 是否展示在列表中，1为展示，0为不展示，默认为1
     */
    private Integer isPublic;
}

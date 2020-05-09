package com.jiebai.qqsk.live.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "tb_live_room")
public class TbLiveRoomDO {
    /**
     * 直播间id,自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 七牛云推流key
     */
    @Column(name = "stream_key")
    private String streamKey;

    /**
     * 七牛云回放名称
     */
    @Column(name = "stream_fname")
    private String streamFname;


    /**
     * 七牛云回放FLV名称
     */
    @Column(name = "stream_flvfname")
    private String streamFlvFname;

    /**
     * 七牛云推流url
     */
    @Column(name = "stream_url")
    private String streamUrl;

    /**
     * IMID（融云聊天室id）
     */
    @Column(name = "im_id")
    private String imId;

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
    @Column(name = "appoint_start")
    private Date appointStart;

    /**
     * 直播预约结束时间
     */
    @Column(name = "appoint_end")
    private Date appointEnd;

    /**
     * 直播开始时间
     */
    @Column(name = "live_start")
    private Date liveStart;

    /**
     * 直播结束时间
     */
    @Column(name = "live_end")
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
    @Column(name = "visitor_num")
    private Integer visitorNum;

    /**
     * 直播间创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 直播间修改时间
     */
    @Column(name = "gmt_modified")
    private Date gmtModified;

    /**
     * 主播的userId
     */
    @Column(name = "live_user_id")
    private Integer liveUserId;

    /**
     * 点赞数
     */
    @Column(name = "praise_count")
    private Integer praiseCount;

    /**
     * 是否删除：0为否，1为是
     */
    @Column(name = "is_deleted")
    private Integer isDeleted;

    /**
     * 扩展字段json
     */
    private String ext;

    /**
     * 是否展示在列表中，1为展示，0为不展示，默认为1
     */
    @Column(name = "is_public")
    private Integer isPublic;

    /**
     * 回放时长
     */
    @Column(name = "back_time")
    private String backTime;


    /**
     * 排序值
     */
    @Column(name = "rank")
    private Integer rank;

    /**
     * 开播类型 A B C D Z
     */
    @Column(name = "open_type")
    private String openType;

    /**
     * 主播的昵称
     */
    @Transient
    private String liveUserNickName;
    /**
     * 直播标签中文显示名
     */
    @Transient
    private String mark;

}
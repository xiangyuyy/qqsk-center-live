package com.jiebai.qqsk.live.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "tb_live_visitor")
public class TbLiveVisitorDO {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 观众用户的user_id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 直播间id
     */
    @Column(name = "room_id")
    private Integer roomId;

    /**
     * 首次进入直播间时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 扩展字段json, 记录观众行为和时间
     */
    @Column(name = "ext")
    private String ext;

}
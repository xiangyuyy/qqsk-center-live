package com.jiebai.qqsk.live.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author : eyestarrysky
 * @date : Created in 2020/2/29
 */
@Data
@Table(name = "tb_live_star")
public class TbLiveStarDO {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户Id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 标签
     */
    @Column(name = "user_tags")
    private String userTags;

    /**
     * 是否删除：0为否，1为是
     */
    @Column(name = "is_deleted")
    private Integer isDeleted;

    /**
     * 直播间修改时间
     */
    @Column(name = "gmt_modified")
    private Date gmtModified;
}

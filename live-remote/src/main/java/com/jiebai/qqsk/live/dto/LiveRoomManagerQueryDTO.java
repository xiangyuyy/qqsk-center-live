package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 后台直播列表搜索DTO
 *
 * @author lichenguang
 * 2019/11/13
 */
@Data
public class LiveRoomManagerQueryDTO implements Serializable {

    private static final long serialVersionUID = -7837903326144313341L;

    private Integer pageNum;

    private Integer pageSize;

    /**
     * 直播状态（1，未开始，2，直播中， 0，已结束）, 默认为1
     */
    private Integer state;

    /**
     * 标题
     */
    private String title;

    /**
     * 预留，分类名称
     */
    private String category;

    /**
     * 主播的userId
     */
    private Integer liveUserId;

    /**
     * 主播的昵称
     */
    private String liveUserNickName;

    /**
     * 是否展示在列表中，1为展示，0为不展示，默认为1
     */
    private Integer isPublic;

    /**
     * 是否删除：0为否，1为是
     */
    private Integer isDeleted;
}

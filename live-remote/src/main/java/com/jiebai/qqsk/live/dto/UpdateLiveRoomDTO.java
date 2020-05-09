package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 后台修改直播间dto
 *
 * @author cxy
 */
@Data
public class UpdateLiveRoomDTO implements Serializable {

    private static final long serialVersionUID = -67532726144313341L;

    /**
     * 直播间id,自增主键
     */
    private Integer id;

    /**
     * 封面地址
     */
    private String cover;

    /**
     * 直播预约开始时间
     */
    private Date appointStart;

    /**
     * 直播分类标签
     */
    private String name;

    /**
     * 是否展示在列表中，1为展示，0为不展示，默认为1
     */
    private Integer isPublic;

    /**
     * 标题
     */
    private String title;

}

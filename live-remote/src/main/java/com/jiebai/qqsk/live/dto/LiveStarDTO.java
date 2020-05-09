package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 直播红人
 * @author : eyestarrysky
 * date : Created in 2020/2/29
 */
@Data
public class LiveStarDTO implements Serializable {

    private static final long serialVersionUID = -7705383191137846541L;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户头像
     */
    private String headImgUrl;

    /**
     * 用户昵称
     */
    private String shopName;

    /**
     * 用户标签
     */
    private String userTags;

    /**
     * 用户直播列表
     */
    private List<LiveStarRoomDTO> liveStarRoomDTOList;

}

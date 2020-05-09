package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 直播间人员信息DTO
 *
 * @author cxy
 */
@Data
public class LiverUserDTO implements Serializable {

    private static final long serialVersionUID = -7837903326144313341L;
    /**
     * 自身userId
     */
    private Integer userId;

    /**
     * 被查看人自身userId
     */
    private Integer toUserId;
    /**
     * 头像路径
     */
    private String headimgurl;

    /**
     * 微信名
     */
    private String nickname;

    /**
     * 粉丝数
     */
    private Integer fans;

    /**
     * 点赞数
     */
    private Integer praises;

    /**
     * 是否关注 0未关注,1关注
     */
    private Boolean ifConcern;
}

package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 直播间观众DTO
 *
 * @author cxy
 */
@Data
public class LiverVisitorDTO implements Serializable {

    private static final long serialVersionUID = -7837903326144313341L;
    /**
     * 观众userId
     */
    private Integer userId;

    /**
     * 观众头像路径
     */
    private String headimgurl;

    /**
     * 观众微信名
     */
    private String nickname;
}

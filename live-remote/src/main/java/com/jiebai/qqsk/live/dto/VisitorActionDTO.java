package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 直播间观众行为DTO
 *
 * @author cxy
 */
@Data
public class VisitorActionDTO implements Serializable {

    private static final long serialVersionUID = -7837903326144313341L;
    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 动作（0，进入1，退出）
     */
    private int action;

    /**
     * 是否真实 1 真实 0  不真实
     */
    private Integer IsTrue;

}

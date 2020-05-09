package com.jiebai.qqsk.live.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 直播间公开状态枚举
 * @author lichenguang
 * @date 2019/12/24
 */
@Getter
@AllArgsConstructor
public enum LiveRoomPublicStatusEnum {

    /**
     * 直播间公开状态枚举
     */
    IS_PRIVATE(0, "私密"), IS_PUBLIC(1, "公开"),;

    private Integer status;

    private String description;
}

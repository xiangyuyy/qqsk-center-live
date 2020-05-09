package com.jiebai.qqsk.live.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lichenguang
 * 2019/11/15
 */
@Getter
@AllArgsConstructor
public enum DeleteFlagEnum {

    /** 删除枚举 **/
    NOT_DELETED(0, "未删除"), IS_DELETED(1, "已删除"),;

    private Integer code;

    private String description;
}

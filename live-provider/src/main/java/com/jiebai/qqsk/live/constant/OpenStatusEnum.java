package com.jiebai.qqsk.live.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务开通状态枚举
 * @author lichenguang
 * @date 2020/1/3
 */
@Getter
@AllArgsConstructor
public enum OpenStatusEnum {

    /**
     * 开通状态
     */
    NOT_OPEN(0, "未开通"), IS_OPEN(1, "开通"),
    ;

    private Integer status;

    private String description;
}

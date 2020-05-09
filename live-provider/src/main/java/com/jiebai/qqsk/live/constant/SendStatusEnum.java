package com.jiebai.qqsk.live.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lichenguang
 * 2019/11/25
 */
@AllArgsConstructor
@Getter
public enum  SendStatusEnum {

    /** 发送状态 **/
    NOT_SEND(0, "未发送"),
    IS_SEND(1, "已发送"),;

    private Integer status;

    private String desc;
}

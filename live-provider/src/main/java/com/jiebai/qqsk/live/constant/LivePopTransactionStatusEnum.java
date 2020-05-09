package com.jiebai.qqsk.live.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * pop店铺交易状态枚举
 * @author lichenguang
 * date 2019/12/23
 */
@Getter
@AllArgsConstructor
public enum LivePopTransactionStatusEnum {

    /**
     * 结算，待结算
     */
    WAIT_RECEIVE(0, "待结算/待收货"), RECEIVED(1, "结算/收货"),;

    private Integer status;

    private String description;
}

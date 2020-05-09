package com.jiebai.qqsk.live.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * pop店铺交易类型枚举
 * @author lichenguang
 * date 2019/12/23
 */
@Getter
@AllArgsConstructor
public enum LivePopTransactionTypeEnum {

    /**
     * 买家支付，买家退款，店主提现
     */
    BUYER_PAY(0, "买家支付"),
    BUYER_REFUND(1, "买家退款"), POP_WITHDRAW(2, "pop店主提现"),;

    private Integer type;

    private String description;
}

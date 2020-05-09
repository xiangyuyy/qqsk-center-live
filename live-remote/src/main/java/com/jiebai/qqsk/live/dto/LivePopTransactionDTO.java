package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author lcg
 * date 2019/12/23
 */
@Data
public class LivePopTransactionDTO implements Serializable {

    private static final long serialVersionUID = -7288107208943254724L;

    /**
     * pop订单号
     */
    private String orderNo;

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 0为支付，1为退款，2为提现
     */
    private Integer type;

}
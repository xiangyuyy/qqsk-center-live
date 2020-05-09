package com.jiebai.qqsk.live.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author lichenguang
 * @date 2019/12/23
 */
@AllArgsConstructor
@Data
public class PopHomePageDTO implements Serializable {

    private static final long serialVersionUID = -709856982478108908L;
    /**
     * 保证金
     */
    private BigDecimal promiseMoney;

    /**
     * 账户余额
     */
    private BigDecimal accountRemain;

    /**
     * 待结算金额
     */
    private BigDecimal waitReceiveMoney;

    /**
     * 今日订单数
     */
    private Integer todayOrderCount;

    /**
     * 今日销售额
     */
    private BigDecimal todaySaleMoney;

    /**
     * 标识是否弹出补充信息提示框
     */
    private Integer addInformation;

    /**
     * 审核提示
     */
    private Integer auditHints;

    /**
     * 审核状态
     */
    private String status;

    /**
     * 审核失败原因
     */
    private String reason;
}

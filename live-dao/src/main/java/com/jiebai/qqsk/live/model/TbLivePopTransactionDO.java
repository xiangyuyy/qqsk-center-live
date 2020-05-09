package com.jiebai.qqsk.live.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lcg
 * @date 2019/12/23
 */
@Data
@Table(name = "tb_live_pop_transaction")
public class TbLivePopTransactionDO {
    /**
     * 主键id
     */
    @Id
    @Column(name = "pop_transaction_id")
    private Long popTransactionId;

    /**
     * pop订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 退款单号
     */
    @Column(name = "refund_no")
    private String refundNo;

    /**
     * pop店主的user_id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 金额，正数为收入，负数为支出
     */
    private BigDecimal money;

    /**
     * 当前余额 （加了本笔money之后的金额）
     */
    private BigDecimal balance;

    /**
     * 0为支付，1为退款，2为提现
     */
    private Integer type;

    /**
     * 记录创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @Column(name = "gmt_modify")
    private Date gmtModify;
}
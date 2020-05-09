package com.jiebai.qqsk.live.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by lichenguang
 * @author lichenguang
 * @version v1.0.0
 * @date 2020/02/17 18:16:50
 */
@Data
@Table(name = "tb_live_popshop_log")
public class TbLivePopshopLogDO {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * popshop的主键id
     */
    @Column(name = "popshop_id")
    private Integer popshopId;

    /**
     * 操作人姓名
     */
    @Column(name = "operate_name")
    private String operateName;

    /**
     * 操作人id
     */
    private String operator;

    /**
     * 备注（原因）
     */
    private String remark;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 1:提交复审申请 2:审核不通过3:审核通过
     */
    @Column(name = "status")
    private Integer status;
}
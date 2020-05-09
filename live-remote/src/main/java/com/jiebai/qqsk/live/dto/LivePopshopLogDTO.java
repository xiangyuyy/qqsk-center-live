package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by lichenguang
 * @author lichenguang
 * @version v1.0.0
 * @date 2020/02/18 11:16:50
 */
@Data
public class LivePopshopLogDTO implements Serializable {

    private static final long serialVersionUID = 2835483092332279033L;

    private Long id;

    /**
     * popshop的主键id
     */
    private Integer popshopId;

    /**
     * 操作人姓名
     */
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
     * 1:提交复审申请 2:审核不通过3:审核通过
     */
    private Integer status;

    /**
     * 审核状态，0为通过，1为不通过
     */
    private Integer passStatus;

    private Date gmtCreate;
}
package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lilin
 * @date 2020-02-17
 */
@Data
public class LivePopShopDTO implements Serializable {
    private static final long serialVersionUID = -743917396539142L;
    private Integer id;

    /**
     * userId
     */
    private Integer userId;

    /**
     * 店铺名称
     */
    private String popshopName;

    /**
     * 店铺类型
     */
    private String popshopType;

    /**
     * 店铺分类
     */
    private String popshopCategory;

    /**
     * 省
     */
    private String provinceName;

    /**
     * 市
     */
    private String cityName;

    /**
     * 区
     */
    private String districtName;

    /**
     * 详细地址
     */
    private String streetAddress;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 身份证正面照
     */
    private String idCardFrontPhoto;

    /**
     * 身份证背面照
     */
    private String idCardBackPhoto;

    /**
     * 身份证手持照
     */
    private String idCardHandPhoto;
    /**
     * 用户名
     */
    private String userName;

    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 营业执照
     */
    private String businessLicense;

    /**
     * 三证合一 0：否 1：是
     */
    private Integer combinationOfThreeCertificates;

    /**
     * 审核状态 0:未审核 1:审核不通过 2:审核不通过-待复审 3:审核通过
     */
    private String status;

    /**
     * 创建时间
     */
    private Date gmtCreate;


    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 区分是普通还是再次提交审核
     */
    private String type;
}

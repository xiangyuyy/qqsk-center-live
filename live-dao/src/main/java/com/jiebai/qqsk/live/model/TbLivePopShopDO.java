package com.jiebai.qqsk.live.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "tb_live_popshop")
public class TbLivePopShopDO {
    /**
     * id,自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * userId
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 保证金
     */
    @Column(name = "promise_money")
    private BigDecimal promiseMoney;

    /**
     * 账户余额
     */
    @Column(name = "account_remain")
    private BigDecimal accountRemain;

    /**
     * 是否开通直播 0-否 1-是
     */
    @Column(name = "is_open_live")
    private Integer isOpenLive;

    /**
     * 是否开通pop店 0-否 1-是
     */
    @Column(name = "is_open_popshop")
    private Integer isOpenPopShop;

    /**
     * 是否关闭直播 0-否 1-是
     */
    @Column(name = "is_close_live")
    private Integer isCloseLive;

    /**
     * 是否关闭pop店 0-否 1-是
     */
    @Column(name = "is_close_popshop")
    private Integer isClosePopShop;

    /**
     * 店铺名称
     */
    @Column(name = "popshop_name")
    private String popshopName;

    /**
     * 店铺类型
     */
    @Column(name = "popshop_type")
    private String popshopType;

    /**
     * 店铺分类
     */
    @Column(name = "popshop_category")
    private Integer popshopCategory;

    /**
     * 省
     */
    @Column(name = "province_name")
    private String provinceName;

    /**
     * 市
     */
    @Column(name = "city_name")
    private String cityName;

    /**
     * 区
     */
    @Column(name = "district_name")
    private String districtName;

    /**
     * 详细地址
     */
    @Column(name = "street_address")
    private String streetAddress;

    /**
     * 电话
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 邮箱
     */
    @Column(name = "email")
    private String email;

    /**
     * 身份证正面照
     */
    @Column(name = "id_card_front_photo")
    private String idCardFrontPhoto;

    /**
     * 身份证背面照
     */
    @Column(name = "id_card_back_photo")
    private String idCardBackPhoto;

    /**
     * 身份证手持照
     */
    @Column(name = "id_card_hand_photo")
    private String idCardHandPhoto;
    /**
     * 用户名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 身份证号
     */
    @Column(name = "id_card")
    private String idCard;
    /**
     * 企业名称
     */
    @Column(name = "enterprise_name")
    private String enterpriseName;

    /**
     * 营业执照
     */
    @Column(name = "business_license")
    private String businessLicense;

    /**
     * 三证合一
     */
    @Column(name = "combination_of_three_certificates")
    private Integer combinationOfThreeCertificates;

    /**
     * 审核状态 0:未审核 1:审核不通过 2:审核不通过-待复审 3:审核通过
     */
    @Column(name = "status")
    private String status;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 关闭pop店时间时间
     */
    @Column(name = "gmt_close")
    private Date gmtClose;

    /**
     * 修改时间
     */
    @Column(name = "gmt_modified")
    private Date gmtModified;

    /**
     *  开通pop店时间
     */
    @Column(name = "gmt_open_popshop")
    private Date gmtOpenPopShop;

    @Transient
    private String nickname;
    @Transient
    private String loginMobile;
    @Transient
    private String userMemberRole;
}
package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class TbLivePopShopManagerDTO implements Serializable {

    private static final long serialVersionUID = 9066775323335370662L;
    /**
     * id,自增主键
     */
    private Integer id;

    /**
     * userId
     */
    private Integer userId;

    /**
     * 保证金
     */
    private BigDecimal promiseMoney;

    /**
     * 账户余额
     */
    private BigDecimal accountRemain;

    /**
     * 是否开通直播 0-否 1-是
     */
    private Integer isOpenLive;

    /**
     * 是否开通pop店 0-否 1-是
     */
    private Integer isOpenPopShop;

    /**
     * 是否关闭直播 0-否 1-是
     */
    private Integer isCloseLive;

    /**
     * 是否关闭pop店 0-否 1-是
     */
    private Integer isClosePopShop;

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
     * 三证合一
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
     * 关闭pop店时间时间
     */
    private Date gmtClose;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     *  开通pop店时间
     */
    private Date gmtOpenPopShop;

    private String nickname;
    private String loginMobile;
    private String userMemberRole;
    /**
     *  直播次数
     */
    private int openLives;

    /**
     * 审核记录
     */
    private List<LivePopshopLogDTO> logList;
}
package com.jiebai.qqsk.live.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 后台主播搜索DO
 *
 * @author cxy
 */
@Data
public class LivePopShopManagerQueryDO implements Serializable {

    private static final long serialVersionUID = -7837903326144313341L;

    /**
     * 主播信息
     */
    private String nickname;

    /**
     * 等级
     */
    private String userMemberRole;

    /**
     * 是否开通pop店 0-否 1-是
     */
    private Integer isOpenPopShop;

    /**
     * 店铺类型
     */
    private String popshopType;

    /**
     * 审核状态 0:未审核 1:审核不通过 2:审核不通过-待复审 3:审核通过
     */
    private String status;

    /**
     * 成为主播时间
     */
    private Date gmtCreateBegin;

    /**
     * 成为主播时间
     */
    private Date gmtCreateEnd;

    /**
     * 开通pop店时间
     */
    private Date gmtOpenPopShopBegin;

    /**
     * 开通pop店时间
     */
    private Date gmtOpenPopShopEnd;
}

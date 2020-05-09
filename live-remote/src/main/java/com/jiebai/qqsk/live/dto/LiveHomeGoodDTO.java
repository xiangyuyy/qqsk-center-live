package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 直播首页列表商品DTO
 *
 * @author cxy
 */
@Data
public class LiveHomeGoodDTO implements Serializable {

    private static final long serialVersionUID = -7837903326144313341L;
    /**
     * 商品spu
     */
    private String spu;
    /**
     * 价格
     */
    private String price;

    /**
     * 产品标题
     */
    private String spuTitle;
    /**
     * 头图
     */
    private String spuImage;

    /**
     * 商品spuId
     */
    private Integer spuId;

    /**
     * 商品spucode
     */
    private String spuCode;

    /**
     * 展示状态：0没操作，1待讲解，2讲解中
     */
    private Integer showState;
    /**
     * 商品返利金额
     */
    private String rebateAmount;
    private String bonus;

}

package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lichenguang
 * 2019/11/14
 */
@Data
public class LiveGoodsDTO implements Serializable {

    private Long id;

    /**
     * 主播的userId
     */
    private Integer userId;

    /**
     * 直播间id,自增主键
     */
    private Integer roomId;

    /**
     * 商品spuCode
     */
    private String spuCode;


    /**
     * 展示状态：0没操作，1待讲解，2讲解中
     */
    private Integer showState;
}

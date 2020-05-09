package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lichenguang
 * 2019/11/21
 */
@Data
public class SaveLiveGoodsDTO implements Serializable {

    private static final long serialVersionUID = -8791356730174489204L;
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
    private List<String> spuCodeList;
}

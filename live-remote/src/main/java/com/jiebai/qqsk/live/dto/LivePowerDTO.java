package com.jiebai.qqsk.live.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 直播权限实体
 * @author lichenguang
 * date 2019/12/20
 */
@Data
@AllArgsConstructor
public class LivePowerDTO implements Serializable {

    /**
     * 是否开通直播
     */
    Boolean isOpenLive;

    /**
     * 是否开通pop店铺
     */
    Boolean isOpenPop;

    /**
     * 店铺是否通过审核
     */
    Boolean isPassCheck;
}

package com.jiebai.qqsk.live.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lichenguang
 * 2019/11/15
 */
@Getter
@AllArgsConstructor
public enum LiveStatusEnum {

    /** 直播状态描述 **/
    LIVE_NOT_START(1, "未开始"), LIVE_STARTING(2, "进行中"), LIVE_OVER(0, "已结束"),
    ;

    private Integer status;

    private String description;
}

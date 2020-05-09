package com.jiebai.qqsk.live.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author cxy
 */
@AllArgsConstructor
@Getter
public enum AddVisitorsKindEnum {

    /** 增加人气种类 **/
    OPEN_VISITORS(0, "开直播打底"),
    SHARE(1, "分享直播间"),
    ADDPRAISE_FOLLOW(2, "点赞或者被关注"),
    BULLET_CHAT(3, "弹幕增加"),;

    private Integer kind;

    private String desc;
}

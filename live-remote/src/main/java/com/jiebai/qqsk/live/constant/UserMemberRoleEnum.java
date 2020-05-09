package com.jiebai.qqsk.live.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lichenguang
 * @date 2019/11/29
 */
@Getter
@AllArgsConstructor
public enum UserMemberRoleEnum {

    /** 用户等级 **/
    GUEST("GUEST", "游客"),
    FANS("FANS", "金卡"),
    NORMAL("NORMAL", "黑卡"),
    ULTIMATE("ULTIMATE", "旗舰店"),
    MANAGER("MANAGER", "大客户经理"),
    NORMAL_688("NORMAL", "688会员");

    private String role;

    private String description;
}

package com.jiebai.qqsk.live.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 直播间开播类型
 * @author cxy
 * @date 2020/1/3
 */
@Getter
@AllArgsConstructor
public enum LiveOpenTypeEnum {

    A("A"), B("B"),C("C"),D("D"),Z("Z");

    private String  type;
}

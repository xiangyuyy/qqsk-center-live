package com.jiebai.qqsk.live.enums;

/**
 * @author lilin
 * @date 2020-02-18
 */
public enum UpdateTypeEnum {
    NORMAL("NORMAL","正常"),
    VERIFY("VERIFY","审核")
    ;

    private String type;
    private String msg;
    UpdateTypeEnum(String type,String msg){
        this.type = type;
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }
}


package com.jiebai.qqsk.live.enums;

/**
 * @author lilin
 * @date 2020-02-18
 */
public enum PopshopStatusEnum {
    ZERO("0","未审核"),
    ONE("1","审核不通过-待复审"),
    TWO("2","审核不通过"),
    THREE("3","审核通过")

    ;
    private String status;
    private String msg;
    PopshopStatusEnum(String status,String msg){
        this.status = status;
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}

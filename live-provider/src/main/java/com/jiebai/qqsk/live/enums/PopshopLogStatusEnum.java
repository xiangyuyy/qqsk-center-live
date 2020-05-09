package com.jiebai.qqsk.live.enums;

/**
 * @author cxy
 */
public enum PopshopLogStatusEnum {
    ONE(1,"提交复审申请"),
    TWO(2,"审核不通过"),
    THREE(3,"审核通过")

    ;
    private Integer status;
    private String msg;
    PopshopLogStatusEnum(Integer status, String msg){
        this.status = status;
        this.msg = msg;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}

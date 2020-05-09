package com.jiebai.qqsk.live.enums;

/**
 * @author lilin
 * @date 2020-02-17
 * pop店铺类型枚举
 */
public enum PopshopTypeEnum {
    PERSONAL("PERSONAL","个人"),
    INDIVIDUAL_BUSINESS("INDIVIDUAL_BUSINESS","个体工商户"),
    ENTERPRISE("ENTERPRISE","企业")
    ;
    private String type;
    private String msg;
    PopshopTypeEnum(String type,String msg){
        this.type = type;
        this.msg = msg;
    }
    public String getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public static String getMsgByType(String type){
        if(type!=null && !"".equals(type)){
            for(PopshopTypeEnum item : PopshopTypeEnum.values()){
                if(item.getType().equals(type)){
                    return item.getMsg();
                }
            }
        }
        return null;
    }
}

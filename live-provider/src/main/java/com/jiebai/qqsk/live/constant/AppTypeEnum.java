package com.jiebai.qqsk.live.constant;

/**
 * AppTypeEnum : 程序类型.
 * @author : lichenguang
 * @date : 2019/12/12 15:03
 */
public enum AppTypeEnum {

    /**
     * 苛选商城小程序
     */
    WXMA_KXSC("WXMA_KXSC", "wxcc59f87a8b37d57f", "苛选商城小程序", 4),
    /**
     * 精选商城小程序
     */
    WXMA_JXSC("WXMA_JXSC", "wx918508b1bf9580f5", "精选商城小程序", 4),
    /**
     * 买手商城（高拥商城）小程序
     */
    WXMA_MSSC("WXMA_MSSC", "wxf7dd4cdddfe664ed", "买手商城小程序", 5),
    /**
     * 时刻团小程序
     */
    WXMA_SKT("WXMA_SKT", "wx9082161c2fb8ed61", "时刻团小程序", 4),
    // 暂时写4
    WXMA_SQTG("WXMA_SQTG", "wx9082161c2fb8ed61", "社区团购小程序", 4),
    /**
     * 中通创客小程序
     */
    WXMA_ZTCK("WXMA_ZTCK", "", "中通创客小程序", 4),
    /**
     * 中通快运小程序
     */
    WXMA_ZTKY("ZTKY", "wxd815276ff0497587", "中通快运小程序", 4),
    /**
     * 为邻精选小程序
     */
    WXMA_WLJX("WLJX", "wxc141a90333870054", "为邻精选小程序", 4),
    /**
     * 全球时刻公众号
     */
    WXMQ_QQSK("WXMQ_QQSK", "wxd8cdbabd89b10019", "全球时刻公众号", 1),
    /**
     * 全球时刻优选公众号
     */
    WXMQ_QQSKYX("WXMQ_QQSKYX", "wx3ef889e2c9de7363", "全球时刻优选公众号", 1),
    /**
     * 中通创客公众号
     */
    WXMQ_ZTCK("WXMQ_ZTCK", "wx0073308c30a3a207", "中通创客公众号", 0),
    /**
     * 全球时刻APP-安卓
     */
    QQSK_ANDROID("QQSK_ANDROID", "", "全球时刻APP-安卓", 2),
    /**
     * 全球时刻APP-IOS
     */
    QQSK_IOS("QQSK_IOS", "", "全球时刻APP-IOS", 3),
    

    ;

    private String type;
    private String appId;
    private String desc;
    private int client;

    AppTypeEnum(String type, String appId, String desc, int client) {
        this.type = type;
        this.appId = appId;
        this.desc = desc;
        this.client = client;
    }

    public static AppTypeEnum getByType(String type) {
        for (AppTypeEnum bs : AppTypeEnum.values()) {
            if (bs.getType().equals(type)) {
                return bs;
            }
        }
        return null;
    }

    public static AppTypeEnum getByAppId(String appId) {
        for (AppTypeEnum bs : AppTypeEnum.values()) {
            if (bs.getAppId().equals(appId)) {
                return bs;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public String getAppId() {
        return appId;
    }

    public String getDesc() {
        return desc;
    }

    public int getClient() {
        return client;
    }
}

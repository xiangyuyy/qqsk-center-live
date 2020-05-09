package com.jiebai.qqsk.live.constant;

/**
 * 一些不好归类的常量
 * @author lichenguang
 * @date 2020/1/13
 */
public class LiveConstant {

    /**
     * 发现页展示主播头像数目
     */
    public final static int HEAD_IMAGE_COUNT = 3;

    /**
     * 直播列表展示的商品图片数目
     */
    public static final Integer LiVEROOM_SPU_SHOW_MAX_COUNT = 3;

    /**
     * 延迟时间处理长时间不开播的直播间
     */
    public static final int DELAY_SECOND = 60 * 10;

    /**
     * key有效期默认置为2小时
     */
    public final static Integer EXPIRE_SECOND = 60 * 60 * 2;
    /**
     * 直播间历史观众redis key
     */
    public final static String VISITORSREDISKEY = "QQSK:HISTORYLIVEVISITORS";

    /**
     * 直播间定时刷进入消息的redis List key 定时任务
     */
    public final static String ONLIVEREDISLISTKEY = "QQSK:ONLIVEREDISLISTKEY";

    /**
     * 直播间定时刷进入消息的redis List key DELAY 延迟任务  是否发送消息判断redis key
     */
    public final static String ONLIVEREDISDELAYLISTKEY = "QQSK:ONLIVEREDISDELAYLISTKEY";


    /**
     * 直播间定时刷进入消息的redis key 表示是否初始化 多台服务器防止每台服务器都初始化延迟队列
     */
    public final static String ONLIVEREDISINITTKEY = "QQSK:ONLIVEREDISINITTKEY";

}

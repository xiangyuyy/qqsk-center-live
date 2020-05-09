package com.jiebai.qqsk.live.config;

/**
 * 云配置
 *
 * @author cxy
 */
public class YunConfig {

    //七牛云配置
    public static final String qiniu_accessKey = "6mhJMasP5ltZsrh85n3V6bfpj4ZPsNKFsZJiLwmt";
    public static final String qiniu_secretKey = "i_HBZDX5ZwsBCzDJWzxbu55afbE6zW67GhOFuKTN";
    public static final String qiniu_hubName = "globaltime";
    public static final String qiniu_streamKeyPrefix = "QQSK-ROOM";
    public static final String qiniu_signkey = "masterpw";

    //RTMP推流域名
    public static final String qiniu_RTMPPublishDomain = "pili-publish.qqsk.com";
    //RTMP播流域名
    public static final String qiniu_RTMPPlayURL = "pili-live-rtmp.qqsk.com";
    //HLS播流域名
    public static final String qiniu_HLSPlayURL = "pili-live-hls.qqsk.com";
    //HDL播流域名
    public static final String qiniu_HDLPlayURL = "pili-live-hdl.qqsk.com";
    //SnapshotPlayURL 直播封面
    public static final String qiniu_SnapshotPlayURL = "pili-snapshot.qqsk.com";
    //存储空间地址 七牛后台设置
    public static final String qiniu_downloadDomainOfStorageBucket = "pili-vod.qqsk.com";


    //融云配置
/*    public static final String rongyun_appKey = "lmxuhwagl5hqd";
    public static final String rongyun_appSecret = "007P1uTIEJ";*/
    public static final String rongyun_api = "";
    public static final String rongyun_imKeyPrefix = "QQSK-IM";

}

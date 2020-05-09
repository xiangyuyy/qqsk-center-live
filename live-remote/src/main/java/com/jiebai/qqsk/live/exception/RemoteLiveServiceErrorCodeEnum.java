package com.jiebai.qqsk.live.exception;

/**
 * remoteService 错误码枚举
 * @author lichenguang
 * @version 1.0.0
 * @date 2019/11/13
 */
public enum RemoteLiveServiceErrorCodeEnum {

    /** 处理错误枚举 **/
    BUSINESS_HANDLING_FAIL("BUSINESS_HANDLING_FAIL", "业务处理失败"),
    ROOM_ID_NOT_NULL("ROOM_ID_NOT_NULL", "直播间id不能为空"),
    LIVE_USER_ENTITY_NOT_NULL("LIVE_USER_ENTITY_NOT_NULL", "直播间主播找不到数据"),
    ROOM_ENTITY_NOT_NULL("ROOM_ENTITY_NOT_NULL", "直播间找不到数据"),
    IM_LIVE_JOIN_MAX("IM_LIVE_JOIN_MAX", "达到当天融云日活最大值"),
    EXISET_ROOM_NOT_OVER("EXISET_ROOM_STARTING", "有未结束的直播，不能预约!"),
    EXISET_IN_LIVE_GOODS("EXISET_IN_LIVE_GOODS","直播间橱窗已经有该商品了，请勿重复添加"),
    QINIU_GET_STREAM_URL_FAILURE("QINIU_GET_STREAM_URL_FAILURE", "获取七牛云推流地址失败!"),
    RONGYUN_KEEPALIVE_FAILURE("RONGYUN_KEEPALIVE_FAILURE", "融云保活失败!"),
    LIVE_GOODS_CANNOT_DELETED("LIVE_GOODS_CANNOT_DELETED", "橱窗商品至少得有一件!"),
    CREATE_STREAM_FAIL("CREATE_STREAM_FAIL", "创建七牛云推流地址失败"),
    NOT_FOLLOW_SELF("NOT_FOLLOW_SELF", "不能关注自己"),
    GET_IMID_FAIL("GET_IMID_FAIL", "获取imId失败"),
    LIVE_ROOM_IS_DELETED("LIVE_ROOM_IS_DELETED", "开播失败, 超过预约直播时间10分钟以上，直播间被关闭!"),
    LIVE_GOODS_COUNT_REACH_LIMIT("LIVE_GOODS_COUNT_REACH_LIMIT", "橱窗最多添加30件商品!"),
    LIVE_ROOM_NOT_END("LIVE_ROOM_NOT_END", "不能删除未结束的直播间!"),
    LIVE_ROOM_NOT_EXIST("LIVE_ROOM_NOT_EXIST", "直播间不存在!"),
    LIVE_ROOM_NOT_VISITORS("LIVE_ROOM_NOT_VISITORS", "直播间修改人气值数据不正确!"),
    LIVE_ROOM_LIVE_CATEGORY_MORE("LIVE_ROOM_LIVE_CATEGORY_MORE", "直播间分类数据错误!"),
    LIVE_EXIST_SUBSCRIBE("LIVE_EXIST_SUBSCRIBE", "处理失败，您已经预约过该直播间了!"),
    LIVE_IS_OPEN("LIVE_IS_OPEN","直播间已经开通了，无需再次开通!"),
    LIVE_IS_NOT_OPEN("直播权限未开通","直播权限未开通")
    ;

    private String errorCode;
    private String message;

    RemoteLiveServiceErrorCodeEnum(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}

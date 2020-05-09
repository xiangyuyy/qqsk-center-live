package com.jiebai.qqsk.live.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * dubbo服务业务处理异常
 *
 * @author lichenguang
 * @version 1.0.0
 * @date 2019/7/25 9:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RemoteLiveServiceException extends RuntimeException implements Serializable {

    /**
     * 错误代码
     */
    private String errorCode;

    private RemoteLiveServiceErrorCodeEnum errorCodeEnum;


    public RemoteLiveServiceException(String message) {
        super(message);
    }

    public RemoteLiveServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMessage());
        this.errorCode = errorCodeEnum.getErrorCode();
        this.errorCodeEnum = errorCodeEnum;
    }

}

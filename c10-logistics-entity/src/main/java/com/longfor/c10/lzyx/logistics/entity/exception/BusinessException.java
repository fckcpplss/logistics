package com.longfor.c10.lzyx.logistics.entity.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述:自定义统一的异常
 *
 * @author wanghai03
 * @date 2021/10/28 下午8:36
 */
@Getter
@Setter
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = -2054023852066901556L;
    private int errorCode;

    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(Throwable e) {
        super(e);
    }
}

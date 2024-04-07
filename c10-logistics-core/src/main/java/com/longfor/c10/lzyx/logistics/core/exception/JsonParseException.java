package com.longfor.c10.lzyx.logistics.core.exception;

/**
 * @author liuqinglin
 * @date 2021/11/8
 **/
public class JsonParseException extends RuntimeException {
    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(Throwable e) {
        super(e);
    }
}

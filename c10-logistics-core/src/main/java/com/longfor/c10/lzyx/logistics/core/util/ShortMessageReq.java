package com.longfor.c10.lzyx.logistics.core.util;

import lombok.Data;

/**
 * 发送短信请求对象
 * @author renwei03
 */
@Data
public class ShortMessageReq {
    private String channelCode;
    private String prodLineCode;
    private SingleTypeMsg singleTypeMsg;
}

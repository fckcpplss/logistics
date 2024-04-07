package com.longfor.c10.lzyx.logistics.core.util;

import lombok.Data;

/**
 * 发送云图梭请求对象
 * @author renwei03
 */
@Data
public class PushMessageReq {
    private String channelCode;
    private String prodLineCode;
    private PushSingleTypeMsg singleTypeMsg;
}

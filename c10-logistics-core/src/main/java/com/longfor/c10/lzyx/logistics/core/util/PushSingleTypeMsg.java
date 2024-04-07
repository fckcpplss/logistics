package com.longfor.c10.lzyx.logistics.core.util;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 发送云图梭对象
 */
@Data
public class PushSingleTypeMsg {
    private Map<String, Object> configParams;
    private List<Object> userIds;
    private String pushContent;
}

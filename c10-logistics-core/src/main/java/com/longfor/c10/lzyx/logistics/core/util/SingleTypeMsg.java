package com.longfor.c10.lzyx.logistics.core.util;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 发送短信对象
 */
@Data
public class SingleTypeMsg {
    private Map<String, Object> configParams;
    private List<Object> userIds;
    private String msgTemplateId;
    private String pushApiKey;
}

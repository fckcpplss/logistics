package com.longfor.c10.lzyx.logistics.core.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description: 打印面单京东配置
 * @author: zhaoyalong
 */
@Data
@Component
@ConfigurationProperties(prefix = "ebill.jd")
public class EBillJDConfig {

    private String appKey;

    private String appSecret;

    private String refreshToken;

    private String customerCode;
    //京东地址
    private String serverUrl;

    @Value("${ebill.jd.wsSocketUrl:ws://localhost:9113}")
    private String wsSocketUrl;
}

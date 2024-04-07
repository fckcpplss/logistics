package com.longfor.c10.lzyx.logistics.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description: 打印面单顺丰配置
 * @author: zhaoyalong
 */
@Data
@Component
@ConfigurationProperties(prefix = "ebill.sf")
public class EBillSFConfig {

    //模板编码
    private String templateCode;

    //顾客编码
    private String clientCode;

    //校验码
    private String checkWord;

    //请求地址
    private String callUrl;
}
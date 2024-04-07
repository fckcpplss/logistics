package com.longfor.c10.lzyx.logistics.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author lanxiaolong
 * @Date 2022/4/15 6:23 下午
 */
@Data
@Component
@ConfigurationProperties(prefix = "logistics.pick.up.send")
public class PickUpNoticePropertiesConfig {

    private String operatorOaNumber;

    private String operatorPhoneNumber;

    private String userMessageTaskCode;

    private String userMessageLastTaskCode;

    private String operatorMessageTaskCode;

    private String userPushTaskCode;

    private String userPushLastTaskCode;

    private String operatorPushTaskCode;
}

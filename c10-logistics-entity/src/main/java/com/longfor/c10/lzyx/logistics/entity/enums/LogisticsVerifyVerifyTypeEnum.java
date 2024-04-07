package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 自提/核销核销类型
 *
 * @author zhaoyalong
 */
@Getter
@AllArgsConstructor
public enum LogisticsVerifyVerifyTypeEnum {

    VERIFY_USER(0, "用户端核销"),
    VERIFY_ADMIN(1, "运营端核销");

    private final Integer code;

    private final String desc;

    public static LogisticsVerifyVerifyTypeEnum fromCode(int code) {
        return Arrays.stream(LogisticsVerifyVerifyTypeEnum.values()).filter(x -> x.getCode() == code).findFirst().orElse(null);
    }
}

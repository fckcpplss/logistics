package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 自提/核销核销状态
 *
 * @author zhaoyalong
 */
@Getter
@AllArgsConstructor
public enum LogisticsVerifyVerifyStatusEnum {

    VERIFY_NO(0, "待核销"),
    VERIFY_SUCCESS(1, "核销成功"),
    VERIFY_FAIL(2, "核销失败"),
    VERIFY_TIMEOUT(3, "核销超时");;

    private final Integer code;

    private final String desc;

    public static LogisticsVerifyVerifyStatusEnum fromCode(int code) {
        return Arrays.stream(LogisticsVerifyVerifyStatusEnum.values()).filter(x -> x.getCode() == code).findFirst().orElse(null);
    }
}

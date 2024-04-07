package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 物流流程类型
 *
 * @author zhaoyalong
 */
@Getter
@AllArgsConstructor
public enum SendReturnTypeEnum {

    FORWARD(1, "正向"),
    BACKWARD(2, "逆向");

    private final Integer code;

    private final String desc;

    public static SendReturnTypeEnum fromCode(int code) {
        return Arrays.stream(SendReturnTypeEnum.values()).filter(x -> x.getCode() == code).findFirst().orElse(null);
    }
}

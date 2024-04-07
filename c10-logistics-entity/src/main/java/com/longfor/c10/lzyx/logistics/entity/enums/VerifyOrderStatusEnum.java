package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 自提/核销订单状态
 * @author zhaoyalong
 **/
@Getter
@AllArgsConstructor
public enum VerifyOrderStatusEnum {
    WAIT_PICKUP(16,"待提货"),

    IS_PICKUP(17,"已提货"),

    PICKUP_TIMEOUT(18,"提货超期"),

    WAIT_VERIFY(20,"待核销"),

    IS_VERIFY(21,"已核销"),

    VERIFY_TIMEOUT(22,"核销超期"),
    ;
    private final Integer code;

    private final String desc;


    public static VerifyOrderStatusEnum fromCode(int code) {
        return Arrays.stream(VerifyOrderStatusEnum.values())
                .filter(x -> x.getCode() == code)
                .findAny()
                .orElse(null);
    }

}

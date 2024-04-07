package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AddressOwnerTypeEnum {
    /**
     * 地址类型
     */
    USER(0, "用户收货地址"),
    SHOP(1, "商铺发货地址"),
    STORE(2, "项目发货地址");
    private final Integer code;
    private final String message;

    public static AddressOwnerTypeEnum fromCode(Integer code) {
        return Arrays.stream(AddressOwnerTypeEnum.values()).filter(e -> e.getCode().equals(code)).findFirst()
                .orElse(null);
    }

}

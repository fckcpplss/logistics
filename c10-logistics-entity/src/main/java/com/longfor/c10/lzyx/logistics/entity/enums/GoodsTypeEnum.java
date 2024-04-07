package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

/**
 * @description: 商品类型枚举
 * @author zhaoyalong
 */
@ToString
@Getter
@AllArgsConstructor
public enum GoodsTypeEnum {
    GOODS(1, "商品"),
    CARD(2, "卡卷");

    private final Integer code;
    private final String desc;

    public static GoodsTypeEnum fromCode(int code) {
        return Arrays.stream(GoodsTypeEnum.values())
                .filter(x -> x.getCode() == code)
                .findAny()
                .orElse(null);
    }
}

package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

/**
 * @description: 顺丰产品类别
 * @author: jiamingqiang
 * @date: 2021/11/23
 */
@ToString
@Getter
@AllArgsConstructor
public enum SfProductTypeEnum {
    /**
     * 顺丰产品类型
     */
    SFTK( "1", "顺丰特快", "1"),
    SFBK( "2", "顺丰标快", "1"),
    SFJR( "6", "顺丰即日", "1"),
    DZXZD("16", "大闸蟹专递", "1"),
    THZP( "208","特惠专配", "0"),
    LYBG( "231", "陆运包裹", "1"),
    DSBK( "247", "电商标快", "0");

    private final String code;
    private final String desc;
    private final String isDoCall;

    public static SfProductTypeEnum fromCode(String code) {
        return Arrays.stream(SfProductTypeEnum.values())
                .filter(x -> x.getCode().equals(code))
                .findAny()
                .orElse(null);
    }
}

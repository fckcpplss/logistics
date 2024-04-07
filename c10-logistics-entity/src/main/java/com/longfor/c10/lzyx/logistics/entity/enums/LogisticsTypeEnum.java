package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 商品级物流费用承担方 1、本组织 2、供应商
 *
 * @author zhaoyalong
 */
@Getter
@AllArgsConstructor
public enum LogisticsTypeEnum {

    SELF_PLAT(1, "平台"),
    SHOP(2, "商户"),
    OTHER(-1, "未知");

    private final Integer code;

    private final String desc;

    public static LogisticsTypeEnum fromCode(int code) {
        return Arrays.stream(LogisticsTypeEnum.values()).filter(x -> x.getCode() == code).findFirst().orElse(LogisticsTypeEnum.OTHER);
    }
}

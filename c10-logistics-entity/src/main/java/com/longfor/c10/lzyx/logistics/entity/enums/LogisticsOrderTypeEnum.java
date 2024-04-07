package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 物流订单类型
 * @author zhaoyalong
 */
@Getter
@AllArgsConstructor
public enum LogisticsOrderTypeEnum {

    /**
     * 订单类型
     */
    REAL(1, "实物"),
    DUMMY(2, "虚拟");

    private final Integer code;
    private final String desc;

    public static LogisticsOrderTypeEnum fromCode(Integer code){
        return Arrays.stream(LogisticsOrderTypeEnum.values())
                .filter(x -> x.getCode() == code)
                .findFirst()
                .orElse(null);
    }

}

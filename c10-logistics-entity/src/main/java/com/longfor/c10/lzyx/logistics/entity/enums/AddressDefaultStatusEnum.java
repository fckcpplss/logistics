package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 地址默认状态枚举类
 * @author zhaoyalong
 */
@AllArgsConstructor
@Getter
public enum AddressDefaultStatusEnum {

    //是
    YES(1,"是"),

    //否
    NO(2,"否");

    private Integer code;
    private String desc;

    public static AddressDefaultStatusEnum fromCode(Integer code) {
        return Arrays.stream(AddressDefaultStatusEnum.values()).filter(e -> e.getCode().equals(code)).findFirst()
                .orElse(null);
    }
}

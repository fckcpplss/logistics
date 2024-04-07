package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Arrays;

/**
 * 顺丰费用类型
 **/
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum SFFeeTypeEnum {
    YF("1", "运费"),
    OTHER("2", "其他费用"),
    BF("3", "保价"),
    RY("14", "燃油附加费"),
    ;
    private String code;
    private String desc;
    public static SFFeeTypeEnum fromCode(String code) {
        return Arrays.stream(SFFeeTypeEnum.values()).filter(x -> x.getCode().equals(code)).findFirst().orElse(null);
    }
}

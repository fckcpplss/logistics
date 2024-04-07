package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

/**
 * @description: 快递公司枚举
 * @author: zhaoyalong
 */
@ToString
@Getter
@AllArgsConstructor
public enum CompanyCodeEnum {

    JD("jd", "京东"),

    SF("shunfeng", "顺丰"),

    KD100("kuaidi100", "快递100"),

    ZTO("zto", "中通"),

    OTHER("other", "其他");

    private final String code;
    private final String desc;


    public static CompanyCodeEnum fromCode(String code) {
        return Arrays.stream(CompanyCodeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(KD100);
    }

    public static CompanyCodeEnum fromCodeWithThrow(String code) {
        return Arrays.stream(CompanyCodeEnum.values()).filter(e -> e.getCode().equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效的公司类型"));
    }

    /**
     * 平台支持 SF JD
     *
     * @param code
     * @return
     */
    public static boolean isPlatformSupport(String code) {
        CompanyCodeEnum companyCodeEnum = fromCode(code);
        return companyCodeEnum.equals(JD) || companyCodeEnum.equals(SF);
    }
}

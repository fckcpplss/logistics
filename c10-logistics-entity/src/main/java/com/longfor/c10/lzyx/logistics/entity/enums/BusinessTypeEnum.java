package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 物流业务类型枚举类
 * @author zhaoyl
 * @date 2022/2/15 下午5:48
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum BusinessTypeEnum {
    MERCHANT_CANCEL(1, "商家取消"),

    REFUND_ONLY(2, "用户退款（仅退款）"),

    REFUND_GOODS(3, "用户退款（退货退款）"),

    SIGN_ERROR(4, "签收失败"),

    DELIVERY_ERROR(5, "运输异常"),

    SEND_AGAIN(6, "再次发货");

    private final Integer code;
    private final String desc;

    public static BusinessTypeEnum fromCode(Integer code) {
        return Arrays.stream(BusinessTypeEnum.values()).filter(e -> e.getCode().equals(code)).findFirst()
                .orElse(null);
    }
}


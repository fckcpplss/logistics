package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 运单表物流状态
 * @author zhaoyalong
 */
@Getter
@AllArgsConstructor
public enum DeliveryLogisticsStatusEnum {
    /**
     * 物流状态
     */
    TO_SEND(0, "未发货"),
    SENDED(1, "已下单"),
    TO_RECEIVED(2, "待揽收"),
    RECEIVED(3, "已揽收"),
    TRANSPORTING(4, "运输中"),
    SENDING(5, "派送中"),
    SIGNED(6, "已签收"),
    SIGNED_FAIL(7, "签收失败"),
    DELIVERY_ERROR(10, "派送异常"),
    PLATFORM_CANCEL(11, "平台取消"),
    OTHER(8, "未知");

    private final int code;

    private final String desc;

    public static DeliveryLogisticsStatusEnum fromCode(int code) {
        return Arrays.stream(DeliveryLogisticsStatusEnum.values()).filter(x-> x.getCode() == code).findFirst().orElse(DeliveryLogisticsStatusEnum.OTHER);
    }
}

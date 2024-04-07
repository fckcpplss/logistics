package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 发货类型枚举
 * @author zhaoyalong
 */
@Getter
@AllArgsConstructor
public enum DeliveryTypeEnum {
    /**
     * 物流类型配置
     */
    WECHAT_JD_THS("JDL","微信-京东物流",0, "特惠送"),
    JD_THS("JD","京东物流",1, "特惠送"),
    SF_WL("SFWL", "顺丰物流", 3, "顺丰"),
    WULIU100("WULIU100", "物流100",2, "物流100");

    private final String deliveryId;

    private final String deliveryName;

    private final Integer serviceType;

    private final String serviceTypeName;

    public static DeliveryTypeEnum fromDeliveryId(String deliveryId) {
        return Arrays.stream(DeliveryTypeEnum.values()).filter(x -> x.getDeliveryId().equals(deliveryId)).findAny().orElse(null);
    }
}

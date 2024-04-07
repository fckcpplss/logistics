package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 下单渠道标识
 */
@Getter
@AllArgsConstructor
public enum ChannelBizCodeEnum {
    C10_ORDER_PROPOSE("c10_order_propose", "珑珠优选-单商品下单"),
    c10_SHOPPING_CART("c10_shopping_cart", "珑珠优选-购物车下单"),
    c10_ORDER_ADMIN("c10_order_admin", "珑珠优选-运营端");
    private final String code;
    private final String message;
}

package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 商品订单状态
 * @author zhaoyalong
 **/
@Getter
@AllArgsConstructor
public enum GoodsOrderStatusEnum {
    UNDELIVERED(11, "未发货"),
    SECTION_DELIVERED(21, "部分发货"),
    DELIVERED(12, "已发货"),
    SIGNED(13, "物流签收"),
    SIGNED_CONFIRM(131, "主动签收"),
    SIGNED_FAIL(14, "签收失败")
    ;
    private final Integer code;

    private final String desc;


    public static GoodsOrderStatusEnum fromCode(int code) {
        return Arrays.stream(GoodsOrderStatusEnum.values())
                .filter(x -> x.getCode() == code)
                .findAny()
                .orElse(null);
    }

}

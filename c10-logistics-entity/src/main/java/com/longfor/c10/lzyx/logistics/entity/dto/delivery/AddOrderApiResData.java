package com.longfor.c10.lzyx.logistics.entity.dto.delivery;

import lombok.Data;

@Data
public class AddOrderApiResData {

    /**
     * 订单ID，下单成功时返回
     */
    private String orderId;

    /**
     * 运单ID，下单成功时返回
     */
    private String waybillId;
}

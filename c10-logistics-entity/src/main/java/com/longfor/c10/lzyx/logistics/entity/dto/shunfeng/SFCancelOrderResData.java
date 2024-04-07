package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import lombok.Data;

/**
 * 顺丰取消订单响应DTO
 */
@Data
public class SFCancelOrderResData {
    private String msg;
    private String succ;
    private SFCancelOrderResult result;

    @Data
    class SFCancelOrderResult{
        private String orderid;
        private String code;
    }
}

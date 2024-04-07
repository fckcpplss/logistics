package com.longfor.c10.lzyx.logistics.client.entity.param.user;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 订单发货信息请求参数
 * @author: zhaoyl
 */
@Data
public class DeliveryInfoReqDate extends BaseReqData {
    /**
     * 子单号
     */
    @NotNull(message = "子单号为空")
    private String childOrderId;
}

package com.longfor.c10.lzyx.logistics.client.entity.param.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 物流订单状态dto
 * @author zhaoyl
 * @date 2022/4/27 上午9:59
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsOrderStatusDTO {
    /**
     * 子单号
     */
    private String childOrderId;

    /**
     * sku状态集合
     */
    private List<LogisticsOrderSkuStatusDTO> skuStatuss;

}

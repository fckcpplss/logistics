package com.longfor.c10.lzyx.logistics.client.entity.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 自提/核销订单批量核销DTO
 * @author zhaoyl
 * @date 2022/4/13 上午11:30
 * @since 1.0
 */
@Data
public class LogisticsVerifyOrderVerifyListDTO {
    /**
     * 订单编号
     */
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;
    /**
     * 商品sku编号集合
     */
    @NotEmpty(message = "商品sku不能为空")
    private List<String> skuIds;

}

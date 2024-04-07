package com.longfor.c10.lzyx.logistics.client.entity.param.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 监控详情请求参数
 * @author zhaoyl
 * @date 2022/2/17 下午5:21
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsMonitorDetailReqData {
    /**
     * 订单编号
     */
    @NotBlank(message = "订单编号不能为空")
    private String childOrderId;

    /**
     * 运单号
     */
    @NotBlank(message = "运单号不能为空")
    private String deliveryNo;
}

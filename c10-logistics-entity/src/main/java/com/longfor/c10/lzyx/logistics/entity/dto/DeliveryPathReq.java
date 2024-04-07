package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @Description : 物流单轨迹请求实体
 * @Author : zhaoyalong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPathReq extends BaseReqData{

    /**
     * 子订单号
     */
    @NotBlank(message = "子订单号不能为空")
    private String childOrderId;

    /**
     * 快递单号
     */
    @NotBlank(message = "快递单号不能为空")
    private String deliveryNo;
}

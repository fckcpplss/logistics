package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description : 物流单轨迹请求实体
 * @Author : zhaoyalong
 */
@Data
public class DeliveryPathReq extends BaseReqData {

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

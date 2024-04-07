package com.longfor.c10.lzyx.logistics.entity.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @description: 查看物流轨迹Req
 * @author zhaoyl
 */
@Data
@ApiModel(value = "查看物流轨迹Req")
public class TrajectoryReq {

    @ApiModelProperty(value = "子订单id")
    @NotEmpty(message = "子订单id不能为空")
    private String childOrderId;

    @ApiModelProperty(value = "快递单号")
    @NotEmpty(message = "快递单号不能为空")
    private String deliveryNo;
}

package com.longfor.c10.lzyx.logistics.client.entity.param.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 订单发货信息Resp
 * @author: zhaoyl
 */
@Data
@ApiModel(value = "订单发货信息Resp")
public class DeliveryInfoResp {
    @ApiModelProperty(value = "订单发货信息详情")
    private List<DeliveryInfo> deliveryInfos = new ArrayList<>();

    @ApiModelProperty(value = "商品总数")
    private Integer total = 0;

    @ApiModelProperty(value = "已发商品数")
    private Integer send = 0;
}

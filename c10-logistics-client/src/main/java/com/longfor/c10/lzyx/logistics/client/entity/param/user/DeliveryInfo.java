package com.longfor.c10.lzyx.logistics.client.entity.param.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 订单发货信息
 * @author zhaoyl
 */
@Data
@ApiModel(value = "订单发货信息")
public class DeliveryInfo {
    @ApiModelProperty(value = "子订单号")
    private String childOrderId;
    @ApiModelProperty(value = "运单号")
    private String deliveryNo;
    @ApiModelProperty(value = "快递运营商")
    private String company;
    @ApiModelProperty(value = "物流状态")
    private String logisticsStatus;
    @ApiModelProperty(value = "商品信息")
    private List<GoodsInfo> goodsInfos = new ArrayList<>();
    @ApiModelProperty(value = "最新一条物流轨迹")
    private String latestTrajectory;
    @ApiModelProperty(value = "发货时间")
    private LocalDateTime createDateTime;
}

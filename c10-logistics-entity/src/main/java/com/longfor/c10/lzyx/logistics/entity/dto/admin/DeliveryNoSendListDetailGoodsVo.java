package com.longfor.c10.lzyx.logistics.entity.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发货详情商品vo
 * @author zhaoyl
 * @date 2022/2/24 下午2:26
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryNoSendListDetailGoodsVo  extends GoodsVO{
    /**
     * 物流公司名称
     */
    String companyName;
    /**
     * 物流公司代码
     */
    String compangCode;
    /**
     * 自定义物流状态，0：待下单 1：已下单 2:待揽收 3:已揽收 4:运输中 5:派送中 6:已签收 7:签收失败
     */
    private Integer logisticsStatusCode;
    /**
     * 自定义物流状态名称
     */
    private String logisticsStatusName;
    /**
     * 运单号
     */
    String deliveryNo;
    /**
     * 发货地址
     */
    String sendAddress;
}

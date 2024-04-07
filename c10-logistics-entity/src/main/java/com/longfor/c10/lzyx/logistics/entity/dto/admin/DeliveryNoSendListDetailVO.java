package com.longfor.c10.lzyx.logistics.entity.dto.admin;

import lombok.Data;

import java.util.List;

/**
 * 发货列表详情VO
 */
@Data
public class DeliveryNoSendListDetailVO {

    /**
     * 子单编号
     */
    private String childOrderId;

    /**
     * 运费承担方编码
     */
    private Integer logisticsTypeCode;
    /**
     * 运费承担方名称
     */
    private String logisticsTypeName;
    /**
     * 商品列表
     */
    private List<DeliveryNoSendListDetailGoodsVo> sendGoodList;
    /**
     * 收货人名称
     */
    private String receiveName;
    /**
     * 收货人电话
     */
    private String receivePhone;
    /**
     * 收货人地址
     */
    private String receiveAddress;
    /**
     * 子订单所属供应商ID
     */
    private String shopId;

    /**
     * 订单创建时间
     */
    private String orderCreateTime;
}

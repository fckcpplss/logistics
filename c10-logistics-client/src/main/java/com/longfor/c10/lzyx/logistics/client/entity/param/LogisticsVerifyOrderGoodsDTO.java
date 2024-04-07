package com.longfor.c10.lzyx.logistics.client.entity.param;

import lombok.Data;

import java.util.Date;

/**
 * 自提/核销订单列表返回实体
 * @author zhaoyl
 * @date 2022/4/13 上午11:48
 * @since 1.0
 */
@Data
public class LogisticsVerifyOrderGoodsDTO {

    /**
     * 商品类型
     */
    private Integer goodsTypeShow;

    /**
     * 商品名称
     */
    private String goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品描述
     */
    private String goodsDesc;

    /**
     * 商品数量
     */
    private Integer goodsNum;

    /**
     * 商品图片地址(仅一个地址)
     */
    private String goodsImgUrl;

    /**
     * sku_id
     */
    private String skuId;

    /**
     * 商品规格
     */
    private String skuSpecs;

    /**
     * 自提点
     */
    private String pickupSpot;

    /**
     * 自提地址
     */
    private String pickupAddress;

    /**
     * 自提说明
     */
    private String pickupDesc;

    /**
     * 自提开始时间
     */
    private Date pickupStartTime;

    /**
     * 自提结束时间
     */
    private Date pickupEndTime;

    /**
     * 核销状态
     */
    private String verifyStatusShow;

    /**
     * 提款状态
     */
    private String refundStatusShow;

    /**
     * 核销标记：1.可以核销，0.不能核销
     */
    private Integer verifyFlag;

}

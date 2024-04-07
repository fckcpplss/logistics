package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.Data;

/**
 * 自提/核销订单核销记录列表请求实体
 * @author zhaoyl
 * @date 2022/4/13 上午11:30
 * @since 1.0
 */
@Data
public class LogisticsVerifyOrderRecordsListReqData extends BaseReqData{
    /**
     * 运营组织
     */
    private String orgId;

    /**
     * 自提地址id
     */
    private String pickupAddressId;
    /**
     * 订单编号,模糊匹配
     */
    private String orderNo;

    /**
     * 核销单号
     */
    private String verifyNo;

    /**
     * sku_id
     */
    private String skuId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 核销用户账号
     */
    private String verifyUserAccount;

    /**
     * 订单开始时间
     */
    private String startOrderCreateTime;

    /**
     * 订单结束时间
     */
    private String endOrderCreateTime;
}

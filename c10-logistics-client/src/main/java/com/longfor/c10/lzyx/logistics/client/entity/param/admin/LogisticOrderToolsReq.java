package com.longfor.c10.lzyx.logistics.client.entity.param.admin;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.Data;

/**
 * 物流运维工具列表查询参数
 * @author zhaoyl
 * @date 2021/12/6 上午11:25
 * @since 1.0
 */
@Data
public class LogisticOrderToolsReq extends BaseReqData {
    /**
     * 父订单编号
     */
    private String orderId;
    /**
     * 订单编号
     */
    private String childOrderId;

    /**
     * 商品skuId
     */
    private String skuId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 订单状态
     */
    private String orderStatuss;

    /**
     * 运单号
     */
    private String deliverNo;

    /**
     * 物流状态
     */
    private String logisticStatuss;

    /**
     * 运单id
     */
    private String logisticsDeliveryId;

    /**
     * 运费id
     */
    private String logisticsFeeId;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 是否取消
     */
    private Integer isCancel;

    /**
     * 运费更新状态
     */
    private Integer updateFeeStatus;



}

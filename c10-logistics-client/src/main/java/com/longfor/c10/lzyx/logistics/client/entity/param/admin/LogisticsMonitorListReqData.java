package com.longfor.c10.lzyx.logistics.client.entity.param.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 物流运单监控列表请求参数
 * @author zhaoyl
 * @date 2022/2/17 上午9:42
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsMonitorListReqData {
    /**
     * 组织id
     */
    private String orgId;

    /**
     * 组织id集合
     */
    private List<String> orgIds;

    /**
     * 供应商id
     */
    private String shopId;

    /**
     *供应商id集合
     */
    private List<String> shopIds;

    /**
     * 供应商名称，模糊匹配
     */
    private String shopName;

    /**
     * 商品skuId
     */
    private String skuId;

    /**
     * 商品名称，模糊匹配
     */
    private String goodsName;

    /**
     *运单编号
     */
    private String deliveryNo;

    /**
     * 订单编号
     */
    private String childOrderId;

    /**
     *收货人姓名，模糊匹配
     */
    private String receiptName;

    /**
     *收货电话
     */
    private String receiptPhone;

    /**
     *物流状态
     */
    private Integer logisticsStatus;

    /**
     *物流状态集合
     */
    private List<Integer> logisticsStatuss;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 订单状态集合
     */
    private List<Integer> orderStatuss;

    /**
     *物流类型,1.平台京东，2.平台顺丰，3.商家顺丰，4.其他
     */
    private Integer logisticsType;

    /**
     * 物流类型集合
     */
    private List<Integer> logisticsTypes;

    /**
     *运费承担方,1.平台，2.商家
     */
    private Integer feeType;

    /**
     * 运费承担方集合
     */
    private List<Integer> feeTypes;

    /**
     *业务类型,1.商家取消，2.用户退款（仅退款），3.用户退款（退货退款），4.签收失败，5.运输异常
     */
    private Integer businessType;

    /**
     * 业务类型集合
     */
    private List<Integer> businessTypes;

    /**
     *是否取消
     */
    private Integer ifCancel;

    /**
     *物流备注，模糊匹配
     */
    private String remark;

    /**
     * 订单开始时间
     */
    private String startOrderCreateTime;

    /**
     * 订单结束时间
     */
    private String endOrderCreateTime;

    /**
     * 运单开始时间
     */
    private String startDeliveryCreateTime;

    /**
     * 运单结束时间
     */
    private String endDeliveryCreateTime;



}

package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 自提/核销订单列表返回实体
 * @author zhaoyl
 * @date 2022/4/13 上午11:48
 * @since 1.0
 */
@Data
public class LogisticsVerifyOrderListResData{
    /**
     * 主键id
     */
    private String id;
    /**
     * 运营组织
     */
    private String orgName;

    /**
     * 订单编号
     */
    private String childOrderId;

    /**
     * 珑民手机号
     */
    private String lmPhone;

    /**
     * 珑民昵称
     */
    private String lmNickname;

    /**
     * 珑民id
     */
    private String lmId;

    /**
     * 订单创建时间
     */
    private Date orderCreateTime;

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
     * 核销码
     */
    private String pickupCode;

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
     * 核销状态：0,待核销，1.核销成功，2.核销失败，3.核销超期
     */
    private Integer verifyStatus;

    /**
     * 提款状态：0.未退款，1.退款中，2.已退款,3.退款失败
     */
    private Integer refundStatus;

    /**
     * 商品列表
     */
    private List<LogisticsVerifyOrderGoodsDTO> goodsList;

}

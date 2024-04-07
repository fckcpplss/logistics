package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 自提/核销订单详情返回实体
 * @author zhaoyl
 * @date 2022/4/13 上午11:48
 * @since 1.0
 */
@Data
public class LogisticsVerifyOrderDetailResData {
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
     * 自提点id
     */
    private String pickupAddressId;

    /**
     * 自提说明
     */
    private String pickupDesc;

    /**
     * 自提开始时间
     */
    private String pickupStartTime;

    /**
     * 自提结束时间
     */
    private String pickupEndTime;

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
     * 商品类型（1.商品 2.卡券 3.券包 4.停车充值）
     */
    private Integer goodsType;

    /**
     * 订单商品信息
     */
    private List<LogisticsVerifyOrderGoodsDTO> goodsList;

    /**
     * 可被核销商品信息
     */
    private List<LogisticsVerifyOrderGoodsDTO> canVerifyList;

    /**
     * 不可被核销商品信息
     */
    private List<LogisticsVerifyOrderGoodsDTO> canNotVerifyList;
}

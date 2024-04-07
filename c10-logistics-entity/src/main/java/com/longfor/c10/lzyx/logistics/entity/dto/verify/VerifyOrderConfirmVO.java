package com.longfor.c10.lzyx.logistics.entity.dto.verify;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author liuxin41
 * @version 1.0
 * @description:
 * @date 2022/4/14 11:47
 */
@Data
public class VerifyOrderConfirmVO {

    /**
     * 子订单id
     */
    private String childOrderId;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 商铺ID
     */
    private String shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 所属主体类型 1、商场 2、商户
     */
    private Integer ownerType;

    /**
     * 商品类型（1.商品 2.卡券 3.券包 4.停车充值）
     */
    private Integer goodsType;

    /**
     * 支付单号
     */
    private String payOrderId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 支付方式(1：珑珠支付 2：电银微信JSAPI支付 3：免支付 ）
     */
    private Integer payType;

    /**
     * 订单失效时间
     */
    private Date expireTime;

    /**
     * 支付成功时间
     */
    private Date paySuccessTime;

    /**
     * 订单备注
     */
    private String orderDesc;

    /**
     * 自提点
     */
    private String pickupSpot;

    /**
     * 自提地址，默认所属主体一致，可为空
     */
    private String pickupAddress;

    /**
     * 自提描述
     */
    private String pickupDesc;

    /**
     * 自提开始日期
     */
    private Date pickupStartTime;

    /**
     * 自提结束日期/签收截止日期
     */
    private Date pickupEndTime;

    /**
     * 自提码路径/核销码路径
     */
    private String pickupQrcodeUrl;

    /**
     * 自提码/核销码
     */
    private String pickupCode;

    /**
     * 收款账号
     */
    private String accountNo;

    /**
     * 订单类型（1商品类订单、2停车类订单、3卡券类订单、4券包类订单）
     */
    private Integer orderType;

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
     * 端来源
     */
    private String entrySourse;

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
    private List<VerifyResGoodsVO> pickupGroupList;

}

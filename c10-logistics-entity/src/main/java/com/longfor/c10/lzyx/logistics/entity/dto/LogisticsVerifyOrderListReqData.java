package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 自提/核销订单列表请求实体
 * @author zhaoyl
 * @date 2022/4/13 上午11:30
 * @since 1.0
 */
@Data
public class LogisticsVerifyOrderListReqData extends BaseReqData{


    /**
     * 自提地址id
     */
    private String pickupAddressId;

    /**
     * 订单编号,模糊匹配
     */
    private String orderNo;

    /**
     * 订单编号集合
     */
    private List<String> orderNos;

    /**
     * 订单开始时间
     */
    private String startOrderCreateTime;

    /**
     * 订单结束时间
     */
    private String endOrderCreateTime;

    /**
     *用户手机号,模糊匹配
     */
    private String userPhone;

    /**
     * 核销码,模糊匹配
     */
    private String verifyCode;

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
     * 核销类型0、用户端核销，1.运营端核销
     */
    private Integer verifyType;

    /**
     * 核销类型集合
     */
    private List<Integer> verifyTypes;

    /**
     * 核销状态：0,待核销，1.核销成功，2.核销失败，3.核销超期
     */
    private Integer verifyStatus;

    /**
     * 核销状态集合
     */
    private List<Integer> verifyStatuss;

    /**
     * 提款状态：0.未退款，1.退款中，2.已退款
     */
    private Integer refundStatus;

    /**
     * 退款状态集合
     */
    private List<Integer> refundStatuss;

    /**
     * 核销开始时间
     */
    private String startVerifyTime;

    /**
     * 核销结束时间
     */
    private String endVerifyTime;


}

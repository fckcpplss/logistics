package com.longfor.c10.lzyx.logistics.client.entity.param;

import lombok.Data;

import java.util.Date;

/**
 * 自提/核销订单核销记录列表返回实体
 * @author zhaoyl
 * @date 2022/4/13 上午11:48
 * @since 1.0
 */
@Data
public class LogisticsVerifyOrderRecordsListResData extends LogisticsVerifyOrderGoodsDTO{
    /**
     * 主键id
     */
    private String id;

    /**
     * 运营组织名称
     */
    private String orgName;

    /**
     * 核销单号
     */
    private String verifyNo;

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
     * 核销用户id
     */
    private String verifyUserId;

    /**
     * 核销用户id
     */
    private String verifyUserAccount;

    /**
     * 核销用户名称
     */
    private String verifyUserName;

    /**
     * 订单创建时间
     */
    private Date orderCreateTime;

    /**
     * 自提/核销时间
     */
    private Date verifyTime;
}

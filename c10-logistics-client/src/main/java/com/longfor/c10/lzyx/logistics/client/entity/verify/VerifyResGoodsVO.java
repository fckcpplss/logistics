package com.longfor.c10.lzyx.logistics.client.entity.verify;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author liuxin
 * @date 2022/5/10 18:51
 */
@Data
public class VerifyResGoodsVO {

    /**
     * 提货时间
     */
    private Date pickupTime;
    /**
     * 自提地址编号
     */
    private String pickupAddressId;
    /**
     * 自提点
     */
    private String pickupSpot;
    /**
     * 自提详细地址
     */
    private String pickupAddress;
    /**
     * 自提开始时间
     */
    private String pickupStartTime;
    /**
     * 自提结束时间
     */
    private String pickupEndTime;
    /**
     * 自提过期时间
     */
    private Date pickupExpireTime;
    /**
     * 自提说明
     */
    private String pickupDesc;
    /**
     * 自提说明
     */
    private String pickupQrcodeUrl;
    /**
     * 自提码
     */
    private String pickupCode;
    /**
     * 自提商品集合
     */
    List<VerifyOrderGoodsVO> orderGoodsList;
}

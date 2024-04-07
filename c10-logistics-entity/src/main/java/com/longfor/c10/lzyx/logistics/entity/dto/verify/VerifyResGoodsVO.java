package com.longfor.c10.lzyx.logistics.entity.dto.verify;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private String pickupStartTime;
    /**
     * 自提结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
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

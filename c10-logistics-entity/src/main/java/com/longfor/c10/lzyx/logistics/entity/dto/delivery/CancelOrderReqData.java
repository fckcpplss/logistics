package com.longfor.c10.lzyx.logistics.entity.dto.delivery;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import com.longfor.c10.lzyx.logistics.entity.enums.BusinessTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeliveryTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelOrderReqData{
    /**
     * 必填
     * 订单 ID，需保证全局唯一
     */
    private String orderId;
    /**
     * 用户openid
     */
    private String openid;
    /**
     * 必填
     * 运单ID
     */
    private String deliveryId;
    /**
     * 必填
     * 所选类型
     */
    private DeliveryTypeEnum deliveryTypeEnum;

    /**
     * 商家编码
     */
    private String vendorCode;

    /**
     * 取消原因
     */
    private String interceptReason = "用户发起取消";

    /**
     * 取消原因编码： 1-用户发起取消； 2-超时未支付
     */
    private Integer cancelReasonCode = 1;

    /**
     * 取消操作人
     */
    private String cancelOperator;

    /**
     * 取消时间
     */
    private Date cancelTime;
    /**
     * 业务类型
     */
    private BusinessTypeEnum businessTypeEnum;
}

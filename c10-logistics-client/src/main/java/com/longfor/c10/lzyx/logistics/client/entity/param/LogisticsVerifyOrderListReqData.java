package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.Data;

/**
 * 自提/核销订单列表请求实体
 * @author zhaoyl
 * @date 2022/4/13 上午11:30
 * @since 1.0
 */
@Data
public class LogisticsVerifyOrderListReqData extends BaseReqData {

    /**
     * 自提地址id
     */
    private String pickupAddressId;
    /**
     * 订单编号,模糊匹配
     */
    private String orderNo;

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
}

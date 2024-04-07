package com.longfor.c10.lzyx.logistics.entity.dto.delivery;

import lombok.Data;

/**
 * @描述:
 * @author: zhaoyalong
 */
@Data
public class DeliveryDTO {
    /**
     * 来源, JD、SF、KUAIDI100、XXL_JOB
     */
    private String source;

    private String logisticsDeliveryId;

    /**
     * 物流单号
     */
    private String deliveryNo;
    /**
     * 公司编码
     */
    private String companyCode;
    /**
     * 物流订单ID
     *
     */
    private String shopLogisticsId;
    /**
     * 源公司, 对应 CompanyCodeEnum code值
     */
    private String sourceCompany;
}

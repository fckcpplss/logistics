package com.longfor.c10.lzyx.logistics.client.entity.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryCompanyResData {
    /**
     * 物流公司编码
     */
    private String deliveryCompanyCode;
    /**
     * 物流公司名称
     */
    private String deliveryCompanyName;
    /**
     * 首字母
     */
    private String initial;
    /**
     *单号长度
     */
    private Integer lengthPre;
}

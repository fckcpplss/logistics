package com.longfor.c10.lzyx.logistics.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName : DeliveryCompanyReqData
 * @Description : 获取物流公司
 * @Author : ws
 * @Date: 2021-05-07 16:19
 */
@Data
public class DeliveryCompanyListReqData extends BaseReqData{

    /**
     * 物流公司编码
     */
    private String deliveryCompanyCode;

    /**
     * 物流公司名称
     */
    private String deliveryCompanyName;
}

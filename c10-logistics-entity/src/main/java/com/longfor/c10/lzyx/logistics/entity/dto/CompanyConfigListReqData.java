package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 获取供应商可选的物流公司请求参数
 * @author zhaoyl
 * @date 2022/4/20 上午9:55
 * @since 1.0
 */
@Data
public class CompanyConfigListReqData extends BaseReqData{
    @NotBlank(message = "shopId不能为空")
    private String shopId;

    @NotNull(message = "物流类型不能为空")
    private Integer logisticsTypeCode;

    /**
     * 订单编号
     */
    private String orderNo;
}

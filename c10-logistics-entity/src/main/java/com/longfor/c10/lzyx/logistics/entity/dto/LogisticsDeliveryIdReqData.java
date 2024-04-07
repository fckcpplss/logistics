package com.longfor.c10.lzyx.logistics.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 物流单详情查询参数
 * @author zhaoyalong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsDeliveryIdReqData extends BaseReqData{
    /**
     * 物流单号
     */
    @NotBlank(message = "物流单号不能为空")
    private Long deliveryId;
}

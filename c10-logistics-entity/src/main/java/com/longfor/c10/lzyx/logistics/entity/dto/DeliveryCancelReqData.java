package com.longfor.c10.lzyx.logistics.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName : DeliverySendReqData
 * @Description : 快递取消发货请求实体
 * @Author : zhaoyalong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryCancelReqData extends BaseReqData{
    /**
     * 物流单号
     */
    @NotBlank(message = "物流单号不能为空")
    private Long deliveryId;
}

package com.longfor.c10.lzyx.logistics.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 发货地址删除请求对象
 */
@Data
@ApiModel(value = "")
public class LogisticsReturnAddressDelReq   extends  BaseReqData{
    /**
     * 地址id
     */
    @NotNull(message = "地址id不能为空")
    private Integer id;
}

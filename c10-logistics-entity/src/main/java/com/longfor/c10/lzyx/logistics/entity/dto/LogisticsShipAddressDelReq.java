package com.longfor.c10.lzyx.logistics.entity.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LogisticsShipAddressDelReq   extends  BaseReqData{
    @NotNull(message = "地址id不能为空")
    private Integer id;
}

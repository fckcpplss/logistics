package com.longfor.c10.lzyx.logistics.entity.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "设置默认地址请求对象")
public class LogisticsAddressDefaultReq extends  BaseReqData{
    @NotBlank(message = "地址id不能为空")
    private String addressId;
}

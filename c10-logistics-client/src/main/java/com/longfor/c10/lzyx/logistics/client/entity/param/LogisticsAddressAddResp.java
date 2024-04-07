package com.longfor.c10.lzyx.logistics.client.entity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LogisticsAddressAddResp {
    @ApiModelProperty(value = "地址id")
    private String addressId;
}

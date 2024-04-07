package com.longfor.c10.lzyx.logistics.client.entity.verify;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuxin41
 * @version 1.0
 * @description:
 * @date 2022/4/13 11:15
 */
@Data
public class VerifyPickUpCodeVO {

    @ApiModelProperty(value = "自提详细地址")
    private String childOrderId;
    @ApiModelProperty(value = "自提二维码")
    private String pickupQrcodeUrl;
    @ApiModelProperty(value = "自提码")
    private String pickupCode;

}

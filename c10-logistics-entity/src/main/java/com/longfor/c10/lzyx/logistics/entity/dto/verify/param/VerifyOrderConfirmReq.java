package com.longfor.c10.lzyx.logistics.entity.dto.verify.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author liuxin41
 * @version 1.0
 * @description:
 * @date 2022/4/13 11:16
 */
@Data
public class VerifyOrderConfirmReq {

    @NotBlank(message = "未知订单号")
    private String childOrderId;

    @NotEmpty(message = "核销商品不能为空")
    private List<String> skuIds;

    @NotBlank(message = "核销人未知")
    private String oaAccount;

}

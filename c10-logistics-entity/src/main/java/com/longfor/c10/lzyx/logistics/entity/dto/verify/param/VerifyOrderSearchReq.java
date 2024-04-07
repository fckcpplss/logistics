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
public class VerifyOrderSearchReq {

    @NotEmpty(message = "订单号不能为空")
    private List<String> childOrderIds;

    @NotBlank(message = "未知用户")
    private String lmId;
}

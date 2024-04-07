package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户地址详情请求参数
 * @author zhaoyl
 */
@Data
public class LogisticsAddressDetailReqData extends BaseReqData {
    /**
     * 地址id
     */
    @NotBlank(message = "物流地址id不能为空")
    private String addressId;
}

package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 设置默认地址请求对象
 * @author zhaoyl
 */
@Data
public class LogisticsAddressDefaultReq extends BaseReqData {
    /**
     * 地址id
     */
    @NotBlank(message = "地址id不能为空")
    private String addressId;
}

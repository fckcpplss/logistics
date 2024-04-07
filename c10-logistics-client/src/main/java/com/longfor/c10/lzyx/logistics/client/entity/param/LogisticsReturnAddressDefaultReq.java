package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 退货地址设置默认请求实体
 * @author zhaoyl
 */
@Data
public class LogisticsReturnAddressDefaultReq  extends BaseReqData {
    @NotNull(message = "地址id不能为空")
    private Integer id;

}

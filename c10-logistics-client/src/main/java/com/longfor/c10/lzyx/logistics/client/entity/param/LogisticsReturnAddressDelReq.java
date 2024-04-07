package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * @author zhaoyl
 */
@Data
public class LogisticsReturnAddressDelReq  extends BaseReqData {
    @NotNull(message = "地址id不能为空")
    private Integer id;
}

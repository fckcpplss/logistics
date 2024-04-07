package com.longfor.c10.lzyx.logistics.entity.dto;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "物流地址删除请求对象")
public class LogisticsAddressDelReq  extends BaseReqData {
    @NotBlank(message = "物流地址id不能为空")
    private String addressId;
}

package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LogisticsShipAddressDefaultReq extends BaseReqData {
    @NotNull(message = "地址id不能为空")
    private Integer id;
}

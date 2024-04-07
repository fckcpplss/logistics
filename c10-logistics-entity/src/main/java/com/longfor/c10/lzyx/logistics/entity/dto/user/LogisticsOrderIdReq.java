package com.longfor.c10.lzyx.logistics.entity.dto.user;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zhaoyalong
 * @date 2021/10/29
 **/
@Data
public class LogisticsOrderIdReq extends BaseReqData {
    @NotBlank(message = "子订单号不能为空")
    private String childOrderId;

    @ApiModelProperty(value = "商户ID")
    private String shopId;
}

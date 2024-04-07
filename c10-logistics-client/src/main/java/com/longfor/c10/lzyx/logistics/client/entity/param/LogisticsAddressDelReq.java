package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 物流用户地址删除请求参数
 * @author zhaoyalong
 */
@Data
public class LogisticsAddressDelReq  extends BaseReqData {
    /**
     * 地址id
     */
    @NotBlank(message = "物流地址id不能为空")
    private String addressId;
}

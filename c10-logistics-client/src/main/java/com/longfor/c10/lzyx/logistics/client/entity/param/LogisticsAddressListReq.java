package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 物流列表查询请求对象
 */
@Data
public class LogisticsAddressListReq  extends BaseReqData {
    /**
     * 地址模糊查询关键字
     */
    private String addressQueryKey;
}

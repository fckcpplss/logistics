package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName : LogisticsCompanyRes
 * @Description :
 * @Author : zhaoyalong
 */
@Data
public class CompanyDeleteReq extends BaseReqData {
    @ApiModelProperty(value = "主键id",required = true)
    @NotNull(message = "id不能为空")
    private Integer id;
}

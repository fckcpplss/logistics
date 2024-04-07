package com.longfor.c10.lzyx.logistics.client.entity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName : LogisticsCompanyRes
 * @Description :
 * @Author : zhaoyalong
 */
@Data
public class CompanyUpdateReq extends CompanyAddReq {
    @ApiModelProperty(value = "主键id",required = true)
    @NotNull(message = "id不能为空")
    private Integer id;
}

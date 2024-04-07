package com.longfor.c10.lzyx.logistics.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @ClassName : LogisticsCompanyRes
 * @Description :
 * @Author : zhaoyalong
 */
@Data
public class CompanyAddReq extends BaseReqData {
    @ApiModelProperty(value = "公司编码",required = true)
    @NotBlank(message = "公司编码不能为空")
    private String companyCode;

    @ApiModelProperty(value = "公司名称",required = true)
    @NotBlank(message = "公司编码不能为空")
    private String companyName;

    @ApiModelProperty(value = "是否可获取物流轨迹 1:是 0:否",required = true)
    @NotNull(message = "是否可获取物流轨迹不能为空")
    private Integer isSupportDeliveryPath;
}

package com.longfor.c10.lzyx.logistics.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName : LogisticsCompanyRes
 * @Description :
 * @Author : zhaoyalong
 */
@Data
public class CompanyListRes {
    @ApiModelProperty(value = "公司编码")
    private String companyCode;
    @ApiModelProperty(value = "公司名称")
    private String companyName;
    @ApiModelProperty(value = "是否可获取物流轨迹 1:是 0:否")
    private Integer isSupportDeliveryPath;
    @ApiModelProperty(value = "创建人")
    private String createBy;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    @ApiModelProperty(value = "修改人")
    private String updateBy;
    @ApiModelProperty(value = "修改时间")
    private String updateTime;
}

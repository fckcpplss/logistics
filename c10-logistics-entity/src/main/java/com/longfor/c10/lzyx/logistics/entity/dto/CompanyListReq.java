package com.longfor.c10.lzyx.logistics.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName : LogisticsCompanyRes
 * @Description :
 * @Author : zhaoyalong
 */
@Data
public class CompanyListReq  extends BaseReqData{
    @ApiModelProperty(value = "公司编码")
    private String companyCode;
    @ApiModelProperty(value = "公司名称")
    private String companyName;
    @ApiModelProperty(value = "是否可获取物流轨迹 1:是 0:否")
    private Integer isSupportDeliveryPath;
}

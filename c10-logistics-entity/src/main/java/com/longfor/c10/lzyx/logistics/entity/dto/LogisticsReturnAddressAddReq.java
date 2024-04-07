package com.longfor.c10.lzyx.logistics.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

/**
 * @ClassName :
 * @Description :
 * @Author :
 * @Date:
 */
@Data
@ApiModel(value = "发货地址新增请求对象")
public class LogisticsReturnAddressAddReq   extends  BaseReqData{

    @ApiModelProperty(value = "收件人",required = true)
    @NotBlank(message = "收件人不能为空")
    @Size(max = 25,min = 1,message = "收件人姓名最多25个字")
    private String addresser;

    @ApiModelProperty(value = "电话号码",required = true)
    @NotBlank(message = "电话号码不能为空")
    private String phoneNumber;

    @ApiModelProperty(value = "省名称")
    @NotBlank(message = "省名称不能为空")
    private String provinceName;

    @ApiModelProperty(value = "省编码",required = true)
    private String provinceCode;

    @ApiModelProperty(value = "市名称")
    @NotBlank(message = "市名称不能为空")
    private String cityName;

    @ApiModelProperty(value = "市编码",required = true)
    private String cityCode;

    @ApiModelProperty(value = "区县名称")
    private String areaName;

    @ApiModelProperty(value = "区县编码",required = true)
    private String areaCode;

    @ApiModelProperty(value = "乡镇街编码")
    private String streetCode;

    @ApiModelProperty(value = "乡镇街名称")
    private String streetName;

    @ApiModelProperty(value = "详细地址",required = true)
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 50,min = 1,message = "详情地址最多50个字")
    private String addressDetail;

    @ApiModelProperty(value = "是否是默认地址1.默认2.非默认  默认的是非默认")
    private Integer isDefault;

    private String userName;

}

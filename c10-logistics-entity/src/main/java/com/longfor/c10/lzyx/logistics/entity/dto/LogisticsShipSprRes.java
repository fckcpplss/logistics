package com.longfor.c10.lzyx.logistics.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @ClassName :
 * @Description :
 * @Author :
 * @Date:
 */
@Data
@ApiModel(value = "发货地址供应商信息对象")
public class LogisticsShipSprRes {
    @ApiModelProperty(value = "地址id")
    private Integer id;

    @ApiModelProperty(value = "发件人")
    private String addresser;

    @ApiModelProperty(value = "电话号码")
    private String phoneNumber;

    @ApiModelProperty(value = "省名称")
    private String provinceName;

    @ApiModelProperty(value = "省编码")
    private String provinceCode;

    @ApiModelProperty(value = "市名称")
    private String cityName;

    @ApiModelProperty(value = "市编码")
    private String cityCode;

    @ApiModelProperty(value = "区县名称")
    private String areaName;

    @ApiModelProperty(value = "区县编码")
    private String areaCode;

    @ApiModelProperty(value = "乡镇街编码")
    private String streetCode;

    @ApiModelProperty(value = "乡镇街名称")
    private String streetName;

    @ApiModelProperty(value = "详细地址")
    private String addressDetail;

    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @ApiModelProperty(value = "是否是默认地址1.默认2.非默认  默认的是非默认")
    private Integer isDefault;

}

package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 物流地址修改请求对象
 */
@Data
public class LogisticsAddressUpdateReq  extends BaseReqData {

    /**
     * 物流地址id
     */
    @NotBlank(message = "物流地址id不能为空")
    private String addressId;

    /**
     * 收件人姓名
     */
    @NotBlank(message = "收件人姓名不能为空")
    private String recipientName;

    /**
     * 收件人电话
     */
    @NotBlank(message = "收件人电话不能为空")
    @Pattern(regexp = "1[0-9]{10}",message = "请输入正确的手机号")
    private String recipientPhone;

    /**
     * 省名称
     */
    @NotBlank(message = "省名称不能为空")
    private String provinceName;

    /**
     * 省编码
     */
    @NotBlank(message = "收件人省编码不能为空")
    private String provinceCode;

    /**
     * 市名称
     */
    @NotBlank(message = "市名称不能为空")
    private String cityName;

    /**
     * 市编码
     */
    @NotBlank(message = "市编码不能为空")
    private String cityCode;

    /**
     * 区县名称
     */
    @NotBlank(message = "区县名称不能为空")
    private String areaName;

    /**
     * 区县编码
     */
    @NotBlank(message = "区县编码不能为空")
    private String areaCode;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String addressDetail;

    /**
     * 是否为默认地址：1、默认地址；2、非默认
     */
    private Integer isDef;

}

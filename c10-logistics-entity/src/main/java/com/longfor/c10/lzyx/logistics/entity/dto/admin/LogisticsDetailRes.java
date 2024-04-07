package com.longfor.c10.lzyx.logistics.entity.dto.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class LogisticsDetailRes {
    @ApiModelProperty(value = "子订单号")
    private String childOrderId;
    @ApiModelProperty(value = "下单用户id")
    private String userId;
    @ApiModelProperty(value = "下单渠道id")
    private String channelId;
    @ApiModelProperty(value = "自提码")
    private String pickupCode;
    @ApiModelProperty(value = "自提二维码地址")
    private String pickupQrcodeUrl;
    @ApiModelProperty(value = "核销类型(0、商场核销 1、商户核销)")
    private Integer verificationType;
    @ApiModelProperty(value = "实际核销店铺id")
    private String verificationShopId;
    @ApiModelProperty(value = "实际核销项目id")
    private String verificationStoreId;
    @ApiModelProperty(value = "快递单号")
    private String deliveryNo;
    @ApiModelProperty(value = "快递公司名称")
    private String deliveryCompanyName;
    @ApiModelProperty(value = "快递公司编码")
    private String deliveryCompanyCode;
    @ApiModelProperty(value = "发货时间")
    private String deliveryTime;
    @ApiModelProperty(value = "取消发货时间")
    private String cancelDeliveryTime;
    @ApiModelProperty(value = "备注")
    private String deliveryRemarks;
    @ApiModelProperty(value = "快递操作人")
    private String deliveryOperator;
    @ApiModelProperty(value = "快递操作人id")
    private String deliveryOperatorId;
    @ApiModelProperty(value = "快递操作时间")
    private String deliveryOperatorTime;
    @ApiModelProperty(value = "物流类型 (1：门店自提，2：平台物流，3：商家物流)")
    private Integer logisticsType;
    @ApiModelProperty(value = "核销/签收时间")
    private String logisticsVerifyTime;
    @ApiModelProperty(value = "核销操作人/签收人")
    private String logisticsVerifyPeople;
    @ApiModelProperty(value = "核销操作人id")
    private String logisticsVerifyPeopleId;
    @ApiModelProperty(value = "物流状态")
    private Integer logisticsStatus;
    @ApiModelProperty(value = "所属主体类型(1：商场， 2：商户)")
    private Integer ownerType;
    //收件人信息
    @ApiModelProperty(value = "收件人姓名")
    private String receiptName;
    @ApiModelProperty(value = "收件人电话")
    private String receiptPhone;
    @ApiModelProperty(value = "收件人省名称")
    private String receiptProvince;
    @ApiModelProperty(value = "收件人市名称")
    private String receiptCity;
    @ApiModelProperty(value = "收件人区县名称")
    private String receiptArea;
    @ApiModelProperty(value = "收件人详细地址")
    private String receiptAddress;
}

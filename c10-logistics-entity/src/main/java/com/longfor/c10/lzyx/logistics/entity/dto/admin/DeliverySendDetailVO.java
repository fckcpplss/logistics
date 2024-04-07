package com.longfor.c10.lzyx.logistics.entity.dto.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 发货列表详情VO
 */
@Data
public class DeliverySendDetailVO {

    @ApiModelProperty(value = "运营组织")
    private String operOrgName;

    @ApiModelProperty(value = "供应商名称")
    private String shopName;

    @ApiModelProperty(value = "子订单id")
    private String childOrderId;

    @ApiModelProperty(value = "运单号")
    private String deliveryNo;

    @ApiModelProperty(value = "发货时间")
    private Date deliveryTime;

    @ApiModelProperty(value = "订单创建时间")
    private String orderCreateTime;

    @ApiModelProperty(value = "物流公司")
    private String company;

    @ApiModelProperty(value = "物流账号")
    private String account;

    @ApiModelProperty(value = "运费承担方")
    private String logisticsType;

    @ApiModelProperty(value = "物流状态")
    private String logisticsStatus;

    @ApiModelProperty(value = "是否取消发货")
    private Boolean ifCancel;

    @ApiModelProperty(value = "发货人姓名")
    private String sendName;

    @ApiModelProperty(value = "发货人电话")
    private String sendPhone;

    @ApiModelProperty(value = "发货人地址")
    private String sendAddress;

    @ApiModelProperty(value = "收货人姓名")
    private String receiptName;

    @ApiModelProperty(value = "收货人电话")
    private String receiptPhone;

    @ApiModelProperty(value = "收货人地址")
    private String receiptAddress;

    @ApiModelProperty(value = "商品列表")
    private List<GoodsVO> orderGoodsList = new ArrayList<>();

    /**
     * 销售模式
     */
    private Integer sellType;
    /**
     * 销售模式展示
     */
    private String sellTypeShow;

    /**
     * 订单描述
     */
    private String orderDesc;
}

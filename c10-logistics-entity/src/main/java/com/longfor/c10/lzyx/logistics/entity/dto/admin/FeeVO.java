package com.longfor.c10.lzyx.logistics.entity.dto.admin;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 运费列表实体类
 * @author: 赵亚龙
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeVO implements Serializable {

    @ApiModelProperty(value = "ID")
    @ExcelIgnore
    private Long id;

    @ApiModelProperty(value = "运营组织名称")
    @Excel(name = "运营组织",width = 20)
    private String orgName;

    @ApiModelProperty(value = "运单号")
    @Excel(name = "运单号",width = 20)
    private String deliveryNo;

    @ApiModelProperty(value = "子订单ID")
    @Excel(name = "订单编号",width = 20)
    private String childOrderId;

    @ApiModelProperty(value = "运营组织ID")
    @ExcelIgnore
    private String orgId;

    @ApiModelProperty(value = "供应商名称")
    @Excel(name = "供应商名称",width = 20)
    private String shopName;

    @ApiModelProperty(value = "供应商ID")
    @ExcelIgnore
    private String shopId;

    @ApiModelProperty(value = "物流公司名称")
    @Excel(name = "物流公司",width = 20)
    private String logisticsCompanyName;

    @ApiModelProperty(value = "物流公司编码")
    @ExcelIgnore
    private String logisticsCompanyCode;

    @ApiModelProperty(value = "标准费用（折前费用）")
    @Excel(name = "运费折前(元)",width = 20)
    private String standardFee;

    @ApiModelProperty(value = "创建时间-下单时间")
    @Excel(name = "下单时间", format = "yyyy-MM-dd HH:mm:ss",width = 20)
    private Date createTime;

    @ApiModelProperty(value = "折后费用")
    @Excel(name = "运费折后(元)",width = 20)
    private String discountFee;

    @ApiModelProperty(value = "揽收时间")
    @Excel(name = "揽收时间", format = "yyyy-MM-dd HH:mm:ss",width = 20)
    private Date pickupTime;

    @ApiModelProperty(value = "签收时间")
    @Excel(name = "签收时间", format = "yyyy-MM-dd HH:mm:ss",width = 20)
    private Date signTime;

    @ApiModelProperty(value = "商品名称")
    @Excel(name = "商品名称",width = 20)
    private String goodsName;

    @ApiModelProperty(value = "自定义物流状态，0：待下单 1：已下单 2:待揽收 3:已揽收 4:运输中 5:派送中 6:已签收 7:签收失败")
    @ExcelIgnore
    private Integer logisticsStatus;

    @Excel(name = "物流状态",width = 20)
    private String logisticsStatusShow;

    @ApiModelProperty(value = "物流账户")
    @Excel(name = "物流账户",width = 20)
    private String logisticsAccount;

    @ApiModelProperty(value = "运费承担方1. 平台，2商家")
    @ExcelIgnore
    private Integer feeBearer;

    @Excel(name = "运费承担方",width = 20)
    private String feeBearerShow;

    @ApiModelProperty(value = "发货地址")
    @Excel(name = "发货地址",width = 20)
    private String shipAddress;

    @ApiModelProperty(value = "收货地址")
    @Excel(name = "收货地址",width = 20)
    private String deliveryAddress;

    @ApiModelProperty(value = "结算类型: 1.现结、2.月结")
    @ExcelIgnore
    private String settlementType;

    @Excel(name = "结算方式",width = 20)
    private String settlementTypeShow;

    @ApiModelProperty("付款方式：1.寄付，2.到付")
    @ExcelIgnore
    private String paymentType;

    @Excel(name = "付款方式",width = 20)
    private String paymentTypeShow;
    /**
     * 物流类型 (1：平台物流，2：商家物流）
     */
    @ExcelIgnore
    private Integer logisticsType;

    /**
     * 供应商物流配置id
     */
    @ExcelIgnore
    private Integer shopLogisticsId;
    /**
     * 快递公司编码
     */
    @ExcelIgnore
    private String companyCode;

    /**
     * 航道编码
     */
    @ExcelIgnore
    private String bizChannelCode;

    /**
     * 销售模式
     */
    @ExcelIgnore
    private Integer sellType;

    /**
     * 销售模式展示
     */
    @Excel(name = "销售模式",width = 20)
    private String sellTypeShow;

    /**
     * 销售模式展示
     */
    @Excel(name = "订单备注",width = 20)
    private String orderDesc;

}

package com.longfor.c10.lzyx.logistics.client.entity.vo.admin;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 运费列表实体类
 * @author: 赵亚龙
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ColumnWidth(value = 20)
public class FeeVO implements Serializable {

    @ApiModelProperty(value = "ID")
    @ExcelIgnore
    private Long id;

    @ApiModelProperty(value = "运营组织名称")
    @ExcelProperty(value = "运营组织")
    private String orgName;

    @ApiModelProperty(value = "运单号")
    @ExcelProperty(value = "运单号")
    private String deliveryNo;

    @ApiModelProperty(value = "子订单ID")
    @ExcelProperty(value = "订单编号")
    private String childOrderId;

    @ApiModelProperty(value = "运营组织ID")
    @ExcelIgnore
    private String orgId;

    @ApiModelProperty(value = "供应商名称")
    @ExcelProperty(value = "供应商名称")
    private String shopName;

    @ApiModelProperty(value = "供应商ID")
    @ExcelIgnore
    private String shopId;

    @ApiModelProperty(value = "物流公司名称")
    @ExcelProperty(value = "物流公司")
    private String logisticsCompanyName;

    @ApiModelProperty(value = "物流公司编码")
    @ExcelIgnore
    private String logisticsCompanyCode;

    @ApiModelProperty(value = "标准费用（折前费用）")
    @ExcelProperty(value = "运费折前(元)")
    private String standardFee;

    @ApiModelProperty(value = "创建时间-下单时间")
    @ExcelProperty(value = "下单时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "折后费用")
    @ExcelProperty(value = "运费折后(元)")
    private String discountFee;

    @ApiModelProperty(value = "揽收时间")
    @ExcelIgnore
    private Date pickupTime;

    @ApiModelProperty(value = "签收时间")
    @ExcelIgnore
    private Date signTime;

    @ApiModelProperty(value = "商品名称")
    @ExcelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "自定义物流状态，0：待下单 1：已下单 2:待揽收 3:已揽收 4:运输中 5:派送中 6:已签收 7:签收失败")
    @ExcelIgnore
    private Integer logisticsStatus;

    @ExcelProperty(value = "物流状态")
    private String logisticsStatusShow;

    @ApiModelProperty(value = "物流账户")
    @ExcelProperty(value = "物流账户")
    private String logisticsAccount;

    @ApiModelProperty(value = "运费承担方1. 平台，2商家")
    @ExcelIgnore
    private Integer feeBearer;

    @ExcelProperty(value = "运费承担方")
    private String feeBearerShow;

    @ApiModelProperty(value = "发货地址")
    @ExcelProperty(value = "发货地址")
    @ColumnWidth(value = 30)
    private String shipAddress;

    @ApiModelProperty(value = "收货地址")
    @ExcelProperty(value = "收货地址")
    @ColumnWidth(value = 30)
    private String deliveryAddress;

    @ApiModelProperty(value = "结算类型: 1.现结、2.月结")
    @ExcelIgnore
    private String settlementType;

    @ExcelProperty(value = "结算方式")
    private String settlementTypeShow;

    @ApiModelProperty("付款方式：1.寄付，2.到付")
    @ExcelIgnore
    private String paymentType;

    @ExcelProperty(value = "付款方式")
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
     * 销售模式
     */
    @ExcelIgnore
    private Integer sellType;
    /**
     * 销售模式展示
     */
    @ExcelProperty(value = "销售模式")
    private String sellTypeShow;
}

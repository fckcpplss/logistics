package com.longfor.c10.lzyx.logistics.client.entity.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 物流费用DTO
 * @author zhaoyl
 * @date 2021/12/6 下午6:05
 * @since 1.0
 */
@Data
public class LogisticFeeInfoDTO {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 运单号
     */
    @ApiModelProperty(value = "运单号")
    private String deliveryNo;

    /**
     * 子订单ID
     */
    @ApiModelProperty(value = "子订单ID")
    private String childOrderId;
    /**
     * 运营组织ID
     */
    @ApiModelProperty(value = "运营组织ID")
    private String orgId;
    /**
     * 运营组织名称
     */
    @ApiModelProperty(value = "运营组织名称")
    private String orgName;

    /**
     * 商铺ID
     */
    @ApiModelProperty(value = "商铺ID")
    private String shopId;
    /**
     * 物流公司名称
     */
    @ApiModelProperty(value = "物流公司名称")
    private String logisticsCompanyName;

    /**
     * 物流公式编码
     */
    @ApiModelProperty(value = "物流公式编码")
    private String logisticsCompanyCode;

    /**
     * 运费承担方1. 平台，2商家
     */
    @ApiModelProperty(value = "运费承担方1")
    private Byte feeBearer;

    /**
     * 标准费用（折前费用）
     */
    @ApiModelProperty(value = "标准费用")
    private BigDecimal standardFee;

    /**
     * 折后费用
     */
    @ApiModelProperty(value = "折后费用")
    private BigDecimal discountFee;

    /**
     * 付款类型 1 寄付 2 到付 3 第三方付
     */
    @ApiModelProperty(value = "付款类型")
    private Byte paymentType;

    /**
     * 结算类型 1 现结 2 月结
     */
    @ApiModelProperty(value = "结算类型")
    private Byte settlementType;
    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    /**
     * 自定义物流状态，0：待下单 1：已下单 2:待揽收 3:已揽收 4:运输中 5:派送中 6:已签收 7:签收失败
     */
    @ApiModelProperty(value = "自定义物流状态")
    private Byte logisticsStatus;

    /**
     * 物流账户
     */
    @ApiModelProperty(value = "物流账户")
    private String logisticsAccount;

    /**
     * 发货地址
     */
    @ApiModelProperty(value = "收货地址")
    private String shipAddress;

    /**
     * 收货地址
     */
    @ApiModelProperty(value = "收货地址")
    private String deliveryAddress;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 删除标志 0:未删除 1:已删除
     */
    @ApiModelProperty(value = "删除标志")
    private Byte deleteStatus;

    /**
     * 创建人账号
     */
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人姓名
     */
    @ApiModelProperty(value = "创建人姓名")
    private String creatorName;

    /**
     * 更新人账号
     */
    @ApiModelProperty(value = "更新人账号")
    private String updateAccount;

    /**
     * '更新人姓名'
     */
    @ApiModelProperty(value = "更新人账号")
    private String updateName;
    @ApiModelProperty(value = "版本")
    private Integer version;
}

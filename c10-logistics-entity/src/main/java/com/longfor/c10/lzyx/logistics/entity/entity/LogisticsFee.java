package com.longfor.c10.lzyx.logistics.entity.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 运单费用
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
@Getter
@Setter
@TableName("logistics_fee")
public class LogisticsFee implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 运单号
     */
    private String deliveryNo;

    /**
     * 子订单ID
     */
    private String childOrderId;

    /**
     * 运营组织名称
     */
    private String orgName;

    /**
     * 商铺ID
     */
    private String shopId;

    /**
     * 物流公司名称
     */
    private String logisticsCompanyName;

    /**
     * 物流公式编码
     */
    private String logisticsCompanyCode;

    /**
     * 运费承担方1. 平台，2商家
     */
    private Integer feeBearer;

    /**
     * 标准费用（折前费用）
     */
    private BigDecimal standardFee;

    /**
     * 折后费用
     */
    private BigDecimal discountFee;

    /**
     * 付款类型 1 寄付 2 到付 3 第三方付
     */
    private Integer paymentType;

    /**
     * 结算类型 1 现结 2 月结
     */
    private Integer settlementType;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 自定义物流状态，0：待下单 1：已下单 2:待揽收 3:已揽收 4:运输中 5:派送中 6:已签收 7:签收失败
     */
    private Integer logisticsStatus;

    /**
     * 物流账户
     */
    private String logisticsAccount;

    /**
     * 发货地址
     */
    private String shipAddress;

    /**
     * 收货地址
     */
    private String deliveryAddress;

    /**
     * 删除标志 0:未删除 1:已删除
     */
    private Integer deleteStatus;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
      @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 创建人账号
     */
    private String creatorAccount;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 更新人账号
     */
    private String updateAccount;

    /**
     * 更新人姓名
     */
    private String updateName;

    /**
     * 版本控制
     */
    private Integer version;

    /**
     * 运营组织ID
     */
    private String orgId;


}

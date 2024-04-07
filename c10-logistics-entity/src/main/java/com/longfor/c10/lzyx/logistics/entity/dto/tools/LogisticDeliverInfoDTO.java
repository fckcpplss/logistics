package com.longfor.c10.lzyx.logistics.entity.dto.tools;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 运维工具，物流运单DTO
 * @author zhaoyl
 * @date 2021/12/6 下午1:44
 * @since 1.0
 */
@Data
public class LogisticDeliverInfoDTO{
    /**
     * 主键，雪花算法生成
     */
    private Long id;

    /**
     * 物流单主键
     */
    private Long logisticsOrderId;

    /**
     * 商品表主键集合
     */
    private String goodsIds;

    /**
     * 公司编码
     */
    private String companyCode;

    /**
     * 物流类型 (1：平台物流，2：商家物流）
     */
    private Byte logisticsType;

    /**
     * 商户物流账号配置表主键，logistics_type=2，且选择平台物流、自有物流账户顺丰时不为空
     */
    private Integer shopLogisticsId;

    /**
     * 自定义物流状态，0：待下单 1：已下单 2:待揽收 3:已揽收 4:运输中 5:派送中 6:已签收 7:签收失败
     */
    private Byte logisticsStatus;

    /**
     * 运单号
     */
    private String deliveryNo;

    /**
     * 是否取消发货 1:是 0:否
     */
    private Boolean ifCancel;

    /**
     * 发件人地址id
     */
    private Integer sendAddressId;

    /**
     * 发货人姓名
     */
    private String sendName;

    /**
     * 发货人电话
     */
    private String sendPhone;

    /**
     * 发货人省名称
     */
    private String sendProvince;

    /**
     * 发货人市名称
     */
    private String sendCity;

    /**
     * 发货人区名称
     */
    private String sendArea;

    /**
     * 发货人详细地址
     */
    private String sendAddress;

    /**
     * 收件人地址id
     */
    private String receiptAddressId;

    /**
     * 收货人姓名
     */
    private String receiptName;

    /**
     * 收货人电话
     */
    private String receiptPhone;

    /**
     * 收货人省名称
     */
    private String receiptProvince;

    /**
     * 收货人市名称
     */
    private String receiptCity;

    /**
     * 收货人区名称
     */
    private String receiptArea;

    /**
     * 收货人详细地址
     */
    private String receiptAddress;

    /**
     * 更新运单号对应的费用的状态，0待更新，1正在更新，2已更新，3更新失败
     */
    private Byte updateFeeStatus;

    /**
     * 发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deliveryTime;

    /**
     * 签收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime signTime;

    /**
     * 快递揽收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime collectTime;

    /**
     * 记录快递单号时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime recordDeliveryNoTime;

    /**
     * 公司名
     */
    private String companyName;

    /**
     * 物流类型中文展示
     */
    private String logisticsTypeShow;

    /**
     * 物流状态中文展示
     */
    private String logisticsStatusShow;

    /**
     * 是否取消发货
     */
    private String ifCancelShow;

    /**
     * 运费更新状态
     */
    private String updateFeeStatusShow;

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
    private Byte deleteStatus;

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
     * '更新人姓名'
     */
    private String updateName;

    private Integer version;



}

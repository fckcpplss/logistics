package com.longfor.c10.lzyx.logistics.entity.dto.tools;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 运维工具，物流订单DTO
 * @author zhaoyl
 * @date 2021/12/6 下午1:44
 * @since 1.0
 */
@Data
public class LogisticOrderInfoDTO{
    /**
     * 主键，雪花算法生成
     */
    private Long id;

    /**
     * 子订单id
     */
    private String childOrderId;

    /**
     * 父订单号
     */
    private String orderId;

    /**
     * shop名称
     */
    private String shopName;

    /**
     * 商品订单状态，11 ：未发货 21:部分发货 12:已发货 13：已签收  14：签收失败 22：已退款
     */
    private Integer goodsOrderStatus;

    /**
     * 是否退单：0 否；1 是；
     */
    private Boolean ifRefund;

    /**
     * 运营组织ID
     */
    private String orgId;

    /**
     * 运营组织名称
     */
    private String orgName;

    /**
     * 下单用户id
     */
    private String userId;
    /**
     * 下单渠道id channel_id varchar(255)
     */
    private String channelId;
    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 备注
     */
    private String deliveryRemarks;

    /**
     * 所属商铺id
     */
    private String shopId;

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
     * 物流发货退换货:1、发货物流，2.退货物流
     */
    private Integer sendReturn;
    /**
     * 删除标志 0:未删除 1:已删除
     */
    protected Byte deleteStatus;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected LocalDateTime updateTime;

    /**
     * 创建人账号
     */
    protected String creatorAccount;

    /**
     * 创建人姓名
     */
    protected String creatorName;

    /**
     * 更新人账号
     */
    protected String updateAccount;

    /**
     * '更新人姓名'
     */
    protected String updateName;

    protected Integer version;

    /**
     * 订单状态中文展示
     */
    private String goodsOrderStatusShow;

    /**
     * 是否退款中文展示
     */
    private String ifRefundShow;
}

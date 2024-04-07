package com.longfor.c10.lzyx.logistics.entity.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 物流订单商品信息表
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
@Getter
@Setter
@TableName("logistics_order")
public class LogisticsOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，雪花算法生成
     */
      private Long id;

    /**
     * 子订单id
     */
    private String childOrderId;

    /**
     * 渠道标识
     */
    private String bizChannelCode;

    /**
     * 订单描述
     */
    private String orderDesc;

    /**
     * 父订单号
     */
    private String orderId;

    /**
     * 商品订单状态，11 ：未发货 21:部分发货 12:已发货 13：已签收  14：签收失败 62：仅退款 63：退款中
     */
    private Integer goodsOrderStatus;

    /**
     * 物流发货退换货:1、发货物流，2.退货物流
     */
    private Integer sendReturn;

    /**
     * 是否退单：0 否；1 是；
     */
    private Integer ifRefund;

    /**
     * 下单用户id
     */
    private String userId;

    /**
     * 下单渠道id
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
     * 数据版本号
     */
    private Integer version;

    /**
     * 运营组织ID
     */
    private String orgId;

    /**
     * 运营组织名称
     */
    private String orgName;

    /**
     * 商铺名称
     */
    private String shopName;

    /**
     * 确认收货状态
     */
    private Integer signConfirmFlag;

    /**
     * 确收时间
     */
    private Date signConfirmTime;

}

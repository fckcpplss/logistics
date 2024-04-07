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
 * 物流订单运单表
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
@Getter
@Setter
@TableName("logistics_delivery")
public class LogisticsDelivery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，雪花算法生成
     */
      private Long id;

    /**
     * 物流订单ID
     */
    private Long logisticsOrderId;

    /**
     * 商品表主键集合
     */
    private String goodsIds;

    /**
     * 快递公司编码
     */
    private String companyCode;

    /**
     * 物流类型 (1：平台物流，2：商家物流）
     */
    private Integer logisticsType;

    /**
     * 商户物流账号配置表主键，logistics_type=2，且选择自有物流账户顺丰时不为空
     */
    private String shopLogisticsId;

    /**
     * 自定义物流状态，0：待下单 1：已下单 2:待揽收 3:已揽收 4:运输中 5:派送中 6:已签收 7:签收失败
     */
    private Integer logisticsStatus;

    /**
     * 运单号
     */
    private String deliveryNo;

    /**
     * 是否取消发货 1:是 0:否
     */
    private Integer ifCancel;

    /**
     * 发件人地址id
     */
    private String sendAddressId;

    /**
     * 收件人地址id
     */
    private String receiptAddressId;

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
     * 发货时间,非京东、顺丰时需要商户手动填写，其余与创建时间相同
     */
    private Date deliveryTime;

    /**
     * 签收时间
     */
    private Date signTime;

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
     * 揽收时间
     */
    private Date collectTime;

    /**
     * 对于顺丰和京东是下单时间，对于快递100是填写运单号的时间
     */
    private Date recordDeliveryNoTime;

    /**
     * 京东物流更新费用状态，0没有更新过，1正在更新，2更新失败，3更新成功
     */
    private Integer updateFeeStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 物流异常附件
     */
    private String attachment1;

    /**
     * 子单号
     */
    @TableField(exist = false)
    private String childOrderId;


}

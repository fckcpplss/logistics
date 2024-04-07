package com.longfor.c10.lzyx.logistics.entity.entity;

import com.baomidou.mybatisplus.annotation.*;

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
@TableName("logistics_order_goods")
public class LogisticsOrderGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，雪花算法生成
     */
      private Long id;

    /**
     * 物流单主键
     */
    private Long logisticsOrderId;

    /**
     * 运单表主键
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long logisticsDeliveryId;

    /**
     * 商品id
     */
    private String goodsId;

    /**
     * 商品类型：1、商品, 2、卡券
     */
    private Integer goodsType;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品描述
     */
    private String goodsDesc;

    /**
     * 商品数量
     */
    private Integer goodsNum;

    /**
     * 商品图片地址
     */
    private String goodsImgUrl;

    /**
     * 商品规格编号
     */
    private String skuId;

    /**
     * 物流类型 1:平台物流 2:商户物流
     */
    private Integer logisticsType;

    /**
     * 商品规格
     */
    private String skuSpecs;

    /**
     * 删除标志 0:未删除 1:已删除
     */
    private Integer deleteStatus;

    /**
     * 业务类型：1.商家取消，2.用户退款（仅退款），3.用户退款（退货退款），4.签收失败，5.运输异常
     */
    private Integer businessType;

    /**
     * 备注
     */
    private String remark;

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


}

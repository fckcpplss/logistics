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
 * 物流状态映射表
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
@Getter
@Setter
@TableName("logistics_order_status_mapping")
public class LogisticsOrderStatusMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
      private Long id;

    /**
     * 节点操作状态码
     */
    private String operationCode;

    /**
     * 节点操作描述
     */
    private String operationDesc;

    /**
     * 公司编码
     */
    private String companyCode;

    /**
     * 物流状态LogisticsStatusEnum 0：待下单 1：已下单 2:待揽收 3:已揽收 4:运输中 5:派送中 6:已签收 7:签收失败
     */
    private Integer logisticsStatus;

    /**
     * 订单状态OrderStatusEnum  11:未发货 12:已发货 13:已签收 14:签收失败 15:签收超时	

     */
    private Integer orderStatus;

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


}

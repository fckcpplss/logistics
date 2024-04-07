package com.longfor.c10.lzyx.logistics.entity.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 供应商物流配置模板
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
@Getter
@Setter
@TableName("shop_logistics_config")
public class ShopLogisticsConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物流代号
     */
    private String logisticsCode;

    /**
     * 物流名称
     */
    private String logisticsName;

    /**
     * 物流类型
     */
    private String typeCode;

    /**
     * 物流类型名称
     */
    private String typeName;

    /**
     * 商户自有物流账户
     */
    private String account;

    /**
     * 商户自有物流appKey（顺丰接口使用）
     */
    private String appKey;

    /**
     * 是否使用默认的物流账号和appKey，目前是顺丰+商户选项为否，其他都为是，1：是，0：否
     */
    private Integer useDefault;

    /**
     * 根据这个排序
     */
    private Integer orderNumber;

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

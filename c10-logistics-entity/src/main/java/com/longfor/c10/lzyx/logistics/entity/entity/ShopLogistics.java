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
 * 供应商物流配置
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
@Getter
@Setter
@TableName("shop_logistics")
public class ShopLogistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 供应商ID
     */
    private String shopId;

    /**
     * 商户物流配置模板ID
     */
    private Integer shopLogisticsConfigId;

    /**
     * 商户自有物流账户
     */
    private String account;

    /**
     * 商户自有物流appKey（顺丰接口使用）
     */
    private String appKey;

    /**
     * 商户顺丰产品类别，1 顺丰特快 2 顺丰标快 6 顺丰即日 16 大闸蟹专递 231 陆运包裹 247 电商标快
     */
    private String expressType;

    /**
     * 是否被选择启用，1启用，0不启用
     */
    private Integer choose;

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

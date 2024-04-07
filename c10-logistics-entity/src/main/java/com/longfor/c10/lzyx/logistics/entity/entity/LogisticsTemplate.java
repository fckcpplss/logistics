package com.longfor.c10.lzyx.logistics.entity.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("logistics_template")
public class LogisticsTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板id
     */
      @TableId(value = "template_id", type = IdType.AUTO)
    private Integer templateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 渠道id
     */
    private String channelId;

    /**
     * 地址id
     */
    private String addressId;

    /**
     * 运费承担者（1、用户承担，2、商家承担）
     */
    private Integer freightBearer;

    /**
     * 计价方式（1、按重量计费，2、按件数计费，3、按体积计费）
     */
    private Integer pricingMethod;

    /**
     * 模板类型：1、商家模板，2、项目模板
     */
    private Integer ownerType;

    /**
     * 所属id：shopId projectId
     */
    private String ownerId;

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
     * 0:未删除；1:删除
     */
    private Integer isDelete;


}

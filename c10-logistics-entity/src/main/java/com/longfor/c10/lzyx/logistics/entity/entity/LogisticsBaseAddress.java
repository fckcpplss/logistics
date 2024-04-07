package com.longfor.c10.lzyx.logistics.entity.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 物流地址基本表
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
@Getter
@Setter
@TableName("logistics_base_address")
public class LogisticsBaseAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地址id
     */
    @TableId
    private String addressId;

    /**
     * 所属id：userId shopId projectId
     */
    private String ownerId;

    /**
     * 渠道id   user用户必填
     */
    private String channelId;

    /**
     * 地址类型：0 用户收货地址；1 商铺发货地址; 2 项目发货地址；3 商铺退货地址；4 项目退货地址
     */
    private Integer ownerType;

    /**
     * 联系人姓名
     */
    private String personName;

    /**
     * 联系人电话
     */
    private String personPhone;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 省编码
     */
    private String provinceCode;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 市编码
     */
    private String cityCode;

    /**
     * 区县名称
     */
    private String areaName;

    /**
     * 区编码
     */
    private String areaCode;

    /**
     * 详细地址
     */
    private String addressDetail;

    /**
     * 0:未删除；1:删除
     */
    private Integer isDelete;

    /**
     * 是否为默认地址：1默认地址；2非默认
     */
    private Integer isDefault;

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
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;


}

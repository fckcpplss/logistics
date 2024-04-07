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
 * 物流公司
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
@Getter
@Setter
@TableName("logistics_delivery_company")
public class LogisticsDeliveryCompany implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，雪花算法生成
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 快递公司编码
     */
    private String companyCode;

    /**
     * 快递公司名称
     */
    private String companyName;

    /**
     * 是否可获取物流轨迹 0:否 1:是
     */
    private Integer isSupportDeliveryPath;

    /**
     * 0:失效；1:有效
     */
    private Integer status;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
      @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 首字母
     */
    private String initials;

    /**
     * 排序数字
     */
    private Integer orderNo;


}

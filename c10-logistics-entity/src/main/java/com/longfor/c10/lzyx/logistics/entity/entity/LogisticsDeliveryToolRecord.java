package com.longfor.c10.lzyx.logistics.entity.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 物流运单号修改记录表
 * </p>
 *
 * @author liuxin41
 * @since 2022-06-15
 */
@Getter
@Setter
@TableName("logistics_delivery_tool_record")
public class LogisticsDeliveryToolRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，雪花算法生成
     */
      private Long id;

    /**
     * 物流订单ID
     */
    private Long childOrderId;

    /**
     * 所属商铺id
     */
    private String shopId;

    /**
     * 商铺名称
     */
    private String shopName;

    /**
     * 老快递公司编码
     */
    private String oldCompanyCode;

    /**
     * 新快递公司名称
     */
    private String newCompanyCode;

    /**
     * 旧物流运费承担方 (1：平台物流，2：商家物流）
     */
    private Integer oldLogisticsType;

    /**
     * 新物流运费承担方 (1：平台物流，2：商家物流）
     */
    private Integer newLogisticsType;

    /**
     * 旧运单号
     */
    private String oldDeliveryNo;

    /**
     * 新运单号
     */
    private String newDeliveryNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 物流异常附件
     */
    private String attachment;

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
    private String creatorOa;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 更新人账号
     */
    private String updateOa;

    /**
     * 更新人姓名
     */
    private String updateName;


}

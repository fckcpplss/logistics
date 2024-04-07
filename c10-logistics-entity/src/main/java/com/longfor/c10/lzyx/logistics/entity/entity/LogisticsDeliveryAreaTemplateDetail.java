package com.longfor.c10.lzyx.logistics.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 配送区域模板详情
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
@Getter
@Setter
@TableName("logistics_delivery_area_template_detail")
public class LogisticsDeliveryAreaTemplateDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配送区域模板ID
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 配送区域模板
     */
    private Long templateId;

    /**
     * 地点编码
     */
    private String code;

    /**
     * 地点名称
     */
    private String name;

    /**
     * 上级地点编码
     */
    private String parentCode;

    /**
     * 创建时间
     */
    private Long crtTime;

    /**
     * 地点类型，1：大区，2：省份，3：市，4：区/县,5：乡/镇,6：街道/村庄
     */
    private Integer type;


}

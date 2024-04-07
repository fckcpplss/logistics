package com.longfor.c10.lzyx.logistics.entity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 配送区域模板
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
@Getter
@Setter
@TableName("logistics_delivery_area_template")
public class LogisticsDeliveryAreaTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配送区域模板ID
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 配送区域模板名称
     */
    private String name;

    /**
     * 运行组织编码
     */
    private String operOrgCode;

    /**
     * 运营组织名称
     */
    private String operOrgName;

    /**
     * 创建人ID
     */
    private String crtUid;

    /**
     * 创建人名称
     */
    private String crtUname;

    /**
     * 修改人ID
     */
    private String updUid;

    /**
     * 修改人名称
     */
    private String updUname;

    /**
     * 创建时间
     */
    private Long crtTime;

    /**
     * 更新时间
     */
    private Long updTime;

    /**
     * 是否已删除 0、已删除 1、未删除
     */
    private Integer isDel;

    /**
     * 版本号
     */
    private Long ver;

    /**
     * 配送区域模板描述
     */
    private String desc;


}

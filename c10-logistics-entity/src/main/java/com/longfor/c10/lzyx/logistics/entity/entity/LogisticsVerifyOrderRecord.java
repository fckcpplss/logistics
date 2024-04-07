package com.longfor.c10.lzyx.logistics.entity.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 运维工具-批量核销日志表
 * </p>
 *
 * @author zhaoyl
 * @since 2022-04-14
 */
@Getter
@Setter
@TableName("logistics_verify_order_record")
public class LogisticsVerifyOrderRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
      private Long id;

    /**
     * 子订单id
     */
    private String childOrderId;

    /**
     * 订单文件名称
     */
    private String orderFileName;

    /**
     * 订单文件url
     */
    private String orderFileUrl;

    /**
     * 业务确认截图
     */
    private String verifyPictureInfo;

    /**
     * 创建人时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createUser;
}

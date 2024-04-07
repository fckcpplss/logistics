package com.longfor.c10.lzyx.logistics.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 物流签收超期实体类
 * @author zhaoyl
 * @date 2022/4/27 下午6:36
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsDeliveryOvertimeSignedDTO{
    /**
     * 子单id
     */
    private String childOrderId;

    /**
     * 物流订单ID
     */
    private Long logisticsOrderId;

    /**
     * 运单号
     */
    private String deliveryNo;

    /**
     * 签收时间
     */
    private String signTime;
}

package com.longfor.c10.lzyx.logistics.entity.dto.mq;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单状态发生变化推送数据DTO
 * @author zhaoyalong
 */
@Data
public class OrderStatusChangePushDTO implements Serializable {
    /**
     * 订单编号
     */
    private String childOrderId;

    /**
     * 物流订单id
     */
    private Long logisticsId;
    /**
     * 11:待发货,12:已发货,13:确认收货（物流签收）,14:签收失败,15:确认收货（系统超时）
     */
    private Integer orderStatus;
    /**
     * 物流类型1，正向物流，2逆向退货/换货物流
     */
    private Integer logisticsType = 1;
    /**
     * 状态改变时间
     */
    private LocalDateTime statusTime;
    /**
     * 退单单号
     */
    private String returnOrderId;

    /**
     * 数据标识，0.确收历史数据，1.确收新数据
     */
    private Integer confirmDataFlag = 0;
}

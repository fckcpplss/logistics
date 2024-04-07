package com.longfor.c10.lzyx.logistics.entity.dto.mq;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物流核销状态发生变化推送数据DTO
 * @author zhaoyalong
 */
@Data
public class OrderVerifyStatusChangePushDTO implements Serializable {
    /**
     * 订单编号
     */
    private String childOrderId;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 状态改变时间
     */
    private String statusTime;
}

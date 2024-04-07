package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.Data;

/**
 * 物流订单更新参数
 * @author zhaoyl
 * @date 2021/12/6 上午11:25
 * @since 1.0
 */
@Data
public class LogisticOrderUpdateReq extends BaseReqData {
    /**
     * 订单编号
     */
    private String childOrderId;

    /**
     * 更新状态
     * 更新is_refund是否退单 0-否 1-是
     * 更新delete_status删除状态 0-未删除 1-已删除
     */
    private Integer updateStatus;
    /**
     * 订单状态 11-待发货 12-已发货
     */
    private Integer orderStatus;


}

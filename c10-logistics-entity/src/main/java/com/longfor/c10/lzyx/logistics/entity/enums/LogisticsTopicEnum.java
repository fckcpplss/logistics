package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物流topic枚举类
 * @author zhaoyl
 */
@Getter
@AllArgsConstructor
public enum LogisticsTopicEnum {
    C10_LOGISTICS_STATUS("c10_logistics_status","物流状态变更"),
    C10_ORDER_CHILD("c10_order_child","子单消息"),
    JD_FEE_MQ_TOPIC("c10_logistics_update_jd_fee","京东费用更新"),
    C10_LOGISTICS_VERIFY_STATUS("c10_logistics_verify_status","物流核销状态变更"),
    ;
    private String code;
    private String desc;
}

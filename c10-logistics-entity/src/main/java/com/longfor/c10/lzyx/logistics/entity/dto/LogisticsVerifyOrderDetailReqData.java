package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 自提/核销订单详情列表请求实体
 * @author zhaoyl
 * @date 2022/4/13 上午11:30
 * @since 1.0
 */
@Data
public class LogisticsVerifyOrderDetailReqData extends BaseReqData{
    /**
     * 订单编号集合
     */
    private List<String> orderNos;

    /**
     * 订单编号
     */
    private String orderNo;

}

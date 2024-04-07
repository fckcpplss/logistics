package com.longfor.c10.lzyx.logistics.client.entity.param;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 自提/核销订单详情返回实体
 * @author zhaoyl
 * @date 2022/4/13 上午11:48
 * @since 1.0
 */
@Data
public class LogisticsVerifyOrderDetailResWithCodeData {
    /**
     * 详情数据
     */
    private List<LogisticsVerifyOrderDetailResData> data;
    /**
     * 错误码
     */
    private Integer code;
}

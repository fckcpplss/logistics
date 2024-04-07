package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 自提/核销订单详情返回实体
 * @author zhaoyl
 * @date 2022/4/13 上午11:48
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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

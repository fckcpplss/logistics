package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuqinglin
 * @date 2021/11/22
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SFGetEstimatedDeliveryTimeResData {
    /**
     * 预计派送时间 yyyy-MM-dd HH:mm:ss
     */
    private String promiseTm;

    /**
     * 顺丰运单号
     */
    private String searchNo;
}

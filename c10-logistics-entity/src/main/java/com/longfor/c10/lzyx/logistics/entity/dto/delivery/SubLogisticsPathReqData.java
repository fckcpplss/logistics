package com.longfor.c10.lzyx.logistics.entity.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoyl
 * @date 2022/1/14 上午11:54
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubLogisticsPathReqData {
    /**
     * 运单号
     */
    private String deliverNo;
    /**
     * 快递公司编码
     */
    private String companyCode;

}

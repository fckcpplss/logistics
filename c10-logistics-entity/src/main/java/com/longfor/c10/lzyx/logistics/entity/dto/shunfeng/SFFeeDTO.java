package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 顺丰运费DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SFFeeDTO implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = 1L;
    /**
     * 费用类型
     */
    private String type;
    /**
     * 费用名称
     */
    private String name;
    /**
     * 费用金额
     */
    private String value;
    /**
     * 付款类型	1-寄付；2-到付；3-第三方付；
     */
    private String paymentTypeCode;
    /**
     * 结算类型	1-现结；2-月结；
     */
    private String settlementTypeCode;
}

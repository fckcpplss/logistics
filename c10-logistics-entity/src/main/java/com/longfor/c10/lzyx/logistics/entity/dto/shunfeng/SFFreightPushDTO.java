package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 顺丰费用推送DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SFFreightPushDTO implements Serializable {
    /**
     * 订单号
     */
    private static final long serialVersionUID = 1L;
    /**
     * 订单没有
     */
    private String orderNo;
    /**
     * 运单号：顺丰下单接口返回的15或12位运单号（母单号），如：SF10116351372291
     */
    private String waybillNo;
    /**
     * 包裹数：托寄物包裹数量
     */
    private int quantity;
    /**
     * 计费重量：包裹计费重量（单位：kg）
     */
    private int meterageWeightQty;
    /**
     * 产品名称。如顺丰特惠、顺丰标快
     */
    private String productName;
    /**
     * 费用：List形式
     */
    private List<FeeInfoDTO> feeList;

    /**
     * 密文
     */
    private String params;
    private String customerAcctCode;
    private String addresseeAddr;
    private String addresseeMobile;
    private String addresseePhone;
    private String consignorAddr;
    private String consignorPhone;
    private String consignorMobile;
}

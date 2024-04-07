package com.longfor.c10.lzyx.logistics.entity.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 预计到达时间req
 * @author: zhaoyalong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliverTimeReqData {
    /**
     * 订单 ID，需保证全局唯一
     */
    private String orderId;
    /**
     * 运单ID
     */
    private String waybillId;
    /**
     * 必填
     * 所选类型
     */
    private String companyCode;
    /**
     * 快递100 出发地信息
     */
    private String from;
    /**
     * 快递100 目的地信息
     */
    private String to;
    /**
     * 快递100 查询 收、寄件人的电话号码（手机和固定电话均可，只能填写一个，顺丰速运和丰网速运必填，其他快递公司选填。如座机号码有分机号，分机号无需上传）
     */
    private String phone;
}

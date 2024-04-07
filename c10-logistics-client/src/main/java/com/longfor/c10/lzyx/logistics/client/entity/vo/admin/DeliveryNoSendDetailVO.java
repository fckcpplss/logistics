package com.longfor.c10.lzyx.logistics.client.entity.vo.admin;

import lombok.Data;

import java.util.List;

/**
 * 待发货订单详情返回报文格式
 * @author heandong
 */
@Data
public class DeliveryNoSendDetailVO {
    /**
     * 运营组织
     */
    String operOrgName;
    /**
     * 供应商id
     */
    String shopId;
    /**
     * 供应商名称
     */
    String shopName;
    /**
     * 子订单编号
     */
    String childOrderId;

    /**
     * 订单创建时间
     */
    private String orderCreateTime;

    /**
     * 数据列表
     */
    List<DeliveryNoSendListDetailVO> list;

    /**
     * 销售模式
     */
    private Integer sellType;
    /**
     * 销售模式展示
     */
    private String sellTypeShow;

    /**
     * 订单描述
     */
    private String orderDesc;
}
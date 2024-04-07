package com.longfor.c10.lzyx.logistics.client.entity.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *  待发货列表vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryNoSendListVO {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 运营组织名称
     */
    private String orgName;

    /**
     * 供应商名称
     */
    private String shopName;

    /**
     * 商品信息列表
     */
    private List<GoodsVO> goodsVos;

    /**
     * 收货人姓名
     */
    private String receiptName;

    /**
     * 收货人地址
     */
    private String receiptAddress;

    /**
     * 收货人手机号
     */
    private String receiptPhoneNumber;

    /**
     * 物流类型
     */
    private String logisticsTypeShow;

    /**
     * 订单创建时间
     */
    private String orderCreateTime;

    /**
     * 航道编码
     */
    private String bizChannelCode;

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

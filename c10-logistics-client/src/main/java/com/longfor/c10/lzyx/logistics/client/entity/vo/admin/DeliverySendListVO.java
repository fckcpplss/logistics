package com.longfor.c10.lzyx.logistics.client.entity.vo.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

/**
 * 已发货列表vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliverySendListVO {
    /**
     * 运营组织名称
     */
    private String operOrgName;
    /**
     * 供应商名称
     */
    private String shopName;
    /**
     * 快递ID
     */
    private String deliveryId;
    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 发货时间
     */
    private String deliveryTime;

    /**
     * 运单编号
     */
    private String deliveryNo;
    /**
     * 物流状态
     */
    private Integer logisticsStatus;
    /**
     * 物流状态展示
     */
    private String logisticsStatusShow;

    /**
     * 物流类型中文展示
     */
    private String logisticsTypeShow;

    /**
     * 该运单中包含的商品
     */
    private List<GoodsVO> goodsList;

    /**
     * 订单创建时间
     */
    private String orderCreateTime;

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
    private Integer logisticsType;

    /**
     * 商家物流id
     */
    private Integer shopLogisticsId;

    /**
     * 物流商品id
     */
    private String goodsIds;

    /**
     * 快递公司编码
     */
    private String companyCode;

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

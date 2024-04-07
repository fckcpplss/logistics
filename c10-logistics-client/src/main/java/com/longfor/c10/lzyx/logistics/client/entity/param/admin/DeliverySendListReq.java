package com.longfor.c10.lzyx.logistics.client.entity.param.admin;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

/**
 * 已发货列表请求参数
 */
@Data
public class DeliverySendListReq extends BaseReqData {
    /**
     * 子订单编号
     */
    @Length(max = 64)
    private String childOrderId;
    /**
     * 快递单号
     */
    @Length(max = 64)
    private String deliveryNo;
    /**
     * 物流状态
     */
    private Integer logisticsStatus;
    /**
     * 商品名称
     */
    @Length(max = 64)
    private String goodsName;
    /**
     * 供应商名称
     */
    @Length(max = 64)
    private String shopName;
    /**
     * 所属运营组织列表
     */
    private String operOrgCode;

    /**
     * 发货开始时间
     */
    private LocalDateTime startDeliveryTime;

    /**
     * 发货结束时间
     */
    private LocalDateTime endDeliveryTime;


    /**
     * 物流类型,1.平台京东，2.平台顺丰，3.商家顺丰，4.其他
     */
    private Integer logisticsType;

    /**
     * 订单开始时间
     */
    private String startOrderCreateTime;

    /**
     * 订单结束时间
     */
    private String endOrderCreateTime;


    /**
     * 销售模式,1.批发，2.零售
     */
    private Integer sellType;
}

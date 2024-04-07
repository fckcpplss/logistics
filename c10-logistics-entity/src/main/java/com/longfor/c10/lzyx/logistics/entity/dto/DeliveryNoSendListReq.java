package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 待发货列表请求参数
 * @author zhaoyl
 * @date 2022/2/21 下午4:52
 * @since 1.0
 */
@Data
public class DeliveryNoSendListReq extends BaseReqData {
    /**
     * 订单编号
     */
    @Length(max = 64)
    private String orderNo;


    private String orgId;

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
     * 物流类型,1.平台，2.商家
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

    /**
     * 订单渠道编码集合
     */
    private List<String> bizChannelCodes;

}

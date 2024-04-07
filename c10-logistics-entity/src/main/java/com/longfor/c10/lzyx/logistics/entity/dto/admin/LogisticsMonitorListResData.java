package com.longfor.c10.lzyx.logistics.entity.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 运单监控列表返回参数
 * @author zhaoyl
 * @date 2022/2/17 上午9:45
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsMonitorListResData {
    /**
     * 组织id
     */
    private String orgId;

    /**
     *组织名称
     */
    private String orgName;

    /**
     * 供应商名称
     */
    private String shopName;

    /**
     *运单编号
     */
    private String deliveryNo;

    /**
     * 订单编号
     */
    private String childOrderId;

    /**
     *收货人姓名
     */
    private String receiptName;

    /**
     *收货电话
     */
    private String receiptPhone;

    /**
     * 收货人省名称
     */
    private String receiptProvince;

    /**
     * 收货人市名称
     */
    private String receiptCity;

    /**
     * 收货人区名称
     */
    private String receiptArea;

    /**
     * 收货人详细地址
     */
    private String receiptAddress;

    /**
     *物流状态
     */
    private Integer logisticsStatus;

    /**
     *供应商物流id
     */
    private String shopLogisticsId;

    /**
     *运费承担方类型
     */
    private Integer feeType;

    /**
     *订单状态
     */
    private String orderStatus;

    /**
     *物流订单id
     */
    private String logisticsOrderId;

    /**
     *物流运单id
     */
    private String logisticsDeliveryId;

    /**
     * 物流商品id
     */
    private String logisticsGoodsIds;

    /**
     * 订单创建时间
     */
    private String orderCreateTime;

    /**
     * 运单时间
     */
    private String deliveryTime;

    /**
     *是否取消
     */
    private Integer ifCancel;

    /**
     *物流备注
     */
    private String remark;

    /**
     * 异常附件
     */
    private String attachment1;
    /**
     * 快递公司编码
     */
    private String companyCode;

    /**
     * 快递公司名称
     */
    private String companyCodeShow;
}

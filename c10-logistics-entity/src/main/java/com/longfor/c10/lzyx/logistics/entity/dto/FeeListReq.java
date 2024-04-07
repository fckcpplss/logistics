package com.longfor.c10.lzyx.logistics.entity.dto;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 运费列表请求参数
 */
@Data
public class FeeListReq extends BaseReqData {

    /**
     * 运单号
     */
    @Length(max = 64)
    private String deliveryNo;

    /**
     * 商品名称
     */
    @Length(max = 64)
    private String goodsName;

    /**
     * 签收时间-开始时间
     */
    private String signTimeStart;

    /**
     * 签收时间-结束时间
     */
    private String signTimeEnd;

    /**
     * 下单时间-开始
     */
    private String createTimeStart;

    /**
     * 下单时间-结束
     */
    private String createTimeEnd;

    /**
     * 揽收时间-开始
     */
    private String pickupTimeStart;

    /**
     * 揽收时间-结束
     */
    private String pickupTimeEnd;

    /**
     * 商户名称
     */
    @Length(max = 64)
    private String shopName;

    /**
     * 子订单号
     */
    @Length(max = 64)
    private String childOrderId;

    /**
     * 运费承担方1. 平台，2商家
     */
    private Integer feeBearer;

    /**
     * 结算类型: 1.现结、2.月结
     */
    private String settlementType;

    /**
     * 付款方式：1.寄付，2.到付
     */
    private String paymentType;

    /**
     * 物流公司名称
     */
    private String logisticsCompanyName;

    /**
     * 物流账户0.全部 1.平台顺丰、2.平台京东、3.商家顺丰
     */
    private String logisticsAccount;

    /**
     * 物流类型 (1：平台物流，2：商家物流）
     */
    private String logisticsType;
    /**
     * 快递公司编码
     */
    private List<String> companyCode;

    /**
     * 销售模式,1.批发，2.零售
     */
    private Integer sellType;


    private List<String> bizChannelCodes;
}

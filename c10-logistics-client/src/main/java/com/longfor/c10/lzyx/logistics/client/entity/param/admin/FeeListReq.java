package com.longfor.c10.lzyx.logistics.client.entity.param.admin;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import java.util.List;

/**
 * 运费列表请求参数
 */
@Data
@ApiModel("运单费用查询参数")
public class FeeListReq extends BaseReqData {

    @Length(max = 64)
    @ApiModelProperty(value = "运单号")
    private String deliveryNo;

    @Length(max = 64)
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "签收时间-开始时间")
    private String signTimeStart;

    @ApiModelProperty(value = "签收时间-结束时间")
    private String signTimeEnd;

    @ApiModelProperty(value = "下单时间-开始")
    private String createTimeStart;

    @ApiModelProperty(value = "下单时间-结束")
    private String createTimeEnd;

    @ApiModelProperty(value = "揽收时间-开始")
    private String pickupTimeStart;

    @ApiModelProperty(value = "揽收时间-结束")
    private String pickupTimeEnd;

    @Length(max = 64)
    @ApiModelProperty(value = "商户名称")
    private String shopName;

    @Length(max = 64)
    @ApiModelProperty(value = "子订单号")
    private String childOrderId;

    @ApiModelProperty(value = "运费承担方1. 平台，2商家")
    private Integer feeBearer;

    @ApiModelProperty(value = "结算类型: 1.现结、2.月结")
    private String settlementType;

    @ApiModelProperty("付款方式：1.寄付，2.到付")
    private String paymentType;

    @ApiModelProperty(value = "物流公司名称")
    private String logisticsCompanyName;

    @ApiModelProperty(value = "物流账户0.全部 1.平台顺丰、2.平台京东、3.商家顺丰")
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
}

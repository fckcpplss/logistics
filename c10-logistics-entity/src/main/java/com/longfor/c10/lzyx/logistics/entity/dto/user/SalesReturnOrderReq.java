package com.longfor.c10.lzyx.logistics.entity.dto.user;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @描述: 退货单号请求参数
 * @author: lizhexun
 * @date: 2021-10-14
 */
@Data
public class SalesReturnOrderReq extends BaseReqData {


    /**
     * 快递公司编码
     */
    @NotBlank(message = "快递公司编码不能为空")
    private String deliveryCompanyCode;

    /**
     * 退单单号ID
     */
    @NotBlank(message = "退单单号ID不能为空")
    private String salesReturnOrderId;

    /**
     * 快递公司名称
     */
    @NotBlank(message = "快递公司名称不能为空")
    private String deliveryCompanyName;

    /**
     * 快递单号
     */
    @NotBlank(message = "快递单号不能为空")
    private String deliveryNo;

    /**
     * 发货/退货地址Ip
     */
    private Integer sendAddrId;
}

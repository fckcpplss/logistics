package com.longfor.c10.lzyx.logistics.entity.dto;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * 待发货、已发货列表详情查询参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliverySendDetailReq extends BaseReqData {

    /**
     * 子订单id
     */
    @NotEmpty(message = "子订单id不能为空")
    private String childOrderId;

    /**
     * 运单号
     */
    @NotEmpty(message = "运单号不能为空")
    private String deliveryNo;
}

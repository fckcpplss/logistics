package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 退单请求参数
 * @author zhaoyalong
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryRefundOrderReq extends BaseReqData {
    /**
     * 子订单ID
     */
    @NotBlank(message = "子订单号不能为空")
    private String childOrderId;

    /**
     * skuId
     */
    @NotEmpty(message = "skuId列表不能为空")
    private List<String> skuIds;
}

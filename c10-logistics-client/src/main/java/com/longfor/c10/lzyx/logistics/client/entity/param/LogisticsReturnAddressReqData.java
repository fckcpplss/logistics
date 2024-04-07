package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author liuqinglin
 * @date 2021/10/29
 **/
@Data
public class LogisticsReturnAddressReqData  extends BaseReqData {
    /**
     * 子订单id
     */
    @NotBlank(message = "子订单号不能为空")
    private String childOrderId;
}

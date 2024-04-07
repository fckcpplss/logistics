package com.longfor.c10.lzyx.logistics.entity.dto;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName : DeliveryCompanyReqData
 * @Description : 获取物流公司
 * @Author : ws
 * @Date: 2021-05-07 16:19
 */
@Data
public class AutoNumberReq extends BaseReqData {
    @NotBlank(message = "快递单号不能为空")
    private String deliveryNo;
}

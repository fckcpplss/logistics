package com.longfor.c10.lzyx.logistics.entity.dto;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * 待发货列表详情查询参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryNoSendDetailReq extends BaseReqData {

    /**
     * 子订单id
     */
    @NotEmpty(message = "子订单id不能为空")
    private String childOrderId;

    /**
     * 脱敏标记，默认脱敏
     */
    private boolean desensitizedFlag = true;
}

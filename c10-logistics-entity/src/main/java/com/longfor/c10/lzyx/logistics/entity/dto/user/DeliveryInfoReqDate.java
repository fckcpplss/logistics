package com.longfor.c10.lzyx.logistics.entity.dto.user;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 订单发货信息请求参数
 * @author: zhaoyl
 */
@Data
public class DeliveryInfoReqDate extends BaseReqData {
    /**
     * 子单号
     */
    @NotNull(message = "子单号为空")
    private String childOrderId;
}

package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 自提/核销订单核销请求实体
 * @author zhaoyl
 * @date 2022/4/13 上午11:30
 * @since 1.0
 */
@Data
public class LogisticsVerifyOrderVerifyReqData extends BaseReqData {
    /**
     * 批量核销列表
     */
    @NotEmpty(message = "核销商品信息不能为空")
    private List<LogisticsVerifyOrderVerifyListDTO> verifyList;

    /**
     * 核销码
     */
    @NotBlank(message = "核销码不能为空")
    private String pickupCode;
}

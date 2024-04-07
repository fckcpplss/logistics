package com.longfor.c10.lzyx.logistics.entity.dto;

import com.longfor.c10.lzyx.logistics.entity.enums.LogisticsVerifyVerifyTypeEnum;
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
public class LogisticsVerifyOrderVerifyReqData extends BaseReqData{
    /**
     * 批量核销列表
     */
    @NotEmpty(message = "核销商品信息不能为空")
    private List<LogisticsVerifyOrderVerifyListDTO> verifyList;

    /**
     * 核销码
     */
    private String pickupCode;

    /**
     * 核销类型
     */
    private LogisticsVerifyVerifyTypeEnum verifyType = LogisticsVerifyVerifyTypeEnum.VERIFY_ADMIN;
}

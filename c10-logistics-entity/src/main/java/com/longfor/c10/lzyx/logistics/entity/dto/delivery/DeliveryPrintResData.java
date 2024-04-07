package com.longfor.c10.lzyx.logistics.entity.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 运单打印返回实体类
 * @author zhaoyl
 * @date 2022/4/29 下午7:46
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPrintResData {
    private String apiErrorMsg;
    private String apiResponseID;
    private String apiResultCode;
    private String apiResultData;
}

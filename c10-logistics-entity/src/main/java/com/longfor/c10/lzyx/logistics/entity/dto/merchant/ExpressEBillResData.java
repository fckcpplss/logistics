package com.longfor.c10.lzyx.logistics.entity.dto.merchant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @description: 查看物流轨迹Req
 * @author: zhaoyalong
 * @date: 2021/11/01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpressEBillResData {
    private String apiErrorMsg;
    private String apiResponseID;
    private String apiResultCode;
    private String apiResultData;
}

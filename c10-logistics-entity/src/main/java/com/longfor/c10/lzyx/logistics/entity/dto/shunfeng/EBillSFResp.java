package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import lombok.Data;

/**
 * @description: 顺丰打印面单返回
 * @author: zhaoyalong
 */
@Data
public class EBillSFResp {
    private String apiErrorMsg;
    private String apiResponseID;
    private String apiResultCode;
    private String apiResultData;
}

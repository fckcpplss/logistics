package com.longfor.c10.lzyx.logistics.client.entity.param.merchant;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @description: 查看物流轨迹Req
 * @author: zhaoyalong
 * @date: 2021/11/01
 */
@Data
public class ExpressEBillResData {
    private String apiErrorMsg;
    private String apiResponseID;
    private String apiResultCode;
    private String apiResultData;
}

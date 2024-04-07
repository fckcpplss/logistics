package com.longfor.c10.lzyx.logistics.client.entity.param.verify;

import lombok.Data;

import java.util.List;

/**
 * @author liuxin41
 * @version 1.0
 * @description:
 * @date 2022/4/13 11:16
 */
@Data
public class VerifyOrderSearchReq {

    private List<String> childOrderIds;

    private String lmId;
}

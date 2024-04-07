package com.longfor.c10.lzyx.logistics.client.entity.param.open;

import lombok.Data;

/**
 * 快递100物流轨迹更新结果
 * @author zhaoyalong
 */
@Data
public class Kuaidi100PathUpdateRes {
    private Boolean result;

    private String returnCode;

    private String message;

}

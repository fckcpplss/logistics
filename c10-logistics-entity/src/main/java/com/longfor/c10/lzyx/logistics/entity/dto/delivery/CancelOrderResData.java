package com.longfor.c10.lzyx.logistics.entity.dto.delivery;

import lombok.Data;

@Data
public class CancelOrderResData {
    /**
     * 提示码
     */
    private Integer code;
    /**
     * 提示信息
     */
    private String msg;
}

package com.longfor.c10.lzyx.logistics.entity.dto.open;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 描述:京东物流推送结果对象
 *
 * @author wanghai03
 * @date 2021/10/19 下午4:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JdRoutResponse implements Serializable {
    /**
     * 0 或者其他
     */
    private Integer statusCode;

    /**
     * OK 或者其他
     */
    private String statusMessage;
}

package com.longfor.c10.lzyx.logistics.entity.dto.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 运维公交-批量核销
 * @author renwei03
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticOrderVerifyDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 1-正常 0-异常
     */
    private Integer status;

    /**
     * 异常信息
     */
    private String msg;
}

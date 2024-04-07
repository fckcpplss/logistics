package com.longfor.c10.lzyx.logistics.client.entity.dto.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 自提待核销文件校验
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
    @ApiModelProperty(value = "状态1-正常 0-异常")
    private Integer status;

    /**
     * 异常信息
     */
    @ApiModelProperty(value = "异常信息")
    private String msg;
}

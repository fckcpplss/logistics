package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 设置默认地址请求对象
 * @author admin
 */
@Data
public class LogisticsReturnAddressDefaultReq  extends  BaseReqData{
    @NotNull(message = "地址id不能为空")
    private Integer id;

}

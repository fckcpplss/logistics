package com.longfor.c10.lzyx.logistics.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "获取供应商信息请求对象")
public class LogisticsReturnSprReq   extends  BaseReqData{

}

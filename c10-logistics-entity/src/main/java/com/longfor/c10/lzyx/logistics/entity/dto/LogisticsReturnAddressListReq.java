package com.longfor.c10.lzyx.logistics.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 收货地址列表查询请求对象
 * @author admin
 */
@Data
public class LogisticsReturnAddressListReq   extends  BaseReqData{
    /**
     * 收货人
     */
    private String addresser;

    /**
     * 电话号码
     */
    private String phoneNumber;

    private String userName;


}

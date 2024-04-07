package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.Data;

@Data
public class LogisticsShipAddressListReq   extends  BaseReqData{
    /**
     * 发货人
     */
    private String addresser;

    /**
     * 电话号码
     */
    private String phoneNumber;

    private String userName;
}

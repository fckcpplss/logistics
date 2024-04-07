package com.longfor.c10.lzyx.logistics.client.entity.param;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @描述:
 * @author: lizhexun
 * @date: 2021-10-19
 */
@Data
public class LogisticsReturnAddressResData {

    @ApiModelProperty("退货地址")
    private List<LogisticsReturnAddress> returnAddressList;

    @ApiModelProperty("发货地址")
    private LogisticsShipAddress sendAddress;


}

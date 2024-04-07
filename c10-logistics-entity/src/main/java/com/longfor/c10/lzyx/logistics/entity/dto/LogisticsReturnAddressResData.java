package com.longfor.c10.lzyx.logistics.entity.dto;


import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsReturnAddress;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsShipAddress;
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

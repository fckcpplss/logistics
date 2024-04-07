package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发货地址列表查询请求对象
 * @author zhoayl
 */
@Data
public class LogisticsShipAddressListReq  extends BaseReqData {
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

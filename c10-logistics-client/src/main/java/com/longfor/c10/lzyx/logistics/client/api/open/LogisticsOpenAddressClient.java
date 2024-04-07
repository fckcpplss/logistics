package com.longfor.c10.lzyx.logistics.client.api.open;

import com.longfor.c10.lzyx.logistics.client.entity.param.LogisticsAddressDetailReqData;
import com.longfor.c10.lzyx.logistics.client.entity.param.LogisticsAddressInfo;
import com.longfor.c10.lzyx.logistics.client.entity.param.open.LogisticsAddressByChannelAndUserListReqData;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * 物流运费client
 * @author zhaoyl
 * @date 2022/2/7 下午3:10
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsOpenAddressClient {

    /**
     * 查询用户地址集合
     */
    @PostMapping(path = "lzyx/logistics/open/address/user/list")
    Response<List<LogisticsAddressInfo>> listByChannelAndUserId(Request<LogisticsAddressByChannelAndUserListReqData> request);



    /**
     * 查询用户地址
     */
    @PostMapping(path = "lzyx/logistics/open/address/user/detail")
    Response<LogisticsAddressInfo> detail(Request<LogisticsAddressDetailReqData> request);

}

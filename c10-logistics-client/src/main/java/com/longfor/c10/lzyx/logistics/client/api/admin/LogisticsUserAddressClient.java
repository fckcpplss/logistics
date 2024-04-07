package com.longfor.c10.lzyx.logistics.client.api.admin;

import com.longfor.c10.lzyx.logistics.client.entity.param.*;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户地址client
 * @author zhaoyl
 * @date 2022/2/7 下午3:10
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsUserAddressClient {
    @PostMapping(path = "lzyx/logistics/admin/fee/add")
    Response<LogisticsAddressAddResp> add(@Valid  @RequestBody Request<LogisticsAddressAddReqData> request);

    @PostMapping(path = "lzyx/logistics/admin/fee/update")
    Response<Void> update(@Valid @RequestBody Request<LogisticsAddressUpdateReq> request);

    @PostMapping(path = "lzyx/logistics/admin/fee/list")
    PageResponse<List<LogisticsAddressInfo>> queryList(@Valid @RequestBody PageRequest<LogisticsAddressListReq> request);

    @PostMapping(path = "lzyx/logistics/admin/fee/delete")
    Response<Void> delete(@Valid @RequestBody Request<LogisticsAddressDelReq> request);
}

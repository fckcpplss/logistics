package com.longfor.c10.lzyx.logistics.client.api.admin;

import com.longfor.c10.lzyx.logistics.client.entity.param.*;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 物流自提/核销订单client
 * @author zhaoyl
 * @date 2022/2/7 下午3:10
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsVerifyOrderClient {
    @PostMapping(path = "lzyx/logistics/admin/order/verify/list")
    PageResponse<List<LogisticsVerifyOrderListResData>> list(@RequestBody PageRequest<LogisticsVerifyOrderListReqData> request);

    @PostMapping(path = "lzyx/logistics/admin/order/verify/record/list")
    PageResponse<List<LogisticsVerifyOrderRecordsListResData>> recordList(@RequestBody PageRequest<LogisticsVerifyOrderRecordsListReqData> request);

    @PostMapping(value = "lzyx/logistics/admin/order/verify/detail/batch")
    public Response<LogisticsVerifyOrderDetailResWithCodeData> batchDetail(@RequestBody @Valid Request<LogisticsVerifyOrderDetailReqData> request);

    @PostMapping(value = "lzyx/logistics/admin/order/verify/detail")
    public Response<LogisticsVerifyOrderDetailResData> detail(@RequestBody @Valid Request<LogisticsVerifyOrderDetailReqData> request);

    @PostMapping(value = "lzyx/logistics/admin/order/verify/verify")
    public Response<String> verify(@RequestBody @Valid Request<LogisticsVerifyOrderVerifyReqData> request);
}

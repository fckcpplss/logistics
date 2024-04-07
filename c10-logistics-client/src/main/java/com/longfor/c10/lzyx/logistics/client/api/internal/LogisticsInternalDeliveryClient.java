package com.longfor.c10.lzyx.logistics.client.api.internal;

import com.longfor.c10.lzyx.logistics.client.entity.param.DeliveryCancelReqData;
import com.longfor.c10.lzyx.logistics.client.entity.param.DeliveryCancelResData;
import com.longfor.c10.lzyx.logistics.client.entity.param.DeliveryRefundOrderReq;
import com.longfor.c10.lzyx.logistics.client.entity.param.LogisticsDeliveryIdReqData;
import com.longfor.c10.lzyx.logistics.client.entity.param.internal.LogisticsOrderSkuStatusDTO;
import com.longfor.c10.lzyx.logistics.client.entity.param.internal.LogisticsOrderStatusDTO;
import com.longfor.c10.lzyx.logistics.client.entity.param.internal.LogisticsOrderStatusReqData;
import com.longfor.c10.lzyx.logistics.client.entity.vo.admin.LogisticsDetailRes;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * 物流内部调用client
 * @author zhaoyl
 * @date 2022/2/7 下午3:10
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsInternalDeliveryClient {

    @PostMapping(path = "lzyx/logistics/internal/delivery/status/sku/list")
    public Response<List<LogisticsOrderStatusDTO>> getOrderSkuLogisticsStatusList(Request<LogisticsOrderStatusReqData> request);

    @PostMapping(path = "lzyx/logistics/internal/delivery/status/sku/map")
    public Response<Map<String, Map<String, LogisticsOrderSkuStatusDTO>>> getOrderSkuLogisticsStatusMap(Request<LogisticsOrderStatusReqData> request);

    /***
     * 取消订单
     */
    @PostMapping(path = "lzyx/logistics/common/order/cancel")
    Response<Void> cancel(@RequestBody Request<DeliveryCancelReqData> request);

    /**
     * 退单
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/common/order/refundOrder")
    Response<Map<String, DeliveryCancelResData>> refundOrder(@RequestBody Request<DeliveryRefundOrderReq> request);

    /**
     * 物流单详情
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/common/order/detail")
    Response<LogisticsDetailRes> detail(@RequestBody Request<LogisticsDeliveryIdReqData> request);
}


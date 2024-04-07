package com.longfor.c10.lzyx.logistics.client.api;

import com.longfor.c10.lzyx.logistics.client.entity.param.*;
import com.longfor.c10.lzyx.logistics.client.entity.vo.AutoNumberVO;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 物流运费client
 * @author zhaoyl
 * @date 2022/2/7 下午3:10
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsCommonClient {
    @PostMapping(path = "/lzyx/logistics/common/order/autoNumber")
    Response<AutoNumberVO> autoNumber(Request<AutoNumberReq> request);

    @PostMapping(path = "/lzyx/logistics/common/order/delivery/getPath")
    @ApiOperation(value = "物流轨迹", notes = "查询物流轨迹")
    Response<TrajectoryResp> getPath(@RequestBody Request<DeliveryPathReq> request);

    @PostMapping(path = "/lzyx/logistics/common/order/cancel")
    Response<Void> cancel(@RequestBody Request<DeliveryCancelReqData> request);

    @PostMapping(path = "/lzyx/logistics/common/order/refundOrder")
    Response<Map<String, DeliveryCancelResData>> refundOrder(@RequestBody Request<DeliveryRefundOrderReq> request);
}

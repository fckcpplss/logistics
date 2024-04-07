package com.longfor.c10.lzyx.logistics.core.controller.internal;

import cn.hutool.core.util.StrUtil;
import com.longfor.c10.lzyx.logistics.core.service.internal.ILogisticsInternalDeliveryService;
import com.longfor.c10.lzyx.logistics.core.service.user.ILogisticsUserDeliveryService;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryCompanyListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryCompanyListResData;
import com.longfor.c10.lzyx.logistics.entity.dto.internal.LogisticsOrderSkuStatusDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.internal.LogisticsOrderStatusDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.internal.LogisticsOrderStatusReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.user.DeliveryInfoReqDate;
import com.longfor.c10.lzyx.logistics.entity.dto.user.DeliveryInfoResp;
import com.longfor.c10.lzyx.logistics.entity.dto.user.SalesReturnOrderReq;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrderStatusMapping;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 内部调用-物流运单管理控制层
 * @author zhaoyl
 * @date 2022/2/21 上午9:30
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/internal/delivery")
public class LogisticsInternalDeliveryController {
    @Autowired
    private ILogisticsInternalDeliveryService logisticsInternalDeliveryService;

    @PostMapping(path = "status/sku/list")
    public Response<List<LogisticsOrderStatusDTO>> getOrderSkuLogisticsStatusList(@Valid @RequestBody  Request<LogisticsOrderStatusReqData> request) {
        LogisticsOrderStatusReqData req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数为空"));
        return logisticsInternalDeliveryService.getOrderSkuLogisticsStatusList(req);
    }
    @PostMapping(path = "status/sku/map")
    public Response<Map<String,Map<String,LogisticsOrderSkuStatusDTO>>> getOrderSkuLogisticsStatusMap(@Valid @RequestBody Request<LogisticsOrderStatusReqData> request) {
        return Response.ok(Optional.ofNullable(getOrderSkuLogisticsStatusList(request))
                .map(Response::getData)
                .map(list -> {
                    return ListUtils.emptyIfNull(list).stream().collect(Collectors.toMap(LogisticsOrderStatusDTO::getChildOrderId,x -> {
                        return ListUtils.emptyIfNull(x.getSkuStatuss())
                                .stream()
                                .collect(Collectors.toMap(y -> new StringBuilder(StrUtil.blankToDefault(y.getGoodsId(),"")).append("_").append(StrUtil.blankToDefault(y.getSkuId(),"")).toString(), Function.identity(),(a, b) -> a));
                    },(a,b) -> a));
                })
                .orElse(Collections.emptyMap()));
    }

}

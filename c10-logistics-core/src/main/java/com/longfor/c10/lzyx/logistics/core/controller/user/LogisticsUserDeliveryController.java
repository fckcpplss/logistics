package com.longfor.c10.lzyx.logistics.core.controller.user;

import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonOrderService;
import com.longfor.c10.lzyx.logistics.core.service.user.ILogisticsUserDeliveryService;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryCompanyListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryCompanyListResData;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryPathReq;
import com.longfor.c10.lzyx.logistics.entity.dto.user.*;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 用户端-物流运单管理控制层
 * @author zhaoyl
 * @date 2022/2/21 上午9:30
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/user/delivery")
public class LogisticsUserDeliveryController {
    @Autowired
    private ILogisticsUserDeliveryService logisticsUserDeliveryService;

    @Autowired
    private ILogisticsCommonOrderService logisticsCommonOrderService;

    @PostMapping("/deliveryInfo")
    @ApiOperation(value = "C端查看订单发货信息", notes = "C端查看订单发货信息")
    public Response<DeliveryInfoResp> deliveryInfo(@Valid @RequestBody Request<DeliveryInfoReqDate> request) {
        DeliveryInfoReqDate req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数为空"));
        return logisticsUserDeliveryService.deliveryInfo(req);
    }

    /**
     * 绑定退货物流单号
     */
    @PostMapping("/order/return/bind")
    Response<String> bindingSalesReturnOrder(@Valid @RequestBody Request<SalesReturnOrderReq> request){
        SalesReturnOrderReq req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数为空"));
        return logisticsUserDeliveryService.bindingSalesReturnOrder(req);
    }

    @PostMapping(path = "/company/list")
    Response<List<DeliveryCompanyListResData>> getDeliveryCompanyList(@Valid @RequestBody Request<DeliveryCompanyListReqData> request){
        DeliveryCompanyListReqData req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsUserDeliveryService.getDeliveryCompanyList(req);
    }

    /**
     * 获取子单签收时间
     * @param request
     * @return
     */
    @PostMapping(path = "time/sign")
    Response<Date> getOrderSignTime(@Valid @RequestBody Request<LogisticsOrderIdReq> request){
        LogisticsOrderIdReq req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsUserDeliveryService.getOrderSignTime(req);
    }

}

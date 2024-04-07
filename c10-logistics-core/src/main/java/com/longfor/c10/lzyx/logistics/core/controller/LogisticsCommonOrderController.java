package com.longfor.c10.lzyx.logistics.core.controller;

import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonOrderService;
import com.longfor.c10.lzyx.logistics.core.service.impl.delivery.KuaiDi100ServiceImpl;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsDetailRes;
import com.longfor.c10.lzyx.logistics.entity.dto.user.TrajectoryResp;
import com.longfor.c10.lzyx.logistics.entity.enums.BusinessTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * 通用订单操作控制层
 * @author zhaoyl
 * @date 2022/3/31 下午2:59
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/common/order")
public class LogisticsCommonOrderController {

    @Autowired
    private ILogisticsCommonOrderService logisticsCommonOrderService;

    @Autowired
    private KuaiDi100ServiceImpl kuaiDi100Service;

    /***
     * 智能单号识别
     */
    @PostMapping(path = "/autoNumber")
    Response<AutoNumberVO> autoNumber(@RequestBody Request<AutoNumberReq> request){
        String deliveryNo = Optional.ofNullable(request).map(Request::getData).map(AutoNumberReq::getDeliveryNo).orElseThrow(() -> new BusinessException("快递编码不能为空"));
        return Response.ok(Optional.ofNullable(kuaiDi100Service.autoNumber(deliveryNo))
                .map(r -> new AutoNumberVO(r.getComCode(),r.getName(),null,r.getLengthPre()))
                .orElse(null));
    }

    /***
     * 取消订单
     */
    @PostMapping(path = "/cancel")
    Response<Void> cancel(@RequestBody Request<DeliveryCancelReqData> request){
        DeliveryCancelReqData req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数不能为空"));
        return logisticsCommonOrderService.cancel(req, BusinessTypeEnum.MERCHANT_CANCEL);
    }
    @PostMapping(path = "/refundOrder")
    Response<Map<String, DeliveryCancelResData>> refundOrder(@RequestBody Request<DeliveryRefundOrderReq> request){
        DeliveryRefundOrderReq req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数不能为空"));
        return logisticsCommonOrderService.refundOrder(req);
    }

    @PostMapping(path = "/detail")
    @ApiOperation(value = "物流单详情", notes = "物流单详情")
    Response<LogisticsDetailRes> detail(@RequestBody Request<LogisticsDeliveryIdReqData> request){
        LogisticsDeliveryIdReqData req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数不能为空"));
        return logisticsCommonOrderService.detail(req);
    }

    @PostMapping(path = "delivery/getPath")
    @ApiOperation(value = "物流轨迹", notes = "查询物流轨迹")
    Response<TrajectoryResp> getPath(@RequestBody Request<DeliveryPathReq> request){
        DeliveryPathReq req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数不能为空"));
        return logisticsCommonOrderService.getPath(req);
    }

}

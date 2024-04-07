package com.longfor.c10.lzyx.logistics.core.controller.merchant;

import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonOrderService;
import com.longfor.c10.lzyx.logistics.core.service.merchant.ILogisticsMerchantPrintService;
import com.longfor.c10.lzyx.logistics.core.service.merchant.IOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryCancelReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.AddOrderReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.ExpressEBillReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.ExpressEBillResData;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsEbill;
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

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 物流订单api
 * @author zhaoyl
 * @date 2022/1/19 上午11:52
 * @since 1.0
 */
@RequestMapping("lzyx/logistics/merchant/order")
@RestController
public class LogisticsOrderController {
    @Autowired
    private IOrderService orderService;

    @Autowired
    private ILogisticsCommonOrderService logisticsCommonOrderService;

    @Autowired
    private ILogisticsMerchantPrintService logisticsMerchantPrintService;

    @PostMapping(path = "/addOrder")
    Response<String> addOrder(@Valid @RequestBody Request<AddOrderReqData> addOrderReqDatas){
        AddOrderReqData addOrderListReqData = Optional.ofNullable(addOrderReqDatas)
                .map(Request::getData)
                .orElseThrow((() -> new BusinessException("参数为空")));

        return orderService.doSendOrder(addOrderListReqData);
    }
    /***
     * 取消运单
     */
    @PostMapping(path = "/cancel")
    Response<Void> cancel(@RequestBody Request<DeliveryCancelReqData> request){
        DeliveryCancelReqData req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数不能为空"));
        return logisticsCommonOrderService.cancel(req, BusinessTypeEnum.MERCHANT_CANCEL);
    }

    /**
     * 打印电子面单
     * @param req
     * @return
     */
    @PostMapping("/print")
    public Response<List<ExpressEBillResData>> print(@RequestBody Request<ExpressEBillReqData> request) {
        ExpressEBillReqData req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数为空"));
        List<ExpressEBillResData> handlerResults = logisticsMerchantPrintService.print(req);
        return Response.ok(handlerResults);
    }

    /**
     * 电子面单pdf
     * @param req
     * @return
     */
    @PostMapping("/pdf")
    public Response<List<LogisticsEbill>> pdf(@RequestBody Request<ExpressEBillReqData> request) {
        ExpressEBillReqData req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数为空"));
        return logisticsMerchantPrintService.pdf(req);
    }

}

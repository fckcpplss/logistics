package com.longfor.c10.lzyx.logistics.client.api.merchant;

import com.longfor.c10.lzyx.logistics.client.entity.param.DeliveryCancelReqData;
import com.longfor.c10.lzyx.logistics.client.entity.param.merchant.AddOrderReqData;
import com.longfor.c10.lzyx.logistics.client.entity.param.merchant.ExpressEBillReqData;
import com.longfor.c10.lzyx.logistics.client.entity.param.merchant.ExpressEBillResData;
import com.longfor.c10.lzyx.logistics.client.entity.param.merchant.LogisticsEbill;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 物流订单 client
 * @author zhaoyl
 * @date 2022/1/19 上午11:52
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsOrderClient {

    
    /**
     * 物流下单
     */
    @PostMapping(path = "lzyx/logistics/merchant/order/addOrder")
    Response<String> addOrder(@Valid @RequestBody Request<AddOrderReqData> addOrderReqDatas);

    /**
     * 运单取消
     */
    @PostMapping(path = "/lzyx/logistics/merchant/order/cancel")
    Response<Void> cancel(@RequestBody Request<DeliveryCancelReqData> request);

    /**
     * 打印电子面单
     * @param req
     * @return
     */
    @PostMapping("lzyx/logistics/merchant/order/print")
    public Response<List<ExpressEBillResData>> print(@RequestBody Request<ExpressEBillReqData> request);

    /**
     * 电子面单pdf
     * @param req
     * @return
     */
    @PostMapping("lzyx/logistics/merchant/order/pdf")
    public Response<List<LogisticsEbill>> pdf(@RequestBody Request<ExpressEBillReqData> request);

}

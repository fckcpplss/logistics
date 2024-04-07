package com.longfor.c10.lzyx.logistics.client.api.user;

import com.longfor.c10.lzyx.logistics.client.entity.param.AutoNumberReq;
import com.longfor.c10.lzyx.logistics.client.entity.param.DeliveryCompanyListReqData;
import com.longfor.c10.lzyx.logistics.client.entity.param.DeliveryCompanyListResData;
import com.longfor.c10.lzyx.logistics.client.entity.param.user.DeliveryInfoReqDate;
import com.longfor.c10.lzyx.logistics.client.entity.param.user.DeliveryInfoResp;
import com.longfor.c10.lzyx.logistics.client.entity.param.user.LogisticsOrderIdReq;
import com.longfor.c10.lzyx.logistics.client.entity.param.user.SalesReturnOrderReq;
import com.longfor.c10.lzyx.logistics.client.entity.vo.AutoNumberVO;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 物流运费client
 * @author zhaoyl
 * @date 2022/2/7 下午3:10
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsDeliveryClient {
    @PostMapping(path = "lzyx/logistics/common/delivery/autoNumber")
    Response<AutoNumberVO> autoNumber(@Valid @RequestBody Request<AutoNumberReq> request);


    /**
     * 绑定退货物流单号
     */
    @PostMapping("lzyx/logistics/user/delivery/order/return/bind")
    Response<String> orderReturnBind(@RequestBody Request<SalesReturnOrderReq> request);

    /**
     * 查询物流快递公司
     */
    @PostMapping("lzyx/logistics/user/delivery/company/list")
    Response<List<DeliveryCompanyListResData>> getDeliveryCompanyList(@RequestBody Request<DeliveryCompanyListReqData> request);

    /**
     * 查看订单发货信息
     * @param request
     * @return
     */
    @PostMapping("lzyx/logistics/user/delivery/deliveryInfo")
    Response<DeliveryInfoResp> deliveryInfo(@RequestBody Request<DeliveryInfoReqDate> request);

    /**
     * 获取子单签收时间
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/user/delivery/time/sign")
    Response<Date> getOrderSignTime(@RequestBody Request<LogisticsOrderIdReq> request);


}

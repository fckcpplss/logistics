package com.longfor.c10.lzyx.logistics.client.api.user;

import com.longfor.c10.lzyx.logistics.client.entity.param.verify.VerifyOrderConfirmReq;
import com.longfor.c10.lzyx.logistics.client.entity.param.verify.VerifyOrderSearchReq;
import com.longfor.c10.lzyx.logistics.client.entity.verify.VerifyOrderConfirmVO;
import com.longfor.c10.lzyx.logistics.client.entity.verify.VerifyPickUpCodeVO;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @author liuxin41
 * @version 1.0
 * @description:
 * @date 2022/4/13 11:14
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsUserVerifyClient {

    /**
     * 批量查询子订单核销二维码
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/user/verify/order/listQrCodeUrl")
    Response<Map<String, VerifyPickUpCodeVO>> getBatchVerifyQrCodeUrl(Request<VerifyOrderSearchReq> request);

    /**
     * 核销订单确认后更新子单信息
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/user/verify/order/confirm")
    Response<Boolean> confirmVerifyOrder(Request<VerifyOrderConfirmReq> request);

    /**
     * 核销订单确认后更新子单信息
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/user/verify/order/listDetail")
    Response<VerifyOrderConfirmVO> listChildOrderGoods(Request<VerifyOrderConfirmReq> request);

}

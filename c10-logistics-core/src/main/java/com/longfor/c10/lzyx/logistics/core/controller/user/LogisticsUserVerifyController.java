package com.longfor.c10.lzyx.logistics.core.controller.user;

import com.longfor.c10.lzyx.logistics.core.service.user.ILogisticsUserVerifyService;
import com.longfor.c10.lzyx.logistics.entity.dto.verify.VerifyOrderConfirmVO;
import com.longfor.c10.lzyx.logistics.entity.dto.verify.VerifyPickUpCodeVO;
import com.longfor.c10.lzyx.logistics.entity.dto.verify.param.VerifyOrderConfirmReq;
import com.longfor.c10.lzyx.logistics.entity.dto.verify.param.VerifyOrderSearchReq;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * @author liuxin41
 * @version 1.0
 * @description:
 * @date 2022/4/14 11:56
 */
@RestController
@Slf4j
public class LogisticsUserVerifyController {

    @Resource(name = "logisticsUserVerifyServiceImpl")
    private ILogisticsUserVerifyService logisticsUserVerifyService;

    /**
     * 批量查询子订单核销二维码
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/user/verify/order/listQrCodeUrl")
    Response<Map<String, VerifyPickUpCodeVO>> listVerifyQrCodeUrl(@Valid @RequestBody Request<VerifyOrderSearchReq> request){
        return logisticsUserVerifyService.listVerifyQrCodeUrl(request);
    }

    /**
     * 核销订单确认后更新子单信息
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/user/verify/order/confirm")
    Response<Boolean> confirmVerifyOrder(@Valid @RequestBody Request<VerifyOrderConfirmReq> request){
        return logisticsUserVerifyService.confirmVerifyOrder(request);
    }

    /**
     * 子单信息查询返回
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/user/verify/order/listDetail")
    Response<VerifyOrderConfirmVO> listChildOrderGoods(@RequestBody Request<VerifyOrderConfirmReq> request){
        return logisticsUserVerifyService.listChildOrderGoods(request);
    }
}

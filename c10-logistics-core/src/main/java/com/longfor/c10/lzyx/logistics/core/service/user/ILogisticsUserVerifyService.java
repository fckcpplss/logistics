package com.longfor.c10.lzyx.logistics.core.service.user;

import com.longfor.c10.lzyx.logistics.entity.dto.verify.VerifyOrderConfirmVO;
import com.longfor.c10.lzyx.logistics.entity.dto.verify.VerifyPickUpCodeVO;
import com.longfor.c10.lzyx.logistics.entity.dto.verify.param.VerifyOrderConfirmReq;
import com.longfor.c10.lzyx.logistics.entity.dto.verify.param.VerifyOrderSearchReq;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;

import java.util.Map;

/**
 * @author liuxin41
 * @version 1.0
 * @description:
 * @date 2022/4/14 14:01
 */
public interface ILogisticsUserVerifyService {

    /**
     * 批量查询子订单核销二维码
     * @param request
     * @return
     */
    Response<Map<String, VerifyPickUpCodeVO>> listVerifyQrCodeUrl(Request<VerifyOrderSearchReq> request);

    /**
     * 核销订单确认后更新子单信息
     * @param request
     * @return
     */
    Response<Boolean> confirmVerifyOrder(Request<VerifyOrderConfirmReq> request);

    /**
     * 核销订单确认后更新子单信息
     * @param request
     * @return
     */
    Response<VerifyOrderConfirmVO> listChildOrderGoods(Request<VerifyOrderConfirmReq> request);

}

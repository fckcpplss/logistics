package com.longfor.c10.lzyx.logistics.core.service;

import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsDetailRes;
import com.longfor.c10.lzyx.logistics.entity.dto.user.TrajectoryResp;
import com.longfor.c10.lzyx.logistics.entity.enums.BusinessTypeEnum;
import com.longfor.c2.starter.data.domain.response.Response;

import java.util.Map;

/**
 * 物流订单接口类
 * @author zhaoyl
 * @date 2022/1/11 下午2:28
 * @since 1.0
 */
public interface ILogisticsCommonOrderService {

    /**
     * 取消发货
     * @param req
     * @return
     */
    Response<Void> cancel(DeliveryCancelReqData req, BusinessTypeEnum businessTypeEnum);

    /**
     * 物流单详情
     * @param req
     * @return
     */
    Response<LogisticsDetailRes> detail(LogisticsDeliveryIdReqData req);

    /**
     * 物流轨迹
     * @param req
     * @return
     */
    Response<TrajectoryResp> getPath(DeliveryPathReq req);

    /**
     * 退单
     * @param req
     * @return
     */
    Response<Map<String, DeliveryCancelResData>> refundOrder(DeliveryRefundOrderReq req);
}
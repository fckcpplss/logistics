package com.longfor.c10.lzyx.logistics.core.service;

import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;

import java.util.List;

/**
 * 自提/核销订单公共接口类
 * @author zhaoyl
 * @date 2022/4/13 上午11:16
 * @since 1.0
 */
public interface ILogisticsCommonVerifyOrderService {

    /**
     * 自提核销订单列表
     * @param req
     * @return
     */
    PageResponse<List<LogisticsVerifyOrderListResData>> list(LogisticsVerifyOrderListReqData req, PageInfo pageInfo);

    /**
     * 自提核销订单核销记录列表
     * @param req
     * @param pageInfo
     * @return
     */
    PageResponse<List<LogisticsVerifyOrderRecordsListResData>> recordList(LogisticsVerifyOrderListReqData req, PageInfo pageInfo);

    /**
     * 核销订单详情
     * @param req
     * @return
     */
    Response<List<LogisticsVerifyOrderDetailResData>> detailList(LogisticsVerifyOrderDetailReqData req);

    /**
     * 核销订单
     * @param req
     * @return
     */
    Response<String> verify(LogisticsVerifyOrderVerifyReqData req);
}

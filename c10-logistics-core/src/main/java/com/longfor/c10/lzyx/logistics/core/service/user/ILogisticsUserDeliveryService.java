package com.longfor.c10.lzyx.logistics.core.service.user;

import com.longfor.c10.lzyx.download.client.entity.BizExportParamEntity;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryCompanyListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryCompanyListResData;
import com.longfor.c10.lzyx.logistics.entity.dto.FeeListReq;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.FeeVO;
import com.longfor.c10.lzyx.logistics.entity.dto.user.DeliveryInfoReqDate;
import com.longfor.c10.lzyx.logistics.entity.dto.user.DeliveryInfoResp;
import com.longfor.c10.lzyx.logistics.entity.dto.user.LogisticsOrderIdReq;
import com.longfor.c10.lzyx.logistics.entity.dto.user.SalesReturnOrderReq;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;

import java.util.Date;
import java.util.List;

/**
 * 物流用户端运单相关接口接口类
 * @author zhaoyl
 * @date 2022/2/21 上午9:49
 * @since 1.0
 */
public interface ILogisticsUserDeliveryService {

    Response<DeliveryInfoResp> deliveryInfo(DeliveryInfoReqDate req);

    /**
     * 绑定退单编号
     * @param req
     * @return
     */
    Response<String> bindingSalesReturnOrder(SalesReturnOrderReq req);

    /**
     * 物流公司列表
     * @param req
     * @return
     */
    Response<List<DeliveryCompanyListResData>> getDeliveryCompanyList(DeliveryCompanyListReqData req);

    /**
     * 获取物流订单签收时间
     * @param req
     * @return
     */
    Response<Date> getOrderSignTime(LogisticsOrderIdReq req);
}

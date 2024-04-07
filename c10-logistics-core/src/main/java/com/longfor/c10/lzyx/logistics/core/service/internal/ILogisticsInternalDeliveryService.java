package com.longfor.c10.lzyx.logistics.core.service.internal;

import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsAddressInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsAddressListReq;
import com.longfor.c10.lzyx.logistics.entity.dto.internal.LogisticsOrderStatusDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.internal.LogisticsOrderStatusReqData;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;

import java.util.List;

/**
 * @author zhaoyl
 * @date 2022/4/27 上午9:52
 * @since 1.0
 */
public interface ILogisticsInternalDeliveryService {

    /**
     * 物流商品状态
     */
    Response<List<LogisticsOrderStatusDTO>> getOrderSkuLogisticsStatusList(LogisticsOrderStatusReqData req);
}

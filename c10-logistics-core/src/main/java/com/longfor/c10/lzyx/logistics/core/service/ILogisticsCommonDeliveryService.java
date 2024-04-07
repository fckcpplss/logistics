package com.longfor.c10.lzyx.logistics.core.service;

import com.longfor.c10.lzyx.download.client.entity.BizExportParamEntity;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliveryNoSendDetailVO;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliveryNoSendListVO;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliverySendDetailVO;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliverySendListVO;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 物流运单接口类
 * @author zhaoyl
 * @date 2022/2/21 上午9:49
 * @since 1.0
 */
public interface ILogisticsCommonDeliveryService {
    /**
     * 更新待发货状态（用于需求单作废时， 如果子订单未发货， 则更新状态）
     * @param request request
     */
    Response<Boolean> updateNoSend(Request<List<LogisticOrderUpdateReq>> request);
    /**
     * 待发货列表
     */
    PageResponse<List<DeliveryNoSendListVO>> getNoSendList(PageInfo pageInfo, DeliveryNoSendListReq req);


    /**
     * 已发货列表
     */
    PageResponse<List<DeliverySendListVO>> getSendList(PageInfo pageInfo, DeliverySendListReq req);


    /**
     * 待发货列表详情
     * @param req
     * @return
     */
    Response<DeliveryNoSendDetailVO> getNoSendDetail(DeliveryNoSendDetailReq req);

    /**
     * 已发货列表详情
     * @param req
     * @return
     */
    Response<DeliverySendDetailVO> getSendDetail(DeliverySendDetailReq req);

    /**
     * 待发货列表导出
     * @param param
     * @return
     */
    Response<Boolean> exportNoSendList(Request<BizExportParamEntity> param);

    /**
     * 已发货列表导出
     * @param param
     * @return
     */
    Response<Boolean> exportSendList(Request<BizExportParamEntity> param);
}

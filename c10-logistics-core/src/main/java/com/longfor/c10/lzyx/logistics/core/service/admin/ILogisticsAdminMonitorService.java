package com.longfor.c10.lzyx.logistics.core.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorDetailReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorUpdateReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorListResData;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorListResVO;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;

import java.util.List;

/**
 * 物流监控服务接口类
 */
public interface ILogisticsAdminMonitorService {

    /**
     *  运单列表
     */
    PageResponse<List<LogisticsMonitorListResVO>> deliveryList(IPage<LogisticsMonitorListResData> page, LogisticsMonitorListReqData req);

    /**
     * 运单详情
     */
    Response<LogisticsMonitorListResVO> deliveryDetail(LogisticsMonitorDetailReqData logisticsMonitorDetailReqData);

    Response<Boolean> deliveryUpdate(LogisticsMonitorUpdateReqData logisticsMonitorUpdateReqData);
}

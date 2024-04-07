package com.longfor.c10.lzyx.logistics.client.api.admin;

import com.longfor.c10.lzyx.logistics.client.entity.param.admin.LogisticsMonitorDetailReqData;
import com.longfor.c10.lzyx.logistics.client.entity.param.admin.LogisticsMonitorListReqData;
import com.longfor.c10.lzyx.logistics.client.entity.param.admin.LogisticsMonitorUpdateReqData;
import com.longfor.c10.lzyx.logistics.client.entity.vo.admin.LogisticsMonitorListResVO;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 物流运单监控client
 * @author zhaoyalong
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsMonitorClient {

    /**
     * 运营端-物流运单监控列表
     * @param req
     * @return
     */
    @PostMapping("lzyx/logistics/admin/monitor/delivery/list")
    public PageResponse<List<LogisticsMonitorListResVO>> list(@RequestBody @Valid PageRequest<LogisticsMonitorListReqData> req);
    /**
     * 运营端-物流运单监控详情
     * @param req
     * @return
     */
    @PostMapping("lzyx/logistics/admin/monitor/delivery/detail")
    public Response<LogisticsMonitorListResVO> detail(@RequestBody @Valid Request<LogisticsMonitorDetailReqData> req);
    /**
     * 运营端-物流运单监控更新
     * @param req
     * @return
     */
    @PostMapping("lzyx/logistics/admin/monitor/delivery/update")
    public Response<Boolean> update(@RequestBody @Valid Request<LogisticsMonitorUpdateReqData> req);
}

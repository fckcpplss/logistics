package com.longfor.c10.lzyx.logistics.core.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.logistics.core.service.admin.ILogisticsAdminMonitorService;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorDetailReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorUpdateReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorListResVO;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 物流监控管理
 * @author zhaoyalong
 */
@Slf4j
@RestController
@RequestMapping(value = "lzyx/logistics/admin/monitor")
@RequiredArgsConstructor
public class LogisticsMonitorController {

    @Autowired
    private ILogisticsAdminMonitorService logisticsAdminMonitorService;

    /**
     * 物流运单监控列表
     * @param req
     * @return
     */
    @PostMapping("/delivery/list")
    public PageResponse<List<LogisticsMonitorListResVO>> list(@RequestBody @Valid PageRequest<LogisticsMonitorListReqData> req) {
        LogisticsMonitorListReqData logisticsMonitorListReqData = Optional.ofNullable(req).map(PageRequest::getData).orElseThrow(() -> new BusinessException("参数为空"));
        PageInfo pageInfo = Optional.ofNullable(req).map(PageRequest::getPageInfo).orElseThrow(() -> new BusinessException("分页参数为空"));
        return logisticsAdminMonitorService.deliveryList(new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize()),logisticsMonitorListReqData);
    }
    /**
     * 物流运单监控详情
     * @param req
     * @return
     */
    @PostMapping("/delivery/detail")
    public Response<LogisticsMonitorListResVO> detail(@RequestBody @Valid Request<LogisticsMonitorDetailReqData> req) {
        LogisticsMonitorDetailReqData logisticsMonitorDetailReqData = Optional.ofNullable(req).map(Request::getData).orElseThrow(() -> new BusinessException("参数为空"));
        return logisticsAdminMonitorService.deliveryDetail(logisticsMonitorDetailReqData);
    }
    /**
     * 物流运单监控更新
     * @param req
     * @return
     */
    @PostMapping("/delivery/update")
    public Response<Boolean> update(@RequestBody @Valid Request<LogisticsMonitorUpdateReqData> req) {
        LogisticsMonitorUpdateReqData logisticsMonitorUpdateReqData = Optional.ofNullable(req).map(Request::getData).orElseThrow(() -> new BusinessException("参数为空"));
        return logisticsAdminMonitorService.deliveryUpdate(logisticsMonitorUpdateReqData);
    }
}

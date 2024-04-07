package com.longfor.c10.lzyx.logistics.core.controller.admin;

import com.longfor.c10.lzyx.logistics.core.service.admin.ILogisticsAdminOrderToolsService;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsVerifyOrderListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsVerifyOrderRecordsListResData;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticOrderToolsReq;
import com.longfor.c10.lzyx.logistics.entity.dto.tools.*;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 物流运维工具管理
 */
@RestController
@RequestMapping("lzyx/logistics/admin/tools")
public class LogisticToolsController {
    @Autowired
    private ILogisticsAdminOrderToolsService logisticsAdminOrderToolsService;

    @PostMapping(path = "update")
    public Response<Boolean> update(@Valid @RequestBody  Request<List<TableInfoDTO>> request) {
        return logisticsAdminOrderToolsService.update(request);
    }

    @PostMapping(path = "list")
    public PageResponse<List<LogisticOrderToolsInfoDTO>> list(@Valid @RequestBody PageRequest<LogisticOrderToolsReq> request) {
        return logisticsAdminOrderToolsService.list(request);
    }

    @PostMapping(path = "change/deliverNo")
    public Response<Boolean> changeDeliverNo(@Valid @RequestBody Request<LogisticsDeliverToolReq> request) {
        return logisticsAdminOrderToolsService.changeDeliverNo(request);
    }

    @PostMapping(path = "change/deliverNo/record")
    public Response<List<LogisticDeliverToolRecordDTO>> changeDeliverNoRecord(@Valid @RequestBody Request<LogisticsDeliverToolReq> request) {
        return logisticsAdminOrderToolsService.changeDeliverNoRecord(request);
    }


    /**
     * 校验自提待核销文件
     * @param request request
     * @return LogisticOrderFileCheckDTO
     */
    @PostMapping(path = "batchVerify")
    Response<LogisticOrderVerifyDTO> batchVerify(@RequestBody Request<LogisticsOrderVerifyReq> request){
        return logisticsAdminOrderToolsService.batchVerify(request);
    }

    @PostMapping(path = "verifyRecord")
    public Response<LogisticOrderVerifyRecordDTO> verifyRecord(@RequestBody Request<LogisticsOrderVerifyRecordReq> request){
        return logisticsAdminOrderToolsService.verifyRecord(request);
    }

    @PostMapping(value = "batchVerifyList")
    public PageResponse<List<LogisticsVerifyOrderRecordsListResData>> batchVerifyList(@RequestBody @Valid PageRequest<LogisticsVerifyOrderListReqData> request){
        return logisticsAdminOrderToolsService.recordList(request);
    }
}

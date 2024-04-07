package com.longfor.c10.lzyx.logistics.client.api.admin;

import com.longfor.c10.lzyx.logistics.client.entity.dto.admin.LogisticDeliverToolRecordDTO;
import com.longfor.c10.lzyx.logistics.client.entity.dto.admin.LogisticOrderToolsInfoDTO;
import com.longfor.c10.lzyx.logistics.client.entity.dto.admin.LogisticOrderVerifyDTO;
import com.longfor.c10.lzyx.logistics.client.entity.dto.admin.LogisticOrderVerifyRecordDTO;
import com.longfor.c10.lzyx.logistics.client.entity.param.LogisticsVerifyOrderListReqData;
import com.longfor.c10.lzyx.logistics.client.entity.param.LogisticsVerifyOrderListResData;
import com.longfor.c10.lzyx.logistics.client.entity.param.LogisticsVerifyOrderRecordsListReqData;
import com.longfor.c10.lzyx.logistics.client.entity.param.LogisticsVerifyOrderRecordsListResData;
import com.longfor.c10.lzyx.logistics.client.entity.param.admin.*;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 物流运维工具client
 * @author zhaoyalong
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticToolsClient {

    @PostMapping(path = "lzyx/logistics/admin/tools/update")
    public Response<Boolean> update(@Valid @RequestBody Request<List<TableInfoReq>> request);

    @PostMapping(path = "lzyx/logistics/admin/tools/list")
    public PageResponse<List<LogisticOrderToolsInfoDTO>> list(@Valid @RequestBody PageRequest<LogisticOrderToolsReq> request);

    @PostMapping(path = "lzyx/logistics/admin/tools/change/deliverNo")
    public PageResponse<Boolean> changeDeliverNo(@Valid @RequestBody Request<LogisticsDeliverToolReq> request);

    @PostMapping(path = "lzyx/logistics/admin/tools/change/deliverNo/record")
    public Response<List<LogisticDeliverToolRecordDTO>> changeDeliverNoRecord(@Valid @RequestBody Request<LogisticsDeliverToolReq> request);

    /**
     * 校验自提待核销文件
     * @param request request
     * @return LogisticOrderFileCheckDTO
     */
    @PostMapping(path = "lzyx/logistics/admin/tools/batchVerify")
    Response<LogisticOrderVerifyDTO> batchVerify(@RequestBody Request<LogisticsOrderVerifyReq> request);

    /**
     * 获取核销操作日志
     * @param req req
     * @return LogisticOrderVerifyRecordDTO
     */
    @PostMapping(path = "lzyx/logistics/admin/tools/verifyRecord")
    Response<LogisticOrderVerifyRecordDTO> verifyRecord(Request<LogisticsOrderVerifyRecordReq> req);

    @PostMapping(value = "lzyx/logistics/admin/tools/batchVerifyList")
    PageResponse<List<LogisticsVerifyOrderRecordsListResData>> batchVerifyList(@RequestBody PageRequest<LogisticsVerifyOrderRecordsListReqData> request);
}

package com.longfor.c10.lzyx.logistics.core.service.admin;

import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsVerifyOrderListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsVerifyOrderListResData;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsVerifyOrderRecordsListResData;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticOrderToolsReq;
import com.longfor.c10.lzyx.logistics.entity.dto.tools.*;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 运维工具service接口类
 *
 * @author zhalyalong
 * @date 2021-12-06 11:00
 */
public interface ILogisticsAdminOrderToolsService {
    /**
     * B端-运维工具更新
     *
     * @param request
     * @returns
     */
    Response<Boolean> update(@Valid @RequestBody Request<List<TableInfoDTO>> request);

    /**
     * B端-运维工具列表
     *
     * @param request
     * @return
     */
    PageResponse<List<LogisticOrderToolsInfoDTO>> list(@Valid @RequestBody PageRequest<LogisticOrderToolsReq> request);


    /**
     * B端-运维工具-修改物流运单号
     *
     * @param request
     * @returns
     */
    Response<Boolean> changeDeliverNo(Request<LogisticsDeliverToolReq> request);

    /**
     * 修改记录
     *
     * @return com.longfor.c2.starter.data.domain.response.Response<java.util.List < Lo>>
     * @Author dongshaopeng
     * @Date 2022/6/15 3:39 下午
     * @Param
     **/
    Response<List<LogisticDeliverToolRecordDTO>> changeDeliverNoRecord(Request<LogisticsDeliverToolReq> request);

    /**
     * 运维工具-批量核销
     *
     * @param request    request
     * @return Response<LogisticOrderVerifyDTO>
     */
    Response<LogisticOrderVerifyDTO> batchVerify(Request<LogisticsOrderVerifyReq> request);

    /**
     * 运维工具-核销日志
     *
     * @param request childOrderId
     * @return Response<LogisticOrderVerifyRecordDTO>
     */
    Response<LogisticOrderVerifyRecordDTO> verifyRecord(Request<LogisticsOrderVerifyRecordReq> request);

    /**
     * 运维工具-批量核销列表
     * @param request request
     * @return PageResponse
     */
    PageResponse<List<LogisticsVerifyOrderRecordsListResData>> recordList(@RequestBody @Valid PageRequest<LogisticsVerifyOrderListReqData> request);
}

package com.longfor.c10.lzyx.logistics.client.api.merchant;

import com.longfor.c10.lzyx.logistics.client.entity.BizExportParamEntity;
import com.longfor.c10.lzyx.logistics.client.entity.param.AutoNumberReq;
import com.longfor.c10.lzyx.logistics.client.entity.param.DeliveryCompanyListReqData;
import com.longfor.c10.lzyx.logistics.client.entity.param.DeliveryCompanyResData;
import com.longfor.c10.lzyx.logistics.client.entity.param.admin.*;
import com.longfor.c10.lzyx.logistics.client.entity.param.merchant.BatchReadySendOrderDetailReqData;
import com.longfor.c10.lzyx.logistics.client.entity.param.merchant.BatchReadySendOrderDetailResData;
import com.longfor.c10.lzyx.logistics.client.entity.vo.AutoNumberVO;
import com.longfor.c10.lzyx.logistics.client.entity.vo.admin.DeliveryNoSendDetailVO;
import com.longfor.c10.lzyx.logistics.client.entity.vo.admin.DeliveryNoSendListVO;
import com.longfor.c10.lzyx.logistics.client.entity.vo.admin.DeliverySendDetailVO;
import com.longfor.c10.lzyx.logistics.client.entity.vo.admin.DeliverySendListVO;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 物流运单client
 * @author zhaoyl
 * @date 2022/2/7 下午3:10
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsDeliveryClient {
    @PostMapping(path = "lzyx/logistics/common/delivery/update/nosend")
    Response<Boolean> updateNoSend(@RequestBody Request<LogisticOrderUpdateReq> request);

    @PostMapping(path = "lzyx/logistics/common/delivery/list/nosend")
    PageResponse<List<DeliveryNoSendListVO>> getNoSendList(@RequestBody PageRequest<DeliveryNoSendListReq> request);

    @PostMapping(path = "lzyx/logistics/common/delivery/list/send")
    PageResponse<List<DeliverySendListVO>> getSendList(@RequestBody PageRequest<DeliverySendListReq> request);

    @PostMapping(path = "lzyx/logistics/common/delivery/export/nosend")
    Response<Boolean> exportNoSendList(@RequestBody Request<BizExportParamEntity> param);

    @PostMapping(path = "lzyx/logistics/common/delivery/export/send")
    Response<Boolean> exportSendList(@RequestBody Request<BizExportParamEntity> param);

    @PostMapping(path = "lzyx/logistics/common/delivery/detail/nosend")
    Response<DeliveryNoSendDetailVO> getNoSendDetail(@RequestBody Request<DeliveryNoSendDetailReq> request);


    @PostMapping(path = "lzyx/logistics/common/delivery/detail/send")
    Response<DeliverySendDetailVO> getSendDetail(@RequestBody Request<DeliverySendDetailReq> request);

    @PostMapping(path = "lzyx/logistics/common/delivery/autoNumber")
    Response<AutoNumberVO> autoNumber(@Valid @RequestBody Request<AutoNumberReq> request);

    @PostMapping(path = "lzyx/logistics/merchant/delivery/noSend/import",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Response<String> noSendImport(@RequestPart(value = "file") MultipartFile file,@RequestParam("baseReqData") String baseReqData);

    @PostMapping("lzyx/logistics/merchant/delivery/company/list")
    Response<List<DeliveryCompanyResData>> getDeliveryCompanyList(Request<DeliveryCompanyListReqData> request);
    
    @PostMapping("lzyx/logistics/merchant/delivery/batchReadySendOrder")
    Response<List<BatchReadySendOrderDetailResData>> batchReadySendOrder(@RequestBody Request<BatchReadySendOrderDetailReqData> request);
}

package com.longfor.c10.lzyx.logistics.core.controller.merchant;

import com.alibaba.fastjson.JSONObject;
import com.longfor.c10.lzyx.logistics.core.service.merchant.ILogisticsMerchantDeliveryService;
import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryCompanyListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.BatchReadySendOrderDetailResData;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.DeliveryCompanyResData;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.BatchReadySendOrderDetailReqData;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 物流运单管理控制层
 * @author zhaoyl
 * @date 2022/2/21 上午9:30
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/merchant/delivery")
public class LogisticsMerchantDeliveryController {
    @Autowired
    private ILogisticsMerchantDeliveryService logisticsMerchantDeliveryService;

    /**
     * 待发货列表批量导入，用于商户端导入
     * @param file 导入文件
     * @return 已发货列表
     */
    @PostMapping(value = "/noSend/import")
    public Response<String> noSendImport(@RequestPart(value = "file") MultipartFile file, @RequestParam("baseReqData") String baseReqData){
        BaseReqData reqData = JSONObject.parseObject(baseReqData,BaseReqData.class);
        return  logisticsMerchantDeliveryService.noSendImport(file,reqData);
    }

    /**
     * 快递公司列表
     * @param request
     * @return
     */
    @PostMapping(value = "/company/list")
    Response<List<DeliveryCompanyResData>> getDeliveryCompanyList(@RequestBody Request<DeliveryCompanyListReqData> request) {
        DeliveryCompanyListReqData req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsMerchantDeliveryService.getDeliveryCompanyList(req);
    }



    @PostMapping("/batchReadySendOrder")
    public Response<List<BatchReadySendOrderDetailResData>> batchReadySendOrder(@RequestBody @Valid Request<BatchReadySendOrderDetailReqData> request) {
        BatchReadySendOrderDetailReqData req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsMerchantDeliveryService.queryBatchReadySendOrderList(req);
    }
}

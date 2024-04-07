package com.longfor.c10.lzyx.logistics.client.api.merchant;

import com.longfor.c10.lzyx.logistics.client.entity.param.merchant.AddOrderReqData;
import com.longfor.c2.starter.data.domain.response.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 物流订单 client
 * @author zhaoyl
 * @date 2022/1/19 上午11:52
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsExportClient {

    
    /**
     * 物流下单
     */
    @PostMapping(path = "lzyx/logistics/merchant/export/import/template")
    Response<String> noSendImportTemplate();

    /**
     * 快递公司
     */
    @PostMapping("lzyx/logistics/merchant/export/company/info")
    Response<String> companyInfo();

}

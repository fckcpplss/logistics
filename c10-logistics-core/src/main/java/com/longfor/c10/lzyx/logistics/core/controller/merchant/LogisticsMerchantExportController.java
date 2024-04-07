package com.longfor.c10.lzyx.logistics.core.controller.merchant;

import com.longfor.c10.lzyx.logistics.core.service.merchant.ILogisticsMerchantExportService;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryCompanyListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.FeeListReq;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 物流运单管理控制层
 * @author zhaoyl
 * @date 2022/2/21 上午9:30
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/merchant/export")
public class LogisticsMerchantExportController {

    @Autowired
    private ILogisticsMerchantExportService logisticsMerchantExportService;

    @PostMapping("/import/template")
    public Response<String> noSendImportTemplate(){
        return logisticsMerchantExportService.noSendImportTemplate();
    }

    /**
     * 物流公司快递编码下载
     * @return
     */
    @PostMapping("/company/info")
    @ApiOperation(value = "物流公司快递编码", notes = "物流公司快递编码")
    public Response<String> companyInfo(){
        return logisticsMerchantExportService.companyInfo();
    }
}

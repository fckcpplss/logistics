package com.longfor.c10.lzyx.logistics.core.service.merchant;

import com.longfor.c10.lzyx.logistics.entity.dto.FeeListReq;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 商户端导出接口类
 * @author zhaoyl
 * @date 2022/3/31 下午8:19
 * @since 1.0
 */
public interface ILogisticsMerchantExportService {
    public Response<String> noSendImportTemplate();

    /**
     * 快递公司导出
     * @return
     */
    Response<String> companyInfo();
}

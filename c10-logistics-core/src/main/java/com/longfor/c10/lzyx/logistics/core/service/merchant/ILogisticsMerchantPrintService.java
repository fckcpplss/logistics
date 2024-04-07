package com.longfor.c10.lzyx.logistics.core.service.merchant;

import com.longfor.c10.lzyx.logistics.entity.dto.merchant.ExpressEBillReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.ExpressEBillResData;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsEbill;
import com.longfor.c2.starter.data.domain.response.Response;
import com.lop.open.api.sdk.LopException;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 商户端打印接口类
 * @author zhaoyl
 */
public interface ILogisticsMerchantPrintService {

    /**
     *
     * @param data
     * @return
     */
    List<ExpressEBillResData> print(ExpressEBillReqData data);

    Response<List<LogisticsEbill>> pdf(ExpressEBillReqData req);
}

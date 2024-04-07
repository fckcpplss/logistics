package com.longfor.c10.lzyx.logistics.core.service.merchant;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryCompanyListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.BatchReadySendOrderDetailResData;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.DeliveryCompanyResData;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.BatchReadySendOrderDetailReqData;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 商户端运单服务接口类
 * @author zhaoyl
 * @date 2022/3/29 下午2:17
 * @since 1.0
 */
public interface ILogisticsMerchantDeliveryService {
    Response<String> noSendImport(MultipartFile file,BaseReqData baseReqData);

    /**
     * 物流公司列表
     * @param req
     * @return
     */
    Response<List<DeliveryCompanyResData>> getDeliveryCompanyList(DeliveryCompanyListReqData req);

    /**
     * 物流发货详情
     * @param req
     * @return
     */
    Response<List<BatchReadySendOrderDetailResData>> queryBatchReadySendOrderList(BatchReadySendOrderDetailReqData req);
}

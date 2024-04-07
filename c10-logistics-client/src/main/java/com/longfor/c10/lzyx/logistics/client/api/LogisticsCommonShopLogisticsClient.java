package com.longfor.c10.lzyx.logistics.client.api;

import com.longfor.c10.lzyx.logistics.client.entity.param.*;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 商家物流配置
 * @author zhaoyl
 * @date 2022/3/23 下午1:40
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsCommonShopLogisticsClient {
    /**
     * 获取物流配置
     * @param request
     * @return
     */
    @PostMapping("/lzyx/logistics/common/shop/config/getConfigList")
    public Response<List<ShopLogisticsRep>> getConfigList(@RequestBody @Valid Request<ShopLogisticsListReq> request);

    /**
     * 更新物流配置
     * @param request
     * @return
     */
    @PostMapping("/lzyx/logistics/common/shop/config/updateConfigList")
    public Response<Void> updateConfigList(@RequestBody @Valid Request<ShopLogisticsListUpdReq> request);

    /**
     * 根据物流公司获取供应商的物流配置
     * @param request
     * @return
     */
    @PostMapping("/lzyx/logistics/common/shop/config/getConfigListByCompany")
    public Response<List<ShopLogisticsByCompanyRep>> getConfigListByCompany(@RequestBody @Valid Request<ShopLogisticsListReq> request);

    /**
     * 获取供应商可选的物流公司
     * @param request
     * @return
     */
    @PostMapping("/lzyx/logistics/common/shop/config/getCompanyConfigByShopId")
    public Response<List<CompangConfigListResData>> getCompanyConfigByShopId(@Valid @RequestBody Request<CompanyConfigListReqData> request);
}

package com.longfor.c10.lzyx.logistics.core.controller;

import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonShopLogisticsService;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 发货地址
 * @author zhaoyl
 * @date 2022/3/23 下午1:40
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/common/shop/config")
public class LogisticsCommonShopLogisticsController {

    @Resource
    private ILogisticsCommonShopLogisticsService logisticsCommonShopLogisticsService;

    /**
     * 获取物流配置
     * @param request
     * @return
     */
    @PostMapping("/getConfigList")
    public Response<List<ShopLogisticsRep>> getConfigList(@RequestBody @Valid Request<ShopLogisticsListReq> request) {
        ShopLogisticsListReq req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数不能为空！"));
        return Response.ok(logisticsCommonShopLogisticsService.getShopLogisticsRepList(req));
    }

    /**
     * 更新物流配置
     * @param request
     * @return
     */
    @PostMapping("/updateConfigList")
    @Transactional(rollbackFor = Exception.class)
    public Response<Void> updateConfigList(@RequestBody @Valid Request<ShopLogisticsListUpdReq> request) {
        ShopLogisticsListUpdReq req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数不能为空！"));
        logisticsCommonShopLogisticsService.updateShopLogisticsReq(req);
        return Response.ok(null);
    }

    @PostMapping("/getConfigListByCompany")
    public Response<List<ShopLogisticsByCompanyRep>> getConfigListByCompany(@RequestBody @Valid Request<ShopLogisticsListReq> request) {
        ShopLogisticsListReq req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数不能为空！"));
        return Response.ok(logisticsCommonShopLogisticsService.getShopLogisticsByCompanyRepList(req));
    }

    @PostMapping("/getCompanyConfigByShopId")
    public Response<List<CompangConfigListResData>> getCompanyConfigByShopId(@Valid @RequestBody Request<CompanyConfigListReqData> request) {
        CompanyConfigListReqData req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数不能为空！"));
        return Response.ok(logisticsCommonShopLogisticsService.getCompanyConfigByShopId(req));
    }
}

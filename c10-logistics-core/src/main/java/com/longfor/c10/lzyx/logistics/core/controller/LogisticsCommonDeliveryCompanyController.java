package com.longfor.c10.lzyx.logistics.core.controller;

import com.longfor.c10.lzyx.logistics.core.factory.DeliveryFactory;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonDeliveryCompanyService;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.CancelOrderReqData;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 配送公司控制层
 * @author zhaoyl
 * @date 2022/3/31 下午2:59
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/common/delivery/company")
public class LogisticsCommonDeliveryCompanyController {
    @Autowired
    private ILogisticsCommonDeliveryCompanyService logisticsCommonDeliveryCompanyService;
    @PostMapping(path = "add")
    @ApiOperation(value = "新增快递公司", notes = "新增快递公司")
    Response<Void> add(@RequestBody Request<CompanyAddReq> request){
        CompanyAddReq req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数为空"));
        return logisticsCommonDeliveryCompanyService.add(req);
    }

    @PostMapping(path = "update")
    @ApiOperation(value = "修改快递公司", notes = "修改快递公司")
    Response<Void> update(@RequestBody Request<CompanyUpdateReq> request){
        CompanyUpdateReq req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数为空"));
        return logisticsCommonDeliveryCompanyService.update(req);
    }

    @PostMapping(path = "delete")
    @ApiOperation(value = "删除快递公司", notes = "删除快递公司")
    Response<Void> delete(@RequestBody Request<CompanyDeleteReq> request){
        CompanyDeleteReq req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数为空"));
        return logisticsCommonDeliveryCompanyService.delete(req);
    }

    @PostMapping(path = "list")
    @ApiOperation(value = "获取快递公司", notes = "获取快递公司")
    Response<List<CompanyListRes>> list(@RequestBody Request<CompanyListReq> request){
        CompanyListReq req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("参数为空"));
        return logisticsCommonDeliveryCompanyService.list(req);
    }

    @PostMapping(path = "listByPage")
    @ApiOperation(value = "分页获取快递公司", notes = "分页获取快递公司")
    PageResponse<List<CompanyListRes>> listByPage(@RequestBody PageRequest<CompanyListReq> request){
        CompanyListReq req = Optional.ofNullable(request).map(PageRequest::getData).orElseThrow(() -> new BusinessException("参数为空"));
        PageInfo pageInfo = Optional.ofNullable(request).map(PageRequest::getPageInfo).orElseThrow(() -> new BusinessException("分页参数为空"));
        return logisticsCommonDeliveryCompanyService.listByPage(req,pageInfo);
    }
}

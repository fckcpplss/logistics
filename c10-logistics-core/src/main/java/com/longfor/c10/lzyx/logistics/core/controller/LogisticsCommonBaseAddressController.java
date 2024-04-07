package com.longfor.c10.lzyx.logistics.core.controller;

import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonBaseAddressService;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 用户地址
 * @author zhaoyl
 * @date 2022/3/23 下午1:40
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/common/address/base")
public class LogisticsCommonBaseAddressController {
    @Autowired
    private ILogisticsCommonBaseAddressService logisticsCommonBaseAddressService;
    /**
     * 新增用户地址
     */
    @PostMapping(path = "/add")
    Response<Boolean> add(@RequestBody @Valid Request<LogisticsAddressAddReqData> request){
        LogisticsAddressAddReqData req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsCommonBaseAddressService.add(req);
    }

    /**
     * 修改用户地址
     */
    @PostMapping(path = "/update")
    Response<Boolean> update(@RequestBody @Valid Request<LogisticsAddressUpdateReq> request){
        LogisticsAddressUpdateReq req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsCommonBaseAddressService.update(req);
    }

    /**
     * 查询用户地址集合
     */
    @PostMapping(path = "/list")
    PageResponse<List<LogisticsAddressInfo>> list(@RequestBody @Valid PageRequest<LogisticsAddressListReq> request){
        LogisticsAddressListReq req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        PageInfo pageInfo = Optional.ofNullable(request)
                .map(PageRequest::getPageInfo)
                .orElseThrow(() -> new BusinessException("分页参数不能为空"));
        return logisticsCommonBaseAddressService.list(req,pageInfo);
    }

    /**
     * 查询用户地址集合
     */
    @PostMapping(path = "/list/user")
    Response<List<LogisticsAddressInfo>> listByUser(@RequestBody @Valid PageRequest<LogisticsCommonNoParamReqData> request){
        LogisticsCommonNoParamReqData req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsCommonBaseAddressService.listByUser(req);
    }

    /**
     * 删除用户地址
     */
    @PostMapping(path = "/delete")
    Response<Boolean> delete(@RequestBody @Valid Request<LogisticsAddressDelReq> request){
        LogisticsAddressDelReq req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsCommonBaseAddressService.delete(req);
    }

    /**
     * 设置默认用户地址
     */
    @PostMapping(path = "/default")
    Response<Boolean> isDefault(@RequestBody @Valid Request<LogisticsAddressDefaultReq> request){
        LogisticsAddressDefaultReq req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsCommonBaseAddressService.isDefault(req);
    }

    /**
     * 地址详情
     */
    @PostMapping(path = "/detail")
    Response<LogisticsAddressInfo> detail(@RequestBody @Valid Request<LogisticsAddressDetailReqData> request){
        LogisticsAddressDetailReqData req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsCommonBaseAddressService.detail(req);
    }

    /**
     * 默认地址
     */
    @PostMapping(path = "/detail/default")
    Response<LogisticsAddressInfo> getDefault(@RequestBody @Valid Request<LogisticsCommonNoParamReqData> request){
        LogisticsCommonNoParamReqData req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsCommonBaseAddressService.defaultDetail(req);
    }
}

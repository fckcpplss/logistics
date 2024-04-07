package com.longfor.c10.lzyx.logistics.core.controller;

import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonReturnAddressService;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
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
 * 退回地址
 * @author zhaoyl
 * @date 2022/3/23 下午1:40
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/common/address/return")
public class LogisticsCommonReturnAddressController {

    @Autowired
    private ILogisticsCommonReturnAddressService logisticsCommonReturnAddressService;

    /**
     * 新增退回地址
     * @param request
     * @return
     */
    @PostMapping(path = "add")
    Response<Boolean> add(@RequestBody @Valid Request<LogisticsReturnAddressAddReq> request){
        LogisticsReturnAddressAddReq req = Optional.ofNullable(request).map(Request::getData).orElse(new LogisticsReturnAddressAddReq());
        return logisticsCommonReturnAddressService.add(req);
    }

    /**
     * 修改退回地址
     * @param request
     * @return
     */
    @PostMapping(path = "update")
    Response<Boolean> update(@RequestBody @Valid Request<LogisticsReturnAddressUpdateReq> request){
        LogisticsReturnAddressUpdateReq req = Optional.ofNullable(request).map(Request::getData).orElse(new LogisticsReturnAddressUpdateReq());
        return logisticsCommonReturnAddressService.update(req);
    }

    /**
     * 查询退回地址集合
     * @param request
     * @return
     */
    @PostMapping(path = "list")
    PageResponse<List<LogisticsReturnAddressInfo>> list(@RequestBody @Valid PageRequest<LogisticsReturnAddressListReq> request){
        LogisticsReturnAddressListReq req = Optional.ofNullable(request).map(Request::getData).orElse(new LogisticsReturnAddressListReq());
        PageInfo pageInfo = Optional.ofNullable(request).map(PageRequest::getPageInfo).orElse(new PageInfo());
        return logisticsCommonReturnAddressService.list(req,pageInfo);
    }

    /**
     * 删除退回地址
     * @param request
     * @return
     */
    @PostMapping(path = "delete")
    Response<Boolean> delete(@RequestBody @Valid Request<LogisticsReturnAddressDelReq> request){
        LogisticsReturnAddressDelReq req = Optional.ofNullable(request).map(Request::getData).orElse(new LogisticsReturnAddressDelReq());
        return logisticsCommonReturnAddressService.delete(req);
    }

    /**
     * 设置默认退回地址
     * @param request
     * @return
     */
    @PostMapping(path = "default")
    Response<Boolean> isDefault(@RequestBody @Valid Request<LogisticsReturnAddressDefaultReq> request){
        LogisticsReturnAddressDefaultReq req = Optional.ofNullable(request).map(Request::getData).orElse(new LogisticsReturnAddressDefaultReq());
        return logisticsCommonReturnAddressService.isDefault(req);
    }

    /**
     * 获取供应商信息
     * @param request
     * @return
     */
    @PostMapping(path = "getSpr")
    Response<List<LogisticsReturnSprRes>> getSpr(@RequestBody @Valid Request<LogisticsReturnSprReq> request){
        LogisticsReturnSprReq req = Optional.ofNullable(request).map(Request::getData).orElse(new LogisticsReturnSprReq());
        return logisticsCommonReturnAddressService.getSpr(req);
    }

    /**
     * 描述: 获取退换货地址-第一条为退回地址
     */
    @PostMapping("getReturnAddress")
    Response<LogisticsReturnAddressResData> getReturnAddress(@RequestBody @Valid Request<LogisticsReturnAddressReqData> request){
        return logisticsCommonReturnAddressService.getReturnAddress(request);
    }
}

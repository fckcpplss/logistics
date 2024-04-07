package com.longfor.c10.lzyx.logistics.core.controller;

import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonShipAddressService;
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
 * 发货地址
 * @author zhaoyl
 * @date 2022/3/23 下午1:40
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/common/address/ship")
public class LogisticsCommonShipAddressController {

    @Autowired
    private ILogisticsCommonShipAddressService logisticsCommonShipAddressService;

    /**
     * 新增发货地址
     * @param request
     * @return
     */
    @PostMapping(path = "add")
    Response<Boolean> add(@RequestBody @Valid Request<LogisticsShipAddressAddReq> request){
        LogisticsShipAddressAddReq req = Optional.ofNullable(request).map(Request::getData).orElse(new LogisticsShipAddressAddReq());
        return logisticsCommonShipAddressService.add(req);
    }

    /**
     * 修改发货地址
     * @param request
     * @return
     */
    @PostMapping(path = "update")
    Response<Boolean> update(@RequestBody @Valid Request<LogisticsShipAddressUpdateReq> request){
        LogisticsShipAddressUpdateReq req = Optional.ofNullable(request).map(Request::getData).orElse(new LogisticsShipAddressUpdateReq());
        return logisticsCommonShipAddressService.update(req);
    }

    /**
     * 查询发货地址集合
     * @param request
     * @return
     */
    @PostMapping(path = "list")
    PageResponse<List<LogisticsShipAddressInfo>> list(@RequestBody @Valid PageRequest<LogisticsShipAddressListReq> request){
        LogisticsShipAddressListReq req = Optional.ofNullable(request).map(Request::getData).orElse(new LogisticsShipAddressListReq());
        PageInfo pageInfo = Optional.ofNullable(request).map(PageRequest::getPageInfo).orElse(new PageInfo());
        return logisticsCommonShipAddressService.list(req,pageInfo);
    }

    /**
     * 删除发货地址
     * @param request
     * @return
     */
    @PostMapping(path = "delete")
    Response<Boolean> delete(@RequestBody @Valid Request<LogisticsShipAddressDelReq> request){
        LogisticsShipAddressDelReq req = Optional.ofNullable(request).map(Request::getData).orElse(new LogisticsShipAddressDelReq());
        return logisticsCommonShipAddressService.delete(req);
    }

    /**
     * 设置默认发货地址
     * @param request
     * @return
     */
    @PostMapping(path = "default")
    Response<Boolean> isDefault(@RequestBody @Valid Request<LogisticsShipAddressDefaultReq> request){
        LogisticsShipAddressDefaultReq req = Optional.ofNullable(request).map(Request::getData).orElse(new LogisticsShipAddressDefaultReq());
        return logisticsCommonShipAddressService.isDefault(req);
    }

    /**
     * 获取供应商信息
     * @param request
     * @return
     */
    @PostMapping(path = "getSpr")
    Response<List<LogisticsShipSprRes>> getSpr(@RequestBody @Valid Request<LogisticsShipSprReq> request){
        LogisticsShipSprReq req = Optional.ofNullable(request).map(Request::getData).orElse(new LogisticsShipSprReq());
        return logisticsCommonShipAddressService.getSpr(req);
    }
}

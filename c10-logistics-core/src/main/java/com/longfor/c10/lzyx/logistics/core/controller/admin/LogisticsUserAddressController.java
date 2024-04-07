package com.longfor.c10.lzyx.logistics.core.controller.admin;

import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c10.lzyx.logistics.core.service.admin.ILogisticsUserAddressService;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 物流用户地址管理控制层
 * @author zhaoyl
 * @date 2022/2/7 下午3:12
 * @since 1.0
 */
@RestController
@RequestMapping("lzyx/logistics/admin/address")
public class LogisticsUserAddressController {

    @Autowired
    private ILogisticsUserAddressService logisticsUserAddressService;

    @PostMapping(path = "add")
    Response<LogisticsAddressAddResp> add(@Valid @RequestBody Request<LogisticsAddressAddReqData> request){
        LogisticsAddressAddReqData req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsUserAddressService.add(req);
    }

    @PostMapping(path = "update")
    Response<Void> update(@Valid @RequestBody Request<LogisticsAddressUpdateReq> request){
        LogisticsAddressUpdateReq req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsUserAddressService.update(req);
    }

    @PostMapping(path = "list")
    PageResponse<List<LogisticsAddressInfo>> queryList(@Valid @RequestBody PageRequest<LogisticsAddressListReq> request){
        PageInfo pageInfo = Optional.ofNullable(request)
                .map(PageRequest::getPageInfo)
                .orElseThrow(() -> new BusinessException("分页参数不能为空"));
        LogisticsAddressListReq req = Optional.ofNullable(request)
                .map(PageRequest::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsUserAddressService.queryList(pageInfo,req);
    }

    @PostMapping(path = "delete")
    Response<Void> delete(@Valid @RequestBody Request<LogisticsAddressDelReq> request){
        Optional.ofNullable(request)
                .map(Request::getData)
                .map(req -> StringUtils.isBlank(req.getAddressId()) ? null : req.getAddressId())
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsUserAddressService.delete(request.getData());
    }
}

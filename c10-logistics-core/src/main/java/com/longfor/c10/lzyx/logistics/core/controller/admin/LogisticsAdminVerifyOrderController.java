package com.longfor.c10.lzyx.logistics.core.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.aliyun.openservices.shade.com.google.common.collect.ImmutableMap;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonVerifyOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.enums.LogisticsVerifyVerifyTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.common.domain.IResultCode;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 自提/核销订单公共控制层
 * @author zhaoyl
 * @date 2022/3/31 下午2:59
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/admin/order/verify")
public class LogisticsAdminVerifyOrderController {

    @Resource(name = "logisticsAdminVerifyOrderServiceImpl")
    private ILogisticsCommonVerifyOrderService logisticsCommonVerifyOrderService;

    @PostMapping(value = "list")
    public PageResponse<List<LogisticsVerifyOrderListResData>> list(@RequestBody @Valid PageRequest<LogisticsVerifyOrderListReqData> request){
        LogisticsVerifyOrderListReqData req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("请求参数为空"));
        PageInfo pageInfo = Optional.ofNullable(request).map(PageRequest::getPageInfo).orElseThrow(() -> new BusinessException("分页参数为空"));
        return logisticsCommonVerifyOrderService.list(req,pageInfo);
    }

    @PostMapping(value = "record/list")
    public PageResponse<List<LogisticsVerifyOrderRecordsListResData>> recordList(@RequestBody @Valid PageRequest<LogisticsVerifyOrderListReqData> request){
        LogisticsVerifyOrderListReqData req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("请求参数为空"));
        PageInfo pageInfo = Optional.ofNullable(request).map(PageRequest::getPageInfo).orElseThrow(() -> new BusinessException("分页参数为空"));
        return logisticsCommonVerifyOrderService.recordList(req,pageInfo);
    }

    @PostMapping(value = "detail/batch")
    public Response<LogisticsVerifyOrderDetailResWithCodeData> batchDetail(@RequestBody @Valid Request<LogisticsVerifyOrderDetailReqData> request){
        LogisticsVerifyOrderDetailReqData req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("请求参数为空"));
        if(CollectionUtils.isEmpty(req.getOrderNos())){
            throw new BusinessException("请求参数为空");
        }
        Response<List<LogisticsVerifyOrderDetailResData>> listResponse = logisticsCommonVerifyOrderService.detailList(req);
        if(ListUtils.emptyIfNull(listResponse.getData()).stream().map(LogisticsVerifyOrderDetailResData::getPickupAddressId).distinct().count() > 1){
            throw new BusinessException(new StringBuilder("请选择相同自提点订单进行核销").toString());
        }
        long lmIdCount = ListUtils.emptyIfNull(listResponse.getData()).stream().map(LogisticsVerifyOrderDetailResData::getLmId).distinct().count();
        return Response.ok(new LogisticsVerifyOrderDetailResWithCodeData(listResponse.getData(),lmIdCount > 1 ? 10001 : IResultCode.IResultCodeEnum.SUCCESS.getCode()));
    }

    @PostMapping(value = "detail")
    public Response<LogisticsVerifyOrderDetailResData> detail(@RequestBody @Valid Request<LogisticsVerifyOrderDetailReqData> request){
        LogisticsVerifyOrderDetailReqData req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("请求参数为空"));
        if(StringUtils.isBlank(req.getOrderNo())){
            throw new BusinessException("请求参数为空");
        }
        return Response.ok(ListUtils.emptyIfNull(logisticsCommonVerifyOrderService.detailList(req).getData())
                .stream()
                .findFirst()
                .orElse(null));
    }

    @PostMapping(value = "verify")
    public Response<String> verify(@RequestBody @Valid Request<LogisticsVerifyOrderVerifyReqData> request){
        LogisticsVerifyOrderVerifyReqData req = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("请求参数为空"));
        Optional.ofNullable(StrUtil.blankToDefault(req.getPickupCode(),null)).orElseThrow(() -> new BusinessException("核销码不能为空"));
        req.setVerifyType(LogisticsVerifyVerifyTypeEnum.VERIFY_ADMIN);
        return logisticsCommonVerifyOrderService.verify(req);
    }

}

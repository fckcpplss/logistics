package com.longfor.c10.lzyx.logistics.core.controller;

import com.longfor.c10.lzyx.download.client.entity.BizExportParamEntity;
import com.longfor.c10.lzyx.logistics.core.factory.DeliveryFactory;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonDeliveryService;
import com.longfor.c10.lzyx.logistics.core.service.impl.delivery.KuaiDi100ServiceImpl;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliveryNoSendDetailVO;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliveryNoSendListVO;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliverySendDetailVO;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliverySendListVO;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 物流运单管理控制层
 * @author zhaoyl
 * @date 2022/2/21 上午9:30
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/common/delivery")
public class LogisticsCommonDeliveryController {

    @Autowired
    private ILogisticsCommonDeliveryService logisticsCommonDeliveryService;

    /**
     * 更新待发货状态（用于需求单作废时， 如果子订单未发货， 则更新状态）
     */
    @PostMapping(path = "/update/nosend")
    public Response<Boolean> updateNoSend(@RequestBody Request<List<LogisticOrderUpdateReq>> request) {
        return logisticsCommonDeliveryService.updateNoSend(request);
    }

    /**
     * 待发货列表
     */
    @PostMapping(path = "/list/nosend")
    public PageResponse<List<DeliveryNoSendListVO>> getNoSendList(@RequestBody PageRequest<DeliveryNoSendListReq> request) {
        PageInfo pageInfo = Optional.ofNullable(request)
                .map(PageRequest::getPageInfo)
                .orElseThrow(() -> new BusinessException("分页参数不能为空"));
        DeliveryNoSendListReq req = Optional.ofNullable(request)
                .map(PageRequest::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsCommonDeliveryService.getNoSendList(pageInfo,req);
    }
    /**
     * 待发货列表详情
     */
    @PostMapping(path = "/detail/nosend")
    public Response<DeliveryNoSendDetailVO> getNoSendDetail(@RequestBody @Valid Request<DeliveryNoSendDetailReq> request) {
        DeliveryNoSendDetailReq req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        //商户端地址不脱敏
        if(CollectionUtils.isNotEmpty(req.getShopIds())){
            req.setDesensitizedFlag(false);
        }
        return logisticsCommonDeliveryService.getNoSendDetail(req);
    }

    /**
     * 已发货列表
     */
    @PostMapping(path = "/list/send")
    public PageResponse<List<DeliverySendListVO>> getSendList(@RequestBody PageRequest<DeliverySendListReq> request) {
        PageInfo pageInfo = Optional.ofNullable(request)
                .map(PageRequest::getPageInfo)
                .orElseThrow(() -> new BusinessException("分页参数不能为空"));
        DeliverySendListReq req = Optional.ofNullable(request)
                .map(PageRequest::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsCommonDeliveryService.getSendList(pageInfo,req);
    }

    /**
     * 已发货列表详情
     */
    @PostMapping(path = "/detail/send")
    public Response<DeliverySendDetailVO> getSendDetail(@RequestBody @Valid Request<DeliverySendDetailReq> request) {
        DeliverySendDetailReq req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsCommonDeliveryService.getSendDetail(req);
    }


    /**
     * 待发货列表导出
     */
    @PostMapping(path = "/export/nosend")
    public Response<Boolean> exportNoSendList(@RequestBody Request<BizExportParamEntity> param) {
        return logisticsCommonDeliveryService.exportNoSendList(param);
    }

    /**
     * 待发货列表导出
     */
    @PostMapping(path = "/export/send")
    public Response<Boolean> exportSendList(@RequestBody Request<BizExportParamEntity> param) {
        return logisticsCommonDeliveryService.exportSendList(param);
    }




}

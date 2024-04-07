package com.longfor.c10.lzyx.logistics.core.service.internal.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.longfor.c10.lzyx.logistics.core.service.internal.ILogisticsInternalDeliveryService;
import com.longfor.c10.lzyx.logistics.dao.service.*;
import com.longfor.c10.lzyx.logistics.entity.dto.internal.LogisticsOrderSkuStatusDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.internal.LogisticsOrderStatusDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.internal.LogisticsOrderStatusReqData;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsVerifyOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.enums.DeleteStatusEnum;
import com.longfor.c10.lzyx.order.entity.enums.ReceiptTypeEnum;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 物流内部-接口实现类
 * @author zhaoyl
 * @date 2022/4/27 上午9:53
 * @since 1.0
 */
@Slf4j
@Service
public class LogisticsInternalDeliveryServiceImpl implements ILogisticsInternalDeliveryService {
    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Autowired
    private ILogisticsVerifyOrderService logisticsVerifyOrderService;

    @Autowired
    private ILogisticsVerifyOrderGoodsService logisticsVerifyOrderGoodsService;

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;
    /**
     * @param req
     * @return
     */
    @Override
    public Response<List<LogisticsOrderStatusDTO>> getOrderSkuLogisticsStatusList(LogisticsOrderStatusReqData req) {
        if(StringUtils.isEmpty(req.getChildOrderId())){
            return Response.ok(Collections.emptyList());
        }
        LogisticsOrder logisticsOrders = logisticsOrderService.getOne(Wrappers.<LogisticsOrder>lambdaQuery().eq(LogisticsOrder::getChildOrderId, req.getChildOrderId()).last(" limit 1"));
        if(Objects.nonNull(logisticsOrders)){
            return getLogisticsOrderStatusDataList(logisticsOrders);
        }
        return getLogisticsVerifyOrderStatusDataList(req.getChildOrderId());
    }

    private Response<List<LogisticsOrderStatusDTO>> getLogisticsOrderStatusDataList(LogisticsOrder logisticsOrder) {
        //订单商品集合
        List<LogisticsOrderGoods> logisticsOrderGoods = logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                .eq(LogisticsOrderGoods::getLogisticsOrderId, logisticsOrder.getId())
                .eq(LogisticsOrderGoods::getDeleteStatus, DeleteStatusEnum.NO.getCode()));
        //商品运单id集合
        List<Long> logisticsDeliveryIds = ListUtils.emptyIfNull(logisticsOrderGoods).stream().map(LogisticsOrderGoods::getLogisticsDeliveryId).distinct().collect(Collectors.toList());
        //运单id和物流状态
        Map<Long, Integer> logisticsDeliveryIdAndStatusMap = CollectionUtils.isEmpty(logisticsDeliveryIds) ? Collections.emptyMap() : ListUtils.emptyIfNull(logisticsDeliveryService.list(Wrappers.<LogisticsDelivery>lambdaQuery().in(LogisticsDelivery::getId, logisticsDeliveryIds).eq(LogisticsDelivery::getIfCancel, 0)))
                .stream()
                .collect(Collectors.toMap(LogisticsDelivery::getId, LogisticsDelivery::getLogisticsStatus, (a, b) -> a));
        return Response.ok(ListUtils.emptyIfNull(logisticsOrderGoods)
                .stream()
                .collect(Collectors.groupingBy(LogisticsOrderGoods::getLogisticsOrderId,Collectors.collectingAndThen(Collectors.toList(),list -> {
                    return ListUtils.emptyIfNull(list).stream().map(goodsInfo ->{
                        LogisticsOrderSkuStatusDTO logisticsOrderSkuStatusDTO = new LogisticsOrderSkuStatusDTO();
                        BeanUtils.copyProperties(goodsInfo,logisticsOrderSkuStatusDTO);
                        Optional.ofNullable(logisticsDeliveryIdAndStatusMap).map(x -> x.get(goodsInfo.getLogisticsDeliveryId())).ifPresent(logisticsStatus -> {
                            logisticsOrderSkuStatusDTO.setLogisticsStatus(logisticsStatus);
                        });
                        logisticsOrderSkuStatusDTO.setReceiptType(ReceiptTypeEnum.BUSINESS_LOGISTICS.getCode());
                        return logisticsOrderSkuStatusDTO;
                    }).collect(Collectors.toList());
                })))
                .entrySet()
                .stream()
                .map(entry -> new LogisticsOrderStatusDTO(logisticsOrder.getChildOrderId(),entry.getValue()))
                .filter(x -> Objects.nonNull(x.getChildOrderId()))
                .collect(Collectors.toList()));
    }
    private Response<List<LogisticsOrderStatusDTO>> getLogisticsVerifyOrderStatusDataList(String childOrderId) {
        List<LogisticsVerifyOrderGoods> logisticsOrderGoods = logisticsVerifyOrderGoodsService.list(Wrappers.<LogisticsVerifyOrderGoods>lambdaQuery()
                .eq(LogisticsVerifyOrderGoods::getChildOrderId, childOrderId)
                .eq(LogisticsVerifyOrderGoods::getIsDelete, DeleteStatusEnum.NO.getCode()));
        return Response.ok(ListUtils.emptyIfNull(logisticsOrderGoods)
                .stream()
                .collect(Collectors.groupingBy(LogisticsVerifyOrderGoods::getChildOrderId,Collectors.collectingAndThen(Collectors.toList(),list -> {
                    return ListUtils.emptyIfNull(list).stream().map(goodsInfo ->{
                        LogisticsOrderSkuStatusDTO logisticsOrderSkuStatusDTO = new LogisticsOrderSkuStatusDTO();
                        BeanUtils.copyProperties(goodsInfo,logisticsOrderSkuStatusDTO);
                        logisticsOrderSkuStatusDTO.setReceiptType(ReceiptTypeEnum.PICKUP.getCode());
                        logisticsOrderSkuStatusDTO.setLogisticsStatus(goodsInfo.getVerifyStatus());
                        return logisticsOrderSkuStatusDTO;
                    }).collect(Collectors.toList());
                })))
                .entrySet()
                .stream()
                .map(entry -> new LogisticsOrderStatusDTO(childOrderId,entry.getValue()))
                .filter(x -> Objects.nonNull(x.getChildOrderId()))
                .collect(Collectors.toList()));
    }
}

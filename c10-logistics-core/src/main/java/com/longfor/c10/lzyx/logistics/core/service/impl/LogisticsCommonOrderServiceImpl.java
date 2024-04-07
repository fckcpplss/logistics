package com.longfor.c10.lzyx.logistics.core.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.longfor.c10.lzyx.logistics.core.factory.DeliveryFactory;
import com.longfor.c10.lzyx.logistics.core.service.IDeliveryService;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonOrderService;
import com.longfor.c10.lzyx.logistics.core.service.impl.delivery.KuaiDi100ServiceImpl;
import com.longfor.c10.lzyx.logistics.core.util.CollectionUtils;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderGoodsService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsDetailRes;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.UserTokenInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.*;
import com.longfor.c10.lzyx.logistics.entity.dto.user.PathItemResData;
import com.longfor.c10.lzyx.logistics.entity.dto.user.TrajectoryResp;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.enums.*;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.common.util.StringUtil;
import com.longfor.c2.starter.data.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaoyl
 * @date 2022/4/20 下午6:28
 * @since 1.0
 */
@Slf4j
@Service
public class LogisticsCommonOrderServiceImpl implements ILogisticsCommonOrderService {
    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;

    @Autowired
    private DeliveryFactory deliveryFactory;

    @Autowired
    private KuaiDi100ServiceImpl kuaiDi100Service;

    @Value("${logistics.track.phone.switch:shunfeng,fengwang}")
    private String logisticsTrackSwitch;

    @Override
    public Response<Void> cancel(DeliveryCancelReqData req,BusinessTypeEnum businessTypeEnum) {
        log.info("订单取消，开始");
        handelCancel(req,businessTypeEnum);
        log.info("订单取消，结束");
        return Response.ok(null);
    }

    @Override
    public Response<Map<String, DeliveryCancelResData>> refundOrder(DeliveryRefundOrderReq req) {
        log.info("退单，请求参数，childOrderId = {},skuList = {}",req.getChildOrderId(),req.getSkuIds());
        Map<String, DeliveryCancelResData> resDataMap = new HashMap<>(8);
        LogisticsOrder logisticsOrder = Optional.ofNullable(logisticsOrderService.getOne(Wrappers.<LogisticsOrder>lambdaQuery().eq(LogisticsOrder::getChildOrderId, req.getChildOrderId()).last(" limit 1"))).orElseThrow(() -> new BusinessException("订单不存在"));
        log.info("退单，订单信息，childOrderId = {},logisticsOrderId = {}",req.getChildOrderId(),logisticsOrder.getId());
        Map<String, LogisticsOrderGoods> skuIdAndMap = ListUtils.emptyIfNull(logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                .eq(LogisticsOrderGoods::getLogisticsOrderId, logisticsOrder.getId())
                .eq(LogisticsOrderGoods::getDeleteStatus,DeleteStatusEnum.NO.getCode())
                .in(LogisticsOrderGoods::getSkuId, req.getSkuIds())))
                .stream().collect(Collectors.toMap(LogisticsOrderGoods::getSkuId, Function.identity(), (a, b) -> a));
        log.info("退单，订单商品信息，childOrderId = {},skuMapSize = {}",req.getChildOrderId(),skuIdAndMap.size());
        req.getSkuIds().stream().filter(skuId -> !skuIdAndMap.containsKey(skuId)).findAny().ifPresent(skuId ->{
            log.info("退单，skuId不存在，childOrderId = {},skuId = {}",req.getChildOrderId(),skuId);
            throw new BusinessException(new StringBuilder("skuId：").append(skuId).append("不存在").toString());
        });
        //运单id集合
        List<Long> logisticsDeliveryIds = skuIdAndMap.values().stream().map(LogisticsOrderGoods::getLogisticsDeliveryId).filter(Objects::nonNull).collect(Collectors.toList());
        //运单id和map
        Map<Long, LogisticsDelivery> deliveryIdAndMap = CollectionUtils.isEmpty(logisticsDeliveryIds) ? Collections.emptyMap() : ListUtils.emptyIfNull(logisticsDeliveryService.listByIds(logisticsDeliveryIds)).stream().collect(Collectors.toMap(LogisticsDelivery::getId, Function.identity(), (a, b) -> a));
        //仅退款sku集合
        List<String> refundOnlySkuIds = ListUtils.emptyIfNull(req.getSkuIds())
                .stream()
                .filter(x -> skuIdAndMap.containsKey(x) && Objects.isNull(skuIdAndMap.get(x).getLogisticsDeliveryId()))
                .collect(Collectors.toList());
        log.info("退单，仅退款sku集合，childOrderId = {},refundOnlySkuIds = {}",req.getChildOrderId(),refundOnlySkuIds);
        req.getSkuIds().removeAll(refundOnlySkuIds);
        //需取消发货sku集合
        List<String> refundGoodsSkuIds = ListUtils.emptyIfNull(req.getSkuIds())
                .stream()
                .filter(x -> {
                    LogisticsDelivery logisticsDelivery = deliveryIdAndMap.get(skuIdAndMap.get(x).getLogisticsDeliveryId());
                    if (Objects.isNull(logisticsDelivery) || Objects.isNull(logisticsDelivery.getShopLogisticsId())) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
        log.info("退单，快递100发货需退货退款sku集合,childOrderId = {},refundGoodsSkuIds= {}",req.getChildOrderId(),refundGoodsSkuIds);
        req.getSkuIds().removeAll(refundGoodsSkuIds);
        log.info("退单，需取消发货sku集合,childOrderId = {},refundGoodsSkuIds= {}",req.getChildOrderId(),req.getSkuIds());
        ListUtils.emptyIfNull(req.getSkuIds())
                .stream()
                .forEach(skuId -> {
                    LogisticsDelivery logisticsDelivery = deliveryIdAndMap.get(skuIdAndMap.get(skuId).getLogisticsDeliveryId());
                    //快递公司编码
                    AtomicReference<String> companyCode = new AtomicReference<>();
                    //获取物流服务
                    IDeliveryService deliveryService = deliveryFactory.getService(logisticsOrder.getSendReturn(),logisticsDelivery.getShopLogisticsId(),logisticsDelivery.getCompanyCode());
                    CancelOrderReqData cancelOrderReqData = new CancelOrderReqData();
                    cancelOrderReqData.setOrderId(String.valueOf(logisticsDelivery.getId()));
                    cancelOrderReqData.setDeliveryId(logisticsDelivery.getDeliveryNo());
                    Optional.ofNullable(req.getAmUserInfo()).ifPresent(userInfo -> {
                        cancelOrderReqData.setCancelOperator(userInfo.getUserName());
                    });
                    if(Objects.nonNull(deliveryService.checkCanCancelOrder(cancelOrderReqData))){
                        log.info("退单，取消发货成功走仅退款sku集合,childOrderId = {},skuId= {}",req.getChildOrderId(),skuId);
                        refundOnlySkuIds.add(skuId);
                    }else {
                        log.info("退单，取消发货失败走退货退款sku集合,childOrderId = {},skuId= {}",req.getChildOrderId(),skuId);
                        refundGoodsSkuIds.add(skuId);
                    }
                });
        ListUtils.emptyIfNull(refundGoodsSkuIds).stream().forEach(item -> {
            resDataMap.put(item,new DeliveryCancelResData(1,"退货退款"));
        });
        ListUtils.emptyIfNull(refundOnlySkuIds).stream().forEach(item -> {
            resDataMap.put(item,new DeliveryCancelResData(2,"仅退款"));
        });
        return Response.ok(resDataMap);
    }

    private void handelCancel(DeliveryCancelReqData data,BusinessTypeEnum businessTypeEnum){
        log.info("通用取消发货，deliveryId = {},businessType = {}",data.getDeliveryId(),businessTypeEnum.getDesc());
        LogisticsDelivery logisticsDelivery = logisticsDeliveryService.getById(data.getDeliveryId());
        if(Objects.isNull(logisticsDelivery)){
            throw new BusinessException("运单不存在");
        }
        log.info("通用取消发货，调用api取消组装参数，deliveryId = {},logisticsType = {}",data.getDeliveryId(),logisticsDelivery.getLogisticsType());
        IDeliveryService deliveryService = Optional.ofNullable(deliveryFactory.getService(null,logisticsDelivery.getShopLogisticsId(),logisticsDelivery.getCompanyCode())).orElseThrow(() -> new BusinessException("未找对物流服务"));
        CancelOrderReqData cancelOrderReqData = new CancelOrderReqData();
        cancelOrderReqData.setBusinessTypeEnum(businessTypeEnum);
        cancelOrderReqData.setOrderId(String.valueOf(logisticsDelivery.getId()));
        cancelOrderReqData.setDeliveryId(logisticsDelivery.getDeliveryNo());
        Optional.ofNullable(data.getAmUserInfo()).ifPresent(userInfo -> {
            cancelOrderReqData.setCancelOperator(userInfo.getUserName());
        });
        Optional.ofNullable(data.getUserTokenInfo()).ifPresent(userInfo -> {
            cancelOrderReqData.setCancelOperator(userInfo.getAccount());
        });
        log.info("通用取消发货,调用api取消组装参数，cancelOrderReqData: {}", JSON.toJSONString(cancelOrderReqData));
        //取消运单
        deliveryService.cancelOrder(cancelOrderReqData);
    }

    @Override
    public Response<LogisticsDetailRes> detail(LogisticsDeliveryIdReqData req) {
        return Response.ok(Optional.ofNullable(logisticsDeliveryService.getById(req.getDeliveryId()))
                .map(delivery ->{
                    LogisticsDetailRes logisticsDetailRes  = new LogisticsDetailRes();
                    Optional.ofNullable(logisticsOrderService.getById(delivery.getLogisticsOrderId())).ifPresent(order -> {
                        logisticsDetailRes.setUserId(order.getUserId());
                        logisticsDetailRes.setDeliveryRemarks(order.getDeliveryRemarks());
                    });
                    logisticsDetailRes.setChildOrderId(String.valueOf(delivery.getLogisticsOrderId()));
                    logisticsDetailRes.setReceiptName(delivery.getReceiptName());
                    logisticsDetailRes.setReceiptPhone(delivery.getReceiptPhone());
                    logisticsDetailRes.setReceiptProvince(delivery.getReceiptProvince());
                    logisticsDetailRes.setReceiptCity(delivery.getReceiptCity());
                    logisticsDetailRes.setReceiptArea(delivery.getReceiptArea());
                    logisticsDetailRes.setReceiptAddress(delivery.getReceiptAddress());
                    return logisticsDetailRes;
                }).orElse(null));
    }

    @Override
    public Response<TrajectoryResp> getPath(DeliveryPathReq req) {
        String lmId = Optional.ofNullable(req.getUserTokenInfo()).map(UserTokenInfo::getLmId).orElse(null);
        LogisticsOrder logisticsOrder = Optional.ofNullable(logisticsOrderService.getOne(Wrappers.<LogisticsOrder>lambdaQuery()
                .eq(LogisticsOrder::getChildOrderId, req.getChildOrderId())
                .eq(StringUtils.isNotBlank(lmId), LogisticsOrder::getUserId, lmId)
                .eq(LogisticsOrder::getDeleteStatus, DeleteStatusEnum.NO.getCode())
                .last(" limit 1"))).orElseThrow(() -> new BusinessException("未查询到物流轨迹"));
        log.info("查询物流轨迹，查询订单信息，childOrderId = {},logisticsOrderId = {}",req.getChildOrderId(),logisticsOrder.getId());
        LogisticsDelivery logisticsDelivery = Optional.ofNullable(logisticsDeliveryService.getOne(Wrappers.<LogisticsDelivery>lambdaQuery()
                .eq(LogisticsDelivery::getLogisticsOrderId, logisticsOrder.getId())
                .eq(StringUtils.isNotBlank(req.getDeliveryNo()), LogisticsDelivery::getDeliveryNo, req.getDeliveryNo())
                .eq(LogisticsDelivery::getDeleteStatus, DeleteStatusEnum.NO.getCode())
                .eq(LogisticsDelivery::getIfCancel, 0)
                .last(" limit 1"))).orElseThrow(() -> new BusinessException("未查询到物流轨迹"));
        log.info("查询物流轨迹，查询运单信息，childOrderId = {},logisticsDeliveryId = {}",req.getChildOrderId(),logisticsDelivery.getId());
        //获取物流服务
        IDeliveryService deliveryService = deliveryFactory.getService(logisticsOrder.getSendReturn(),logisticsDelivery.getShopLogisticsId(),logisticsDelivery.getCompanyCode());
        log.info("查询物流轨迹，查询物流服务，childOrderId = {},deliveryService = {}",req.getChildOrderId(),deliveryService);
        DeliveryPathReqData deliveryPathReqData = new DeliveryPathReqData();
        deliveryPathReqData.setCompanyCode(logisticsDelivery.getCompanyCode());
        deliveryPathReqData.setLogisticsOrderId(String.valueOf(logisticsOrder.getId()));
        deliveryPathReqData.setChildOrderId(logisticsOrder.getChildOrderId());
        deliveryPathReqData.setWaybillId(logisticsDelivery.getDeliveryNo());
        //调用三方快递接口传递收件人电话
        //deliveryPathReqData.setPhone(Optional.ofNullable(logisticsDelivery.getSendPhone()).orElse(""));
        if(logisticsDelivery.getCompanyCode() !=null && logisticsTrackSwitch.contains(logisticsDelivery.getCompanyCode())){
            deliveryPathReqData.setPhone(StringUtil.isNotBlank(logisticsDelivery.getReceiptPhone()) ? logisticsDelivery.getReceiptPhone() : logisticsDelivery.getSendPhone());
        }else {
            deliveryPathReqData.setPhone("");
        }
        log.info("查询物流轨迹，查询轨迹请求参数，childOrderId = {},deliveryPathReqData = {}",req.getChildOrderId(), JSON.toJSONString(deliveryPathReqData));
        List<DeliveryPathResData.PathItem> pathList = Optional.ofNullable(deliveryService.getDeliveryPath(deliveryPathReqData))
                .map(DeliveryPathResData::getPathItemList)
                .map(CollectionUtils::nullIfEmpty)
                .orElseThrow(() -> new BusinessException("未查询到物流轨迹"));
        log.info("查询物流轨迹，查询轨迹返回结果，childOrderId = {},pathList = {}",req.getChildOrderId(), JSON.toJSONString(pathList));
        List<PathItemResData> pathItemResDataList = pathList.stream().map(path -> {
            PathItemResData pathItemResData = new PathItemResData();
            Pair<Integer, Integer> orderStatus = deliveryService.getFromPathState(path.getPathState());
            pathItemResData.setPathTime(DateUtil.formatDateTime(DateUtil.date(path.getPathTime())));
            pathItemResData.setPathMsg(path.getPathMsg());
            pathItemResData.setPathDes(path.getPathDes());
            pathItemResData.setPathState(path.getPathState());
            pathItemResData.setLogisticsCenterStateName(Optional.ofNullable(orderStatus).map(Pair::getKey).map(DeliveryLogisticsStatusEnum::fromCode).map(DeliveryLogisticsStatusEnum::getDesc).orElse("未知"));
            return pathItemResData;
        }).collect(Collectors.toList());
        TrajectoryResp trajectoryResp = new TrajectoryResp();
        trajectoryResp.setDeliverTime(null);
//        trajectoryResp.setDeliverTime(getDeliveryTime(deliveryService,logisticsDelivery,logisticsOrder));
        trajectoryResp.setPathItemList(pathItemResDataList);
        return Response.ok(trajectoryResp);
    }

    /**
     * 获取预计送达时间
     * @param deliveryService
     * @param logisticsDelivery
     * @return
     */
    private String getDeliveryTime(IDeliveryService deliveryService, LogisticsDelivery logisticsDelivery,LogisticsOrder logisticsOrder) {
        try{
            DeliverTimeReqData deliverTimeReqData = new DeliverTimeReqData();
            deliverTimeReqData.setOrderId(String.valueOf(logisticsDelivery.getId()));
            deliverTimeReqData.setWaybillId(logisticsDelivery.getDeliveryNo());
            deliverTimeReqData.setCompanyCode(logisticsDelivery.getCompanyCode());
            deliverTimeReqData.setFrom(StrUtil.concat(true,logisticsDelivery.getSendProvince(),logisticsDelivery.getSendCity(),logisticsDelivery.getSendArea(),logisticsDelivery.getSendAddress()));
            deliverTimeReqData.setTo(StrUtil.concat(true,logisticsDelivery.getReceiptProvince(),logisticsDelivery.getReceiptCity(),logisticsDelivery.getReceiptArea(),logisticsDelivery.getReceiptAddress()));
            deliverTimeReqData.setPhone(logisticsDelivery.getSendPhone());
            String deliverTime = deliveryService.getDeliverTime(deliverTimeReqData);
            return deliverTime;
        }catch (Exception ex){
            log.error("查询物流轨迹，查询送达时间异常",ex);
        }
        return null;
    }
}

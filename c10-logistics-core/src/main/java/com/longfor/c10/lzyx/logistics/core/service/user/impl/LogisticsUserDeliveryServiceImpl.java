package com.longfor.c10.lzyx.logistics.core.service.user.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Preconditions;
import com.longfor.c10.lzyx.logistics.core.factory.DeliveryFactory;
import com.longfor.c10.lzyx.logistics.core.service.IDeliveryService;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonOrderService;
import com.longfor.c10.lzyx.logistics.core.service.impl.delivery.KuaiDi100ServiceImpl;
import com.longfor.c10.lzyx.logistics.core.service.user.ILogisticsUserDeliveryService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryCompanyService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderGoodsService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryCompanyListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryCompanyListResData;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryPathReq;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.UserTokenInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.DeliveryPathReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.DeliveryPathResData;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.SubLogisticsPathReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.DeliveryCompanyResData;
import com.longfor.c10.lzyx.logistics.entity.dto.user.*;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDeliveryCompany;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeleteStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeliveryLogisticsStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.GoodsTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c10.lzyx.order.entity.enums.ReceiptTypeEnum;
import com.longfor.c10.lzyx.order.entity.enums.ResultCode;
import com.longfor.c2.starter.data.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 物流用户端运单接口实现类
 * @author zhaoyl
 * @date 2022/4/2 上午11:49
 * @since 1.0
 */
@Slf4j
@Service
public class LogisticsUserDeliveryServiceImpl implements ILogisticsUserDeliveryService {
    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;
    
    @Autowired
    private ILogisticsCommonOrderService logisticsCommonOrderService;

    @Autowired
    private ILogisticsDeliveryCompanyService logisticsDeliveryCompanyService;

    @Autowired
    private KuaiDi100ServiceImpl kuaiDi100Service;

    @Override
    public Response<DeliveryInfoResp> deliveryInfo(DeliveryInfoReqDate req) {
        UserTokenInfo userTokenInfo = req.getUserTokenInfo();

        LogisticsOrder order = logisticsOrderService.getOne(Wrappers.<LogisticsOrder>lambdaQuery().select(LogisticsOrder::getId, LogisticsOrder::getChildOrderId)
                .eq(LogisticsOrder::getUserId, userTokenInfo.getLmId())
                .eq(LogisticsOrder::getChildOrderId, req.getChildOrderId()));
        Preconditions.checkNotNull(order, ResultCode.NOT_EXIST.getMessage());

        List<LogisticsOrderGoods> orderGoodsEntities = logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                .eq(LogisticsOrderGoods::getLogisticsOrderId, order.getId())
                .eq(LogisticsOrderGoods::getDeleteStatus,DeleteStatusEnum.NO.getCode()));
        Preconditions.checkNotNull(orderGoodsEntities, "商品不存在");

        DeliveryInfoResp resp = new DeliveryInfoResp();
        resp.setTotal(orderGoodsEntities.size());

        //已发货列表
        List<LogisticsOrderGoods> sent = orderGoodsEntities.stream().filter(e -> Objects.nonNull(e.getLogisticsDeliveryId())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(sent)) {
            resp.setSend(sent.size());

            //运单信息
            Set<Long> deliveryIds = sent.stream().map(LogisticsOrderGoods::getLogisticsDeliveryId).collect(Collectors.toSet());
            List<LogisticsDelivery> deliveryEntities = logisticsDeliveryService.listByIds(deliveryIds);
            Map<Long, LogisticsDelivery> deliveryEntitiesMap = deliveryEntities.stream().collect(Collectors.toMap(LogisticsDelivery::getId, e -> e));

            //同运单的商品信息
            Map<Long, List<LogisticsOrderGoods>> sameOrderMap = sent.stream().collect(Collectors.groupingBy(LogisticsOrderGoods::getLogisticsDeliveryId));
            sameOrderMap.forEach((deliveryId, entities) -> {
                DeliveryInfo deliveryInfo = new DeliveryInfo();
                LogisticsDelivery deliveryEntity = deliveryEntitiesMap.get(deliveryId);

                //路由信息
                deliveryInfo.setChildOrderId(req.getChildOrderId());
                deliveryInfo.setDeliveryNo(deliveryEntity.getDeliveryNo());
                deliveryInfo.setLogisticsStatus(Optional.ofNullable(DeliveryLogisticsStatusEnum.fromCode(deliveryEntity.getLogisticsStatus())).map(DeliveryLogisticsStatusEnum::getDesc).orElse(null));
                deliveryInfo.setCompany(Optional.ofNullable(CompanyCodeEnum.fromCode(deliveryEntity.getCompanyCode())).map(CompanyCodeEnum::getDesc).orElse(null));
                try{
                    //查询轨迹
                    TrajectoryResp trajectoryResp = Optional.ofNullable(logisticsCommonOrderService.getPath(new DeliveryPathReq(req.getChildOrderId(),deliveryEntity.getDeliveryNo()))).map(Response::getData).orElse(null);
                    Optional.ofNullable(trajectoryResp).ifPresent(r -> {
                        List<PathItemResData> pathItemList = r.getPathItemList();
                        if (!CollectionUtils.isEmpty(pathItemList)) {
                            PathItemResData pathItemResData = pathItemList.get(0);
                            deliveryInfo.setLatestTrajectory(pathItemResData.getPathMsg());
                        }
                    });
                }catch (Exception ex){
                }
                deliveryInfo.setCreateDateTime(LocalDateTime.ofInstant(deliveryEntity.getCreateTime().toInstant(), ZoneId.systemDefault()));

                //商品信息
                entities.forEach(orderGoodsEntity -> {
                    GoodsInfo goodsInfo = new GoodsInfo();
                    BeanUtils.copyProperties(orderGoodsEntity, goodsInfo);
                    goodsInfo.setGoodsType(Optional.ofNullable(GoodsTypeEnum.fromCode(orderGoodsEntity.getGoodsType())).map(GoodsTypeEnum::getDesc).orElse(null));
                    goodsInfo.setLogisticsType(ReceiptTypeEnum.getValue(orderGoodsEntity.getLogisticsType()).getMessage());
                    deliveryInfo.getGoodsInfos().add(goodsInfo);
                });
                resp.getDeliveryInfos().add(deliveryInfo);
            });
        }
        //按时间排序
        List<DeliveryInfo> sorted = resp.getDeliveryInfos().stream().sorted(Comparator.comparing(DeliveryInfo::getCreateDateTime)).collect(Collectors.toList());
        resp.setDeliveryInfos(sorted);

        //未发货列表
        List<LogisticsOrderGoods> noSent = orderGoodsEntities.stream().filter(e -> StringUtils.isEmpty(e.getLogisticsDeliveryId())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(noSent)) {
            DeliveryInfo noSentInfo = new DeliveryInfo();
            noSentInfo.setChildOrderId(req.getChildOrderId());
            noSentInfo.setLogisticsStatus(DeliveryLogisticsStatusEnum.TO_SEND.getDesc());
            resp.getDeliveryInfos().add(noSentInfo);
            noSent.forEach(ns -> {
                GoodsInfo goodsInfo = new GoodsInfo();
                BeanUtils.copyProperties(ns, goodsInfo);
                goodsInfo.setGoodsType(Optional.ofNullable(GoodsTypeEnum.fromCode(ns.getGoodsType())).map(GoodsTypeEnum::getDesc).orElse(null));
                goodsInfo.setLogisticsType(ReceiptTypeEnum.getValue(ns.getLogisticsType()).getMessage());
                noSentInfo.getGoodsInfos().add(goodsInfo);
            });
        }
        return Response.ok(resp);
    }

    @Override
    public Response<String> bindingSalesReturnOrder(SalesReturnOrderReq req) {
        List<LogisticsOrder> oList = logisticsOrderService.list(Wrappers.<LogisticsOrder>lambdaQuery()
                .eq(LogisticsOrder::getChildOrderId, req.getSalesReturnOrderId()));
        if (CollectionUtil.isNotEmpty(oList)) {
            return Response.fail("此退单号已绑定退货物流，请勿重复绑定！");
        }
        UserTokenInfo userTokenInfo = req.getUserTokenInfo();
        LogisticsOrder order = new LogisticsOrder();
        order.setChildOrderId(req.getSalesReturnOrderId());
        order.setIfRefund(0);
        order.setUserId(userTokenInfo.getLmId());
        order.setChannelId(userTokenInfo.getChannelId());
        order.setUserName("未知");
        order.setUserPhone(userTokenInfo.getMobile());
        order.setDeliveryRemarks("退货物流");
        order.setSendReturn(2);
        order.setCreateTime(DateUtil.date());
        order.setUpdateTime(DateUtil.date());
        order.setDeleteStatus(DeleteStatusEnum.NO.getCode());
        order.setCreatorName("未知");
        order.setUserName("未知");
        order.setUpdateName("未知");
        order.setUpdateAccount(userTokenInfo.getLmId());
        order.setCreatorAccount(userTokenInfo.getLmId());
        logisticsOrderService.save(order);
        LogisticsDelivery delivery = new LogisticsDelivery();
        delivery.setLogisticsOrderId(order.getId());
        delivery.setCompanyCode(req.getDeliveryCompanyCode());
        delivery.setLogisticsType(ReceiptTypeEnum.BUSINESS_LOGISTICS.getCode());
        delivery.setLogisticsStatus(3);
        delivery.setDeliveryNo(req.getDeliveryNo());
        delivery.setIfCancel(0);
        delivery.setDeleteStatus(DeleteStatusEnum.NO.getCode());
        delivery.setCreateTime(new Date());
        delivery.setUpdateTime(new Date());
        delivery.setCreatorAccount(userTokenInfo.getLmId());
        delivery.setCreatorName("未知");
        delivery.setUpdateName("未知");
        delivery.setUpdateAccount(userTokenInfo.getLmId());
        delivery.setCreatorAccount(userTokenInfo.getLmId());
        delivery.setDeliveryTime(DateUtil.date());
        logisticsDeliveryService.save(delivery);
        //快递100物流轨迹订阅
        SubLogisticsPathReqData subLogisticsPathReqData = new SubLogisticsPathReqData();
        subLogisticsPathReqData.setCompanyCode(req.getDeliveryCompanyCode());
        subLogisticsPathReqData.setDeliverNo(req.getDeliveryCompanyName());
        kuaiDi100Service.subLogisticsPath(subLogisticsPathReqData);
        return Response.ok("退货物流单号绑定成功");
    }

    @Override
    public Response<List<DeliveryCompanyListResData>> getDeliveryCompanyList(DeliveryCompanyListReqData req) {
        return Response.ok(ListUtils.emptyIfNull(logisticsDeliveryCompanyService.list(Wrappers.<LogisticsDeliveryCompany>lambdaQuery()
                .like(org.apache.commons.lang3.StringUtils.isNotBlank(req.getDeliveryCompanyCode()),LogisticsDeliveryCompany::getCompanyCode,req.getDeliveryCompanyCode())
                .like(org.apache.commons.lang3.StringUtils.isNotBlank(req.getDeliveryCompanyName()),LogisticsDeliveryCompany::getCompanyName,req.getDeliveryCompanyName())))
                .stream()
                .filter(x -> !StringUtils.isEmpty(x.getInitials()))
                .collect(Collectors.groupingBy(LogisticsDeliveryCompany::getInitials,Collectors.collectingAndThen(Collectors.toList(),list -> {
                    return ListUtils.emptyIfNull(list)
                            .stream()
                            .map(info -> new DeliveryCompanyResData(info.getCompanyCode(),info.getCompanyName(),info.getInitials(),null))
                            .collect(Collectors.toList());
                })))
                .entrySet()
                .stream()
                .map(entry -> new DeliveryCompanyListResData(entry.getKey(),entry.getValue()))
                .collect(Collectors.toList()));
    }

    @Override
    public Response<Date> getOrderSignTime(LogisticsOrderIdReq req) {
        LogisticsOrder logisticsOrder = Optional.ofNullable(logisticsOrderService.getOne(Wrappers.<LogisticsOrder>lambdaQuery()
                .eq(LogisticsOrder::getChildOrderId, req.getChildOrderId())
                .eq(LogisticsOrder::getDeleteStatus, DeleteStatusEnum.NO.getCode())
                .last(" limit 1"))).orElseThrow(() -> new BusinessException("订单不存在"));
        return Response.ok(ListUtils.emptyIfNull(logisticsDeliveryService.list(Wrappers.<LogisticsDelivery>lambdaQuery()
                .eq(LogisticsDelivery::getLogisticsOrderId,logisticsOrder.getId())
                .eq(LogisticsDelivery::getLogisticsStatus,DeliveryLogisticsStatusEnum.SIGNED.getCode())
                .eq(LogisticsDelivery::getIfCancel,0)
                .eq(LogisticsDelivery::getDeleteStatus,DeleteStatusEnum.NO.getCode())
                .orderByDesc(LogisticsDelivery::getSignTime)))
                .stream()
                .map(LogisticsDelivery::getSignTime)
                .findFirst()
                .orElse(null));
    }
}

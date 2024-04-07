package com.longfor.c10.lzyx.logistics.core.mq.consumer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.longfor.c10.lzyx.logistics.core.mq.producer.LogisticsStatusChangeProducer;
import com.longfor.c10.lzyx.logistics.core.mq.producer.LogisticsVerifyStatusChangeProducer;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsVerifyOrderGoodsService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsVerifyOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.OrderStatusChangePushDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.OrderVerifyStatusChangePushDTO;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsVerifyOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsVerifyOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.enums.*;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderGoodsService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderService;
import com.longfor.c10.lzyx.order.entity.MqEntity;
import com.longfor.c10.lzyx.order.entity.enums.ChannelBizCodeEnum;
import com.longfor.c10.lzyx.order.entity.enums.OrderStatusEnum;
import com.longfor.c10.lzyx.order.entity.enums.ReceiptTypeEnum;
import com.longfor.c10.lzyx.order.entity.order.ChildOrderInfoEntity;
import com.longfor.c10.lzyx.order.entity.order.OrderGoodsEntity;
import com.longfor.c2.ryh.order.entity.mq.enums.SceneCodeEnum;
import com.longfor.c2.starter.common.util.JsonUtil;
import com.longfor.c2.starter.rocketmq.annotation.MessageListener;
import com.longfor.c2.starter.rocketmq.annotation.RocketListener;
import com.longfor.c2.starter.rocketmq.message.ReceivedMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 消费子单消息
 * @author zhaoyalong
 */
@Slf4j
@Component
@RocketListener(groupID = "GID_c10_order_child_2")
public class ChildOrderDataChangeConsumer {

    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Autowired
    private ILogisticsVerifyOrderService logisticsVerifyOrderService;

    @Autowired
    private ILogisticsVerifyOrderGoodsService logisticsVerifyOrderGoodsService;

    @Autowired
    private LogisticsVerifyStatusChangeProducer logisticsVerifyStatusChangeProducer;

    @Autowired
    private LogisticsStatusChangeProducer logisticsStatusChangeProducer;
    /**
     * 监听订单服务中心子订单状态变化
     *
     * @param message
     */
    @MessageListener(topic = "c10_order_child", orderConsumer = true)
    public void childOrderChange(ReceivedMessage message) {
        MqEntity<List<ChildOrderInfoEntity>> mqEntity = JsonUtil.fromJson(message.getBody(), new TypeReference<MqEntity<List<ChildOrderInfoEntity>>>() {
        });
        if (Objects.isNull(mqEntity) || Objects.isNull(mqEntity.getT())) {
            log.info("消费子单消息，数据来源非订单服务数据、非营销商品历史数据，订单信息不处理");
            return;
        }
        List<ChildOrderInfoEntity> childOrderList = mqEntity.getT();

        //数据处理非自提、虚拟订单落库
        handelOrderData(childOrderList);

        //处理企业订单取消后，在物流删除
        handelDemandOrder(mqEntity);

        //处理自提、虚拟订单
        handelVerifyOrderData(childOrderList);

        //处理订单主动签收状态
        handelChildOrderSignConfirm(childOrderList);
        log.info("消费子单消息，处理结束");
    }

    /**
     * 数据处理
     */
    @Transactional(rollbackFor = Exception.class)
    public void handelOrderData(List<ChildOrderInfoEntity> childOrders) {
        //过滤虚拟商品
        List<ChildOrderInfoEntity> orderList = childOrders.stream()
                .filter(order -> Objects.nonNull(order))
                .filter(order -> order.getOrderStatus().equals(OrderStatusEnum.PAY_SUCCESS.getCode()))
                .peek(info -> log.info("消费子单消息，过滤非支付成功订单后数据，{}", JSON.toJSONString(info)))
                .filter(order -> !LogisticsOrderTypeEnum.DUMMY.getCode().equals(order.getOrderType()))
                .peek(info -> log.info("消费子单消息，过滤虚拟商品后数据，{}", JSON.toJSONString(info)))
                .filter(order -> !ReceiptTypeEnum.No_NEED_LOGISTICS.getCode().equals(order.getReceiptType()) && !ReceiptTypeEnum.PICKUP.getCode().equals(order.getReceiptType()))
                .peek(info -> log.info("消费子单消息，过滤自提、快递订单后数据，{}", JSON.toJSONString(info)))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(orderList)){
            log.info("消费子单消息，需处理数据为空");
            return;
        }
        List<String> childOrderIds = orderList.stream().map(ChildOrderInfoEntity::getChildOrderId).distinct().collect(Collectors.toList());
        //存在的子单集合
        Set<String> existChildOrderIdSet = ListUtils.emptyIfNull(logisticsOrderService.list(Wrappers.<LogisticsOrder>lambdaQuery().in(LogisticsOrder::getChildOrderId, childOrderIds)))
                .stream()
                .map(LogisticsOrder::getChildOrderId)
                .collect(Collectors.toSet());
        List<ChildOrderInfoEntity> needHandelOrderList = orderList.stream().filter(orderId -> !existChildOrderIdSet.contains(orderId)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(needHandelOrderList)){
            return;
        }
        //物流订单
        List<LogisticsOrder> needHandelOrderInsertList = needHandelOrderList.stream()
                .map(order -> childOrder2LogisticsOrder(order))
                .collect(Collectors.toList());
        log.info("消费子单消息，物流订单数据添加或更新开始，logisticsOrders = {}", JSON.toJSONString(needHandelOrderInsertList));
        logisticsOrderService.saveBatch(needHandelOrderInsertList);
        log.info("消费子单消息，物流订单数据添加或更新完成");

        Map<String, Long> childOrderIdAndMap = needHandelOrderInsertList.stream().collect(Collectors.toMap(LogisticsOrder::getChildOrderId, LogisticsOrder::getId, (a, b) -> a));

        //物流订单商品
        List<LogisticsOrderGoods> orderGoodsList = needHandelOrderList.stream()
                .flatMap(order -> ListUtils.emptyIfNull(order.getGoodsListEntity()).stream().map(goods -> goodsInfo2LogisticsOrderGoods(childOrderIdAndMap.get(order.getChildOrderId()),goods)))
                .collect(Collectors.toList());
        log.info("消费子单消息，物流订单商品数据添加或更新开始，orderGoodsList = {}",JSON.toJSONString(orderGoodsList));
        logisticsOrderGoodsService.saveBatch(orderGoodsList);
        log.info("消费子单消息，物流订单商品数据添加或更新完成");

        //物流状态变更
        childOrderIdAndMap.forEach((k,v) -> {
            OrderStatusChangePushDTO orderStatusChangePushDTO = new OrderStatusChangePushDTO();
            orderStatusChangePushDTO.setChildOrderId(k);
            orderStatusChangePushDTO.setLogisticsId(v);
            orderStatusChangePushDTO.setLogisticsType(LogisticsStatusChangeTypeEnum.FORWARD.getCode());
            //待发货
            orderStatusChangePushDTO.setOrderStatus(GoodsOrderStatusEnum.UNDELIVERED.getCode());
            orderStatusChangePushDTO.setStatusTime(LocalDateTimeUtil.now());
            logisticsStatusChangeProducer.send(orderStatusChangePushDTO);
        });
    }



    @Transactional(rollbackFor = Exception.class)
    public void handelDemandOrder(MqEntity<List<ChildOrderInfoEntity>> mqEntity) {
        //处理企业订单取消，删除商品不在代发货列表展示
        List<String> childOrderIds = Optional.ofNullable(mqEntity)
                .filter(mq -> mq.getWishCode().equals(SceneCodeEnum.FOR_PAY_TIMEOUT_CANCEL_SD.getCode()))
                .map(MqEntity::getT)
                .orElse(Collections.emptyList())
                .stream()
                .filter(orderInfo -> orderInfo.getChannelBizCode().equals(ChannelBizCodeEnum.c10_ORDER_ADMIN.getCode()))
                .filter(orderInfo -> orderInfo.getOrderStatus().equals(com.longfor.c10.lzyx.order.entity.enums.OrderStatusEnum.CANCEL_ORDER.getCode()))
                .map(ChildOrderInfoEntity::getChildOrderId)
                .distinct()
                .collect(Collectors.toList());
        log.info("消费子单消息，企业取消订单，订单编号集合,childOrderIds:{}", JSON.toJSONString(childOrderIds));
        if(CollectionUtils.isEmpty(childOrderIds)){
            return;
        }
        Map<String, Long> childOrderIdAndLogisticsOrderIdMap = ListUtils.emptyIfNull(logisticsOrderService.list(Wrappers.<LogisticsOrder>lambdaQuery().in(LogisticsOrder::getChildOrderId, childOrderIds)))
                .stream()
                .collect(Collectors.toMap(LogisticsOrder::getChildOrderId, LogisticsOrder::getId, (a, b) -> a));
        log.info("消费子单消息，企业取消订单,订单编号和物流订单id映射,childOrderIdAndLogisticsOrderIdMap:{}", JSON.toJSONString(childOrderIdAndLogisticsOrderIdMap));
        if(CollectionUtils.isEmpty(childOrderIdAndLogisticsOrderIdMap)){
            return;
        }
        Map<String, List<String>> childOrderIdAndGoodsIdMap = Optional.ofNullable(mqEntity)
                .map(MqEntity::getT)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(ChildOrderInfoEntity::getChildOrderId, x -> ListUtils.emptyIfNull(x.getGoodsListEntity()).stream().map(OrderGoodsEntity::getGoodsId).collect(Collectors.toList()), (a, b) -> a));
        log.info("消费子单消息，企业取消订单,订单编号和商品id映射,childOrderIdAndGoodsIdMap:{}", JSON.toJSONString(childOrderIdAndGoodsIdMap));

        List<Long> logisticsOrderIds = childOrderIdAndLogisticsOrderIdMap.values().stream().collect(Collectors.toList());
        log.info("消费子单消息，企业取消订单,物流订单id集合,logisticsOrderIds:{}", JSON.toJSONString(logisticsOrderIds));


        Map<Long, List<String>> logisticsOrderIdAndGoodsIdsMap = ListUtils.emptyIfNull(logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                        .in(LogisticsOrderGoods::getLogisticsOrderId, logisticsOrderIds)
                        .eq(LogisticsOrderGoods::getDeleteStatus,DeleteStatusEnum.NO.getCode())))
                .stream()
                .collect(Collectors.groupingBy(LogisticsOrderGoods::getLogisticsOrderId, Collectors.collectingAndThen(Collectors.toList(), list -> {
                    return ListUtils.emptyIfNull(list).stream().map(LogisticsOrderGoods::getGoodsId).collect(Collectors.toList());
                })));
        log.info("消费子单消息，企业取消订单,数据库存储的物流订单id和商品id集合map,logisticsOrderIdAndGoodsIdsMap:{}", JSON.toJSONString(logisticsOrderIdAndGoodsIdsMap));
        childOrderIdAndGoodsIdMap.forEach((k,v) ->{
            log.info("消费子单消息，企业取消订单,订单编号 = {},商品id集合:{}",k, JSON.toJSONString(v));
            Long logisticsOrderId = childOrderIdAndLogisticsOrderIdMap.get(k);
            v.stream().forEach(id -> {
                LambdaUpdateWrapper<LogisticsOrderGoods> wrapper = Wrappers.<LogisticsOrderGoods>lambdaUpdate().eq(LogisticsOrderGoods::getGoodsId, id);
                if(Objects.nonNull(logisticsOrderId)){
                    wrapper.eq(LogisticsOrderGoods::getLogisticsOrderId, logisticsOrderId);
                }
                LogisticsOrderGoods logisticsOrderGoodsUpdateInfo = new LogisticsOrderGoods();
                logisticsOrderGoodsUpdateInfo.setDeleteStatus(DeleteStatusEnum.YES.getCode());
                logisticsOrderGoodsUpdateInfo.setUpdateTime(DateUtil.date());
                logisticsOrderGoodsService.update(logisticsOrderGoodsUpdateInfo,wrapper);
                log.info("消费子单消息，企业取消订单，物流订单商品删除成功，logisticsOrderId = {},goodsId = {}",logisticsOrderId,id);
            });
            //全部商品删除，删除订单
            Optional.ofNullable(logisticsOrderIdAndGoodsIdsMap.get(logisticsOrderId)).filter(x -> x.size() == v.size()).ifPresent(list -> {
                LogisticsOrder logisticsOrderUpdateInfo = new LogisticsOrder();
                logisticsOrderUpdateInfo.setId(logisticsOrderId);
                logisticsOrderUpdateInfo.setDeleteStatus(DeleteStatusEnum.YES.getCode());
                logisticsOrderUpdateInfo.setUpdateTime(DateUtil.date());
                logisticsOrderService.updateById(logisticsOrderUpdateInfo);
                log.info("消费子单消息，企业取消订单,商品全部删除，物流订单删除成功,logisticsOrderId = {}",logisticsOrderId);
            });
        });
    }

    /**
     * 数据处理
     */
    @Transactional(rollbackFor = Exception.class)
    public void handelVerifyOrderData(List<ChildOrderInfoEntity> childOrders) {
        //过滤虚拟商品
        List<ChildOrderInfoEntity> verifyOrderList = childOrders.stream()
                .filter(order -> Objects.nonNull(order))
                .filter(order -> order.getOrderStatus().equals(OrderStatusEnum.PAY_SUCCESS.getCode()))
                .peek(info -> log.info("消费子单消息，过滤非支付成功订单后数据，{}", JSON.toJSONString(info)))
                .filter(order -> ReceiptTypeEnum.No_NEED_LOGISTICS.getCode().equals(order.getReceiptType()) || ReceiptTypeEnum.PICKUP.getCode().equals(order.getReceiptType()))
                .peek(info -> log.info("消费子单消息，过滤非自提、非快递订单后数据，{}", JSON.toJSONString(info)))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(verifyOrderList)){
            log.info("消费子单消息，需处理数据为空");
            return;
        }
        List<String> childOrderIds = verifyOrderList.stream().map(ChildOrderInfoEntity::getChildOrderId).distinct().collect(Collectors.toList());
        //存在的子单集合
        Map<String,LogisticsVerifyOrder> existChildOrderIdAndMap = ListUtils.emptyIfNull(logisticsVerifyOrderService.list(Wrappers.<LogisticsVerifyOrder>lambdaQuery().in(LogisticsVerifyOrder::getChildOrderId, childOrderIds)))
                .stream()
                .collect(Collectors.toMap(LogisticsVerifyOrder::getChildOrderId,Function.identity(),(a,b) -> a));
        List<ChildOrderInfoEntity> needHandelVerifyOrderList = verifyOrderList.stream().filter(orderInfo -> !existChildOrderIdAndMap.containsKey(orderInfo.getChildOrderId())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(needHandelVerifyOrderList)){
            return;
        }
        //物流自提/核销订单
        List<LogisticsVerifyOrder> needHandelVerifyOrdersInsertList = needHandelVerifyOrderList.stream()
                .map(order -> childOrder2LogisticsVerifyOrder(order))
                .collect(Collectors.toList());
        log.info("消费子单消息，物流自提/核销订单数据添加或更新开始，logisticsOrders = {}", JSON.toJSONString(needHandelVerifyOrdersInsertList));
        logisticsVerifyOrderService.saveBatch(needHandelVerifyOrdersInsertList);

        log.info("消费子单消息，物流自提/核销订单数据添加或更新完成");
        Map<String, LogisticsVerifyOrder> childOrderIdAndMap = needHandelVerifyOrdersInsertList.stream().collect(Collectors.toMap(LogisticsVerifyOrder::getChildOrderId, Function.identity(), (a, b) -> a));
        List<String> needHandelChildOrderIds = needHandelVerifyOrderList.stream().map(ChildOrderInfoEntity::getChildOrderId).distinct().collect(Collectors.toList());

        //存在的商品集合
        Map<String,Long> existGoodsIdAndMap = ListUtils.emptyIfNull(logisticsVerifyOrderGoodsService.list(Wrappers.<LogisticsVerifyOrderGoods>lambdaQuery().in(LogisticsVerifyOrderGoods::getChildOrderId, needHandelChildOrderIds)))
                .stream()
                .collect(Collectors.toMap(x -> new StringBuilder(x.getChildOrderId()).append("|").append(x.getSkuId()).toString(),LogisticsVerifyOrderGoods::getId,(a,b) -> a));
        //物流自提/核销订单商品
        List<LogisticsVerifyOrderGoods> needHandelOrderGoodsInsertList = needHandelVerifyOrderList.stream()
                .flatMap(order -> ListUtils.emptyIfNull(order.getGoodsListEntity()).stream().map(goods -> goodsInfo2LogisticsVerifyOrderGoods(childOrderIdAndMap.get(order.getChildOrderId()),goods)))
                .filter(x -> !existGoodsIdAndMap.containsKey(new StringBuilder(x.getChildOrderId()).append("|").append(x.getSkuId()).toString()))
                .collect(Collectors.toList());
        log.info("消费子单消息，物流自提/核销订单商品数据添加或更新开始，orderGoodsList = {}",JSON.toJSONString(needHandelOrderGoodsInsertList));
        if(!CollectionUtils.isEmpty(needHandelOrderGoodsInsertList)){
            logisticsVerifyOrderGoodsService.saveBatch(needHandelOrderGoodsInsertList);
            log.info("消费子单消息，物流自提/核销订单商品数据添加或更新完成");
        }

        //推送自提/核销订单状态变更
        childOrderIdAndMap.forEach((k,v) -> {
            //发送自提订单待自提消息通知
            OrderStatusChangePushDTO orderStatusChangePushDTO = new OrderStatusChangePushDTO();
            orderStatusChangePushDTO.setChildOrderId(k);
            orderStatusChangePushDTO.setLogisticsId(v.getId());
            orderStatusChangePushDTO.setLogisticsType(LogisticsStatusChangeTypeEnum.FORWARD.getCode());
            if(GoodsTypeEnum.GOODS.getCode().equals(v.getGoodsType())){
                //商品类型待自提
                orderStatusChangePushDTO.setOrderStatus(VerifyOrderStatusEnum.WAIT_PICKUP.getCode());
            }else{
                //卡券类型待核销
                orderStatusChangePushDTO.setOrderStatus(VerifyOrderStatusEnum.WAIT_VERIFY.getCode());
            }
            orderStatusChangePushDTO.setStatusTime(LocalDateTimeUtil.now());
            logisticsStatusChangeProducer.send(orderStatusChangePushDTO);
        });

    }

    /**
     * 处理子单主动确认收货状态变更
     * @param childOrders
     */
    private void handelChildOrderSignConfirm(List<ChildOrderInfoEntity> childOrders) {
        //过滤虚拟商品
        List<ChildOrderInfoEntity> orderList = childOrders.stream()
                .filter(order -> Objects.nonNull(order))
                .filter(order -> order.getOrderStatus().equals(OrderStatusEnum.INITIATIVE_SIGNED.getCode()))
                .peek(info -> log.info("消费子单消息，过滤非支付成功订单后数据，{}", JSON.toJSONString(info)))
                .filter(order -> !LogisticsOrderTypeEnum.DUMMY.getCode().equals(order.getOrderType()))
                .peek(info -> log.info("消费子单消息，过滤虚拟商品后数据，{}", JSON.toJSONString(info)))
                .filter(order -> !ReceiptTypeEnum.No_NEED_LOGISTICS.getCode().equals(order.getReceiptType()) && !ReceiptTypeEnum.PICKUP.getCode().equals(order.getReceiptType()))
                .peek(info -> log.info("消费子单消息，过滤自提、快递订单后数据，{}", JSON.toJSONString(info)))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(orderList)){
            log.info("消费子单消息，处理订单主动签收，需处理数据为空");
            return;
        }
        orderList.stream().forEach(item -> {
            LogisticsOrder logisticsOrder = new LogisticsOrder();
            logisticsOrder.setSignConfirmFlag(1);
            logisticsOrder.setUpdateTime(DateUtil.date());
            Optional.ofNullable(item.getLogisticsExtraInfo()).map(map -> map.get("initiativeReceiptTime")).ifPresent(signConfirmTime -> {
                logisticsOrder.setSignConfirmTime(DateUtil.parseDate(String.valueOf(signConfirmTime)));
            });
            logisticsOrderService.update(logisticsOrder,Wrappers.<LogisticsOrder>lambdaUpdate().eq(LogisticsOrder::getChildOrderId,item.getChildOrderId()));
            log.info("消费子单消息，处理订单主动签收，订单主动签收状态已更新");
        });
    }

    /**
     * 物流自提/核销订单实体映射
     */
    private LogisticsVerifyOrder childOrder2LogisticsVerifyOrder(ChildOrderInfoEntity childOrderInfoEntity) {
        LogisticsVerifyOrder logisticsOrder = new LogisticsVerifyOrder();
        BeanUtils.copyProperties(childOrderInfoEntity,logisticsOrder);
        logisticsOrder.setCreateTime(DateUtil.date());
        logisticsOrder.setOrderCreateTime(childOrderInfoEntity.getCreateTime());
        logisticsOrder.setVerifyStatus(LogisticsVerifyVerifyStatusEnum.VERIFY_NO.getCode());
        logisticsOrder.setRefundStatus(LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode());
        //自提点、自提地址拆分
        Optional.ofNullable(handelPickupAddress(childOrderInfoEntity.getPickupAddress())).ifPresent(pair -> {
            logisticsOrder.setPickupSpot(pair.getKey());
            logisticsOrder.setPickupAddress(pair.getValue());
        });
        Optional.ofNullable(childOrderInfoEntity.getGoodsListEntity())
                .map(list -> ListUtils.emptyIfNull(list).stream().findAny().orElse(null))
                .map(OrderGoodsEntity::getPickupAddressId).ifPresent(addressId -> {
                    logisticsOrder.setPickupAddressId(addressId);
                });
        return logisticsOrder;
    }

    /**
     * 物流自提/核销订单商品实体映射
     */
    private LogisticsVerifyOrderGoods goodsInfo2LogisticsVerifyOrderGoods(LogisticsVerifyOrder logisticsVerifyOrder, OrderGoodsEntity goodsInfo) {
        if(Objects.isNull(logisticsVerifyOrder)){
            throw new BusinessException("消费子单消息，物流自提/核销订单数据不存在");
        }
        LogisticsVerifyOrderGoods logisticsVerifyOrderGoods = new LogisticsVerifyOrderGoods();
        BeanUtils.copyProperties(goodsInfo,logisticsVerifyOrderGoods);
        logisticsVerifyOrderGoods.setSkuSpecs(ListUtils.emptyIfNull(goodsInfo.getSkuSpecs()).stream().collect(Collectors.joining(",")));
        logisticsVerifyOrderGoods.setLmId(logisticsVerifyOrder.getLmId());
        logisticsVerifyOrderGoods.setLmPhone(logisticsVerifyOrder.getLmPhone());
        logisticsVerifyOrderGoods.setLmNickname(logisticsVerifyOrder.getLmNickname());
        //自提点、自提地址拆分
        Optional.ofNullable(handelPickupAddress(goodsInfo.getPickupAddress())).ifPresent(pair -> {
            logisticsVerifyOrderGoods.setPickupSpot(pair.getKey());
            logisticsVerifyOrderGoods.setPickupAddress(pair.getValue());
        });
        //自提/核销码
        logisticsVerifyOrderGoods.setPickupCode(logisticsVerifyOrder.getPickupCode());
        //订单创建时间
        logisticsVerifyOrderGoods.setOrderCreateTime(logisticsVerifyOrder.getCreateTime());
        //商品表取落库时间
        logisticsVerifyOrderGoods.setCreateTime(DateUtil.date());
        logisticsVerifyOrderGoods.setVerifyStatus(LogisticsVerifyVerifyStatusEnum.VERIFY_NO.getCode());
        logisticsVerifyOrderGoods.setRefundStatus(LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode());
        return logisticsVerifyOrderGoods;
    }

    private Pair<String,String> handelPickupAddress(String pickupAddress){
        return Optional.ofNullable(StrUtil.emptyToDefault(pickupAddress,null))
                .map(x -> {
                    String[] split = x.split("\\#");
                    return new Pair<String,String>(split[0],split.length > 1 ? split[1] : null);
                }).orElse(new Pair<String,String>(null,null));
    }

    /**
     * 物流订单实体映射
     */
    private LogisticsOrder childOrder2LogisticsOrder(ChildOrderInfoEntity childOrderInfoEntity) {
        LogisticsOrder logisticsOrder = new LogisticsOrder();
        logisticsOrder.setReceiptAddressId( childOrderInfoEntity.getReceiptAddrId());
        logisticsOrder.setUserPhone(childOrderInfoEntity.getLmPhone());
        logisticsOrder.setUserName(childOrderInfoEntity.getLmNickname());
        logisticsOrder.setChildOrderId(childOrderInfoEntity.getChildOrderId());
        logisticsOrder.setOrderId(childOrderInfoEntity.getOrderId());
        logisticsOrder.setShopName(childOrderInfoEntity.getShopName());
        logisticsOrder.setOrgId(childOrderInfoEntity.getOrgId());
        logisticsOrder.setOrgName(childOrderInfoEntity.getOrgName());
        logisticsOrder.setUserId(childOrderInfoEntity.getUserId());
        logisticsOrder.setChannelId(childOrderInfoEntity.getChannelId());
        logisticsOrder.setShopId(childOrderInfoEntity.getShopId());
        logisticsOrder.setReceiptName(childOrderInfoEntity.getReceiptName());
        logisticsOrder.setReceiptPhone(childOrderInfoEntity.getReceiptPhone());
        logisticsOrder.setReceiptProvince(childOrderInfoEntity.getReceiptProvince());
        logisticsOrder.setReceiptCity(childOrderInfoEntity.getReceiptCity());
        logisticsOrder.setReceiptArea(childOrderInfoEntity.getReceiptArea());
        logisticsOrder.setReceiptAddress(childOrderInfoEntity.getReceiptAddress());
        logisticsOrder.setCreateTime(DateUtil.date());
        logisticsOrder.setUpdateTime(DateUtil.date());
        logisticsOrder.setIfRefund(0);
        logisticsOrder.setDeleteStatus(DeleteStatusEnum.NO.getCode());
        logisticsOrder.setCreatorAccount("system");
        logisticsOrder.setCreatorName("system");
        logisticsOrder.setUpdateAccount("system");
        logisticsOrder.setUpdateName("system");
        logisticsOrder.setGoodsOrderStatus(OrderStatusEnum.WAIT_DELIVER.getCode());
        logisticsOrder.setBizChannelCode(childOrderInfoEntity.getChannelBizCode());
        logisticsOrder.setOrderDesc(childOrderInfoEntity.getOrderDesc());
        logisticsOrder.setSignConfirmFlag(0);
        return logisticsOrder;
    }

    /**
     * 物流订单商品实体映射
     */
    private LogisticsOrderGoods goodsInfo2LogisticsOrderGoods(Long logisticsOrderId,OrderGoodsEntity goodsInfo) {
        if(Objects.isNull(logisticsOrderId)){
            throw new BusinessException("消费子单消息，物流订单数据主键不存在");
        }
        LogisticsOrderGoods logisticsOrderGoods = new LogisticsOrderGoods();
        logisticsOrderGoods.setLogisticsOrderId(logisticsOrderId);
        if (Objects.nonNull(goodsInfo.getLogisticsExpensePayer())) {
            logisticsOrderGoods.setLogisticsType(goodsInfo.getLogisticsExpensePayer());
        }
        logisticsOrderGoods.setGoodsId(goodsInfo.getGoodsId());
        logisticsOrderGoods.setGoodsType(goodsInfo.getGoodsType());
        logisticsOrderGoods.setGoodsName(goodsInfo.getGoodsName());
        logisticsOrderGoods.setGoodsDesc(goodsInfo.getGoodsDesc());
        logisticsOrderGoods.setGoodsNum(goodsInfo.getGoodsNum());
        logisticsOrderGoods.setGoodsImgUrl(goodsInfo.getGoodsImgUrl());
        logisticsOrderGoods.setSkuId(goodsInfo.getSkuId());
        logisticsOrderGoods.setCreateTime(DateUtil.date());
        logisticsOrderGoods.setUpdateTime(DateUtil.date());
        logisticsOrderGoods.setSkuSpecs(ListUtils.emptyIfNull(goodsInfo.getSkuSpecs()).stream().collect(Collectors.joining(",")));
        logisticsOrderGoods.setDeleteStatus(DeleteStatusEnum.NO.getCode());
        logisticsOrderGoods.setCreatorAccount("system");
        logisticsOrderGoods.setCreatorName("system");
        logisticsOrderGoods.setUpdateAccount("system");
        logisticsOrderGoods.setUpdateName("system");
        return logisticsOrderGoods;
    }
}

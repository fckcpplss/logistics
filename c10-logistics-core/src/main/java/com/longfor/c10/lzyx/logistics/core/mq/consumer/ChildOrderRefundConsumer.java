package com.longfor.c10.lzyx.logistics.core.mq.consumer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Pair;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.longfor.c10.lzyx.logistics.core.mq.producer.LogisticsStatusChangeProducer;
import com.longfor.c10.lzyx.logistics.dao.service.*;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.OrderStatusChangePushDTO;
import com.longfor.c10.lzyx.logistics.entity.entity.*;
import com.longfor.c10.lzyx.logistics.entity.enums.*;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c10.lzyx.order.entity.enums.OrderStatusEnum;
import com.longfor.c10.lzyx.order.entity.enums.RefundInfoRefundStatusEnum;
import com.longfor.c10.lzyx.order.entity.enums.RefundTradeStatusEnum;
import com.longfor.c10.lzyx.order.entity.enums.RefundTypeEnum;
import com.longfor.c10.lzyx.order.entity.order.ChildOrderInfoEntity;
import com.longfor.c10.lzyx.order.entity.refund.RefundChildOrderInfoEntity;
import com.longfor.c10.lzyx.order.entity.refund.RefundGoodsEntity;
import com.longfor.c10.lzyx.order.entity.refund.RefundInfoEntity;
import com.longfor.c2.ryh.order.entity.mq.MqEntity;
import com.longfor.c2.ryh.order.entity.mq.enums.ReceiptTypeEnum;
import com.longfor.c2.starter.common.util.JsonUtil;
import com.longfor.c2.starter.rocketmq.annotation.MessageListener;
import com.longfor.c2.starter.rocketmq.annotation.RocketListener;
import com.longfor.c2.starter.rocketmq.message.ReceivedMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 仅退款消息监听
 * @author huang.jun
 * @date 2021-10-29
 **/
@Slf4j
@Component
@RocketListener(groupID = "GID_c10_order_refund_6")
public class ChildOrderRefundConsumer {
    @Resource
    private ILogisticsOrderService logisticsOrderService;
    @Resource
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Resource
    private ILogisticsDeliveryService logisticsDeliveryService;

    @Resource
    private ILogisticsVerifyOrderService logisticsVerifyOrderService;

    @Resource
    private ILogisticsVerifyOrderGoodsService logisticsVerifyOrderGoodsService;

    @Resource
    private LogisticsStatusChangeProducer logisticsStatusChangeProducer;

    /**
     * 监听订单退款状态
     * @param message
     */
    @MessageListener(topic = "c10_order_refund", orderConsumer = true)
    public void orderRefundMessage(ReceivedMessage message) {
        log.info("处理退单消息,开始处理:{}",message.getBody());
        MqEntity<RefundInfoEntity> mqEntity =  JsonUtil.fromJson(message.getBody(), new TypeReference<MqEntity<RefundInfoEntity>>() {});
        log.info("处理退单消息，数据解析完成");
        if (mqEntity == null || mqEntity.getT() == null) {
            log.info("处理退单消息，子订单退款消息解析为空");
            return;
        }
        RefundInfoEntity refundInfo = mqEntity.getT();
        //处理自提/核销订单退单
        handelVerifyOrderRefund(refundInfo);
        //处理物流订单退单
        handelLogisticsOrderRefund(refundInfo);
        log.info("处理退单消息,处理完成");
    }

    private void handelVerifyOrderRefund(RefundInfoEntity refundInfo){
        boolean present = Optional.ofNullable(refundInfo).filter(info -> Objects.nonNull(LogisticsVerifyRefundStatusEnum.fromOrderRefundStatus(info.getRefundStatus()))).isPresent();
        if(!present){
            log.info("处理退单消息，订单退单状态不处理，refundStatus = {}",refundInfo.getRefundStatus());
            return;
        }
        //过滤虚拟商品
        List<RefundChildOrderInfoEntity> verifyRefundOrderList = ListUtils.emptyIfNull(refundInfo.getRefundChildOrderInfoEntityList()).stream()
                .filter(order -> Objects.nonNull(order))
                .filter(order -> ReceiptTypeEnum.No_NEED_LOGISTICS.getCode().equals(order.getReceiptType()) || ReceiptTypeEnum.PICKUP.getCode().equals(order.getReceiptType()))
                .peek(info -> log.info("处理退单消息，过滤非自提、非快递订单后数据，{}", JSON.toJSONString(info)))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(verifyRefundOrderList)){
            log.info("处理退单消息，需处理自提/核销订单数据为空");
            return;
        }
        //退单状态
        Integer refundStatus = refundInfo.getRefundStatus();
        //退单商品列表
        List<String> refundGoodsSkuIds = ListUtils.emptyIfNull(verifyRefundOrderList).stream().flatMap(x -> x.getRefundGoodsEntityList().stream()).map(RefundGoodsEntity::getSkuId).distinct().collect(Collectors.toList());
        log.info("处理退单消息,自提/核销退单商品sku集合，refundGoodsSkuIds = {}",JSON.toJSONString(refundGoodsSkuIds));

        String childOrderId = ListUtils.emptyIfNull(verifyRefundOrderList).stream().flatMap(x -> x.getRefundGoodsEntityList().stream()).map(RefundGoodsEntity::getChildOrderId).distinct().findAny().orElse(null);
        if (StringUtils.isBlank(childOrderId)) {
            log.info("处理退单消息,自提/核销订单号不存在");
            return;
        }

        //查询核心订单信息
        LogisticsVerifyOrder logisticsVerifyOrder = Optional.ofNullable(logisticsVerifyOrderService.getOne(Wrappers.<LogisticsVerifyOrder>lambdaQuery().eq(LogisticsVerifyOrder::getChildOrderId,childOrderId).eq(LogisticsVerifyOrder::getIsDelete,0).last(" limit 1")))
                .orElse(null);
        if(Objects.isNull(logisticsVerifyOrder)){
            log.info("处理退单消息,自提/核销订单不存在，childOrderId = {}",childOrderId);
            return;
        }
        //查询核销订单商品信息
        List<LogisticsVerifyOrderGoods> verifyOrderGoodsList = logisticsVerifyOrderGoodsService.list(Wrappers.<LogisticsVerifyOrderGoods>lambdaQuery().eq(LogisticsVerifyOrderGoods::getChildOrderId, childOrderId));
        if(CollectionUtils.isEmpty(verifyOrderGoodsList)){
            log.info("处理退单消息,自提/核销订单无商品信息，childOrderId = {}",childOrderId);
            return;
        }
        Map<String, LogisticsVerifyOrderGoods> childOrderIdAndSkuIdAndMap = verifyOrderGoodsList.stream().collect(Collectors.toMap(x -> new StringBuffer(x.getChildOrderId()).append("|").append(x.getSkuId()).toString(), Function.identity(), (a, b) -> a));

        List<LogisticsVerifyOrderGoods> logisticsVerifyOrderGoodsUpdateList = refundGoodsSkuIds.stream().map(skuId -> {
            LogisticsVerifyOrderGoods existGoodsInfo = childOrderIdAndSkuIdAndMap.get(new StringBuilder(childOrderId).append("|").append(skuId).toString());
            if(Objects.isNull(existGoodsInfo)){
                log.info("处理退单消息,自提订单/核销商品sku不存在，childOrderId = {}，skuId = {}",childOrderId,skuId);
                return null;
            }
            LogisticsVerifyOrderGoods logisticsVerifyOrderGoods = new LogisticsVerifyOrderGoods();
            logisticsVerifyOrderGoods.setId(existGoodsInfo.getId());
            logisticsVerifyOrderGoods.setChildOrderId(existGoodsInfo.getChildOrderId());
            logisticsVerifyOrderGoods.setSkuId(existGoodsInfo.getSkuId());
            logisticsVerifyOrderGoods.setVerifyStatus(existGoodsInfo.getVerifyStatus());
            logisticsVerifyOrderGoods.setRefundStatus(LogisticsVerifyRefundStatusEnum.fromOrderRefundStatus(refundStatus));
            logisticsVerifyOrderGoods.setUpdateTime(DateUtil.date());
            return logisticsVerifyOrderGoods;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(logisticsVerifyOrderGoodsUpdateList)){
            return;
        }
        logisticsVerifyOrderGoodsService.updateBatchById(logisticsVerifyOrderGoodsUpdateList);
        log.info("处理退单消息,自提订单/核销商品状态更新完毕，updateInfo = {}",JSON.toJSONString(logisticsVerifyOrderGoodsUpdateList));

        //sku退单状态、核销状态
        Map<String, Pair<Integer,Integer>> orderSkuStatusMap = childOrderIdAndSkuIdAndMap.entrySet().stream().collect(Collectors.toMap(entity -> entity.getKey(), entity -> {
            return ListUtils.emptyIfNull(logisticsVerifyOrderGoodsUpdateList)
                    .stream()
                    .filter(x -> x.getChildOrderId().equals(entity.getValue().getChildOrderId()) && x.getSkuId().equals(entity.getValue().getSkuId()))
                    .map(x -> new Pair(x.getRefundStatus(),x.getVerifyStatus()))
                    .findFirst()
                    .orElse(new Pair(entity.getValue().getRefundStatus(),entity.getValue().getVerifyStatus()));
        },(a,b) -> a));

        LogisticsVerifyOrder logisticsVerifyOrderUpdateInfo = new LogisticsVerifyOrder();
        logisticsVerifyOrderUpdateInfo.setId(logisticsVerifyOrder.getId());
        if(orderSkuStatusMap.values().stream().allMatch(x -> x.getKey().equals(LogisticsVerifyRefundStatusEnum.REFUND_ON.getCode()))){
            logisticsVerifyOrderUpdateInfo.setRefundStatus(LogisticsVerifyRefundStatusEnum.REFUND_ON.getCode());
        }else if(orderSkuStatusMap.values().stream().allMatch(x -> x.getKey().equals(LogisticsVerifyRefundStatusEnum.REFUND_SUCCESS.getCode()))){
            logisticsVerifyOrderUpdateInfo.setRefundStatus(LogisticsVerifyRefundStatusEnum.REFUND_SUCCESS.getCode());
        }else if(orderSkuStatusMap.values().stream().allMatch(x -> x.getKey().equals(LogisticsVerifyRefundStatusEnum.REFUND_FAIL.getCode()))){
            logisticsVerifyOrderUpdateInfo.setRefundStatus(LogisticsVerifyRefundStatusEnum.REFUND_FAIL.getCode());
        }else if(orderSkuStatusMap.values().stream().anyMatch(x -> x.getKey().equals(LogisticsVerifyRefundStatusEnum.REFUND_SUCCESS.getCode()))){
            logisticsVerifyOrderUpdateInfo.setRefundStatus(LogisticsVerifyRefundStatusEnum.REFUND_PART.getCode());
        }
        boolean allVerifyFlag = false;
        List<Pair<Integer, Integer>> canVerirySkuAndStatusList = null;
        //除了退款成功的其他都是已核销，发送核销状态变更消息
        if(orderSkuStatusMap.values().stream().anyMatch(x -> x.getKey().equals(LogisticsVerifyRefundStatusEnum.REFUND_SUCCESS.getCode()))
                && orderSkuStatusMap.values().stream().noneMatch(x -> x.getKey().equals(LogisticsVerifyRefundStatusEnum.REFUND_ON.getCode()) || x.getKey().equals(LogisticsVerifyRefundStatusEnum.REFUND_FAIL.getCode()))
                && (!CollectionUtils.isEmpty(canVerirySkuAndStatusList = orderSkuStatusMap.values().stream().filter(x -> !x.getKey().equals(LogisticsVerifyRefundStatusEnum.REFUND_SUCCESS.getCode())).collect(Collectors.toList()))) && canVerirySkuAndStatusList.stream().allMatch(x -> x.getValue().equals(LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode()))){
            //核销成功
            logisticsVerifyOrderUpdateInfo.setVerifyStatus(LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode());
            allVerifyFlag = true;
        }
        logisticsVerifyOrderUpdateInfo.setUpdateTime(DateUtil.date());
        logisticsVerifyOrderService.updateById(logisticsVerifyOrderUpdateInfo);
        log.info("处理退单消息,自提订单/核销订单状态更新完毕，updateInfo = {}",JSON.toJSONString(logisticsVerifyOrderUpdateInfo));
        if(allVerifyFlag){
            //发送自提订单待自提消息通知
            OrderStatusChangePushDTO orderStatusChangePushDTO = new OrderStatusChangePushDTO();
            orderStatusChangePushDTO.setChildOrderId(logisticsVerifyOrder.getChildOrderId());
            orderStatusChangePushDTO.setLogisticsId(logisticsVerifyOrder.getId());
            orderStatusChangePushDTO.setLogisticsType(LogisticsStatusChangeTypeEnum.FORWARD.getCode());
            if(GoodsTypeEnum.GOODS.getCode().equals(logisticsVerifyOrder.getGoodsType())){
                //商品类型待自提
                orderStatusChangePushDTO.setOrderStatus(VerifyOrderStatusEnum.IS_PICKUP.getCode());
            }else{
                //卡券类型待核销
                orderStatusChangePushDTO.setOrderStatus(VerifyOrderStatusEnum.IS_VERIFY.getCode());
            }
            orderStatusChangePushDTO.setStatusTime(LocalDateTimeUtil.now());
            logisticsStatusChangeProducer.send(orderStatusChangePushDTO);
        }

    }

    private void handelLogisticsOrderRefund(RefundInfoEntity refundInfo) {
        log.info("处理退单消息，退单信息 = {}", JSON.toJSONString(refundInfo));
        Integer refundStatus = refundInfo.getRefundStatus();
        if (!RefundTradeStatusEnum.REFUND_IN.getCode().equals(refundStatus) && !RefundTradeStatusEnum.REFUND_SUCCESS.getCode().equals(refundStatus)) {
            log.info("处理退单消息,非退款中、退款成功状态不处理,refundStatus = {}",refundStatus);
            return;
        }
        //过滤虚拟商品
        List<RefundChildOrderInfoEntity> orderList = refundInfo.getRefundChildOrderInfoEntityList().stream()
                .filter(order -> Objects.nonNull(order))
                .filter(order -> !LogisticsOrderTypeEnum.DUMMY.getCode().equals(order.getOrderType()))
                .peek(info -> log.info("处理退单消息，过滤虚拟商品后数据，{}", JSON.toJSONString(info)))
                .filter(order -> !ReceiptTypeEnum.No_NEED_LOGISTICS.getCode().equals(order.getReceiptType()) && !ReceiptTypeEnum.PICKUP.getCode().equals(order.getReceiptType()))
                .peek(info -> log.info("处理退单消息，过滤自提、快递订单后数据，{}", JSON.toJSONString(info)))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(orderList)){
            log.info("处理退单消息，需处理物流订单数据为空");
            return;
        }

        String childOrderId = ListUtils.emptyIfNull(orderList).stream().flatMap(x -> x.getRefundGoodsEntityList().stream()).map(RefundGoodsEntity::getChildOrderId).distinct().findAny().orElse(null);
        if (StringUtils.isBlank(childOrderId)) {
            log.info("处理退单消息,订单号不存在");
            return;
        }
        //查询物流订单id
        LogisticsOrder logisticsOrder = Optional.ofNullable(logisticsOrderService.getOne(Wrappers.<LogisticsOrder>lambdaQuery().eq(LogisticsOrder::getChildOrderId,childOrderId).eq(LogisticsOrder::getDeleteStatus,0).last(" limit 1")))
                .orElse(null);
        if(Objects.isNull(logisticsOrder)){
            log.info("处理退单消息,订单不存在，childOrderId = {}",childOrderId);
            return;
        }
        //退单商品列表
        List<String> refundGoodsSkuIds = ListUtils.emptyIfNull(orderList).stream().flatMap(x -> x.getRefundGoodsEntityList().stream()).map(RefundGoodsEntity::getSkuId).distinct().collect(Collectors.toList());
        log.info("处理退单消息,退单商品sku集合，refundGoodsSkuIds = {}",refundGoodsSkuIds.size());
        if(CollectionUtils.isEmpty(refundGoodsSkuIds)){
            log.info("处理退单消息,退单商品sku为空不处理");
            return;
        }
        if(RefundTypeEnum.ONLY_MONEY.getCode() == refundInfo.getRefundType()){
            log.info("处理退单消息,开始处理仅退款类型");
            //仅退款
            updateRefunds(refundStatus,logisticsOrder,refundGoodsSkuIds, BusinessTypeEnum.REFUND_ONLY);
        }else if(RefundTypeEnum.MONEY_GOODS.getCode() == refundInfo.getRefundType()){
            //退货退款
            log.info("处理退单消息,退货退款类型暂不处理");
            updateRefunds(refundStatus,logisticsOrder,refundGoodsSkuIds,BusinessTypeEnum.REFUND_GOODS);
        }
        return;
    }

    /**
     * 处理退款消息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRefunds(Integer refundStatus,LogisticsOrder logisticsOrder,List<String> refundGoodsSkuIds,BusinessTypeEnum businessTypeEnum){
        //订单存在的skuId集合
        Map<String,LogisticsOrderGoods> existOrderGoodsSkuIdAndMap = ListUtils.emptyIfNull(logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                .eq(LogisticsOrderGoods::getLogisticsOrderId, logisticsOrder.getId())
                .eq(LogisticsOrderGoods::getDeleteStatus,DeleteStatusEnum.NO.getCode())))
                .stream()
                .collect(Collectors.toMap(LogisticsOrderGoods::getSkuId,Function.identity(),(a,b) -> a));
        log.info("处理退单消息，退款中存在的skuId数据 = {}",JSON.toJSONString(existOrderGoodsSkuIdAndMap));
        List<String> needRefundSkuIds = new ArrayList<>();
        List<LogisticsOrderGoods> orderGoodsUpdateInfos = refundGoodsSkuIds
                .stream()
                .map(skuId -> {
                    if(!existOrderGoodsSkuIdAndMap.containsKey(skuId)){
                        log.info("处理退单消息，sku不存在,{}",skuId);
                        return null;
                    }
                    needRefundSkuIds.add(skuId);
                    LogisticsOrderGoods orderGoodsEntity = new LogisticsOrderGoods();
                    orderGoodsEntity.setId(existOrderGoodsSkuIdAndMap.get(skuId).getId());
                    Optional.ofNullable(businessTypeEnum).ifPresent(enums -> {
                        orderGoodsEntity.setBusinessType(enums.getCode());
                        orderGoodsEntity.setRemark(enums.getDesc());
                    });
                    orderGoodsEntity.setDeleteStatus(DeleteStatusEnum.YES.getCode());
                    orderGoodsEntity.setUpdateAccount("system");
                    orderGoodsEntity.setUpdateName("system");
                    orderGoodsEntity.setUpdateTime(DateUtil.date());
                    return orderGoodsEntity;
                }).filter(Objects::nonNull).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(orderGoodsUpdateInfos)){
            log.info("处理退单消息，待处理sku为");
            return;
        }
        logisticsOrderGoodsService.updateBatchById(orderGoodsUpdateInfos);
        log.info("处理退单消息，物流订单商品更新参数 = {}",JSON.toJSONString(orderGoodsUpdateInfos));

        logisticsOrderService.updateById(new LogisticsOrder() {{
            setId(logisticsOrder.getId());
            setUpdateTime(DateUtil.date());
            //全部退款删除订单
            if(existOrderGoodsSkuIdAndMap.keySet().stream().distinct().count() == needRefundSkuIds.stream().distinct().count()){
                setDeleteStatus(DeleteStatusEnum.YES.getCode());
            }
        }});
        log.info("处理退单消息，订单状态已更新为 logisticsOrderId= {}",logisticsOrder.getId());
        //退货退款流程
        handelRefundDeliveryInfo(businessTypeEnum, existOrderGoodsSkuIdAndMap, needRefundSkuIds);

        //处理退单后全部签收推送物流状态变更
        handelRefundOrderStatusChange(refundStatus,logisticsOrder,existOrderGoodsSkuIdAndMap,needRefundSkuIds);
    }

    private void handelRefundOrderStatusChange(Integer refundStatus,LogisticsOrder logisticsOrder,Map<String, LogisticsOrderGoods> existOrderGoodsSkuIdAndMap, List<String> needRefundSkuIds) {
        if(!RefundInfoRefundStatusEnum.REFUND_SUCCESS.getCode().equals(refundStatus)){
            return;
        }
        log.info("处理退单消息，退款状态完成，开始处理物流状态变更");
        //sku运单状态
        List<Long> skuLogisticsDeliveryIds = existOrderGoodsSkuIdAndMap.values().stream().map(LogisticsOrderGoods::getLogisticsDeliveryId).distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(skuLogisticsDeliveryIds)){
            log.info("处理退单消息，退款状态完成，退款订单商品全部未发货，不处理，logisticsOrderId = {}",logisticsOrder.getId());
            return;
        }
        //运单id和运单信息map
        Map<Long, LogisticsDelivery> deliveryIdAndMap = ListUtils.emptyIfNull(logisticsDeliveryService.listByIds(skuLogisticsDeliveryIds)).stream().collect(Collectors.toMap(LogisticsDelivery::getId, Function.identity(), (a, b) -> a));
        //
        List<String> filterNoRefundSkuIds = existOrderGoodsSkuIdAndMap.entrySet()
                .stream()
                .filter(entry -> ListUtils.emptyIfNull(needRefundSkuIds).stream().noneMatch(x -> x.equals(entry.getKey())))
                .map(entry -> Optional.ofNullable(entry.getValue()).map(LogisticsOrderGoods::getSkuId).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(filterNoRefundSkuIds)){
            log.info("处理退单消息，退款状态完成，除当前退单成功sku外无其他sku，不处理，logisticsOrderId = {}",logisticsOrder.getId());
            return;
        }
        //过滤掉退款状态后，其余sku状态是否全部已签收
        boolean filterRefundIsAllSign = filterNoRefundSkuIds.stream().allMatch(x -> {
            Long skuLogisticsDeliveryId = Optional.ofNullable(existOrderGoodsSkuIdAndMap).map(y -> y.get(x)).map(LogisticsOrderGoods::getLogisticsDeliveryId).orElse(null);
            if (Objects.isNull(skuLogisticsDeliveryId)) {
                log.info("处理退单消息，退款状态完成，判断是否签收，sku绑定的运单id信息为空，过滤掉，logisticsOrderId = {}，skuId = {}", logisticsOrder.getId(), x);
                return false;
            }
            LogisticsDelivery logisticsDelivery = Optional.ofNullable(deliveryIdAndMap).map(y -> y.get(skuLogisticsDeliveryId)).orElse(null);
            if (Objects.isNull(logisticsDelivery)) {
                log.info("处理退单消息，退款状态完成，判断是否签收，sku绑定的运单信息为空，过滤掉，logisticsOrderId = {}，skuId = {}，logisticsDeliveryId = {}", logisticsOrder.getId(), x, logisticsDelivery.getId());
                return false;
            }
            if (Objects.isNull(logisticsDelivery.getLogisticsStatus()) || !logisticsDelivery.getLogisticsStatus().equals(DeliveryLogisticsStatusEnum.SIGNED.getCode())) {
                log.info("处理退单消息，退款状态完成，判断是否签收，sku绑定的运单状态不为已签收，过滤掉，logisticsOrderId = {}，skuId = {}，logisticsDeliveryId = {}", logisticsOrder.getId(), x, logisticsDelivery.getId());
                return false;
            }
            return true;
        });
        log.info("处理退单消息，退款状态完成，过滤退款sku后订单所有商品运单状态是否全部已签收，filterRefundIsAllSign = {}",filterRefundIsAllSign);

        //除了退款成功的其他都是已核销，发送核销状态变更消息
        if(filterRefundIsAllSign){
            //更新订单状态
            LogisticsOrder logisticsOrderUpdateInfo = new LogisticsOrder();
            logisticsOrderUpdateInfo.setId(logisticsOrder.getId());
            logisticsOrderUpdateInfo.setGoodsOrderStatus(GoodsOrderStatusEnum.SIGNED.getCode());
            logisticsOrderUpdateInfo.setUpdateTime(DateUtil.date());
            logisticsOrderService.updateById(logisticsOrderUpdateInfo);
            log.info("处理退单消息，退款状态完成，更新子单物流订单状态未已签收完成，logisticsOrderId = {}",logisticsOrder.getId());
            //发送子单物流签收消息
            OrderStatusChangePushDTO orderStatusChangePushDTO = new OrderStatusChangePushDTO();
            orderStatusChangePushDTO.setChildOrderId(logisticsOrder.getChildOrderId());
            orderStatusChangePushDTO.setLogisticsId(logisticsOrder.getId());
            orderStatusChangePushDTO.setLogisticsType(LogisticsStatusChangeTypeEnum.FORWARD.getCode());
            orderStatusChangePushDTO.setOrderStatus(OrderStatusEnum.IS_SIGNED.getCode());
            orderStatusChangePushDTO.setStatusTime(LocalDateTimeUtil.now());
            logisticsStatusChangeProducer.send(orderStatusChangePushDTO);
            log.info("处理退单消息，退款状态完成，发送子单物流状态变更状态完成，logisticsOrderId = {}，logisticsOrderStatus = {}",logisticsOrder.getId(),OrderStatusEnum.IS_SIGNED.getCode());

        }
    }

    private void handelRefundDeliveryInfo(BusinessTypeEnum businessTypeEnum, Map<String, LogisticsOrderGoods> existOrderGoodsSkuIdAndMap, List<String> needRefundSkuIds) {
        if(!businessTypeEnum.equals(BusinessTypeEnum.REFUND_GOODS)){
            return;
        }
        log.info("处理退单消息，退货退款流程");
        List<Long> logisticsDeliveryIds = existOrderGoodsSkuIdAndMap.entrySet()
                .stream()
                .filter(x -> needRefundSkuIds.stream().anyMatch(y -> y.equals(x)))
                .map(x -> x.getValue())
                .map(LogisticsOrderGoods::getLogisticsDeliveryId)
                .distinct()
                .collect(Collectors.toList());
        log.info("处理退单消息，退货退款流程，退货运单id集合,logisticsDeliveryIds = {}",logisticsDeliveryIds);
        if(CollectionUtils.isEmpty(logisticsDeliveryIds)){
            return;
        }
        List<LogisticsDelivery> logisticsDeliveries = logisticsDeliveryService.listByIds(logisticsDeliveryIds);
        if(CollectionUtils.isEmpty(logisticsDeliveries)){
            log.info("处理退单消息，退货退款流程，运单信息不存在，运单id集合,logisticsDeliveryIds = {}",logisticsDeliveryIds);
            return;
        }
        //更改运单状态未已取消
        List<LogisticsDelivery> logisticsDeliveryUpdateInfos = logisticsDeliveries.stream().map(item -> {
            LogisticsDelivery logisticsDelivery = new LogisticsDelivery();
            logisticsDelivery.setId(item.getId());
            logisticsDelivery.setIfCancel(1);
            logisticsDelivery.setUpdateTime(DateUtil.date());
            return logisticsDelivery;
        }).collect(Collectors.toList());
        logisticsDeliveryService.updateBatchById(logisticsDeliveryUpdateInfos);
        log.info("处理退单消息，退货退款流程，运单信息更新为取消完成");
    }
}

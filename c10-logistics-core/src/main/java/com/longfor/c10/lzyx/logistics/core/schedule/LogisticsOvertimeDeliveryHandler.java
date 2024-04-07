package com.longfor.c10.lzyx.logistics.core.schedule;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.logistics.core.mq.producer.LogisticsStatusChangeProducer;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderGoodsService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.OrderStatusChangePushDTO;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.enums.BusinessTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeleteStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeliveryLogisticsStatusEnum;
import com.longfor.c2.ryh.order.entity.mq.enums.OrderStatusEnum;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 物流派件异常处理
 * @author zhaoyl
 * @date 2022/2/15 下午3:15
 * @since 1.0
 */
@Slf4j
@Component
@JobHandler(value = "logisticsOvertimeDeliveryHandel")
public class LogisticsOvertimeDeliveryHandler extends IJobHandler {

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;

    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Autowired
    private LogisticsStatusChangeProducer logisticsStatusChangeProducer;

    //每页查询大小
    private static final int PER_QUERY_SIZE = 1000;

    @Override
    public ReturnT<String> execute(String s) throws Exception{
        log.info("物流派件异常，定时任务开始执行");
        LambdaQueryWrapper<LogisticsDelivery> lambdaQueryWrapper =  buildLambdaQueryWrapper();
        log.info("物流派件异常,构造查询参数,lambdaQueryWrapper = {}", JSON.toJSONString(lambdaQueryWrapper));
        Integer total = logisticsDeliveryService.count(lambdaQueryWrapper);
        log.info("物流配送超期，待更新条数，total = {}",total);
        if (total == 0){
            return SUCCESS;
        }
        long times = (total + PER_QUERY_SIZE - 1) / PER_QUERY_SIZE;
        //批量查询运单信息
        List<LogisticsDelivery> deliverInfos = IntStream.range(1, (int) times + 1)
                .mapToObj(index -> {
                    Page<LogisticsDelivery> pageList = logisticsDeliveryService.page(new Page<>(index, PER_QUERY_SIZE), lambdaQueryWrapper);
                    return pageList.getRecords();
                })
                .filter(list -> !CollectionUtils.isEmpty(list))
                .flatMap(list -> {
                    return list.stream();
                })
                .collect(Collectors.toList());
        List<String> deliverNos = ListUtils.emptyIfNull(deliverInfos).stream().map(LogisticsDelivery::getDeliveryNo).distinct().collect(Collectors.toList());
        log.info("物流派件异常，待更新运单编号集合，deliverNos = {}",JSON.toJSONString(deliverNos));
        handelData(deliverInfos);
        return SUCCESS;
    }
    @Transactional(rollbackFor = Exception.class)
    public void handelData(List<LogisticsDelivery> deliverInfos){
        if(CollectionUtils.isEmpty(deliverInfos)){
            return;
        }

        //物流运单id集合
        List<Long> logisticsDeliveryIds = deliverInfos.stream().map(LogisticsDelivery::getId).distinct().collect(Collectors.toList());
        //物流订单id
        List<Long> logisticsOrderIds = deliverInfos.stream().map(LogisticsDelivery::getLogisticsOrderId).distinct().collect(Collectors.toList());

        if(CollectionUtils.isEmpty(logisticsDeliveryIds) || CollectionUtils.isEmpty(logisticsOrderIds)){
            log.info("物流派件异常，物流订单或运单不存在");
            return;
        }
        //物流订单id和订单编号映射map
        Map<Long, String> logisticsOrderIdAndChildOrderIdMap = ListUtils.emptyIfNull(logisticsOrderService.list(Wrappers.<LogisticsOrder>lambdaQuery()
                .in(LogisticsOrder::getId, logisticsOrderIds)
                .eq(LogisticsOrder::getDeleteStatus, 0))).stream()
                .collect(Collectors.toMap(LogisticsOrder::getId, LogisticsOrder::getChildOrderId, (a, b) -> a));
        log.info("物流派件异常，物流订单id和订单编号映射map，size = {}",logisticsOrderIdAndChildOrderIdMap.size());

        //批量更新
        List<LogisticsOrderGoods> orderGoodsUpdates = ListUtils.emptyIfNull(logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery().in(LogisticsOrderGoods::getLogisticsDeliveryId, logisticsDeliveryIds).eq(LogisticsOrderGoods::getDeleteStatus, DeleteStatusEnum.NO.getCode())))
                .stream()
                .map(orderGoods -> {
                    LogisticsOrderGoods orderGoodsEntity = new LogisticsOrderGoods();
                    orderGoodsEntity.setId(orderGoods.getId());
                    orderGoodsEntity.setLogisticsDeliveryId(orderGoods.getLogisticsDeliveryId());
                    orderGoodsEntity.setBusinessType(BusinessTypeEnum.DELIVERY_ERROR.getCode());
                    orderGoodsEntity.setRemark("物流状态超时未改变");
                    orderGoodsEntity.setUpdateTime(DateUtil.date());
                    orderGoodsEntity.setUpdateAccount("system");
                    orderGoodsEntity.setUpdateName("system");
                    return orderGoodsEntity;
                }).collect(Collectors.toList());
        log.info("物流派件异常，订单商品更新参数，orderGoodsUpdates = {}",JSON.toJSONString(orderGoodsUpdates));
        //更新物流订单商品表
        logisticsOrderGoodsService.updateBatchById(orderGoodsUpdates);

        LogisticsDelivery deliveryEntity = new LogisticsDelivery();
        //派件异常
        deliveryEntity.setLogisticsStatus(DeliveryLogisticsStatusEnum.DELIVERY_ERROR.getCode());
        deliveryEntity.setRemark("物流状态超时未改变");
        deliveryEntity.setUpdateAccount("system");
        deliveryEntity.setUpdateName("system");
        deliveryEntity.setUpdateTime(DateUtil.date());
        log.info("物流派件异常，运单更新参数，deliveryEntity = {}",JSON.toJSONString(deliveryEntity));

        //修改运单物流表
        logisticsDeliveryService.update(deliveryEntity, Wrappers.<LogisticsDelivery>lambdaUpdate()
                .in(LogisticsDelivery::getLogisticsOrderId, logisticsOrderIds)
                .in(LogisticsDelivery::getId, logisticsDeliveryIds));

        //推送订单消息
        deliverInfos.stream().forEach(deliveryInfo -> {
            Optional.ofNullable(logisticsOrderIdAndChildOrderIdMap.get(deliveryInfo.getLogisticsOrderId())).ifPresent(childOrderId -> {
                OrderStatusChangePushDTO orderStatusChangePushDto = new OrderStatusChangePushDTO();
                orderStatusChangePushDto.setChildOrderId(childOrderId);
                orderStatusChangePushDto.setLogisticsId(deliveryInfo.getLogisticsOrderId());
                orderStatusChangePushDto.setLogisticsType(1);
                //收货超期/派件异常
                orderStatusChangePushDto.setOrderStatus(OrderStatusEnum.SIGNED_FAIL.getCode());
                orderStatusChangePushDto.setStatusTime(LocalDateTimeUtil.now());
                logisticsStatusChangeProducer.send(orderStatusChangePushDto);
                log.info("物流派件异常，推送物流状态变更消息完成，orderStatusChangePushDto = {}",JSON.toJSONString(orderStatusChangePushDto));
            });
        });

    }

    //构造查询参数
    private LambdaQueryWrapper<LogisticsDelivery> buildLambdaQueryWrapper() {
        LambdaQueryWrapper<LogisticsDelivery> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(LogisticsDelivery::getIfCancel,0);
        lambdaQueryWrapper.in(LogisticsDelivery::getLogisticsStatus,Arrays.asList(DeliveryLogisticsStatusEnum.SENDED.getCode()));
        lambdaQueryWrapper.last(" and delivery_no != '' and delivery_no is not null");
        //超七天
        lambdaQueryWrapper.le(LogisticsDelivery::getDeliveryTime,DateUtil.formatDateTime(DateUtil.offsetDay(DateUtil.date(), -7)));
        return lambdaQueryWrapper;
    }
}

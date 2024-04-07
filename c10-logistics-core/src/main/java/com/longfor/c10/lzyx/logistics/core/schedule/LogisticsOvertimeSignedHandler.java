package com.longfor.c10.lzyx.logistics.core.schedule;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.logistics.core.mq.producer.LogisticsStatusChangeProducer;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderGoodsService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsDeliveryOvertimeSignedDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.OrderStatusChangePushDTO;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 物流签收超期处理
 * @author zhaoyl
 * @date 2022/2/15 下午3:15
 * @since 1.0
 */
@Slf4j
@Component
@JobHandler(value = "logisticsOvertimeSignedHandel")
public class LogisticsOvertimeSignedHandler extends IJobHandler {

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;

    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Autowired
    private LogisticsStatusChangeProducer logisticsStatusChangeProducer;

    //每页查询大小
    private static final int PER_QUERY_SIZE = 100;

    @Override
    public ReturnT<String> execute(String s) throws Exception{
        log.info("物流签收超期，定时任务开始执行");
        long total = logisticsDeliveryService.getOvertimeSignedCount();
        log.info("物流签收超期，待更新条数，total = {}",total);
        if (total == 0){
            return SUCCESS;
        }
        long times = (total + PER_QUERY_SIZE - 1) / PER_QUERY_SIZE;
        //批量查询运单信息
        List<LogisticsDeliveryOvertimeSignedDTO> logisticsOrders = IntStream.range(1, (int) times + 1)
                .mapToObj(index -> {
                    IPage<LogisticsDeliveryOvertimeSignedDTO> overtimeSignedList = logisticsDeliveryService.getOvertimeSignedList(new Page<>(index, PER_QUERY_SIZE), null);
                    return overtimeSignedList.getRecords();
                })
                .filter(list -> !CollectionUtils.isEmpty(list))
                .flatMap(list -> {
                    return list.stream();
                })
                .collect(Collectors.toList());
        handelData(logisticsOrders);
        return SUCCESS;
    }
    @Transactional(rollbackFor = Exception.class)
    public void handelData(List<LogisticsDeliveryOvertimeSignedDTO> logisticsOrders){
        if(CollectionUtils.isEmpty(logisticsOrders)){
            return;
        }
        ListUtils.emptyIfNull(logisticsOrders).stream().forEach(item -> {
            //推送订单消息
            OrderStatusChangePushDTO orderStatusChangePushDto = new OrderStatusChangePushDTO();
            orderStatusChangePushDto.setChildOrderId(item.getChildOrderId());
            orderStatusChangePushDto.setLogisticsId(item.getLogisticsOrderId());
            orderStatusChangePushDto.setLogisticsType(1);
            //收货超期/派件异常
            orderStatusChangePushDto.setOrderStatus(OrderStatusEnum.RECEIPT_TIMEOUT.getCode());
            orderStatusChangePushDto.setStatusTime(LocalDateTimeUtil.now());
            logisticsStatusChangeProducer.send(orderStatusChangePushDto);
            log.info("物流签收超期，推送物流状态变更消息完成，orderStatusChangePushDto = {}",JSON.toJSONString(orderStatusChangePushDto));
        });
        List<LogisticsOrder> logisticsOrdersUpdates = logisticsOrders.stream().map(order -> {
            DateTime signConfirmTime = DateUtil.date();
            LogisticsOrder logisticsOrder = new LogisticsOrder();
            logisticsOrder.setSignConfirmFlag(1);
            logisticsOrder.setId(order.getLogisticsOrderId());
            logisticsOrder.setUpdateTime(signConfirmTime);
            //确收时间
            logisticsOrder.setSignConfirmTime(signConfirmTime);
            return logisticsOrder;
        }).collect(Collectors.toList());
        logisticsOrderService.updateBatchById(logisticsOrdersUpdates);

    }
}

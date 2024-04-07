package com.longfor.c10.lzyx.logistics.core.mq.producer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.OrderStatusChangePushDTO;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.enums.LogisticsTopicEnum;
import com.longfor.c2.ryh.order.entity.mq.MqEntity;
import com.longfor.c2.starter.rocketmq.message.OrderMessage;
import com.longfor.c2.starter.rocketmq.producer.RocketmqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 更新订单状态给订单管理系统
 *
 * @author heandong
 */
@Slf4j
@Component
public class LogisticsStatusChangeProducer {
    private static final String DATA_TYPE = "logisticsStatus";

    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    private RocketmqProducer rocketmqProducer;

    @Async
    public void send(OrderStatusChangePushDTO orderStatusChangePushDTO) {
        try {
            LogisticsOrder logisticsOrder = logisticsOrderService.getOne(Wrappers.<LogisticsOrder>lambdaQuery()
                    .eq(LogisticsOrder::getChildOrderId, orderStatusChangePushDTO.getChildOrderId())
                    .orderByDesc(LogisticsOrder::getCreateTime)
                    .last(" limit 1"));
            orderStatusChangePushDTO.setConfirmDataFlag(Optional.ofNullable(logisticsOrder)
                    .map(LogisticsOrder::getSignConfirmFlag)
                    .map(x -> x == -1 ? 0 : 1)
                    .orElse(0));
            MqEntity<OrderStatusChangePushDTO> data = new MqEntity<>();
            data.setDataType(DATA_TYPE);
            data.setT(orderStatusChangePushDTO);
            String msg = JSON.toJSONString(data);
            OrderMessage orderMessage = new OrderMessage();
            orderMessage.setTopic(LogisticsTopicEnum.C10_LOGISTICS_STATUS.getCode());
            orderMessage.setBody(msg);
            orderMessage.setShardingKey(orderStatusChangePushDTO.getChildOrderId());
            orderMessage.setCharset("UTF-8");
            rocketmqProducer.sendOrderMessage(orderMessage);
            log.info("发送物流状态变更信息：{}", msg);
        } catch (Exception e) {
            log.error("发送物流状态变更信息失败：", e);
        }
    }
    @Async
    public void batchSend(List<OrderStatusChangePushDTO> list) {
        try {
            List<String> collect = list.stream().map(OrderStatusChangePushDTO::getChildOrderId).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(collect)){
                return;
            }
            Map<String, LogisticsOrder> map = logisticsOrderService.list(new LambdaQueryWrapper<LogisticsOrder>()
                    .in(LogisticsOrder::getChildOrderId, collect)
                    .orderByDesc(LogisticsOrder::getCreateTime))
                    .stream().collect(Collectors.toMap(LogisticsOrder::getChildOrderId, order -> order, (v1, v2) -> v1));
            for (OrderStatusChangePushDTO orderStatusChangePushDTO : list) {
                orderStatusChangePushDTO.setConfirmDataFlag(Optional.ofNullable(map.get(orderStatusChangePushDTO.getChildOrderId()))
                        .map(LogisticsOrder::getSignConfirmFlag)
                        .map(x -> x == -1 ? 0 : 1)
                        .orElse(0));
                MqEntity<OrderStatusChangePushDTO> data = new MqEntity<>();
                data.setDataType(DATA_TYPE);
                data.setT(orderStatusChangePushDTO);
                String msg = JSON.toJSONString(data);
                OrderMessage orderMessage = new OrderMessage();
                orderMessage.setTopic(LogisticsTopicEnum.C10_LOGISTICS_STATUS.getCode());
                orderMessage.setBody(msg);
                orderMessage.setShardingKey(orderStatusChangePushDTO.getChildOrderId());
                orderMessage.setCharset("UTF-8");
                rocketmqProducer.sendOrderMessage(orderMessage);
            }

        } catch (Exception e) {
            log.error("发送物流状态变更信息失败：", e);
        }
    }
}

package com.longfor.c10.lzyx.logistics.core.mq.producer;

import com.alibaba.fastjson.JSON;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.OrderStatusChangePushDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.OrderVerifyStatusChangePushDTO;
import com.longfor.c10.lzyx.logistics.entity.enums.LogisticsTopicEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.VerifyOrderStatusEnum;
import com.longfor.c2.ryh.order.entity.mq.MqEntity;
import com.longfor.c2.starter.rocketmq.message.OrderMessage;
import com.longfor.c2.starter.rocketmq.producer.RocketmqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 物流核销状态变更
 */
@Slf4j
@Component
public class LogisticsVerifyStatusChangeProducer {
    private static final String DATA_TYPE = "logisticsVerifyStatus";

    @Autowired
    private RocketmqProducer rocketmqProducer;

    @Async
    public void send(OrderVerifyStatusChangePushDTO orderVerifyStatusChangePushDTO) {
        try {
            MqEntity<OrderVerifyStatusChangePushDTO> data = new MqEntity<>();
            data.setDataType(DATA_TYPE);
            data.setT(orderVerifyStatusChangePushDTO);
            String msg = JSON.toJSONString(data);
            OrderMessage orderMessage = new OrderMessage();
            orderMessage.setTopic(LogisticsTopicEnum.C10_LOGISTICS_VERIFY_STATUS.getCode());
            orderMessage.setBody(msg);
            orderMessage.setShardingKey(orderVerifyStatusChangePushDTO.getChildOrderId());
            orderMessage.setCharset("UTF-8");
            rocketmqProducer.sendOrderMessage(orderMessage);
            log.info("发送物流核销状态变更信息：{}", msg);
        } catch (Exception e) {
            log.error("发送物流核销状态变更信息失败：", e);
        }
    }
}

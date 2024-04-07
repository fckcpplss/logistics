package com.longfor.c10.lzyx.logistics.core.mq.producer;

import com.alibaba.fastjson.JSON;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c2.ryh.order.entity.mq.MqEntity;
import com.longfor.c2.starter.rocketmq.message.FixedTimeMessage;
import com.longfor.c2.starter.rocketmq.producer.RocketmqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description : 物流单提取/签收到期生产消息
 * @Author : zhaoyalong
 */
@Slf4j
@Component
public class LogisticsExpireProducer {

    @Autowired
    private RocketmqProducer rocketmqProducer;

    public void send(LogisticsOrder logisticsOrder) {
        try {
            MqEntity<LogisticsOrder> data = new MqEntity<>();
            data.setDataType("logisticsExpire");
            data.setT(logisticsOrder);
            String sendMsg = JSON.toJSONString(data);
            FixedTimeMessage fixedTimeMessage = new FixedTimeMessage();
            //fixedTimeMessage.setFixedTime(logisticsOrder.getLogisticsVerifyEndTime());
            fixedTimeMessage.setTopic("c2_logistics_expire");
            fixedTimeMessage.setBody(sendMsg);
            fixedTimeMessage.setCharset("UTF-8");
            rocketmqProducer.sendFixedTimeMessage(fixedTimeMessage);
            log.info("发送物流单提取/签收到期延迟信息：{}", JSON.toJSONString(fixedTimeMessage));
        } catch (Exception e) {
            log.error("发送物流单提取/签收到期延迟信息失败：{}", e);
        }
    }

}

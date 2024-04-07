package com.longfor.c10.lzyx.logistics.core.mq.producer;

import com.alibaba.fastjson.JSON;
import com.longfor.c10.lzyx.touch.entity.dto.mq.TouchMqMessageDTO;
import com.longfor.c2.starter.rocketmq.message.CommonMessage;
import com.longfor.c2.starter.rocketmq.producer.RocketmqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 物流触达生产者
 */
@Slf4j
@Component
public class LogisticsTouchProducer {

    @Autowired
    private RocketmqProducer rocketmqProducer;

    public void send(List<TouchMqMessageDTO> touchMqMessageDTOList) {
        try {
            String sendMsg = JSON.toJSONString(touchMqMessageDTOList);
            log.info("发送物流触达信息,参数 = {}",sendMsg);
            CommonMessage commonMessage = new CommonMessage();
            commonMessage.setTopic("c10_touch_touchtask_one");
            commonMessage.setBody(sendMsg);
            commonMessage.setCharset("UTF-8");
            rocketmqProducer.sendMessage(commonMessage);
            log.info("发送物流触达信息：{}", sendMsg);
        } catch (Exception e) {
            log.info("发送物流触达信息失败：{}", e);
        }
    }

}

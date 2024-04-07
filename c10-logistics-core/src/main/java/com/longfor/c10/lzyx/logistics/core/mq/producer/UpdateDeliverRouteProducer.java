package com.longfor.c10.lzyx.logistics.core.mq.producer;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.SendResult;
import com.longfor.c10.lzyx.logistics.entity.constant.CommonConstant;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.DeliveryDTO;
import com.longfor.c2.starter.rocketmq.message.CommonMessage;
import com.longfor.c2.starter.rocketmq.producer.RocketmqProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.CharEncoding;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class UpdateDeliverRouteProducer {
    @Resource
    RocketmqProducer rocketmqProducer;

    public boolean sendMessage(DeliveryDTO deliveryDto) {
        try {
            deliveryDto.setSource(CommonConstant.XXL_JOB_ROUT_SOURCE);
            String sendMsg = JSON.toJSONString(deliveryDto);

            log.info("更新物流轨迹，sendMsg：{}", sendMsg);

            CommonMessage commonMessage = CommonMessage.builder()
                    .topic(CommonConstant.C10_LOGISTICS_ROUT_PUSH)
                    .body(sendMsg)
                    .charset(CharEncoding.UTF_8)
                    .build();
            SendResult sendResult = rocketmqProducer.sendMessage(commonMessage);

            log.info("更新物流轨迹,发送消息成功，sendResult:{}", sendResult);

            return true;
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return false;
        }
    }
}


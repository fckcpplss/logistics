package com.longfor.c10.lzyx.logistics.core.mq.producer;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.SendResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longfor.c10.lzyx.logistics.entity.constant.CommonConstant;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.FeeResultReqData;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c2.starter.rocketmq.message.CommonMessage;
import com.longfor.c2.starter.rocketmq.producer.RocketmqProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 京东运费更新生产者
 */
@Slf4j
@Component
public class UpdateJdFeeProducer {
    @Resource
    RocketmqProducer rocketmqProducer;
    public void sendMessage(LogisticsDelivery deliveryEntity) {
        try {
            String sendMsg = JSON.toJSONString(deliveryEntity);
            log.info("京东费用更新，发送mq信息，sendMsg = {}",JSON.toJSONString(sendMsg));
            CommonMessage commonMessage = CommonMessage.builder()
                    .topic(CommonConstant.JD_FEE_MQ_TOPIC)
                    .body(sendMsg)
                    .charset(CharEncoding.UTF_8)
                    .build();
            SendResult sendResult = rocketmqProducer.sendMessage(commonMessage);
            log.info("京东费用更新，发送mq信息成功，sendResult = {}",JSON.toJSONString(sendResult));
        } catch (Exception e) {
            log.error("京东费用更新，发送mq信息失败",e);

        }
    }

}

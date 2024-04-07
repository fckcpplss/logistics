package com.longfor.c10.lzyx.logistics.core.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.enums.DeliveryLogisticsStatusEnum;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderService;
import com.longfor.c2.ryh.order.entity.mq.MqEntity;
import com.longfor.c2.starter.rocketmq.annotation.MessageListener;
import com.longfor.c2.starter.rocketmq.annotation.RocketListener;
import com.longfor.c2.starter.rocketmq.message.ReceivedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Description : 物流过期消费消息
 * @Author : zhaoyalong
 */
@Slf4j
@Component
//@RocketListener(groupID = "GID_c2_logistics_expire_1")
public class LogisticsExpireConsumer {
    @Autowired
    private ILogisticsOrderService logisticsOrderService;

//    @MessageListener(topic = "c2_logistics_expire")
    public void childOrderChange(ReceivedMessage message) {
        log.info("接收物流单过期延迟消息，开始处理:{}", JSON.toJSONString(message));
        try {
            MqEntity<LogisticsOrder> mqEntity = JSONObject.parseObject(message.getBody(), new TypeReference<MqEntity<LogisticsOrder>>() {});
            if (Objects.isNull(mqEntity) || Objects.isNull(mqEntity.getT())) {
                log.info("接收物流单过期延迟消息，数据为空不处理");
                return;
            }
            LogisticsOrder expireEntity = mqEntity.getT();
            LogisticsOrder resEntity = logisticsOrderService.getOne(Wrappers.<LogisticsOrder>lambdaQuery()
                    .eq(LogisticsOrder::getChildOrderId,expireEntity.getChildOrderId())
                    .eq(LogisticsOrder::getDeleteStatus,0)
                    .orderByDesc(LogisticsOrder::getCreateTime)
                    .last(" limit 1"));
            if (Objects.isNull(resEntity)) {
                log.info("接收物流单过期延迟消息，订单信息不存在，childOrderId = {}",expireEntity.getChildOrderId());
                return;
            }
            // TODO: 2022/1/17  
            Integer logisticsStatus = null;
            //判断物流状态
            if (DeliveryLogisticsStatusEnum.SENDING.getCode() != logisticsStatus) {
                log.info("接收物流单过期延迟消息 物流单状态不为:已发货/待提货,不做处理");
                return;
            }
            // TODO: 2022/1/17
            log.info("接收自提单过期延迟消息 处理完毕");
        } catch (Exception e) {
            log.error("接收物流单过期延迟消息 处理失败", e);
        }
    }
}

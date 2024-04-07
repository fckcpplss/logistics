package com.longfor.c10.lzyx.logistics.core.mq.producer;

import com.alibaba.fastjson.JSON;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.BizBaseDataMessage;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.rocketmq.message.OrderMessage;
import com.longfor.c2.starter.rocketmq.producer.RocketmqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 基础数据同步至订单服务
 */
@Slf4j
@Service
public class BizBaseDataToOrderSyncProducer {
    @Value("${rocketmq.basedata.sync.to.order.topic:c10_base_data_event}")
    private String baseDataTopic;

    public String getBaseDataTopic() {
        return baseDataTopic;
    }

    @Resource
    RocketmqProducer rocketmqProducer;

    public boolean sendMsg(BizBaseDataMessage<?> syncData) throws BusinessException {
        Assert.notNull(syncData.getOptType(), "操作类型不能为空");
        Assert.notNull(syncData.getDataType(), "数据类型不能为空");
        Assert.notEmpty(syncData.getData(), "发送数据不能为空");
        long st = System.currentTimeMillis();
        String queryId = UUID.randomUUID().toString();
        boolean sendStatus = true;
        try {
            String bizDataJson = JSON.toJSONString(syncData);
            rocketmqProducer.sendOrderMessage(OrderMessage.builder().topic(baseDataTopic).tags(syncData.getDataType().name())
                    .key(queryId)
                    .shardingKey(syncData.getDataType().name())
                    .body(bizDataJson)
                    .build());
            log.info("发送数据失成功：{}", bizDataJson);
        } catch (Exception e) {
            sendStatus = false;
            log.error("发送数据失败",e);
            throw new BusinessException(String.format("发送数据失败,topic:%s", baseDataTopic));
        } finally {
            log.debug(String.format("%s:%s", baseDataTopic, syncData.getDataType().name()), String
                    .format("topic:%s,status:%s,queryId:%s,data:%s,costTime:%s", baseDataTopic,
                            sendStatus, queryId, JSON.toJSONString(syncData), (System.currentTimeMillis() - st)));
        }
        return sendStatus;
    }
}

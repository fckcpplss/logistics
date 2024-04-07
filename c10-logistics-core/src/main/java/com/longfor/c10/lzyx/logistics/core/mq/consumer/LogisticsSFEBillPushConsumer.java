package com.longfor.c10.lzyx.logistics.core.mq.consumer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsEbill;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsEbillService;
import com.longfor.c2.starter.rocketmq.annotation.MessageListener;
import com.longfor.c2.starter.rocketmq.annotation.RocketListener;
import com.longfor.c2.starter.rocketmq.message.ReceivedMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 顺丰打印面单推送
 * @author zhaoyalong
 */
@Slf4j
@Component
@RocketListener(groupID = "GID_logistics_sf_ebill_push")
public class LogisticsSFEBillPushConsumer {

    @Autowired
    ILogisticsEbillService logisticsEbillService;

    @MessageListener(topic = "logistics_sf_ebill_push", orderConsumer = true)
    @SneakyThrows
    public void routPushMq(ReceivedMessage message) {
        log.info("顺丰打印面单推送，调用参数 = {}",JSON.toJSONString(message));
        String jsonBody = message.getBody();
        Map paramMap = JSON.parseObject(jsonBody, Map.class);
        List<String> logisticID = (List<String>) paramMap.get("logisticID");
        List<String> requestID = (List<String>) paramMap.get("requestID");
        List<String> serviceCode = (List<String>) paramMap.get("serviceCode");
        List<String> msgDigest = (List<String>) paramMap.get("msgDigest");
        List<String> nonce = (List<String>) paramMap.get("nonce");
        List<String> msgData = (List<String>) paramMap.get("msgData");
        msgData.forEach(m -> {
            try{
                String content = JSON.parseObject(m).getString("content");
                String waybillNo = JSON.parseObject(m).getString("waybillNo");
                BASE64Decoder base64Decoder = new BASE64Decoder();
                byte[] fileBytes = base64Decoder.decodeBuffer(content);
                LogisticsEbill eBillEntity = logisticsEbillService.getOne(Wrappers.<LogisticsEbill>lambdaQuery()
                        .eq(LogisticsEbill::getDeliveryNo, waybillNo));
                if(Objects.isNull(eBillEntity)){
                    throw new BusinessException(StrUtil.format("顺丰打印面单推送，面单信息不存在，deliveryNo = {}",waybillNo));
                }

                eBillEntity.setContent(fileBytes);
                eBillEntity.setStatus(2);
                log.info("顺丰打印面单推送，更新参数 = {}",JSON.toJSONString(eBillEntity));
                logisticsEbillService.updateById(eBillEntity);
            }catch (Exception ex){
                ex.printStackTrace();
                log.info("顺丰打印面单推送，更新错误",ex);
            }
        });
    }
}

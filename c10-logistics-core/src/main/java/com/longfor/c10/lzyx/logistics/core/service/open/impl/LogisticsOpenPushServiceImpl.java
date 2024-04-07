package com.longfor.c10.lzyx.logistics.core.service.open.impl;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.SendResult;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.longfor.c10.lzyx.logistics.core.service.open.ILogisticsOpenPushService;
import com.longfor.c10.lzyx.logistics.core.util.JsonUtil;
import com.longfor.c10.lzyx.logistics.core.util.SFUtils;
import com.longfor.c10.lzyx.logistics.entity.constant.CommonConstant;
import com.longfor.c10.lzyx.logistics.entity.dto.open.JdRoutRequest;
import com.longfor.c10.lzyx.logistics.entity.dto.open.JdRoutResponse;
import com.longfor.c10.lzyx.logistics.entity.dto.open.Kuaidi100PathUpdateRes;
import com.longfor.c10.lzyx.logistics.entity.dto.open.SFResponse;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.rocketmq.message.OrderMessage;
import com.longfor.c2.starter.rocketmq.producer.RocketmqProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**
 * 物流推送接口实现类
 * @author zhaoyl
 * @date 2022/4/19 上午9:35
 * @since 1.0
 */
@Slf4j
@Service
public class LogisticsOpenPushServiceImpl implements ILogisticsOpenPushService {

    /**
     * sk
     */
    @Value("${sf.delivery.sk}")
    private String sk;

    /**
     * 京东路由推送加密密钥
     */
    @Value("${jd.rout.push.encryptyKey}")
    private String encryptyKey;

    @Autowired
    private RocketmqProducer rocketmqProducer;

    /**
     * 是否需要顺丰验签，为方便测试，测试环境关闭验签
     */
    @Value("${sf.delivery.check-sign:true}")
    private boolean checkSign;

    private static final String SF_PUSH_RESPONSE_KEY = "status";

    @FunctionalInterface
    interface SFCallback<T> {
        /**
         * 顺丰验签执行成功后的回调方法
         * @param success 是否成功
         * @param body 成功时返回读取到的body
         * @return
         */
        T invoke(boolean success, String body);
    }

    private <T> T checkBodyAndSign(MultiValueMap<String,String> request, SFCallback<T> callback) {
        String body = null;
        try {
            body = request.getFirst("body");
            if (StringUtils.isEmpty(body)) {
                return callback.invoke(false, body);
            }
            log.info("收到顺丰路由回调: {}", request);
            boolean bool = SFUtils.receiveRequestAndCheckSign(request, sk);
            if (checkSign && !bool) {
                return callback.invoke(false, body);
            }
        } catch (Exception e) {
            log.error("顺丰回调请求异常: ", e);
        }
        return callback.invoke(true, body);
    }

    @Override
    public MutablePair<String, String> sfPush(MultiValueMap<String,String> request) {
        return checkBodyAndSign(request, ((success, body) -> {
            MutablePair<String, String> pair = new MutablePair<>(SF_PUSH_RESPONSE_KEY, "ERR");
            if (!success) {
                return pair;
            }
            ObjectNode node = JsonUtil.parse(body, ObjectNode.class);
            node.put(CommonConstant.ROUT_SOURCE_KEY, CommonConstant.SF_ROUT_SOURCE);
            sendRoutMq(node.toString(), CommonConstant.C10_LOGISTICS_ROUT_PUSH);
            pair.setValue("OK");
            return pair;
        }));
    }

    @Override
    public MutablePair<String, String> sfStatePush(MultiValueMap<String,String> request) {
        return checkBodyAndSign(request, ((success, body) -> {
            MutablePair<String, String> pair = new MutablePair<>(SF_PUSH_RESPONSE_KEY, "ERR");
            if (!success) {
                return pair;
            }
            ObjectNode node = JsonUtil.parse(body, ObjectNode.class);
            node.put(CommonConstant.ROUT_SOURCE_KEY, CommonConstant.SF_STATE_SOURCE);
            node.put(CommonConstant.SF_STATE_TIMESTAMP, System.currentTimeMillis());
            sendRoutMq(node.toString(), CommonConstant.C10_LOGISTICS_ROUT_PUSH);
            pair.setValue("OK");
            return pair;
        }));
    }

    @Override
    public MutablePair<String, String> sfPushFreight(MultiValueMap<String,String> request) {
        return checkBodyAndSign(request, ((success, body) -> {
            MutablePair<String, String> pair = new MutablePair<>(SF_PUSH_RESPONSE_KEY, "ERR");
            if (!success) {
                return pair;
            }
            sendRoutMq(body, CommonConstant.C10_LOGISTICS_SF_FREIGHT_PUSH);
            OrderMessage orderMessage = new OrderMessage();
            orderMessage.setTopic(CommonConstant.C10_LOGISTICS_SF_FREIGHT_PUSH);
            orderMessage.setShardingKey("c10");
            orderMessage.setBody(body);
            orderMessage.setCharset(CommonConstant.CHARSET_UTF8);
            rocketmqProducer.sendOrderMessage(orderMessage);
            pair.setValue("OK");
            return pair;
        }));
    }


    @Override
    public SFResponse eBillBack(MultiValueMap<String,String> request) {
        log.info("收到顺丰面单回调: {}", request);
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setTopic(CommonConstant.EBILL_ROUT_PUSH);
        orderMessage.setShardingKey("c10");
        orderMessage.setBody(JSON.toJSONString(request));
        orderMessage.setCharset("UTF-8") ;
        rocketmqProducer.sendOrderMessage(orderMessage);
        SFResponse res = new SFResponse();
        res.setSuccess("true");
        return res;
    }

    @Override
    public JdRoutResponse jdPush(MultiValueMap<String,String> request) {
        log.info("收到京东路由回调: {}", request);
        JdRoutRequest jdRoutRequest = new JdRoutRequest();
        jdRoutRequest.setMessageId(request.getFirst("message_id"));
        jdRoutRequest.setFormat("format");
        jdRoutRequest.setToken(request.getFirst("token"));
        jdRoutRequest.setTimestamp(request.getFirst("timestamp"));
        jdRoutRequest.setRequestBody(JsonUtil.parse(request.getFirst("request_body"), JdRoutRequest.JdRoutRequestBody.class));

        boolean succ = jdRoutRequest.checkData(encryptyKey, request.getFirst("request_body"));
        //数据验签
        if (!succ) {
            return new JdRoutResponse(CommonConstant.CONS1, CommonConstant.MSG_CHECK_ERROR);
        }
        //添加来源（JD）
        ObjectNode node = JsonUtil.convertValue(jdRoutRequest.getRequestBody(), ObjectNode.class);
        node.put(CommonConstant.ROUT_SOURCE_KEY, CommonConstant.JD_ROUT_SOURCE);
        sendRoutMq(node.toString(), CommonConstant.C10_LOGISTICS_ROUT_PUSH);
        return new JdRoutResponse(CommonConstant.CONS0, CommonConstant.OK_MSG);
    }

    @Override
    public Kuaidi100PathUpdateRes kuaidi100LatestPath(MultiValueMap<String,String> request) {
        log.info("物流100回调,接受数据:{}", request);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CommonConstant.ROUT_SOURCE_KEY, CommonConstant.KUAIDI100_ROUT_SOURCE);
        jsonObject.addProperty("param", request.getFirst("body"));
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setTopic(CommonConstant.C10_LOGISTICS_ROUT_PUSH);
        orderMessage.setShardingKey("c10");
        orderMessage.setBody(jsonObject.toString());
        orderMessage.setCharset(CommonConstant.CHARSET_UTF8);
        rocketmqProducer.sendOrderMessage(orderMessage);

        Kuaidi100PathUpdateRes res = new Kuaidi100PathUpdateRes();
        res.setResult(true);
        res.setReturnCode("200");
        res.setMessage("提交成功");
        return res;
    }


    private void sendRoutMq(String msg, String pushTopic) {
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setTopic(pushTopic);
        orderMessage.setShardingKey("c10");
        orderMessage.setBody(msg);
        orderMessage.setCharset(CommonConstant.CHARSET_UTF8);
        log.info("开始发送路由Mq消息，orderMessage：{}", orderMessage);
        SendResult sendResult = rocketmqProducer.sendOrderMessage(orderMessage);
        log.info("发送路由Mq消息成功，sendResult：{}", sendResult);
    }
}

package com.longfor.c10.lzyx.logistics.core.util;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.longfor.c10.lzyx.touch.entity.bo.yuntusuo.TouchBotCardMessageBO;
import com.property.gateway.sign.bean.Md5Sign;
import com.property.gateway.sign.bean.MethodEnum;
import com.property.gateway.sign.bean.SignVersionEnum;
import com.property.gateway.sign.http.GateWayHttp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 发送消息通用配置
 * @author renwei03
 */
@Slf4j
@Component
public class SendCommonHandler {
    @Resource
    private SmsFeignClient smsFeignClient;
    @Resource
    private PushFeignClient pushFeignClient;
    /**
     * 短信产品码
     */
    @Value("${sms.send.prodLineCode:C4_LZYX}")
    private String smsProdLineCode;
    /**
     * 短信渠道编码
     */
    @Value("${sms.send.channelCode:C4_LZYX_LOGISTICS}")
    private String smsChannelCode;

    @Value("${sms.send.signName:龙湖珑珠优选}")
    private String smsSendSignName;

    @Value("${sms.send.paas.pushApiKey:}")
    private String smsSendPushApiKey;

    @Value("${sms.send.paas.gaiaKey:}")
    private String smsSendPaasGaiaKey;

    @Value("${push.single.prodLineCode:}")
    private String pushSingleProdLineCode;

    @Value("${push.single.channelCode:}")
    private String pushSingleChannelCodee;

    @Value("${push.single.token:}")
    private String pushImSingleToken;

    @Value("${push.single.gaiaKey:}")
    private String pushGaiaApiKey;

    @Value("${push.single.appKey:}")
    private String pushAppKey;

    @Value("${push.single.appSecret:}")
    private String pushAppSecret;

    @Value("${push.single.templateId:}")
    private String pushTemplateId;

    @Value("${sms.send.gaiaKey:}")
    private String smsGatewayGaiaKey;
    @Value("${sms.send.url:}")
    private String smsGatewayUrl;
    @Value("${sms.send.lmId.appSecret:}")
    private String smsSendLmIdAppSecret;
    @Value("${sms.send.lmId.appId:}")
    private String smsSendLmIdAppId;

    private static final String SIGN_NAME = "signName";
    private static final String GAIA_API_KEY = "X-Gaia-Api-Key";
    private static final String IM_PUSH_TYPE = "im_push_type";
    private static final String SINGLE = "single";
    private static final String OBJECT_CARD_NAME = "IM:InterActiveMsg";

    public void sendSms(Map<String, Object> configParams, List<Object> userPhones, String templateId){
        configParams.put(SIGN_NAME, smsSendSignName);
        configParams.put(GAIA_API_KEY, smsSendPaasGaiaKey);
        SingleTypeMsg singleTypeMsg = new SingleTypeMsg();
        singleTypeMsg.setUserIds(userPhones);
        singleTypeMsg.setConfigParams(configParams);
        singleTypeMsg.setMsgTemplateId(templateId);
        singleTypeMsg.setPushApiKey(smsSendPushApiKey);
        ShortMessageReq messagePushReq = new ShortMessageReq();
        messagePushReq.setSingleTypeMsg(singleTypeMsg);
        messagePushReq.setProdLineCode(smsProdLineCode);
        messagePushReq.setChannelCode(smsChannelCode);
        log.info("发送短信参数:{}", JSON.toJSONString(messagePushReq));
        smsFeignClient.sendSms(messagePushReq);
    }
    public void sendLmIdSms(Map<String, Object> configParams, List<Object> userPhones,String templateId){
        configParams.put(SIGN_NAME, smsSendSignName);
        configParams.put(GAIA_API_KEY, smsSendPaasGaiaKey);
        SingleTypeMsg singleTypeMsg = new SingleTypeMsg();
        singleTypeMsg.setUserIds(userPhones);
        singleTypeMsg.setConfigParams(configParams);
        singleTypeMsg.setMsgTemplateId(templateId);
        singleTypeMsg.setPushApiKey(smsSendPushApiKey);
        ShortMessageReq messagePushReq = new ShortMessageReq();
        messagePushReq.setSingleTypeMsg(singleTypeMsg);
        messagePushReq.setProdLineCode(smsProdLineCode);
        messagePushReq.setChannelCode(smsChannelCode);
        log.info("根据lmId发送短信参数:{}", JSON.toJSONString(messagePushReq));


        Md5Sign md5Sign = Md5Sign.builder().appId(smsSendLmIdAppId)
                .nonce().timeStamp()
                .url(smsGatewayUrl + "/propertyNotify/push/pushLmSms")
                .signEnum(SignVersionEnum.V1)
                .appSecret(smsSendLmIdAppSecret)
                .method(MethodEnum.POST)
                .build();
        //MD5方式的签名
        String result = GateWayHttp.postGaiaMd5Json(md5Sign, smsGatewayGaiaKey, JsonUtil.toJson(messagePushReq));
        log.info("根据lmId发送短信结果:{}", result);
    }

    /**
     * 发送云图梭个人消息-卡片信息
     * @param userIds userIds
     * @param touchBotCardMessageBO touchBotCardMessageBO
     */
    public void sendPushSingleCard(List<Object> userIds, TouchBotCardMessageBO touchBotCardMessageBO){
        PushSingleTypeMsg singleTypeMsg = new PushSingleTypeMsg();
        singleTypeMsg.setUserIds(userIds);
        Map<String, Object> configParams = new HashMap<>();
        configParams.put(IM_PUSH_TYPE, SINGLE);
        configParams.put("objectName",OBJECT_CARD_NAME);
        configParams.put("im_single_token", pushImSingleToken);
        configParams.put("X-Gaia-Api-Key", pushGaiaApiKey);
        configParams.put("X-IM-App-Key", pushAppKey);
        configParams.put("X-IM-Secret", pushAppSecret);

        singleTypeMsg.setConfigParams(configParams);
        touchBotCardMessageBO.setTemplateId(pushTemplateId);
        singleTypeMsg.setPushContent(JSON.toJSONString(touchBotCardMessageBO));
        PushMessageReq messagePushReq = new PushMessageReq();
        messagePushReq.setSingleTypeMsg(singleTypeMsg);
        messagePushReq.setProdLineCode(pushSingleProdLineCode);
        messagePushReq.setChannelCode(pushSingleChannelCodee);
        log.info("发送云图梭参数:{}", JSON.toJSONString(messagePushReq));
        pushFeignClient.sendYuntusuo(messagePushReq);
    }
}

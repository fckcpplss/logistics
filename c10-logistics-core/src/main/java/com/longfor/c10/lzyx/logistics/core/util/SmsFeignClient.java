package com.longfor.c10.lzyx.logistics.core.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 发送短信feign调用
 */
@Component
@FeignClient(name = "smsFeign", url = "${sms.send.url}")
public interface SmsFeignClient {
    /**
     * 发送短信-手机号
     * @param req req
     */
    @RequestMapping(value = "/propertyNotify/push/pushSms",headers = {"X-Gaia-Api-Key=${sms.send.gaiaKey}"}, method = RequestMethod.POST)
    void sendSms(@RequestBody ShortMessageReq req);
    /**
     * 发送短信-龙民id
     * @param req req
     */
    @RequestMapping(value = "/propertyNotify/push/pushLmSms",headers = {"X-Gaia-Api-Key=${sms.send.gaiaKey}"}, method = RequestMethod.POST)
    void sendLmIdSms(@RequestBody ShortMessageReq req);

}

package com.longfor.c10.lzyx.logistics.core.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 发送消息feign调用
 */
@Component
@FeignClient(name = "pushFeign", url = "${push.send.url}")
public interface PushFeignClient {
    /**
     * 发送云图梭
     * @param req req
     */
    @RequestMapping(value = "/propertyNotify/push/pushCloudShuttle",headers = {"X-Gaia-Api-Key=${push.send.gaiaKey}"}, method = RequestMethod.POST)
    void sendYuntusuo(@RequestBody PushMessageReq req);
}

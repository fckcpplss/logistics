package com.longfor.c10.lzyx.logistics.core.controller;

import com.longfor.c10.lzyx.logistics.core.service.schedule.impl.PickUpNoticeServiceImpl;
import com.longfor.c10.lzyx.logistics.core.util.SendCommonHandler;
import com.longfor.c10.lzyx.logistics.entity.dto.PickUpNoticeDTO;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDeliveryCompany;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.touch.entity.bo.yuntusuo.TouchBotCardMessageBO;
import com.longfor.c2.starter.data.domain.response.Response;
import com.property.gateway.sign.bean.Md5Sign;
import com.property.gateway.sign.bean.MethodEnum;
import com.property.gateway.sign.bean.RsaSign;
import com.property.gateway.sign.bean.SignVersionEnum;
import com.property.gateway.sign.http.GateWayHttp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
public class ProbingController {
    /**
     * 【用户】自提订单消息通知-时间内（新） 标题
     */
    @Value("${push.pickup.inTime.new.title}")
    private String pushPickUpInTimeNewTitle;
    /**
     * 【用户】自提订单消息通知-时间内（新） 模版
     */
    @Value("${push.pickup.inTime.new.template}")
    private String pushPickUpInTimeNewTemplate;
    /**
     * 【用户】自提订单消息通知-时间内（新） 跳转链接
     */
    @Value("${push.pickup.inTime.new.jumpUrl}")
    private String pushPickUpInTimeNewJumpUrl;
    /**
     * 【用户】自提订单消息通知-时间内（新） 开关
     */
    @Value("${push.pickup.inTime.new.switch:true}")
    private boolean pushPickUpInTimeNewSwitch;
    /**
     * 【用户】自提订单消息通知-时间外 标题
     */
    @Value("${push.pickup.outTime.title}")
    private String pushPickUpOutTimeTitle;
    /**
     * 【用户】自提订单消息通知-时间外 模版
     */
    @Value("${push.pickup.outTime.template}")
    private String pushPickUpOutTimeTemplate;
    /**
     * 【用户】自提订单消息通知-时间外 跳转链接
     */
    @Value("${push.pickup.outTime.jumpUrl}")
    private String pushPickUpOutTimeJumpUrl;
    /**
     * 【用户】自提订单消息通知-时间外 开关
     */
    @Value("${push.pickup.outTime.switch:true}")
    private boolean pushPickUpOutTimeSwitch;
    @RequestMapping(value = "lzyx/getProbing", method = RequestMethod.GET)
    Response getProbing(){
        return Response.ok("探活成功");
    }
    @Resource
    private SendCommonHandler sendCommonHandler;
    @Resource
    private PickUpNoticeServiceImpl pickUpNoticeService;
    @RequestMapping(value = "lzyx/push", method = RequestMethod.GET)
    Response push(){
        List<Object> userIds = new ArrayList<>();
        userIds.add("50162673");

        Map<String, Object> configParams = new HashMap<>();
        configParams.put("goodsName", "鸡蛋");
        configParams.put("companyName", "龙湖滨江天街");
        configParams.put("deliveryNo", "1630141081532973057");
        sendCommonHandler.sendLmIdSms(configParams, userIds, "65423");
        return Response.ok("探活成功");
    }
}

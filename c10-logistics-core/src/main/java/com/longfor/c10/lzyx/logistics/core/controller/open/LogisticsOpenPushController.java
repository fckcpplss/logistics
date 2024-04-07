package com.longfor.c10.lzyx.logistics.core.controller.open;

import com.longfor.c10.lzyx.logistics.core.service.open.ILogisticsOpenPushService;
import com.longfor.c10.lzyx.logistics.entity.dto.open.JdRoutResponse;
import com.longfor.c10.lzyx.logistics.entity.dto.open.Kuaidi100PathUpdateRes;
import com.longfor.c10.lzyx.logistics.entity.dto.open.SFResponse;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author zhaoyl
 * @date 2022/2/7 下午3:10
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/open/push")
public class LogisticsOpenPushController {
    @Autowired
    private ILogisticsOpenPushService logisticsOpenPushService;
    /**
     * 顺丰快递路由推送
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/sf")
    MutablePair<String, String> sfPush(@RequestParam MultiValueMap<String,String> request){
        return logisticsOpenPushService.sfPush(request);
    }

    /**
     * 顺丰订单状态推送
     * @param request
     * @return
     */
    @PostMapping("/sf/state")
    MutablePair<String, String> sfStatePush(@RequestParam MultiValueMap<String,String> request){
        return logisticsOpenPushService.sfStatePush(request);
    }

    /**
     * 顺丰快递运费推送
     *
     * @param request
     * @return
     */
    @PostMapping("/sf/freight")
    MutablePair<String, String> sfPushFreight(@RequestParam MultiValueMap<String,String> request){
        return logisticsOpenPushService.sfPushFreight(request);
    }

    @PostMapping("/sf/ewaybills/back")
    SFResponse eBillBack(@RequestBody MultiValueMap<String,String> request){
        return logisticsOpenPushService.eBillBack(request);
    }

    /**
     * 描述:京东快递路由推送
     *
     * @param request
     * @return com.aliyun.openservices.shade.org.apache.commons.lang3.tuple.MutablePair
     * @author wanghai03
     * @date 2021/10/19 下午2:22
     */
    @PostMapping("/jd")
    JdRoutResponse jdPush(@RequestBody MultiValueMap<String,String> request){
        return logisticsOpenPushService.jdPush(request);
    }

    /**
     * 描述:描述:快递100物流轨迹回调
     *
     * @param request
     * @return {@link Kuaidi100PathUpdateRes}
     * @author lizhexun
     * @date 2021/10/28
     */
    @PostMapping(path = "/kuaidi100/latestPath")
    Kuaidi100PathUpdateRes kuaidi100LatestPath(@RequestBody MultiValueMap<String,String> request){
        return logisticsOpenPushService.kuaidi100LatestPath(request);
    }

}

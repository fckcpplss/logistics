package com.longfor.c10.lzyx.logistics.client.api.open;

import com.longfor.c10.lzyx.logistics.client.entity.param.open.CommonOpenReqData;
import com.longfor.c10.lzyx.logistics.client.entity.param.open.JdRoutResponse;
import com.longfor.c10.lzyx.logistics.client.entity.param.open.Kuaidi100PathUpdateRes;
import com.longfor.c10.lzyx.logistics.client.entity.param.open.SFResponse;
import feign.Param;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author zhaoyl
 * @date 2022/2/7 下午3:10
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsOpenPushClient {

    /**
     * 顺丰快递路由推送
     *
     * @param request
     * @return
     */
    @PostMapping(path = "/lzyx/logistics/open/push/sf")
    MutablePair<String, String> sfPush(@RequestParam MultiValueMap<String,String> request);

    /**
     * 顺丰订单状态推送
     * @param request
     * @return
     */
    @PostMapping(path = "/lzyx/logistics/open/push/sf/state")
    MutablePair<String, String> sfStatePush(@RequestParam MultiValueMap<String,String> request);

    /**
     * 顺丰快递运费推送
     *
     * @param request
     * @return
     */
    @PostMapping(path = "/lzyx/logistics/open/push/sf/freight")
    MutablePair<String, String> sfPushFreight(@RequestParam MultiValueMap<String,String> request);

    @PostMapping(path = "/lzyx/logistics/open/push/sf/ewaybills/back")
    SFResponse eBillBack(@RequestBody MultiValueMap<String,String> request);



    /**
     * 描述:京东快递路由推送
     *
     * @param request
     * @return com.aliyun.openservices.shade.org.apache.commons.lang3.tuple.MutablePair
     * @author wanghai03
     * @date 2021/10/19 下午2:22
     */
    @PostMapping(path = "/lzyx/logistics/open/push/jd")
    JdRoutResponse jdPush(@RequestBody MultiValueMap<String,String> request);

    /**
     * 描述:描述:快递100物流轨迹回调
     *
     * @param request
     * @return {@link Kuaidi100PathUpdateRes}
     * @author lizhexun
     * @date 2021/10/28
     */
    @PostMapping(path = "/lzyx/logistics/open/push/kuaidi100/latestPath")
    Kuaidi100PathUpdateRes kuaidi100LatestPath(@RequestBody MultiValueMap<String,String> request);


}

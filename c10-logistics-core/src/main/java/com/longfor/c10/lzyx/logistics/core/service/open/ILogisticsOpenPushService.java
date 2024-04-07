package com.longfor.c10.lzyx.logistics.core.service.open;

import com.longfor.c10.lzyx.logistics.entity.dto.open.CommonOpenReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.open.JdRoutResponse;
import com.longfor.c10.lzyx.logistics.entity.dto.open.Kuaidi100PathUpdateRes;
import com.longfor.c10.lzyx.logistics.entity.dto.open.SFResponse;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 外部推送接口
 * @author zhaoyl
 * @date 2022/4/19 上午9:33
 * @since 1.0
 */
public interface ILogisticsOpenPushService {
    /**
     * 顺丰快递路由推送
     *
     * @param request
     * @return
     */
    MutablePair<String, String> sfPush(MultiValueMap<String,String> request);

    /**
     * 顺丰订单状态推送
     * @param request
     * @return
     */
    MutablePair<String, String> sfStatePush(MultiValueMap<String,String> request);

    /**
     * 描述:京东快递路由推送
     *
     * @param request
     * @return com.aliyun.openservices.shade.org.apache.commons.lang3.tuple.MutablePair
     * @author wanghai03
     * @date 2021/10/19 下午2:22
     */
    JdRoutResponse jdPush(MultiValueMap<String,String> request);

    /**
     * 顺丰快递运费推送
     *
     * @param request
     * @return
     */
    MutablePair<String, String> sfPushFreight(MultiValueMap<String,String> request);

    /**
     * 描述:描述:快递100物流轨迹回调
     *
     * @param request
     * @return {@link Kuaidi100PathUpdateRes}
     * @author lizhexun
     * @date 2021/10/28
     */
    Kuaidi100PathUpdateRes kuaidi100LatestPath(MultiValueMap<String,String> request);

    /**
     * 顺丰面单推送
     * @param paramMap
     * @return
     */
    SFResponse eBillBack(MultiValueMap<String,String> request);
}

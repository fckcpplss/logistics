package com.longfor.c10.lzyx.logistics.core.util;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.longfor.c10.lzyx.logistics.core.mq.producer.LogisticsTouchProducer;
import com.longfor.c10.lzyx.touch.entity.dto.mq.TouchMqMessageDTO;
import com.longfor.c10.lzyx.touch.entity.enums.TouchCustomParamEnum;
import com.longfor.c10.lzyx.touch.entity.enums.TouchSystemCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author lanxiaolong
 * @Date 2022/4/14 4:35 下午
 */
@Component
@Slf4j
public class TouchSendUtil {

    @Autowired
    private LogisticsTouchProducer logisticsTouchProducer;

    public void sendNotice(String touchTaskCode, String touchTaskModeCode, String touchCustomParamCode,
                           String touchCustomParamValue, String msg, List<String> params) {
        TouchMqMessageDTO touchMqMessageDTO = new TouchMqMessageDTO();
        touchMqMessageDTO.setMessageSystemCode(TouchSystemCodeEnum.C10_LOGISTICS.getCode());
        touchMqMessageDTO.setTouchTaskCode(touchTaskCode);
        touchMqMessageDTO.setTouchTaskModeCode(touchTaskModeCode);
        Map<String, String> paramsMap = new HashMap(32);
        paramsMap.put(TouchCustomParamEnum.CYCLE_TIMEOUT.getCode(), SpringUtil.getProperty("logistics.delivery.cycleTimeout"));
        paramsMap.put(TouchCustomParamEnum.CYCLE_UNIT.getCode(), SpringUtil.getProperty("logistics.delivery.cycleUnit"));
        paramsMap.put(TouchCustomParamEnum.MAX_SEND_NUM.getCode(), SpringUtil.getProperty("logistics.delivery.maxSendNum"));
        paramsMap.put(touchCustomParamCode, touchCustomParamValue);
        if (CollectionUtils.isEmpty(params)) {
            touchMqMessageDTO.setCustomParam(paramsMap);
            log.info(msg + "，发送参数 = {}", JSON.toJSONString(touchMqMessageDTO));
            logisticsTouchProducer.send(Arrays.asList(touchMqMessageDTO));
            return;
        }
        for (int i = 0; i < params.size(); ++i) {
            paramsMap.put("#Param" + (i + 1) + "#", params.get(i));
        }
        touchMqMessageDTO.setCustomParam(paramsMap);
        log.info(msg + "，发送参数 = {}", JSON.toJSONString(touchMqMessageDTO));
        logisticsTouchProducer.send(Arrays.asList(touchMqMessageDTO));
    }
}

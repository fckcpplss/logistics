package com.longfor.c10.lzyx.logistics.core.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuqinglin01
 */
@Slf4j
public class SFUtils {

    private SFUtils() {}

    /**
     * 签名有效期（可根据实际业务设定）单位：毫秒
     */
    private static final Long CHECK_TIME = 600000L;
    /**
     * 生成签名
     *
     * @param timestamp 时间戳
     * @param body      请求body
     * @return
     */
    public static String genSign(String timestamp, String body, String sk) {
        if (StringUtils.isEmpty(body)) {
            body = "";
        }

        String sb = body +
                "&sk=" + sk +
                "&timestamp=" + timestamp;

        byte[] bytes = DigestUtils.sha512(sb);
        return Base64.encodeBase64URLSafeString(bytes);
    }



    /**
     * 接收请求且验签(可在控制层调用此方法)
     *
     * @param params  接收请求参数
     * @param request 接收请求
     * @return
     */
    public static boolean receiveRequestAndCheckSign(MultiValueMap<String,String> request, String sk) {
        // 请求方APPID
        String sendAppId = request.getFirst("receiveAppId");
        // 请求方时间戳
        String timestamp = request.getFirst("timestamp");
        // 请求方签名
        String sign = request.getFirst("sign");
        // 消息体
        String body = request.getFirst("body");
        if (StringUtils.isBlank(sendAppId)) {
            log.info("参数sendAppId不能为空");
            return false;
        }
        if (StringUtils.isBlank(timestamp)) {
            log.info("参数timestamp不能为空");
            return false;
        }
        if (StringUtils.isBlank(sign)) {
            log.info("参数sign不能为空");
            return false;
        }
        // 校验签名是否过期
        long requestTime = Long.parseLong(timestamp);
        long now = System.currentTimeMillis();
        if (Math.abs(now - requestTime) > CHECK_TIME) {
            log.info("签名过期!");
            return false;
        }
        // 请求方参数+请求方时间戳+SK 生成签名
        String thisSign = genSign(timestamp, body, sk);
        // 获取的签名和请求方签名比较是否一致
        if (!thisSign.equals(sign)) {
            log.info("签名错误");
            return false;
        }
        return true;
    }
}

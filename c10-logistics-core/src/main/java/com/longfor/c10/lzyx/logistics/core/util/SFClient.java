package com.longfor.c10.lzyx.logistics.core.util;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.longfor.c10.lzyx.logistics.entity.dto.shunfeng.SFApiResData;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

/**
 * 顺丰api调用服务
 * @author zhaoyalong
 **/
@Slf4j
public class SFClient {

    private final String appId;
    private final String sk;
    private final String url;

    public SFClient(String appId, String sk, String url) {
        this.appId = appId;
        this.sk = sk;
        this.url = url;
    }

    /**
     * post调用
     * @param path 路径
     * @param params 参数
     * @return
     */
    public SFApiResData post(String path, Object params) {
        HttpPost httpPost = new HttpPost(url + path);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String body = JSON.toJSONString(params);
        String sign = genSign(timestamp, body, sk);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("sendAppId", appId);
        httpPost.addHeader("timestamp", timestamp);
        httpPost.addHeader("sign", sign);
        httpPost.setEntity(new StringEntity(body, "utf-8"));
        return HttpUtil.post(httpPost,SFApiResData.class);
    }
    /**
     * post调用
     * @param path 路径
     * @param params 参数
     * @return
     */
    public HttpResponse post1(String path, Object params) {
        log.info("顺丰api调用，path = {},params = {},url = {}",path,params,(url + path));
        String body = JSON.toJSONString(params);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String sign = genSign(timestamp, body, sk);
        HttpRequest httpRequest = HttpRequest.post(url + path)
                .header(Header.CONTENT_TYPE,"application/json")
                .header("sendAppId",appId)
                .header("timestamp",String.valueOf(System.currentTimeMillis()))
                .header("sign",sign)
                .body(body)
                .charset("utf-8");
        log.info("顺丰api调用，api请求参数 = {}",JSON.toJSONString(httpRequest));
        HttpResponse httpResponse = null;
        try{
            httpResponse = httpRequest.execute();
        }catch (Exception ex){
            ex.printStackTrace();
            log.error("顺丰api调用，api请求失败",ex);
        }
        log.info("顺丰api调用，api请求结果 = {}",JSON.toJSONString(httpResponse));
        return httpResponse;
    }
    /**
     * 生成签名
     * @param timestamp 时间戳
     * @param body  请求body
     * @return
     */
    public static String genSign(String timestamp, String body, String sk) {
        StringBuilder sb = new StringBuilder();
        sb.append(StrUtil.emptyToDefault(body,""))
                .append("&sk=").append(sk)
                .append("&timestamp=").append(timestamp);
        byte[] bytes = DigestUtil.digester(DigestAlgorithm.SHA512).digest(sb.toString());
        return Base64Encoder.encodeUrlSafe(bytes);
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }
}

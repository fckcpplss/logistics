package com.longfor.c10.lzyx.logistics.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * HTTP请求工具类
 * @author liuqinglin
 * @date 2021/10/25
 **/
@Slf4j
public final class HttpUtil {

    private HttpUtil() {}

    public static <T> T get(HttpGet httpGet, Class<T> clazz) {
        return execute(httpGet, clazz);
    }

    public static String get(HttpGet httpGet) {
        return get(httpGet, String.class);
    }

    public static <T> T post(HttpPost httpPost, Class<T> clazz) {
        return execute(httpPost, clazz);
    }

    public static String post(HttpPost httpPost) {
        return post(httpPost, String.class);
    }

    public static <T> T put(HttpPut httpPut, Class<T> clazz) {
        return execute(httpPut, clazz);
    }

    public static String put(HttpPut httpPut) {
        return put(httpPut, String.class);
    }

    public static <T> T delete(HttpDelete httpDelete, Class<T> clazz) {
        return execute(httpDelete, clazz);
    }

    public static String delete(HttpDelete httpDelete) {
        return delete(httpDelete, String.class);
    }

    public static <T> T execute(HttpUriRequest request, Class<T> clazz) {
        StringBuilder responseString = new StringBuilder();
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request);
             BufferedReader reader = new BufferedReader(new InputStreamReader(
                     response.getEntity().getContent()))
        ) {
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                responseString.append(inputLine);
            }
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.warn("HttpUtil请求 {} 返回状态码 {}, 返回body: {}", request.getURI(), response.getStatusLine().getStatusCode(), responseString);
            }
        } catch (IOException e) {
            log.error("HttpUtil execute请求错误: ", e);
        }
        return JsonUtil.parse(responseString.toString(), clazz);
    }

    public static String readBody(HttpServletRequest request) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(request.getInputStream(), byteArrayOutputStream);
        return byteArrayOutputStream.toString("UTF-8");
    }
}

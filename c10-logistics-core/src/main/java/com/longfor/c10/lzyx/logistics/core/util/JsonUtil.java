package com.longfor.c10.lzyx.logistics.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longfor.c10.lzyx.logistics.core.exception.JsonParseException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述: Json工具类
 *
 * @author wanghai03
 * @date 2021/10/22 下午3:28
 */
@Slf4j
public final class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = ObjectMapperUtil.getIgnoreUnknownPropertiesObjectMapper();
    }

    private JsonUtil() {}

    /**
     * 描述:把对象转成json
     *
     * @param obj
     * @return java.lang.String
     * @author wanghai03
     * @date 2021/10/22 下午3:28
     */
    public static String toJson(Object obj) {
        String result = null;
        try {
            result = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            log.error("把对象转成json失败, obj={}", obj, e);
        }
        return result;
    }

    /**
     * 描述:json转成对象
     *
     * @param json
     * @param cls
     * @return T
     * @author wanghai03
     * @date 2021/10/22 下午3:28
     */
    public static <T> T parse(String json, Class<T> cls) {
        T result = null;
        try {
            result = OBJECT_MAPPER.readValue(json, cls);
        } catch (IOException e) {
            log.error("json转换对象失败, json={}", json, e);
        }

        return result;
    }
    /**
     * 描述: json转换对象
     * @author lizhexun
     * @date 2021/11/3
     * @param json
     * @param c
     * @return {@link T}
     */
    public static <T> T fromJson(String json, Class<T> c) {
        T t = null;

        try {
            if (json != null && json.trim().length() > 0) {
                t = OBJECT_MAPPER.readValue(json, c);
            }
            return t;
        } catch (Exception var4) {
            throw new JsonParseException(var4);
        }
    }

    /**
     * json转换对象
     * @param json
     * @param valueTypeRef
     * @param <T>
     * @author liuqinglin
     * @return
     */
    public static <T> T parse(String json, TypeReference<T> valueTypeRef) {
        T result = null;
        try {
            result = OBJECT_MAPPER.readValue(json, valueTypeRef);
        } catch (IOException e) {
            log.error("json转换对象失败, json={}", json, e);
        }
        return result;
    }

    /**
     * json转换List列表
     * @param json json字符串
     * @param clazz 容器内对象类型
     * @param <T> 容器内对象类型
     * @author liuqinglin
     * @return
     */
    public static <T> List<T> parseList(String json, Class<T> clazz) {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
        try {
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            log.error("json转换List失败, json={}", json);
        }
        return new ArrayList<>();
    }

    /**
     * 类型转换
     * @param from 原始对象
     * @param to 目标对象类型
     * @param <T> 目标对象类型
     * @return
     */
    public static <T> T convertValue(Object from, Class<T> to) {
        return OBJECT_MAPPER.convertValue(from, to);
    }

    /**
     * 创建Json对象
     * @return
     */
    public static ObjectNode createJsonObject() {
        return new ObjectNode(JsonNodeFactory.instance);
    }

    /**
     * Object对象转换为List<T>对象
     * @param obj 原始对象
     * @param clazz 目标对象Class类
     * @param <T>
     * @return
     */
    public static <T> List<T> convert2List(Object obj, Class<T> clazz) {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(ArrayList.class, clazz);
        try {
            return OBJECT_MAPPER.convertValue(obj, javaType);
        } catch (IllegalArgumentException e) {
            log.error("JsonUtil转换List失败: obj={}", JsonUtil.toJson(obj));
        }
        return new ArrayList<>();
    }

    public static ObjectMapper getMapper() {
        return OBJECT_MAPPER;
    }
}
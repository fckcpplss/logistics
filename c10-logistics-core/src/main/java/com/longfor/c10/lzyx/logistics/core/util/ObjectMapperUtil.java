package com.longfor.c10.lzyx.logistics.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 描述:
 *
 * @author wanghai03
 * @date 2021/10/15 下午5:51
 */
public class ObjectMapperUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectMapperUtil.class);

    private static ObjectMapper mapper;

    private ObjectMapperUtil() {}

    static {
        mapper = new ObjectMapper();
        // 获取IgnoreUnknownPropertiesObjectMapper,当对象中存在不能识别的属性时,不报错,直接忽略
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //大小写脱敏
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    /**
     * 描述:
     *
     * @param
     * @return com.fasterxml.jackson.databind.ObjectMapper
     * @author wanghai03
     * @date 2021/10/15 下午5:51
     */
    public static ObjectMapper getIgnoreUnknownPropertiesObjectMapper() {
        return mapper;
    }

    /**
     * 描述:
     *
     * @param entity
     * @return java.lang.String
     * @author wanghai03
     * @date 2021/10/15 下午5:51
     */
    public static <T> String entityToJsonStr(T entity) {
        try {
            return mapper.writeValueAsString(entity);

        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }

    /**
     * json 转指定类型对象
     * @param json json字符串
     * @param typeReference 指定转换类型包装
     * @param <T> 指定转换类型
     * @return 返回指定类型对象
     */
    public static <T> T jsonToObject(String json, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(json) || typeReference == null){
            return null;
        }
        try {
            return mapper.readValue(json,typeReference);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }
}

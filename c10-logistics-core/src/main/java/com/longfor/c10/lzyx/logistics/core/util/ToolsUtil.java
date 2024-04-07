package com.longfor.c10.lzyx.logistics.core.util;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.AmUserInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.tools.CommentDictDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.tools.TableInfoDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.tools.TableModuleEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 运营工具类
 * @author zhaoyl
 * @date 2021/11/17 下午1:48
 * @since 1.0
 */
@Slf4j
public class ToolsUtil {
    /**
     * 可更新字段entity包路径前缀
     */
    private static final String EITITY_FIELDS_PACKAGE_PREFIX = "";

    private static final String SB_ENTITY_PACKAGE_PREFIX = "com.longfor.logistics.common.entity.";

    /**
     * 将运营工具前端数据转换成db数据
     * @param tableInfoDtos
     * @return
     */
    public static Map<String,List<Object>> handelData(List<TableInfoDTO> tableInfoDtos, AmUserInfo userInfo){
        //模块和更新数据映射map
        return tableInfoDtos.stream()
                .filter(info -> !CollectionUtils.isEmpty(info.getData()) && !info.getData().stream().allMatch(dict -> StringUtils.isBlank(dict.getKey()) || StringUtils.isBlank(dict.getValue())))
                .peek(info -> {log.info("=============={}", JSON.toJSONString(info));})
                .collect(Collectors.groupingBy(TableInfoDTO::getModuleName, Collectors.collectingAndThen(Collectors.toList(), list -> {
                    if (CollectionUtils.isEmpty(list)) {
                        return null;
                    }
                    //更新数据
                    List<CommentDictDTO> data = list.stream()
                            .flatMap(tableInfo -> tableInfo.getData().stream())
                            .filter(dict -> StringUtils.isNotBlank(dict.getKey()) && StringUtils.isNotBlank(dict.getValue()))
                            .distinct()
                            .collect(Collectors.toList());
                    if(CollectionUtils.isEmpty(data)){
                        return null;
                    }
                    //更新id
                    List<String> ids = list.stream().flatMap(tableInfo -> tableInfo.getId().stream()).distinct().collect(Collectors.toList());
                    String moduleName = list.get(0).getModuleName();
                    return ListUtils.emptyIfNull(ids).stream().map(id -> {
                        //反射处理数据
                        Object object = ReflectUtil.newInstance(ToolsUtil.getDbEntityFromMuduleName(moduleName));
                        ReflectUtil.setFieldValue(object, "id", id);
                        ListUtils.emptyIfNull(data).stream().forEach(dict -> {
                            ReflectUtil.setFieldValue(object, dict.getKey(), dict.getValue());
                        });
                        ReflectUtil.setFieldValue(object, "updateTime", LocalDateTime.now());
                        ReflectUtil.setFieldValue(object, "updateAccount", Optional.ofNullable(userInfo).map(AmUserInfo::getUserName).orElse("-1"));
                        ReflectUtil.setFieldValue(object, "updateName", Optional.ofNullable(userInfo).map(AmUserInfo::getRealName).orElse("系统"));
                        return object;
                    }).collect(Collectors.toList());
                })));
    }
    /**
     * 从模块获取表可更新字段
     * @param moduleName
     * @return
     */
    public static List<String> getTableFieldsFrom(String moduleName){
        Class fieldEntityClass = TableModuleEnum.getFieldEntityClass(moduleName);
        if(Objects.isNull(fieldEntityClass)){
            //获取表名
            String tableName = Optional.ofNullable(moduleName).map(x -> TableModuleEnum.getTableName(x)).orElseThrow(() -> new BusinessException("模块名称不存在"));
            //获取字段实体
            String fieldEntity = EITITY_FIELDS_PACKAGE_PREFIX + StrUtil.upperFirstAndAddPre(StrUtil.toCamelCase(tableName), "");
            fieldEntityClass = ReflectUtil.newInstance(fieldEntity).getClass();
        }
        Class finalFieldEntityClass = fieldEntityClass;
        return Optional.ofNullable(ReflectUtil.getFields(fieldEntityClass)).map(fields -> {
            return Arrays.stream(fields).map(field -> ReflectUtil.getFieldName(field)).collect(Collectors.toList());
        }).orElseThrow(() -> new BusinessException("类型更新字段为空，class： " + finalFieldEntityClass));
    }

    /**
     * 从模块名称获取数据库更新entity类
     * @param moduleName
     * @return
     */
    public static Class getDbEntityFromMuduleName(String moduleName){
        Class dbEntityClass = TableModuleEnum.getDbEntityClass(moduleName);
        if(Objects.nonNull(dbEntityClass)){
            return dbEntityClass;
        }
        String tableName = Optional.ofNullable(TableModuleEnum.getTableName(moduleName)).orElseThrow(() -> new BusinessException("模块对应的表名不存在"));
        //获取字段实体
        String dbEntity = SB_ENTITY_PACKAGE_PREFIX + StrUtil.upperFirstAndAddPre(StrUtil.toCamelCase(tableName), "");
        return ReflectUtil.newInstance(dbEntity).getClass();
    }

    /**
     * 根据模块名获取mybatis-plus数据库更新service
     * @param moduleName 模块名称
     * @return
     */
    public static ServiceImpl getServiceFromTabelName(String moduleName){
        Map<String, ServiceImpl> matchBeans = SpringUtil.getApplicationContext().getBeansOfType(ServiceImpl.class);
        return Optional.ofNullable(matchBeans).map(map -> {
            return map.entrySet().stream()
                    .filter(service -> getDbEntityFromMuduleName(moduleName) == service.getValue().getEntityClass())
                    .findFirst()
                    .map(m -> m.getValue())
                    .orElseThrow(() -> new BusinessException("模块未找到更新service"));
        }).orElseThrow(() -> new BusinessException(moduleName + "模块未找到更新service"));
    }
}

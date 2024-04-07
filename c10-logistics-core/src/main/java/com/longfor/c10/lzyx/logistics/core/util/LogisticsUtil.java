package com.longfor.c10.lzyx.logistics.core.util;

import cn.hutool.core.collection.CollUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 物流工具类
 * @author zhaoyl
 * @date 2022/2/21 上午10:24
 * @since 1.0
 */
public class LogisticsUtil {
    public static String getLogisticsTypeName(String companyCode, Integer logisticsType, Integer shopLogisticsId) {
        if(StringUtils.isBlank(companyCode) || Objects.isNull(logisticsType)){
            return "其他";
        }
        if("jd".equals(companyCode) && logisticsType == 1){
            return "平台京东";
        }else if("shunfeng".equals(companyCode) && logisticsType == 1){
            return "平台顺丰";
        }else if("shunfeng".equals(companyCode) && logisticsType == 2 && Objects.nonNull(shopLogisticsId)){
            return "商家顺丰";
        }else{
            return "其他";
        }
    }
}

package com.longfor.c10.lzyx.logistics.core.util;

import com.longfor.c10.lzyx.logistics.entity.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 物流单号生成工具类
 * @author zhaoyl
 * @date 2022/4/14 上午11:47
 * @since 1.0
 */
@Component
@Slf4j
public class LogisticsNoUtil {
    @Autowired
    private SnowflakeUtils snowflakeUtils;

    /**
     * 核销单号生成
     * @return
     */
    public String createVerifyNo(){
        return new StringBuilder(CommonConstant.VERIFY_ORDER_VERIFY_NO_PREFIX).append(snowflakeUtils.snowflakeId()).toString();
    }
}

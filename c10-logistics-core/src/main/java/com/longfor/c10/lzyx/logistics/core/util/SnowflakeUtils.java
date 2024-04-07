package com.longfor.c10.lzyx.logistics.core.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 雪花算法生成工具
 * @author zhaoyl
 * @date 2021/9/17 下午5:20
 * @since 1.0
 */
@Component
@Slf4j
public class SnowflakeUtils {
    /**
     * 终端id
     */
    private long  workerId;

    /**
     * 数据中心id
     */
    private long dataCenterId = 1;
    private Snowflake snowflake = IdUtil.createSnowflake(workerId,dataCenterId);
    @PostConstruct
    public void init(){
        log.info("生成雪花id，当前机器ip = {}", NetUtil.getLocalhostStr());
        workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
    }
    public long snowflakeId(){
        return snowflake.nextId();
    }
}

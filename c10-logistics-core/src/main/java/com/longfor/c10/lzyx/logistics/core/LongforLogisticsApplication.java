package com.longfor.c10.lzyx.logistics.core;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;


@Slf4j
@SpringBootApplication
@ComponentScan("com.longfor.**")
@MapperScan({"com.longfor.c10.lzyx.logistics.dao.mapper"})
@EnableFeignClients(basePackages = {"com.longfor.*"})//用于启动Fegin功能
@EnableDiscoveryClient
@EnableScheduling
@Import(cn.hutool.extra.spring.SpringUtil.class)
public class LongforLogisticsApplication {

    public static void main(String[] args) {
        try {
            SpringApplicationBuilder builder = new SpringApplicationBuilder(LongforLogisticsApplication.class);
            ApplicationContext context = builder.web(WebApplicationType.SERVLET).run(args);
            String[] activeProfiles = context.getEnvironment().getActiveProfiles();
            String port = context.getEnvironment().getProperty("server.port");
            log.info("ActiveProfiles = {},ServerPort = {}", StringUtils.arrayToCommaDelimitedString(activeProfiles), port);
        } catch (Exception e) {
            log.error("物流服务启动异常,{}",e);
        }
    }


}

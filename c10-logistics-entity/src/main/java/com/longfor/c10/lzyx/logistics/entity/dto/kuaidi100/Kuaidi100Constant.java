package com.longfor.c10.lzyx.logistics.entity.dto.kuaidi100;

import org.springframework.beans.factory.annotation.Value;

/**
 * 快递100相关常量
 * @author zhaoyl
 */
public class Kuaidi100Constant {
    private String intimeQueryUrl = "";




    @Value("${kuaidi100.intimequery.url}")
    private String url;
    @Value("${kuaidi100.subscribe.url}")
    private String subscribeUrl;
    @Value("${kuaidi100.autonumber.url}")
    private String autonumberUrl;
    @Value("${kuaidi100.maptrack.url}")
    private String maptrackUrl;
    @Value("${kuaidi100.callBack.url}")
    private String callBackUrl;
    @Value("${kuaidi100.key}")
    private String key;
    @Value("${kuaidi100.customer}")
    private String customer;
    @Value("${kuaidi100.resultV2}")
    private int resultV2;
}

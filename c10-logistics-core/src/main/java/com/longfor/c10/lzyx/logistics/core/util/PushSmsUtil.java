package com.longfor.c10.lzyx.logistics.core.util;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsVerifyOrderGoods;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author zhaoyalong
 */
@Slf4j
@Component
public class PushSmsUtil {

    @Autowired
    private Environment environment;

    public String handelGoodsName(List<LogisticsOrderGoods> orderGoodsList){
        return ListUtils.emptyIfNull(orderGoodsList).stream()
                .map(LogisticsOrderGoods::getGoodsName)
                .findFirst()
                .map(str -> {
                    if(!CollectionUtils.isEmpty(orderGoodsList) && orderGoodsList.size() > 1){
                        return new StringBuilder(str).append("等").append(orderGoodsList.size()).append("件").toString();
                    }
                    return str;
                })
                .orElse(null);
    }

    public String handelVerifyGoodsName(List<LogisticsVerifyOrderGoods> orderGoodsList){
        return ListUtils.emptyIfNull(orderGoodsList).stream()
                .map(LogisticsVerifyOrderGoods::getGoodsName)
                .findFirst()
                .map(str -> {
                    if(!CollectionUtils.isEmpty(orderGoodsList) && orderGoodsList.size() > 1){
                        return new StringBuilder(str).append("等").append(orderGoodsList.size()).append("件").toString();
                    }
                    return str;
                })
                .orElse(null);
    }

    public String getOaNumber(String lmId,String buCode){

        String url= environment.getProperty("oa.lmember.auth.url");
        String gaiaKey = environment.getProperty("oa.gaia-api-key");
        log.info("获取到的authUrl:{}",url);
        log.info("获取到的gaiaKey:{}",gaiaKey);
        log.info("获取到的珑民ID:{}",lmId);

        String oaJson = null;
        String oaNumber=null;
        try {
            if(StringUtils.isNoneEmpty(url) && StringUtils.isNoneEmpty(gaiaKey)){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lmid",lmId);
                //todo bucode使用云图索 C20701 M00701
                jsonObject.put("bu_code","M00701");
                oaJson = HttpRequest.post(url)
                        .header("X-GAIA-API-KEY", gaiaKey)
                        .body(jsonObject.toJSONString())
                        .timeout(20000)
                        .execute().body();
                log.info("查询到的oaJson数据:{}",oaJson);
                if(StringUtils.isNotBlank(oaJson)){
                    oaNumber = Optional.ofNullable(JSON.parseObject(oaJson))
                            .map(dataJson -> dataJson.getString("data"))
                            .map(datas -> JSON.parseObject(datas)).map(data -> data.getString("oa_number")).orElse("");
                }
            }
        } catch (HttpException e) {
            log.error("获取OA账户异常",e);
        }
        return oaNumber;
    }

}

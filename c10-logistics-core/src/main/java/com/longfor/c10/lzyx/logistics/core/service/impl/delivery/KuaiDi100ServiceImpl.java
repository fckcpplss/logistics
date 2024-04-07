package com.longfor.c10.lzyx.logistics.core.service.impl.delivery;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.URLDecoder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.longfor.c10.lzyx.logistics.core.service.IDeliveryService;
import com.longfor.c10.lzyx.logistics.entity.annotation.DeliveryCompanyType;
import com.longfor.c10.lzyx.logistics.entity.constant.CommonConstant;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.*;
import com.longfor.c10.lzyx.logistics.entity.dto.kuaidi100.AutoNumberDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.kuaidi100.Kuaidi100MaptrackDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.kuaidi100.Kuaidi100ResData;
import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.LogisticsKuaidi100PathStateEnum;
import com.longfor.c2.starter.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 快递100物流api服务
 * @author zhaoyalong
 */
@Slf4j
@Service
@DeliveryCompanyType(code = CompanyCodeEnum.KD100)
public class KuaiDi100ServiceImpl extends AbstractDeliverService implements IDeliveryService {
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

    @Resource
    private RestTemplate restTemplate;

    @Override
    CompanyCodeEnum getSourceCompanyCode() {
        return CompanyCodeEnum.KD100;
    }

    @Override
    public AddOrderApiResData addApiOrder(AddOrderApiReqData addOrderApiReqData){
        AddOrderApiResData addOrderApiResData = new AddOrderApiResData();
        addOrderApiResData.setWaybillId(addOrderApiReqData.getDeliveryNo());
        return addOrderApiResData;
    }

    @Override
    public CancelOrderResData cancelApiOrder(CancelOrderReqData cancelOrderReqData) {
        log.info("快递100取消流程，不处理");
        CancelOrderResData cancelOrderResData = new CancelOrderResData();
        cancelOrderResData.setCode(CommonConstant.KUAIDI100_API_SUCCESS_CODE);
        return cancelOrderResData;
    }

    @Override
    public FeeResultResData queryFeeResult(FeeResultReqData feeResultReqData) {
        return null;
    }

    /**
     * <p>Title: getLatestDeliveryPath</p>
     * <p>Description: 快递一百实时快递查询接口</p>
     *
     * @param com   查询的快递公司的编码， 一律用小写字母
     * @param num   查询的快递单号， 单号的最大长度是32个字符
     * @param phone 收、寄件人的电话号码（手机和固定电话均可，只能填写一个，顺丰单号必填，其他快递公司选填。如座机号码有分机号，分机号无需上传。）
     * @return
     */
    @Override
    public DeliveryPathResData getLatestDeliveryPath(DeliveryPathReqData deliveryPathReqData) {
        Kuaidi100ResData kuaidi100ResData = null;
        if(StringUtils.isBlank(deliveryPathReqData.getRoutePathData())){
            log.info("快递100实时查询物流轨迹，调用参数 = {}",JSON.toJSONString(deliveryPathReqData));
            ImmutableMap<String,Object> paramsMap = ImmutableMap.of("com", deliveryPathReqData.getCompanyCode(), "num", deliveryPathReqData.getWaybillId(), "phone", deliveryPathReqData.getPhone(), "resultv2", this.resultV2);
            MultiValueMap<String, String> signParams = sign(JSON.toJSONString(paramsMap));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(signParams, headers);
            log.info("快递100实时查询物流轨迹，api请求参数 = {}",JSON.toJSONString(request));
            ResponseEntity<Kuaidi100ResData> response = null;
            try{
                response = restTemplate.postForEntity(url, request, Kuaidi100ResData.class);
            }catch (Exception ex){
                ex.printStackTrace();
                log.error("快递100实时查询物流轨迹，api请求失败",ex);
            }
            log.info("快递100实时查询物流轨迹，api请求结果 = {}", JSON.toJSONString(response));
            kuaidi100ResData = Optional.ofNullable(response).map(ResponseEntity::getBody).orElse(null);
        }else{
            log.info("快递100实时查询物流轨迹，以路由推送为准");
            kuaidi100ResData = Optional.ofNullable(JSONObject.parseObject(deliveryPathReqData.getRoutePathData()))
                    .map(x -> x.getString("lastResult"))
                    .map(x -> JSONObject.parseObject(x,Kuaidi100ResData.class))
                    .orElse(null);
        }

        DeliveryPathResData deliveryPathResData = Optional.ofNullable(kuaidi100ResData).map(data -> {
            DeliveryPathResData dprd = new DeliveryPathResData();
            dprd.setCompanyCode(deliveryPathReqData.getCompanyCode());
            dprd.setSourceCompanyCode(getSourceCompanyCode());
            dprd.setWaybillId(deliveryPathReqData.getWaybillId());
            dprd.setPathState(String.valueOf(data.getState()));
            dprd.setPathItemList(Optional.ofNullable(data.getData()).map(traces -> {
                return ListUtils.emptyIfNull(traces).stream().map(trace -> {
                    DeliveryPathResData.PathItem pathItem = new DeliveryPathResData.PathItem();
                    //轨迹状态
                    pathItem.setPathState(Optional.ofNullable(LogisticsKuaidi100PathStateEnum.fromSubDesc(trace.getStatus())).map(LogisticsKuaidi100PathStateEnum::getCode).map(String::valueOf).orElse(String.valueOf(LogisticsKuaidi100PathStateEnum.NONE.getCode())));
                    //轨迹节点描述
                    pathItem.setPathDes(trace.getStatus());
                    //轨迹节点详情
                    pathItem.setPathMsg(trace.getContext());
                    //轨迹时间戳
                    pathItem.setPathTime(DateUtil.parse(trace.getFtime()).getTime());
                    return pathItem;
                }).collect(Collectors.toList());
            }).orElse(null));
            return dprd;
        }).orElseGet(() -> new DeliveryPathResData());
        log.info("快递100实时查询物流轨迹，返回数据 = {}", JSON.toJSONString(deliveryPathResData));
        return deliveryPathResData;
    }
    /**
     * 快递100物流轨迹订阅
     */
    @Override
    public boolean subLogisticsPath(SubLogisticsPathReqData subLogisticsPathReqData) {
        log.info("快递100订阅物流轨迹，调用参数 = {}",JSON.toJSONString(subLogisticsPathReqData));
        StringBuilder param = new StringBuilder("{");
        param.append("\"company\":\"").append(subLogisticsPathReqData.getCompanyCode()).append("\"");
        param.append(",\"number\":\"").append(subLogisticsPathReqData.getDeliverNo()).append("\"");
        param.append(",\"key\":\"").append(this.key).append("\"");
        param.append(",\"parameters\":{");
        param.append("\"callbackurl\":\"").append(callBackUrl).append("\"");
        param.append(",\"resultv2\":").append(this.resultV2);
        param.append("}");
        param.append("}");

        MultiValueMap<String, String> signParam = sign(param.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(signParam, headers);
        log.info("快递100订阅物流轨迹，api请求参数 = {}",JSON.toJSONString(request));
        ResponseEntity<String> jsonObjectResponseEntity = restTemplate.postForEntity(subscribeUrl, request, String.class);
        log.info("快递100订阅物流轨迹，api请求结果 = {}", JSON.toJSONString(jsonObjectResponseEntity));
        return jsonObjectResponseEntity.getStatusCode().equals(HttpStatus.OK) && JSON.parseObject(jsonObjectResponseEntity.getBody()).getBoolean("result");
    }

    /**
     * 智能单号识别
     */
    public AutoNumberDTO autoNumber(String deliverNo) {
        log.info("快递100智能单号识别，deliverNo = {}", deliverNo);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("num", deliverNo);
        params.add("key", this.key);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        log.info("快递100智能单号识别，api请求参数 = {}", JSON.toJSONString(request));
        ResponseEntity<String> response = restTemplate.postForEntity(autonumberUrl, request, String.class);
        log.info("快递100智能单号识别，api请求结果 = {}", JSON.toJSONString(response));
        if(!response.getStatusCode().equals(HttpStatus.OK)){
            return null;
        }
        return ListUtils.emptyIfNull(JSONObject.parseArray(response.getBody(),AutoNumberDTO.class))
                .stream()
                .findFirst()
                .orElse(null);
    }


    /**
     * 描述: 查询预计送达时间
     */
    @Override
    public String getApiDeliverTime(DeliverTimeReqData req) {
        log.info("快递100查询预计送达时间，调用参数 = {}", JSON.toJSONString(req));
        JSONObject object = new JSONObject();
        object.put("com", req.getCompanyCode());
        object.put("num", req.getWaybillId());
        object.put("phone", req.getPhone());
        object.put("from", req.getFrom());
        object.put("to", req.getTo());
        MultiValueMap<String, String> params = sign(object.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        log.info("快递100查询预计到达时间，api请求参数 = {}", JSON.toJSONString(request));
        ResponseEntity<Kuaidi100MaptrackDTO> response = restTemplate.postForEntity(maptrackUrl, request, Kuaidi100MaptrackDTO.class);
        log.info("快递100查询预计到达时间，api请求结果 = {}", JSON.toJSONString(response));
        String deliveryPromiseTime = Optional.ofNullable(response)
                .map(HttpEntity::getBody)
                .map(Kuaidi100MaptrackDTO::getArrivalTime)
                .orElse(null);
        log.info("快递100查询预计到达时间，返回结果 = {}",deliveryPromiseTime);
        return deliveryPromiseTime;
    }

    @Override
    DeliveryPrintResData getApiPrintData(DeliveryPrintReqData deliveryPrintReqData) {
        return null;
    }

    /**
     * 签名
     */
    private MultiValueMap<String, String> sign(String param) {
        log.info("快递100接口签名，param = {}",param);
        String body = param + this.key + this.customer;
        String sign = MD5Util.getMD5(body);
        log.info("快递100接口签名，body = {},sign = {}",body,sign);
        MultiValueMap<String, String> signParam = new LinkedMultiValueMap<>();
        signParam.add("customer", this.customer);
        signParam.add("sign", sign.toUpperCase());
        signParam.add("param", param);
        log.info("快递100接口签名，signParam = {}", JSON.toJSONString(signParam));
        return signParam;
    }

    public static void main(String[] args) {
        String data = "2022-01-18 10:50:47";
        long time = DateUtil.parse(data).getTime();
        System.out.println(time);
        Date from = DateTime.from(Instant.ofEpochMilli(time));
        String s = DateUtil.formatDateTime(from);
        System.out.println(s);
        String str = "{\"status\":\"polling\",\"message\":\"\",\"lastResult\":{\"message\":\"ok\",\"nu\":\"75873793588306\",\"ischeck\":\"0\",\"com\":\"zhongtong\",\"status\":\"200\",\"state\":2,\"data\":[{\"time\":\"2022-01-25 12:43:09\",\"context\":\"您的快件派送不成功，原因【暂未联系上客户】，预计下次派送时间2022-01-26 17:00\",\"ftime\":\"2022-01-25 12:43:09\",\"areaCode\":null,\"areaName\":null,\"status\":\"疑难\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null},{\"time\":\"2022-01-25 12:27:02\",\"context\":\"您的快件正在派送中，请您准备签收（快递员：潘谊民 ，联系电话：135****1234）。\",\"ftime\":\"2022-01-25 12:27:02\",\"areaCode\":null,\"areaName\":null,\"status\":\"派件\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null},{\"time\":\"2022-01-25 06:18:27\",\"context\":\"您的快件已发车\",\"ftime\":\"2022-01-25 06:18:27\",\"areaCode\":null,\"areaName\":null,\"status\":\"在途\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null},{\"time\":\"2022-01-25 05:50:51\",\"context\":\"您的快件由【上海松江分拣中心】准备发往【上海梅陇营业部】\",\"ftime\":\"2022-01-25 05:50:51\",\"areaCode\":\"CN310117000000\",\"areaName\":\"上海,上海,松江区\",\"status\":\"在途\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null},{\"time\":\"2022-01-25 01:38:23\",\"context\":\"您的快件在【上海松江分拣中心】分拣完成\",\"ftime\":\"2022-01-25 01:38:23\",\"areaCode\":\"CN310117000000\",\"areaName\":\"上海,上海,松江区\",\"status\":\"在途\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null},{\"time\":\"2022-01-24 23:52:53\",\"context\":\"您的快件已到达【上海松江分拣中心】\",\"ftime\":\"2022-01-24 23:52:53\",\"areaCode\":\"CN310117000000\",\"areaName\":\"上海,上海,松江区\",\"status\":\"在途\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null},{\"time\":\"2022-01-24 22:05:00\",\"context\":\"您的快件已发车\",\"ftime\":\"2022-01-24 22:05:00\",\"areaCode\":null,\"areaName\":null,\"status\":\"在途\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null},{\"time\":\"2022-01-24 21:46:00\",\"context\":\"您的快件由【苏州北苑分拣中心】准备发往【上海松江分拣中心】\",\"ftime\":\"2022-01-24 21:46:00\",\"areaCode\":\"CN320500000000\",\"areaName\":\"江苏,苏州市\",\"status\":\"在途\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null},{\"time\":\"2022-01-24 20:54:08\",\"context\":\"您的快件在【苏州北苑分拣中心】分拣完成\",\"ftime\":\"2022-01-24 20:54:08\",\"areaCode\":\"CN320500000000\",\"areaName\":\"江苏,苏州市\",\"status\":\"在途\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null},{\"time\":\"2022-01-24 18:24:54\",\"context\":\"您的快件已发车\",\"ftime\":\"2022-01-24 18:24:54\",\"areaCode\":null,\"areaName\":null,\"status\":\"在途\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null},{\"time\":\"2022-01-24 18:24:50\",\"context\":\"快递司机收箱\",\"ftime\":\"2022-01-24 18:24:50\",\"areaCode\":null,\"areaName\":null,\"status\":\"在途\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null},{\"time\":\"2022-01-24 17:05:56\",\"context\":\"您的快件已由【上海华新营业部】揽收完成\",\"ftime\":\"2022-01-24 17:05:56\",\"areaCode\":null,\"areaName\":null,\"status\":\"揽收\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null},{\"time\":\"2022-01-24 17:05:55\",\"context\":\"京东快递 已收取快件\",\"ftime\":\"2022-01-24 17:05:55\",\"areaCode\":null,\"areaName\":null,\"status\":\"揽收\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null},{\"time\":\"2022-01-24 17:05:55\",\"context\":\"您的快件已到达【上海华新营业部】\",\"ftime\":\"2022-01-24 17:05:55\",\"areaCode\":\"CN310118107000\",\"areaName\":\"上海,上海,青浦区,华新镇\",\"status\":\"在途\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null},{\"time\":\"2022-01-24 11:25:23\",\"context\":\"揽收任务已分配给卢俊\",\"ftime\":\"2022-01-24 11:25:23\",\"areaCode\":null,\"areaName\":null,\"status\":\"揽收\",\"location\":null,\"areaCenter\":null,\"areaPinYin\":null,\"statusCode\":null}],\"loop\":false}}";
        Kuaidi100ResData kuaidi100ResData = Optional.ofNullable(JSONObject.parseObject(str))
                .map(x -> x.getString("lastResult"))
                .map(x -> JSONObject.parseObject(x,Kuaidi100ResData.class))
        .orElse(null);
        System.out.println(JSON.toJSONString(kuaidi100ResData));
    }
}


package com.longfor.c10.lzyx.logistics.core.mq.consumer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longfor.c10.lzyx.logistics.core.factory.DeliveryFactory;
import com.longfor.c10.lzyx.logistics.core.util.JsonUtil;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryService;
import com.longfor.c10.lzyx.logistics.entity.constant.CommonConstant;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.DeliveryDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.DeliveryPathReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.kuaidi100.UpdateLatestPathReq;
import com.longfor.c10.lzyx.logistics.entity.dto.open.JdRoutRequest;
import com.longfor.c10.lzyx.logistics.entity.dto.shunfeng.SFRouteRequest;
import com.longfor.c10.lzyx.logistics.entity.dto.shunfeng.SFStatePush;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.common.util.StringUtil;
import com.longfor.c2.starter.rocketmq.annotation.MessageListener;
import com.longfor.c2.starter.rocketmq.annotation.RocketListener;
import com.longfor.c2.starter.rocketmq.message.ReceivedMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RocketListener(groupID = "GID_c10_logistics_sf_push")
public class LogisticsRoutPushConsumer {

    @Autowired
    private DeliveryFactory deliveryFactory;

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;

    @Value("${logistics.track.phone.con.switch:shunfeng,fengwang}")
    private String logisticsTrackConSwitch;

    @MessageListener(topic = CommonConstant.C10_LOGISTICS_ROUT_PUSH, orderConsumer = true)
    public void routPushMq(ReceivedMessage message) {
        log.info("物流路由推送，接受消息，message = {}", JSON.toJSONString(message));
        String jsonBody = message.getBody();
        log.info(CommonConstant.MQ_ALL_PUSH_ROUTE_BODY, jsonBody);
        ObjectNode node = JsonUtil.parse(jsonBody, ObjectNode.class);
        if (CommonConstant.SF_ROUT_SOURCE.equals(node.get(CommonConstant.ROUT_SOURCE_KEY).asText())) {
            // 顺丰路由回调
            SFRouteRequest sfRequest = JsonUtil.convertValue(node, SFRouteRequest.class);
            if(Objects.isNull(sfRequest)){
                return;
            }
            queLatestDelivery(true,sfRequest.getOrderid(),sfRequest.getMailno(),null,CompanyCodeEnum.SF,JSON.toJSONString(sfRequest));
        } else if (CommonConstant.SF_STATE_SOURCE.equals(node.get(CommonConstant.ROUT_SOURCE_KEY).asText())) {
            SFStatePush sfStatePush = JsonUtil.convertValue(node, SFStatePush.class);
            if(Objects.isNull(sfStatePush)){
                return;
            }
            queLatestDelivery(true,null,sfStatePush.getWaybillNo(),null,CompanyCodeEnum.SF,JSON.toJSONString(convertStateToRoutePath(sfStatePush)));
        } else if (CommonConstant.JD_ROUT_SOURCE.equals(node.get(CommonConstant.ROUT_SOURCE_KEY).asText())) {
            //1.调用JD路由查询，存入路由库
            JdRoutRequest.JdRoutRequestBody jdRoutRequestBody = JsonUtil.parse(message.getBody(), JdRoutRequest.JdRoutRequestBody.class);
            String deliveryNo = Optional.ofNullable(jdRoutRequestBody).map(JdRoutRequest.JdRoutRequestBody::getWaybillCode).orElseThrow(() -> new BusinessException(StrUtil.format("运单单号不存在")));
            queLatestDelivery(true,null,deliveryNo,null,CompanyCodeEnum.JD,null);
        } else if (CommonConstant.KUAIDI100_ROUT_SOURCE.equals(node.get(CommonConstant.ROUT_SOURCE_KEY).asText())) {
            String object = node.get("param").asText();
            UpdateLatestPathReq req = JsonUtil.fromJson(object, UpdateLatestPathReq.class);
            UpdateLatestPathReq.LastResult lastResult = req.getLastResult();
            if(Objects.isNull(lastResult) || StringUtils.isBlank(lastResult.getNu())){
                return;
            }
            queLatestDelivery(true,null,lastResult.getNu(),lastResult.getCom(),CompanyCodeEnum.KD100,object);
        } else if (CommonConstant.XXL_JOB_ROUT_SOURCE.equals(node.get(CommonConstant.ROUT_SOURCE_KEY).asText())) {
            // 来源是XXL_JOB 定时任务推送
            DeliveryDTO deliveryDto = JsonUtil.convertValue(node, DeliveryDTO.class);
            if(Objects.isNull(deliveryDto)){
                return;
            }
            queLatestDelivery(false,deliveryDto.getLogisticsDeliveryId(),deliveryDto.getDeliveryNo(),deliveryDto.getCompanyCode(),CompanyCodeEnum.KD100,null);
        }
    }

    /**
     * 实时查询物流轨迹
     * @param queryLatest 是否实时查询
     * @param deliveryNo 运单编号
     * @param companyCode 快递100快递公司编码
     * @param companyCodeEnum 物流类型
     * @param routePathData 实时轨迹内容，没有走api实时查询
     */
    private void queLatestDelivery(boolean queryLatest,String logisticsDeliveryId,String deliveryNo,String companyCode,CompanyCodeEnum companyCodeEnum,String routePathData){
        if(StringUtils.isBlank(deliveryNo)){
            return;
        }
        List<LogisticsDelivery> logisticsDeliverys = logisticsDeliveryService.list(Wrappers.<LogisticsDelivery>lambdaQuery()
                .eq(StringUtils.isNotBlank(logisticsDeliveryId),LogisticsDelivery::getId,Optional.ofNullable(StrUtil.emptyToDefault(logisticsDeliveryId,null)).map(Long::parseLong).orElse(null))
                .eq(LogisticsDelivery::getDeliveryNo,deliveryNo)
                .eq(LogisticsDelivery::getIfCancel,0)
                .orderByDesc(LogisticsDelivery::getCreateTime));
        if(CollectionUtils.isEmpty(logisticsDeliverys)){
            log.info(StrUtil.format("物流路由推送，{}轨迹推送，运单号不存在"),companyCodeEnum.getDesc());
            return;
        }
        Map<Long, LogisticsDelivery> logisticsOrderIdAndMap = logisticsDeliverys.stream().collect(Collectors.toMap(LogisticsDelivery::getLogisticsOrderId, Function.identity(), (a, b) -> a));
        logisticsOrderIdAndMap.forEach((k,v) -> {
            DeliveryPathReqData deliveryPathReqData = new DeliveryPathReqData();
            deliveryPathReqData.setWaybillId(deliveryNo);
            deliveryPathReqData.setLogisticsOrderId(String.valueOf(k));
            deliveryPathReqData.setCompanyCode(companyCode);
            //根据快递公司判断是否传递手机号 20230608 by fuqiang5
            if(v.getCompanyCode() !=null && logisticsTrackConSwitch.contains(v.getCompanyCode())){
                deliveryPathReqData.setPhone(StringUtil.isNotBlank(v.getReceiptPhone()) ? v.getReceiptPhone() : v.getSendPhone());
            }else {
                deliveryPathReqData.setPhone(StrUtil.emptyToDefault(v.getSendPhone(),""));
            }
            //deliveryPathReqData.setPhone(StrUtil.emptyToDefault(v.getSendPhone(),""));
            //实时
            deliveryPathReqData.setQueryLatest(queryLatest);
            //实时路由
            deliveryPathReqData.setRoutePathData(routePathData);
            //2.处理结果, 并更新数据库 + 推送订单服务
            Optional.ofNullable(deliveryFactory.getService(companyCodeEnum)).map(service -> service.getDeliveryPath(deliveryPathReqData)).orElseThrow(() -> new BusinessException(StrUtil.format("{}路由处理异常",companyCodeEnum.getDesc())));
        });
    }
    private SFRouteRequest convertStateToRoutePath(SFStatePush sfStatePush) {
        SFRouteRequest sfRouteRequest = new SFRouteRequest();
        sfRouteRequest.setOpCode(sfStatePush.getOrderStateCode());
        sfRouteRequest.setRemark(sfStatePush.getOrderStateDesc());
        sfRouteRequest.setMailno(sfStatePush.getWaybillNo());
        sfRouteRequest.setOrderid(sfStatePush.getOrderNo());
        sfRouteRequest.setAcceptTime(DateUtil.formatDateTime(sfStatePush.getCreateTm()));
        return sfRouteRequest;
    }
}

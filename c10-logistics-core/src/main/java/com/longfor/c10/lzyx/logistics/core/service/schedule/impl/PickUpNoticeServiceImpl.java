package com.longfor.c10.lzyx.logistics.core.service.schedule.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longfor.c10.lzyx.logistics.core.config.PickUpNoticePropertiesConfig;
import com.longfor.c10.lzyx.logistics.core.service.schedule.IPickUpNoticeService;
import com.longfor.c10.lzyx.logistics.core.util.PushSmsUtil;
import com.longfor.c10.lzyx.logistics.core.util.SendCommonHandler;
import com.longfor.c10.lzyx.logistics.core.util.TouchSendUtil;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsVerifyOrderGoodsService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsVerifyOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.PickUpNoticeDTO;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsVerifyOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsVerifyOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.enums.LogisticsVerifyRefundStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.LogisticsVerifyVerifyStatusEnum;
import com.longfor.c10.lzyx.order.entity.constant.CommonConstant;
import com.longfor.c10.lzyx.order.entity.enums.TouchSmsTypeEnum;
import com.longfor.c10.lzyx.touch.entity.bo.yuntusuo.TouchBotCardMessageBO;
import com.longfor.c10.lzyx.touch.entity.enums.TouchCustomParamEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author lanxiaolong
 * @Date 2022/4/14 10:14 上午
 */
@Slf4j
@Service
public class PickUpNoticeServiceImpl implements IPickUpNoticeService {

    @Autowired
    private TouchSendUtil touchSendUtil;

    @Autowired
    private PushSmsUtil pushSmsUtil;

    @Autowired
    private ILogisticsVerifyOrderService logisticsVerifyOrderService;

    @Autowired
    private ILogisticsVerifyOrderGoodsService logisticsVerifyOrderGoodsService;

    @Autowired
    private PickUpNoticePropertiesConfig pickUpNoticePropertiesConfig;
    /**
     * 未核销短信模版id
     */
    @Value("${sms.no.verify.paas.templateId:65416}")
    private String smsNoVerifyTemplateId;
    /**
     * 未核销短信开关
     */
    @Value("${sms.no.verify.switch:false}")
    private boolean noVerifySwitch;
    /**
     * 自提短信模版id-时间内
     */
    @Value("${sms.pickup.inTime.paas.templateId:65416}")
    private String smsPickupInTimeTemplateId;
    /**
     * 自提短信开关-时间内
     */
    @Value("${sms.pickup.inTime.switch:false}")
    private boolean smsPickInTimeSwitch;
    /**
     * 自提短信模版id-时间外
     */
    @Value("${sms.pickup.outTime.paas.templateId:65416}")
    private String smsPickupOutTimeTemplateId;
    /**
     * 自提短信开关-时间外
     */
    @Value("${sms.pickup.outTime.switch:false}")
    private boolean smsPickOutTimeSwitch;
    /**
     * 未核销云图梭开关
     */
    @Value("${push.no.verify.switch:false}")
    private boolean pushNoVerifySwitch;
    /**
     * 未核销云图梭模版
     */
    @Value("${push.no.verify.template}")
    private String pushNoVerifyTemplate;

    /**
     * 未核销云图梭标题
     */
    @Value("${push.no.verify.title}")
    private String pushNoVerifyTitle;

    /**
     * 【用户】自提订单消息通知-时间外 标题
     */
    @Value("${push.pickup.outTime.title}")
    private String pushPickUpOutTimeTitle;
    /**
     * 【用户】自提订单消息通知-时间外 模版
     */
    @Value("${push.pickup.outTime.template}")
    private String pushPickUpOutTimeTemplate;
    /**
     * 【用户】自提订单消息通知-时间外 跳转链接
     */
    @Value("${push.pickup.outTime.jumpUrl}")
    private String pushPickUpOutTimeJumpUrl;
    /**
     * 【用户】自提订单消息通知-时间外 开关
     */
    @Value("${push.pickup.outTime.switch:false}")
    private boolean pushPickUpOutTimeSwitch;

    /**
     * 【用户】自提订单消息通知-时间内（新） 标题
     */
    @Value("${push.pickup.inTime.new.title}")
    private String pushPickUpInTimeNewTitle;
    /**
     * 【用户】自提订单消息通知-时间内（新） 模版
     */
    @Value("${push.pickup.inTime.new.template}")
    private String pushPickUpInTimeNewTemplate;
    /**
     * 【用户】自提订单消息通知-时间内（新） 跳转链接
     */
    @Value("${push.pickup.inTime.new.jumpUrl}")
    private String pushPickUpInTimeNewJumpUrl;
    /**
     * 【用户】自提订单消息通知-时间内（新） 开关-云图梭
     */
    @Value("${push.pickup.inTime.new.switch:false}")
    private boolean pushPickUpInTimeNewSwitch;

    @Resource
    private SendCommonHandler sendCommonHandler;

    /**
     * 通知客服人员核销
     */
    @Override
    public void noticeOperator() {
        LambdaQueryWrapper<LogisticsVerifyOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(LogisticsVerifyOrder::getVerifyStatus, LogisticsVerifyVerifyStatusEnum.VERIFY_NO.getCode(),
                LogisticsVerifyVerifyStatusEnum.VERIFY_TIMEOUT.getCode());
        queryWrapper.eq(LogisticsVerifyOrder::getRefundStatus, LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode());
        Integer count = logisticsVerifyOrderService.count(queryWrapper);
        if (count > 0) {
            if(noVerifySwitch){
                Map<String, Object> configParams = new HashMap<>();
                configParams.put(CommonConstant.NO_VERIFY_COUNT, count);
                List<Object> userPhones = new ArrayList<>();
                userPhones.add(pickUpNoticePropertiesConfig.getOperatorPhoneNumber());
                log.info("发送客服提醒短信参数:{}", JSONObject.toJSONString(configParams));
                sendCommonHandler.sendSms(configParams, userPhones, smsNoVerifyTemplateId);
            } else {
                noticeOperator(count, TouchSmsTypeEnum.SHORT_MESSAGE.getTouchType());
            }
            if(pushNoVerifySwitch){
                List<Object> userIds = new ArrayList<>();
                userIds.add(pickUpNoticePropertiesConfig.getOperatorOaNumber());
                sendCommonHandler.sendPushSingleCard(userIds, buildNoticeOperator(count));
            } else {
                noticeOperator(count, TouchSmsTypeEnum.PUSSH.getTouchType());
            }

        }
    }
    private TouchBotCardMessageBO buildNoticeOperator(Integer count){
        TouchBotCardMessageBO touchBotCardMessageBO = new TouchBotCardMessageBO();
        touchBotCardMessageBO.setSupportCardLink(false);
        touchBotCardMessageBO.setOpenType(1);
        touchBotCardMessageBO.setHeaderTitle(pushNoVerifyTitle);
        String content = pushNoVerifyTemplate.replace("#param1#", String.valueOf(count));
        touchBotCardMessageBO.setTextContent(content);
        return touchBotCardMessageBO;
    }

    /**
     * 通知用户自提
     */
    @Override
    public void noticeUser() {
        LambdaQueryWrapper<LogisticsVerifyOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LogisticsVerifyOrder::getVerifyStatus, LogisticsVerifyVerifyStatusEnum.VERIFY_NO.getCode());
        queryWrapper.eq(LogisticsVerifyOrder::getRefundStatus, LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode());
        Date currentTime = new Date();
        queryWrapper.le(LogisticsVerifyOrder::getPickupStartTime, currentTime);
        queryWrapper.ge(LogisticsVerifyOrder::getPickupEndTime, currentTime);
        queryWrapper.isNotNull(LogisticsVerifyOrder::getPickupAddress).isNotNull(LogisticsVerifyOrder::getPickupSpot).isNotNull(LogisticsVerifyOrder::getLmId);
        List<LogisticsVerifyOrder> pickUpNoticeList = logisticsVerifyOrderService.list(queryWrapper);
        if (!CollectionUtils.isEmpty(pickUpNoticeList)) {
            List<PickUpNoticeDTO> pickUpNoticeDTOS = pickUpNoticeList.stream().map(e -> PickUpNoticeDTO.builder()
                    .childOrderId(e.getChildOrderId()).orderId(e.getOrderId()).pickupAddress(e.getPickupAddress()).pickupEndTime(e.getPickupEndTime())
                    .pickupSpot(e.getPickupSpot()).last(false).lmId(e.getLmId()).build()).collect(Collectors.toList());
            for (PickUpNoticeDTO dto : pickUpNoticeDTOS) {
                LambdaQueryWrapper<LogisticsVerifyOrderGoods> orderGoodsQueryWrapper = new LambdaQueryWrapper<>();
                orderGoodsQueryWrapper.eq(LogisticsVerifyOrderGoods::getChildOrderId, dto.getChildOrderId());
                List<LogisticsVerifyOrderGoods> orderGoods = logisticsVerifyOrderGoodsService.list(orderGoodsQueryWrapper);
                dto.setGoodsName(pushSmsUtil.handelVerifyGoodsName(orderGoods));
            }
            if(smsPickInTimeSwitch){
                for (PickUpNoticeDTO dto : pickUpNoticeDTOS) {
                    List<Object> userPhones = new ArrayList<>();
                    userPhones.add(dto.getLmId());

                    Map<String, Object> configParams = new HashMap<>();
                    configParams.put("childOrderId", dto.getChildOrderId());
                    configParams.put("goodsName", dto.getGoodsName());
                    configParams.put("pickupSpot", dto.getPickupSpot());
                    configParams.put("pickupAddress", dto.getPickupAddress());
                    configParams.put("date", DateUtil.format(dto.getPickupEndTime(), "yyyy-MM-dd HH:mm:ss"));
                    log.info("自提短信（时间内）发送短信参数:{}", JSONObject.toJSONString(configParams));
                    sendCommonHandler.sendLmIdSms(configParams, userPhones, smsPickupInTimeTemplateId);
                }
            } else {
                noticeUser(pickUpNoticeDTOS, TouchSmsTypeEnum.SHORT_MESSAGE.getTouchType(), pickUpNoticePropertiesConfig.getUserMessageTaskCode(), false);
            }
            if(pushPickUpInTimeNewSwitch){
                sendNoticeUserPush(pickUpNoticeDTOS, pushPickUpInTimeNewTemplate, pushPickUpInTimeNewJumpUrl, pushPickUpInTimeNewTitle);
            } else {
                noticeUser(pickUpNoticeDTOS, TouchSmsTypeEnum.PUSSH.getTouchType(), pickUpNoticePropertiesConfig.getUserPushTaskCode(), true);
            }
        }

        LambdaQueryWrapper<LogisticsVerifyOrder> outTimeQueryWrapper = new LambdaQueryWrapper<>();
        outTimeQueryWrapper.eq(LogisticsVerifyOrder::getVerifyStatus, 0);
        outTimeQueryWrapper.between(LogisticsVerifyOrder::getPickupEndTime, DateUtil.offsetHour(currentTime, -2), currentTime);
        outTimeQueryWrapper.isNotNull(LogisticsVerifyOrder::getPickupAddress).isNotNull(LogisticsVerifyOrder::getPickupSpot).isNotNull(LogisticsVerifyOrder::getLmId);
        List<LogisticsVerifyOrder> lastPickUpNoticeList = logisticsVerifyOrderService.list(outTimeQueryWrapper);
        if (!CollectionUtils.isEmpty(lastPickUpNoticeList)) {
            List<PickUpNoticeDTO> lastPickUpNoticeDTOS = lastPickUpNoticeList.stream().map(e -> PickUpNoticeDTO.builder()
                    .childOrderId(e.getChildOrderId()).orderId(e.getOrderId()).pickupAddress(e.getPickupAddress()).pickupEndTime(e.getPickupEndTime())
                    .pickupSpot(e.getPickupSpot()).last(true).lmId(e.getLmId()).build()).collect(Collectors.toList());
            for (PickUpNoticeDTO dto : lastPickUpNoticeDTOS) {
                LambdaQueryWrapper<LogisticsVerifyOrderGoods> orderGoodsQueryWrapper = new LambdaQueryWrapper<>();
                orderGoodsQueryWrapper.eq(LogisticsVerifyOrderGoods::getChildOrderId, dto.getChildOrderId());
                List<LogisticsVerifyOrderGoods> orderGoods = logisticsVerifyOrderGoodsService.list(orderGoodsQueryWrapper);
                dto.setGoodsName(pushSmsUtil.handelVerifyGoodsName(orderGoods));
            }
            if(smsPickOutTimeSwitch){
                for (PickUpNoticeDTO dto : lastPickUpNoticeDTOS) {
                    List<Object> userPhones = new ArrayList<>();
                    userPhones.add(dto.getLmId());

                    Map<String, Object> configParams = new HashMap<>();
                    configParams.put("childOrderId", dto.getChildOrderId());
                    configParams.put("goodsName", dto.getGoodsName());
                    configParams.put("pickupSpot", dto.getPickupSpot());
                    configParams.put("pickupAddress", dto.getPickupAddress());
                    log.info("自提短信（时间外）发送短信参数:{}", JSONObject.toJSONString(configParams));
                    sendCommonHandler.sendLmIdSms(configParams, userPhones, smsPickupOutTimeTemplateId);
                }
            } else {
                noticeUser(lastPickUpNoticeDTOS, TouchSmsTypeEnum.SHORT_MESSAGE.getTouchType(), pickUpNoticePropertiesConfig.getUserMessageLastTaskCode(), false);
            }
            if(pushPickUpOutTimeSwitch){
                sendNoticeUserPush(lastPickUpNoticeDTOS, pushPickUpOutTimeTemplate, pushPickUpOutTimeJumpUrl, pushPickUpOutTimeTitle);
            } else {
                noticeUser(lastPickUpNoticeDTOS, TouchSmsTypeEnum.PUSSH.getTouchType(), pickUpNoticePropertiesConfig.getUserPushLastTaskCode(), true);
            }
        }
    }
    public void sendNoticeUserPush(List<PickUpNoticeDTO> lastPickUpNoticeDTOS, final String template, final String jumpUrl, final String title){
        for (PickUpNoticeDTO dto : lastPickUpNoticeDTOS) {
            ThreadUtil.execute(() -> {
                List<String> params = new ArrayList<>();
                params.add(dto.getChildOrderId());
                params.add(dto.getGoodsName());
                params.add(dto.getPickupSpot());
                params.add(dto.getPickupAddress());
                if (!dto.getLast()) {
                    params.add(DateUtil.format(dto.getPickupEndTime(), "yyyy-MM-dd HH:mm:ss"));
                }
                params.add(dto.getOrderId());
                params.add(dto.getChildOrderId());
                String content = template;
                String url = jumpUrl;
                for (int i = 0; i < params.size(); ++i) {
                    content = content.replace("#Param" + (i + 1) + "#", params.get(i));
                    //url = url.replace("#Param" + (i + 1) + "#", params.get(i));
                }
                List<Object> userIds = new ArrayList<>();
                userIds.add(pickUpNoticePropertiesConfig.getOperatorOaNumber());
                sendCommonHandler.sendPushSingleCard(userIds, buildNoticeUser(content, url, title));
            });
        }
    }
    private TouchBotCardMessageBO buildNoticeUser(String content, String jumpUrl, String title){
        TouchBotCardMessageBO touchBotCardMessageBO = new TouchBotCardMessageBO();
        touchBotCardMessageBO.setSupportCardLink(false);
        touchBotCardMessageBO.setUri(jumpUrl);
        touchBotCardMessageBO.setAndroidUri(jumpUrl);
        touchBotCardMessageBO.setIosUri(jumpUrl);
        touchBotCardMessageBO.setPcUri(jumpUrl);
        touchBotCardMessageBO.setOpenType(1);
        touchBotCardMessageBO.setHeaderTitle(title);
        touchBotCardMessageBO.setTextContent(content);
        return touchBotCardMessageBO;
    }
    private void noticeOperator(Integer count, String type) {
        ThreadUtil.execute(() -> {
            List<String> params = new ArrayList<>();
            params.add(count.toString());
            touchSendUtil.sendNotice(pickUpNoticePropertiesConfig.getOperatorMessageTaskCode(), type, TouchCustomParamEnum.PAAS_PHONE.getCode(), pickUpNoticePropertiesConfig.getOperatorPhoneNumber(), "未核销数据短信提醒-客服", params);
            touchSendUtil.sendNotice(pickUpNoticePropertiesConfig.getOperatorPushTaskCode(), type, TouchCustomParamEnum.YUNTUSUO_TARGET.getCode(), pickUpNoticePropertiesConfig.getOperatorOaNumber(), "未核销数据push提醒-客服", params);
        });
    }


    private void noticeUser(List<PickUpNoticeDTO> pickUpNoticeDTOS, String type, String touchTaskCode, Boolean push) {
        for (PickUpNoticeDTO dto : pickUpNoticeDTOS) {
            ThreadUtil.execute(() -> {
                List<String> params = new ArrayList<>();
                params.add(dto.getChildOrderId());
                params.add(dto.getGoodsName());
                params.add(dto.getPickupSpot());
                params.add(dto.getPickupAddress());
                if (!dto.getLast()) {
                    params.add(DateUtil.format(dto.getPickupEndTime(), "yyyy-MM-dd HH:mm:ss"));
                }
                if (push) {
                    params.add(dto.getOrderId());
                    params.add(dto.getChildOrderId());
                }
                touchSendUtil.sendNotice(touchTaskCode, type, TouchCustomParamEnum.LONGFOR_USERID.getCode(), dto.getLmId(), "自提短信提醒-用户", params);
                touchSendUtil.sendNotice(touchTaskCode, type, TouchCustomParamEnum.YUNTUSUO_TARGET.getCode(),
                        Optional.ofNullable(pushSmsUtil.getOaNumber(dto.getLmId(), null)).orElse(null), "自提push提醒-用户", params);
            });
        }
    }



}

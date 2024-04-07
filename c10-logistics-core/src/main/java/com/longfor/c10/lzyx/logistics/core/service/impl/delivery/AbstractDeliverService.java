package com.longfor.c10.lzyx.logistics.core.service.impl.delivery;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.longfor.c10.lzyx.logistics.core.mq.producer.LogisticsStatusChangeProducer;
import com.longfor.c10.lzyx.logistics.core.mq.producer.LogisticsTouchProducer;
import com.longfor.c10.lzyx.logistics.core.service.IDeliveryService;
import com.longfor.c10.lzyx.logistics.core.util.PushSmsUtil;
import com.longfor.c10.lzyx.logistics.core.util.SendCommonHandler;
import com.longfor.c10.lzyx.logistics.dao.mapper.LogisticsShipAddressMapper;
import com.longfor.c10.lzyx.logistics.dao.mapper.ShopLogisticsConfigMapper;
import com.longfor.c10.lzyx.logistics.dao.mapper.ShopLogisticsMapper;
import com.longfor.c10.lzyx.logistics.dao.service.*;
import com.longfor.c10.lzyx.logistics.entity.constant.CommonConstant;
import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.AmUserInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.*;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.OrderStatusChangePushDTO;
import com.longfor.c10.lzyx.logistics.entity.entity.*;
import com.longfor.c10.lzyx.logistics.entity.enums.*;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c10.lzyx.order.client.api.request.demand.DemandOrderQueryByChildOrderIdReqDto;
import com.longfor.c10.lzyx.order.client.api.response.demand.DemandOrderQueryResDto;
import com.longfor.c10.lzyx.order.client.client.admin.order.DemandOrderQueryClient;
import com.longfor.c10.lzyx.order.entity.enums.DemandOrderStatusEnum;
import com.longfor.c10.lzyx.order.entity.enums.OrderStatusEnum;
import com.longfor.c10.lzyx.order.entity.enums.TocuhSmsParamEnum;
import com.longfor.c10.lzyx.order.entity.enums.TouchSmsTypeEnum;
import com.longfor.c10.lzyx.touch.entity.bo.yuntusuo.TouchBotCardMessageBO;
import com.longfor.c10.lzyx.touch.entity.dto.mq.TouchMqMessageDTO;
import com.longfor.c10.lzyx.touch.entity.enums.TouchCustomParamEnum;
import com.longfor.c10.lzyx.touch.entity.enums.TouchSystemCodeEnum;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import com.lop.open.api.sdk.LopException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 物流抽象服务类
 * @author zhaoyl
 * @date 2022/1/11 下午2:28
 * @since 1.0
 */
@Slf4j
@Service
public abstract class AbstractDeliverService implements IDeliveryService {

    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Autowired
    private LogisticsShipAddressMapper logisticsShipAddressMapper;

    @Autowired
    private ShopLogisticsMapper shopLogisticsMapper;

    @Autowired
    private ILogisticsDeliveryCompanyService logisticsDeliveryCompanyService;

    @Autowired
    private ShopLogisticsConfigMapper shopLogisticsConfigMapper;

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;

    @Autowired
    private LogisticsStatusChangeProducer logisticsStatusChangeProducer;

    @Autowired
    private ILogisticsOrderStatusMappingService logisticsOrderStatusMappingService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private LogisticsTouchProducer logisticsTouchProducer;

    @Autowired
    private PushSmsUtil pushSmsUtil;

    @Resource
    private DemandOrderQueryClient demandOrderQueryClient;
    /**
     * 物流发货通知短信模版id
     */
    @Value("${sms.logistics.send.paas.templateId:65423}")
    private String smsLogisticsSendTemplateId;
    /**
     * 物流发货通知短信开关
     */
    @Value("${sms.logistics.send.switch:false}")
    private boolean smsLogisticsSendSwitch;
    /**
     * 物流签收通知短信模版id
     */
    @Value("${sms.logistics.sign.paas.templateId:65424}")
    private String smsLogisticsSignTemplateId;
    /**
     * 物流签收通知短信开关
     */
    @Value("${sms.logistics.sign.switch:false}")
    private boolean smsLogisticsSignSwitch;

    /**
     * 物流发货通知云图梭开关
     */
    @Value("${push.logistics.send.switch:false}")
    private boolean pushLogisticsSendSwitch;
    /**
     * 物流发货通知云图梭标题
     */
    @Value("${push.logistics.send.title:}")
    private String pushLogisticsSendTitle;
    /**
     * 物流发货通知云图梭模板
     */
    @Value("${push.logistics.send.template:}")
    private String pushLogisticsSendTemplate;

    /**
     * 物流签收通知云图梭开关
     */
    @Value("${push.logistics.sign.switch:false}")
    private boolean pushLogisticsSignSwitch;
    /**
     * 物流签收通知云图梭标题
     */
    @Value("${push.logistics.sign.title:}")
    private String pushLogisticsSignTitle;
    /**
     * 物流签收通知云图梭模板
     */
    @Value("${push.logistics.sign.template:}")
    private String pushLogisticsSignTemplate;
    @Resource
    private SendCommonHandler sendCommonHandler;

    //物流轨迹缓存key
    private static final String  DELIVERY_PATH_CACHE_KEY = "logistics::delivery_path_cache_key::";

    //订单状态缓存key
    private static final String ORDER_STATUS_MAPPING_KEY = "logistics::order_status_mapping";

    //运单预计送达时间缓存key
    private static final String DELIVERY_NO_TIME_CACHE_KEY = "logistics::delivery_no_time::";
    //物流轨迹缓存过期时间,天
    private static final long DELIVERY_PATH_CACHE_EXPIRED_TIME_DAYS = 60;

    //物流轨迹缓存过期时间,4小时
    private static final long DELIVERY_PATH_CACHE_EXPIRED_TIME_HOURS = 4;

    //物流轨迹缓存过期分钟,120分钟
    private static final long DELIVERY_PATH_CACHE_EXPIRED_TIME_MINUTES = 2 * 60;

    /**
     * 获取物流轨迹缓存keyP
     * @return
     */
    public String getDeliveryPathCacheKey(String childOrderId,String deliveryNo){
        return StrUtil.concat(true,DELIVERY_PATH_CACHE_KEY,childOrderId,"::",deliveryNo);
    }

    /**
     * 获取物流轨迹过期时间
     */
    public long getDeliveryPathCacheExpiredTime(TimeUnit timeUnit){
        return Optional.ofNullable(timeUnit).map(x -> {
            if(x.equals(TimeUnit.HOURS)){
                return DELIVERY_PATH_CACHE_EXPIRED_TIME_HOURS;
            }else if(x.equals(TimeUnit.DAYS)){
                return DELIVERY_PATH_CACHE_EXPIRED_TIME_DAYS;
            }else if(x.equals(TimeUnit.MINUTES)){
                return DELIVERY_PATH_CACHE_EXPIRED_TIME_MINUTES;
            }
            return DELIVERY_PATH_CACHE_EXPIRED_TIME_HOURS;
        }).orElse(DELIVERY_PATH_CACHE_EXPIRED_TIME_HOURS);
    }

    abstract CompanyCodeEnum getSourceCompanyCode();

    /**
     * 物流api下单
     * @return
     */
    abstract AddOrderApiResData addApiOrder(AddOrderApiReqData addOrderApiReqData);

    /**
     * 物流api取消运单
     */
    abstract CancelOrderResData cancelApiOrder(CancelOrderReqData cancelOrderReqData);

    /**
     * 实时查询物流轨迹
     * @return
     */
    abstract DeliveryPathResData getLatestDeliveryPath(DeliveryPathReqData deliveryPathReqData);

    /**
     * 查询物流送达时间
     */
    abstract String getApiDeliverTime(DeliverTimeReqData req);

    /**
     * 面单打印
     * @return
     */
    abstract DeliveryPrintResData getApiPrintData(DeliveryPrintReqData deliveryPrintReqData) throws LopException, UnsupportedEncodingException;

    /**
     * 通用下单
     * @param addOrderReqData
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addOrder(AddOrderListReqData addOrderReqData,BaseReqData baseReqData){
        log.info("物流下单，请求参数 = {}", JSON.toJSONString(addOrderReqData));
        //校验并获取订单、商品信息
        Pair<List<LogisticsOrderGoods>,LogisticsOrder> goodsAndOrderPair = checkAndGetOrderGoodsEntities(addOrderReqData.getOrderNo(),addOrderReqData.getGoodsIds(),baseReqData.getShopIds());

        //校验并获取发货地址
        LogisticsShipAddress logisticsShipAddress = checkAndGetSenderAddress(addOrderReqData);

        //校验并获取商户配置信息
        ShopLogisticsDTO shopLogisticsDTO = checkAndGetShopLogisticsConfig(addOrderReqData.getShopLogisticsId());

        //构造运单信息
        LogisticsDelivery deliveryEntity = buildSaveOrUpdateDeliveryEntity(addOrderReqData, goodsAndOrderPair.getKey(), goodsAndOrderPair.getValue(),logisticsShipAddress,shopLogisticsDTO,baseReqData.getAmUserInfo());

        //构造api下单参数
        AddOrderApiReqData addOrderApiReqData = buildAddOrderApiReqData(addOrderReqData,goodsAndOrderPair.getKey(), goodsAndOrderPair.getValue(), deliveryEntity,logisticsShipAddress,shopLogisticsDTO);

        //物流api发货，生成delivery_no
        AddOrderApiResData addOrderApiResData = addApiOrder(addOrderApiReqData);

        //保存或更新运单信息
        saveOrUpdateDeliveryEntity(addOrderApiResData,deliveryEntity);

        //物流轨迹订阅
        subLogisticsPath(SubLogisticsPathReqData.builder().companyCode(addOrderReqData.getCompanyCode()).deliverNo(addOrderReqData.getDeliveryNo()).build());

        //更新订单商品绑定运单id
        updateLogisticsGoods(addOrderReqData, deliveryEntity,baseReqData.getAmUserInfo());

        //更新物流订单状态，部分发货、已发货
        updateLogisticsOrder(baseReqData.getAmUserInfo(),goodsAndOrderPair.getValue(),deliveryEntity.getGoodsIds());

        //发送物流状态变更消息
        sendLogisticsStatusChangeMq(goodsAndOrderPair.getValue().getChildOrderId(),goodsAndOrderPair.getValue().getId(), OrderStatusEnum.WAIT_RECEIPT);

        //发送发货通知，sms&&push
        pushLogisticsSendNotify(goodsAndOrderPair.getValue(),goodsAndOrderPair.getKey(),deliveryEntity);

        return true;
    }

    /**
     * 发货消息通知
     */
    protected void pushLogisticsSendNotify(LogisticsOrder logisticsOrder, List<LogisticsOrderGoods> orderGoodsEntityList, LogisticsDelivery deliveryEntity) {
        ThreadUtil.execute(() ->{
            SellTypeEnum sellTypeEnum = SellTypeEnum.fromChannelCode(logisticsOrder.getBizChannelCode());
            if (SellTypeEnum.RETAIL == sellTypeEnum) {
                LogisticsDeliveryCompany deliveryCompanyEntity = logisticsDeliveryCompanyService.getOne(Wrappers.<LogisticsDeliveryCompany>lambdaQuery().in(LogisticsDeliveryCompany::getCompanyCode, deliveryEntity.getCompanyCode()).last(" limit 1"));
                TouchMqMessageDTO touchMqMessageDTO = new TouchMqMessageDTO();
                touchMqMessageDTO.setMessageSystemCode(TouchSystemCodeEnum.C10_LOGISTICS.getCode());
                touchMqMessageDTO.setTouchTaskCode(SpringUtil.getProperty("logistics.delivery.send.taskCode"));
                touchMqMessageDTO.setTouchTaskModeCode(TouchSmsTypeEnum.SHORT_MESSAGE.getTouchType());
                Map<String, String> smsParamsMap = new HashMap(32);
                smsParamsMap.put(TouchCustomParamEnum.LONGFOR_USERID.getCode(),Optional.ofNullable(logisticsOrder).map(LogisticsOrder::getUserId).orElse(null));
                smsParamsMap.put(TouchCustomParamEnum.CYCLE_TIMEOUT.getCode(),SpringUtil.getProperty("logistics.delivery.cycleTimeout"));
                smsParamsMap.put(TouchCustomParamEnum.CYCLE_UNIT.getCode(),SpringUtil.getProperty("logistics.delivery.cycleUnit"));
                smsParamsMap.put(TouchCustomParamEnum.MAX_SEND_NUM.getCode(),SpringUtil.getProperty("logistics.delivery.maxSendNum"));
                smsParamsMap.put(TocuhSmsParamEnum.PARAM_ONE.getCode(), pushSmsUtil.handelGoodsName(orderGoodsEntityList));
                smsParamsMap.put(TocuhSmsParamEnum.PARAM_TWO.getCode(), Optional.ofNullable(deliveryCompanyEntity).map(LogisticsDeliveryCompany::getCompanyName).orElse(""));
                smsParamsMap.put(TocuhSmsParamEnum.PARAM_THREE.getCode(), deliveryEntity.getDeliveryNo());
                touchMqMessageDTO.setCustomParam(smsParamsMap);
                log.info("物流零售发货短信通知，发送参数 = {}", JSON.toJSONString(touchMqMessageDTO));
                if(smsLogisticsSendSwitch){
                    List<Object> userIds = new ArrayList<>();
                    userIds.add(Optional.of(logisticsOrder).map(LogisticsOrder::getUserId).orElse(null));

                    Map<String, Object> configParams = new HashMap<>();
                    configParams.put("goodsName", pushSmsUtil.handelGoodsName(orderGoodsEntityList));
                    configParams.put("companyName", Optional.ofNullable(deliveryCompanyEntity).map(LogisticsDeliveryCompany::getCompanyName).orElse(""));
                    configParams.put("deliveryNo", deliveryEntity.getDeliveryNo());
                    sendCommonHandler.sendLmIdSms(configParams, userIds, smsLogisticsSendTemplateId);
                } else {
                    logisticsTouchProducer.send(Arrays.asList(touchMqMessageDTO));
                }

                TouchMqMessageDTO touchMqMessageDTO1 = new TouchMqMessageDTO();
                touchMqMessageDTO1.setMessageSystemCode(TouchSystemCodeEnum.C10_LOGISTICS.getCode());
                touchMqMessageDTO1.setTouchTaskCode(SpringUtil.getProperty("logistics.delivery.send.push.taskCode"));
                touchMqMessageDTO1.setTouchTaskModeCode(TouchSmsTypeEnum.PUSSH.getTouchType());
                Map<String, String> pushParamsMap = new HashMap(32);
                pushParamsMap.put(TouchCustomParamEnum.YUNTUSUO_TARGET.getCode(),Optional.ofNullable(pushSmsUtil.getOaNumber(logisticsOrder.getUserId(),null)).orElse(null));
                pushParamsMap.put(TouchCustomParamEnum.CYCLE_TIMEOUT.getCode(),SpringUtil.getProperty("logistics.delivery.cycleTimeout"));
                pushParamsMap.put(TouchCustomParamEnum.CYCLE_UNIT.getCode(),SpringUtil.getProperty("logistics.delivery.cycleUnit"));
                pushParamsMap.put(TouchCustomParamEnum.MAX_SEND_NUM.getCode(),SpringUtil.getProperty("logistics.delivery.maxSendNum"));
                pushParamsMap.put(TocuhSmsParamEnum.PARAM_ONE.getCode(), pushSmsUtil.handelGoodsName(orderGoodsEntityList));
                pushParamsMap.put(TocuhSmsParamEnum.PARAM_TWO.getCode(), Optional.ofNullable(deliveryCompanyEntity).map(LogisticsDeliveryCompany::getCompanyName).orElse(""));
                pushParamsMap.put(TocuhSmsParamEnum.PARAM_THREE.getCode(), deliveryEntity.getDeliveryNo());
                pushParamsMap.put(TocuhSmsParamEnum.PARAM_FOUR.getCode(), logisticsOrder.getOrderId());
                pushParamsMap.put(TocuhSmsParamEnum.PARAM_FIVE.getCode(), logisticsOrder.getChildOrderId());
                touchMqMessageDTO1.setCustomParam(pushParamsMap);
                log.info("物流零售发货Push通知，发送参数 = {}", JSON.toJSONString(touchMqMessageDTO1));
                if(pushLogisticsSendSwitch){
                    List<Object> userIds = new ArrayList<>();
                    String oaNumber = pushSmsUtil.getOaNumber(logisticsOrder.getUserId(),null);
                    log.info("根据lmId-{}获取oaNumber-{}", logisticsOrder.getUserId(), oaNumber);
                    if(StringUtils.isNotBlank(oaNumber)){
                        userIds.add(oaNumber);
                        String content = pushLogisticsSendTemplate;
                        content = content.replace(TocuhSmsParamEnum.PARAM_ONE.getCode(), pushSmsUtil.handelGoodsName(orderGoodsEntityList));
                        content = content.replace(TocuhSmsParamEnum.PARAM_TWO.getCode(), Optional.ofNullable(deliveryCompanyEntity).map(LogisticsDeliveryCompany::getCompanyName).orElse(""));
                        content = content.replace(TocuhSmsParamEnum.PARAM_THREE.getCode(), deliveryEntity.getDeliveryNo());
                        sendCommonHandler.sendPushSingleCard(userIds, buildLogisticsSignOrSend(content, null, pushLogisticsSendTitle));
                    }
                } else {
                    logisticsTouchProducer.send(Arrays.asList(touchMqMessageDTO1));
                }
            } else if (SellTypeEnum.WHOLESALE == sellTypeEnum && StringUtils.isNotEmpty(logisticsOrder.getReceiptPhone())) {
                LogisticsDeliveryCompany deliveryCompanyEntity = logisticsDeliveryCompanyService.getOne(Wrappers.<LogisticsDeliveryCompany>lambdaQuery().in(LogisticsDeliveryCompany::getCompanyCode, deliveryEntity.getCompanyCode()).last(" limit 1"));
                TouchMqMessageDTO touchMqMessageDTO = new TouchMqMessageDTO();
                touchMqMessageDTO.setMessageSystemCode(TouchSystemCodeEnum.C10_LOGISTICS.getCode());
                touchMqMessageDTO.setTouchTaskCode(SpringUtil.getProperty("logistics.delivery.send.taskCode"));
                touchMqMessageDTO.setTouchTaskModeCode(TouchSmsTypeEnum.SHORT_MESSAGE.getTouchType());
                Map<String, String> smsParamsMap = new HashMap(32);
                smsParamsMap.put(TouchCustomParamEnum.PAAS_PHONE.getCode(), logisticsOrder.getReceiptPhone());
                smsParamsMap.put(TouchCustomParamEnum.CYCLE_TIMEOUT.getCode(),SpringUtil.getProperty("logistics.delivery.cycleTimeout"));
                smsParamsMap.put(TouchCustomParamEnum.CYCLE_UNIT.getCode(),SpringUtil.getProperty("logistics.delivery.cycleUnit"));
                smsParamsMap.put(TouchCustomParamEnum.MAX_SEND_NUM.getCode(),SpringUtil.getProperty("logistics.delivery.maxSendNum"));
                smsParamsMap.put(TocuhSmsParamEnum.PARAM_ONE.getCode(), pushSmsUtil.handelGoodsName(orderGoodsEntityList));
                smsParamsMap.put(TocuhSmsParamEnum.PARAM_TWO.getCode(), Optional.ofNullable(deliveryCompanyEntity).map(LogisticsDeliveryCompany::getCompanyName).orElse(""));
                smsParamsMap.put(TocuhSmsParamEnum.PARAM_THREE.getCode(), deliveryEntity.getDeliveryNo());
                touchMqMessageDTO.setCustomParam(smsParamsMap);
                log.info("物流批发发货短信通知，发送参数 = {}", JSON.toJSONString(touchMqMessageDTO));
                if(smsLogisticsSendSwitch){
                    List<Object> userPhones = new ArrayList<>();
                    userPhones.add(logisticsOrder.getReceiptPhone());

                    Map<String, Object> configParams = new HashMap<>();
                    configParams.put("goodsName", pushSmsUtil.handelGoodsName(orderGoodsEntityList));
                    configParams.put("companyName", Optional.ofNullable(deliveryCompanyEntity).map(LogisticsDeliveryCompany::getCompanyName).orElse(""));
                    configParams.put("deliveryNo", deliveryEntity.getDeliveryNo());
                    sendCommonHandler.sendSms(configParams, userPhones, smsLogisticsSendTemplateId);
                } else {
                    logisticsTouchProducer.send(Arrays.asList(touchMqMessageDTO));
                }
            }
        });
    }

    /**
     * 签收消息通知
     */
    protected void pushLogisticsSignedNotify(LogisticsOrder logisticsOrder, List<LogisticsOrderGoods> orderGoodsEntityList, LogisticsDelivery deliveryEntity) {
        //异步短信通知
        ThreadUtil.execute(() -> {
            LogisticsDeliveryCompany deliveryCompanyEntity = logisticsDeliveryCompanyService.getOne(Wrappers.<LogisticsDeliveryCompany>lambdaQuery().in(LogisticsDeliveryCompany::getCompanyCode, deliveryEntity.getCompanyCode()).last(" limit 1"));
            TouchMqMessageDTO touchMqMessageDTO = new TouchMqMessageDTO();
            touchMqMessageDTO.setMessageSystemCode(TouchSystemCodeEnum.C10_LOGISTICS.getCode());
            touchMqMessageDTO.setTouchTaskCode(SpringUtil.getProperty("logistics.delivery.signd.taskCode"));
            touchMqMessageDTO.setTouchTaskModeCode(TouchSmsTypeEnum.SHORT_MESSAGE.getTouchType());
            Map<String, String> smsParamsMap = new HashMap();
            smsParamsMap.put(TouchCustomParamEnum.LONGFOR_USERID.getCode(),Optional.ofNullable(logisticsOrder).map(LogisticsOrder::getUserId).orElse(null));
            smsParamsMap.put(TouchCustomParamEnum.CYCLE_TIMEOUT.getCode(), SpringUtil.getProperty("logistics.delivery.cycleTimeout"));
            smsParamsMap.put(TouchCustomParamEnum.CYCLE_UNIT.getCode(),SpringUtil.getProperty("logistics.delivery.cycleUnit"));
            smsParamsMap.put(TouchCustomParamEnum.MAX_SEND_NUM.getCode(),SpringUtil.getProperty("logistics.delivery.maxSendNum"));
            smsParamsMap.put(TocuhSmsParamEnum.PARAM_ONE.getCode(),pushSmsUtil.handelGoodsName(orderGoodsEntityList));
            smsParamsMap.put(TocuhSmsParamEnum.PARAM_TWO.getCode(), Optional.ofNullable(deliveryCompanyEntity).map(LogisticsDeliveryCompany::getCompanyName).orElse(""));
            smsParamsMap.put(TocuhSmsParamEnum.PARAM_THREE.getCode(), deliveryEntity.getDeliveryNo());
            touchMqMessageDTO.setCustomParam(smsParamsMap);
            log.info("物流签收短信通知，发送参数 = {}", JSON.toJSONString(touchMqMessageDTO));
            if(smsLogisticsSignSwitch){
                List<Object> userIds = new ArrayList<>();
                userIds.add(Optional.ofNullable(logisticsOrder).map(LogisticsOrder::getUserId).orElse(null));

                Map<String, Object> configParams = new HashMap<>();
                configParams.put("goodsName", pushSmsUtil.handelGoodsName(orderGoodsEntityList));
                configParams.put("companyName", Optional.ofNullable(deliveryCompanyEntity).map(LogisticsDeliveryCompany::getCompanyName).orElse(""));
                configParams.put("deliveryNo", deliveryEntity.getDeliveryNo());
                sendCommonHandler.sendLmIdSms(configParams, userIds, smsLogisticsSignTemplateId);
            } else {
                logisticsTouchProducer.send(Arrays.asList(touchMqMessageDTO));
            }


            TouchMqMessageDTO touchMqMessageDTO1 = new TouchMqMessageDTO();
            touchMqMessageDTO1.setMessageSystemCode(TouchSystemCodeEnum.C10_LOGISTICS.getCode());
            touchMqMessageDTO1.setTouchTaskCode(SpringUtil.getProperty("logistics.delivery.signd.push.taskCode"));
            touchMqMessageDTO1.setTouchTaskModeCode(TouchSmsTypeEnum.PUSSH.getTouchType());
            Map<String, String> pushParamsMap = new HashMap(32);
            pushParamsMap.put(TouchCustomParamEnum.YUNTUSUO_TARGET.getCode(),Optional.ofNullable(pushSmsUtil.getOaNumber(logisticsOrder.getUserId(),null)).orElse(null));
            pushParamsMap.put(TouchCustomParamEnum.CYCLE_TIMEOUT.getCode(), SpringUtil.getProperty("logistics.delivery.cycleTimeout"));
            pushParamsMap.put(TouchCustomParamEnum.CYCLE_UNIT.getCode(),SpringUtil.getProperty("logistics.delivery.cycleUnit"));
            pushParamsMap.put(TouchCustomParamEnum.MAX_SEND_NUM.getCode(),SpringUtil.getProperty("logistics.delivery.maxSendNum"));
            pushParamsMap.put(TocuhSmsParamEnum.PARAM_ONE.getCode(), pushSmsUtil.handelGoodsName(orderGoodsEntityList));
            pushParamsMap.put(TocuhSmsParamEnum.PARAM_TWO.getCode(), Optional.ofNullable(deliveryCompanyEntity).map(LogisticsDeliveryCompany::getCompanyName).orElse(""));
            pushParamsMap.put(TocuhSmsParamEnum.PARAM_THREE.getCode(), deliveryEntity.getDeliveryNo());
            pushParamsMap.put(TocuhSmsParamEnum.PARAM_FOUR.getCode(), logisticsOrder.getOrderId());
            pushParamsMap.put(TocuhSmsParamEnum.PARAM_FIVE.getCode(), logisticsOrder.getChildOrderId());
            touchMqMessageDTO1.setCustomParam(pushParamsMap);
            log.info("物流签收Push通知，发送参数 = {}", JSON.toJSONString(touchMqMessageDTO1));
            if(pushLogisticsSignSwitch){
                List<Object> userIds = new ArrayList<>();
                String oaNumber = pushSmsUtil.getOaNumber(logisticsOrder.getUserId(),null);
                log.info("根据lmId-{}获取oaNumber-{}", logisticsOrder.getUserId(), oaNumber);
                if(StringUtils.isNotBlank(oaNumber)){
                    userIds.add(oaNumber);
                    String content = pushLogisticsSignTemplate;
                    content = content.replace(TocuhSmsParamEnum.PARAM_ONE.getCode(), pushSmsUtil.handelGoodsName(orderGoodsEntityList));
                    content = content.replace(TocuhSmsParamEnum.PARAM_TWO.getCode(), Optional.ofNullable(deliveryCompanyEntity).map(LogisticsDeliveryCompany::getCompanyName).orElse(""));
                    content = content.replace(TocuhSmsParamEnum.PARAM_THREE.getCode(), deliveryEntity.getDeliveryNo());
                    sendCommonHandler.sendPushSingleCard(userIds, buildLogisticsSignOrSend(content, null, pushLogisticsSignTitle));
                }
            } else {
                logisticsTouchProducer.send(Arrays.asList(touchMqMessageDTO1));
            }
        });
    }
    private TouchBotCardMessageBO buildLogisticsSignOrSend(String content, String jumpUrl, String title){
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
    public LogisticsDelivery checkCanCancelOrder(CancelOrderReqData cancelOrderReqData){
        try{
            log.info("校验运单是否可以取消，childOrderId = {},deliveryNo = {}",cancelOrderReqData.getOrderId(),cancelOrderReqData.getDeliveryId());
            //校验并获取运单信息
            LogisticsDelivery logisticsDelivery = getAndCheckDelivery(Long.parseLong(cancelOrderReqData.getOrderId()));

            //校验物流状态
            checkLogisticsDeliveryStatusForCancel(logisticsDelivery.getLogisticsStatus());

            //调用快递公司取消api取消订单
            CancelOrderResData cancelOrderResData = cancelApiOrder(cancelOrderReqData);

            //校验api取消状态
            Optional.ofNullable(cancelOrderResData)
                    .map(CancelOrderResData::getCode)
                    .filter(code -> checkCancelSuccess(getSourceCompanyCode(),code))
                    .orElseThrow(() -> new BusinessException("api取消运单失败"));
            return logisticsDelivery;
        }catch (Exception ex){
            log.error("校验运单是否可以取消，取消失败",ex);
            return null;
        }
    }

    /**
     * 通用取消流程
     * @param cancelOrderReqData 取消参数
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(CancelOrderReqData cancelOrderReqData){
        LogisticsDelivery logisticsDelivery = Optional.ofNullable(checkCanCancelOrder(cancelOrderReqData)).orElseThrow(() -> new BusinessException("取消运单失败"));
        //修改订单、运单、商品状态
        boolean isAllGoodsCancel = handelCancelDataStatus(logisticsDelivery, cancelOrderReqData.getBusinessTypeEnum());
        if(isAllGoodsCancel){
            sendLogisticsStatusChangeMq(cancelOrderReqData.getOrderId(),logisticsDelivery.getLogisticsOrderId(), OrderStatusEnum.WAIT_DELIVER);
        }
    }

    /**
     * 取消运单修改订单、运单、商品、状态
     * @param logisticsDelivery 运单信息
     * @param businessTypeEnum 业务类型
     * @return  是否全部取消
     */
    protected boolean handelCancelDataStatus(LogisticsDelivery logisticsDelivery, BusinessTypeEnum businessTypeEnum) {
        // 根据物流单ID获取所有商品
        List<LogisticsOrderGoods> logisticsOrderGoods = logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                .eq(LogisticsOrderGoods::getLogisticsDeliveryId,logisticsDelivery.getId())
                .eq(LogisticsOrderGoods::getDeleteStatus, DeleteStatusEnum.NO.getCode()));
        // 记录物流单历史商品ID
        logisticsDelivery.setIfCancel(1);
        logisticsDelivery.setUpdateTime(new Date());
        logisticsDelivery.setRemark(businessTypeEnum.getDesc());
        logisticsDeliveryService.updateById(logisticsDelivery);
        List<LogisticsOrderGoods> logisticsOrderGoodsUpdateList = ListUtils.emptyIfNull(logisticsOrderGoods).stream().map(goodsInfo -> {
            goodsInfo.setLogisticsDeliveryId(null);
            goodsInfo.setUpdateTime(DateUtil.date());
            goodsInfo.setUpdateAccount("system");
            goodsInfo.setUpdateName("system");
            //商户端取消，商品业务类型记录为商户取消
            if (Objects.nonNull(businessTypeEnum) && BusinessTypeEnum.MERCHANT_CANCEL.getCode().equals(businessTypeEnum.getCode())) {
                goodsInfo.setBusinessType(businessTypeEnum.getCode());
                goodsInfo.setRemark(businessTypeEnum.getDesc());
            } else if (Objects.nonNull(businessTypeEnum) && BusinessTypeEnum.REFUND_GOODS.getCode().equals(businessTypeEnum.getCode())) {
                //用户退货退款记录为再次发货
                goodsInfo.setBusinessType(BusinessTypeEnum.SEND_AGAIN.getCode());
                goodsInfo.setRemark("由于商品退单，运单取消，商品需再次发货");
            }
            return goodsInfo;

        }).collect(Collectors.toList());
        logisticsOrderGoodsService.updateBatchById(logisticsOrderGoodsUpdateList);
        log.info("通用取消发货流程，更新商品logisticsDeliveryId = null结束，goodsIds = {}",logisticsOrderGoodsUpdateList.stream().map(LogisticsOrderGoods::getId).map(String::valueOf).collect(Collectors.joining(",")));

        LogisticsOrder logisticsOrder = checkAndGetLogisticsOrder(logisticsDelivery.getLogisticsOrderId());
        log.info("通用取消发货流程，查询订单信息，logisticsOrder = {}",JSON.toJSONString(logisticsOrder));
        boolean isUndelivered = false;
        // 否则查看订单下商品是否所有都是未发货，如果不是，则为部分发货，否则为未发货
        int num = logisticsOrderGoodsService.count(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                .eq(LogisticsOrderGoods::getLogisticsOrderId,logisticsDelivery.getLogisticsOrderId())
                .isNotNull(LogisticsOrderGoods::getLogisticsDeliveryId)
                .eq(LogisticsOrderGoods::getDeleteStatus,DeleteStatusEnum.NO.getCode()));
        if (num > 0) {
            log.info("通用取消发货流程，设置订单部分发货，logisticsOrderId = {}",logisticsDelivery.getLogisticsOrderId());
            // 部分发货
            logisticsOrder.setGoodsOrderStatus(GoodsOrderStatusEnum.SECTION_DELIVERED.getCode());
        } else {
            // 未发货
            log.info("通用取消发货流程，设置订单未发货，logisticsOrderId = {}",logisticsDelivery.getLogisticsOrderId());
            logisticsOrder.setGoodsOrderStatus(GoodsOrderStatusEnum.UNDELIVERED.getCode());
            isUndelivered = true;
        }
        logisticsOrderService.updateById(logisticsOrder);
        log.info("通用取消发货流程，订单状态是否变更，result = {}",isUndelivered);
        return isUndelivered;
    }

    /**
     * 查询物流轨迹
     */
    @Override
    public DeliveryPathResData getDeliveryPath(DeliveryPathReqData deliveryPathReqData) {
        log.info("查询物流轨迹，调用参数 = {}",JSON.toJSONString(deliveryPathReqData));
        String deliveryPathCacheValue = redisTemplate.opsForValue().get(getDeliveryPathCacheKey(deliveryPathReqData.getLogisticsOrderId(),deliveryPathReqData.getWaybillId()));
        if(!deliveryPathReqData.isQueryLatest() && StringUtils.isNotBlank(deliveryPathCacheValue)){
            log.info("查询物流轨迹，缓存查询");
            DeliveryPathResData deliveryPathResData = JSONObject.parseObject(deliveryPathCacheValue,DeliveryPathResData.class);
            log.info("查询物流轨迹，查询缓存结果 = {}",JSON.toJSONString(deliveryPathResData));
            //校验物流状态变更
            checkLogisticsStatusChange(deliveryPathReqData.getLogisticsOrderId(),deliveryPathReqData.getWaybillId(),deliveryPathResData);
            return deliveryPathResData;
        }
        log.info("查询物流轨迹，开始实时查询");
        //调用物流服务查询实时物流轨迹
        DeliveryPathResData latestDeliveryPath = getLatestDeliveryPath(deliveryPathReqData);
        if(Objects.isNull(latestDeliveryPath) || CollectionUtils.isEmpty(latestDeliveryPath.getPathItemList())){
            log.info("查询物流轨迹，实时查询，结果为空");
            cacheNoDataDeliveryPath(deliveryPathReqData.getLogisticsOrderId(),deliveryPathReqData);
            return latestDeliveryPath;
        }
        log.info("查询物流轨迹，实时查询，pathStatus = {}",latestDeliveryPath.getPathState());
        //物流状态
        Pair<Integer,Integer> newStatusPair = Optional.ofNullable(getFromPathState(latestDeliveryPath.getPathState())).orElseGet(() ->{
            log.info("查询物流轨迹，实时查询，状态映射表关系不存在,sourceCompanyCode= {},pathState = {}",latestDeliveryPath.getSourceCompanyCode(),latestDeliveryPath.getPathState());
            return new Pair(null,null);
        });
        log.info("查询物流轨迹，实时查询，最新物流状态，logisticsStatus = {},logisticsOrderStatus = {}",newStatusPair.getKey(),newStatusPair.getValue());
        latestDeliveryPath.setLogisticsStatus(newStatusPair.getKey());
        latestDeliveryPath.setOrderStatus(newStatusPair.getValue());
        //缓存物流轨迹
        cacheDeliveryPath(deliveryPathReqData.getLogisticsOrderId(),latestDeliveryPath);
        //校验物流状态变更
        checkLogisticsStatusChange(deliveryPathReqData.getLogisticsOrderId(),deliveryPathReqData.getWaybillId(),latestDeliveryPath);
        return latestDeliveryPath;
    }
    //校验物流状态变更
    private void checkLogisticsStatusChange(String logisticsOrderId,String waybillId, DeliveryPathResData latestDeliveryPath) {
        log.info("查询物流轨迹，校验物流状态变更");
        if(StringUtils.isBlank(logisticsOrderId)
                || StringUtils.isBlank(waybillId)
                || Objects.isNull(latestDeliveryPath)
                || Objects.isNull(latestDeliveryPath.getLogisticsStatus())
                || CollectionUtils.isEmpty(latestDeliveryPath.getPathItemList())){
            log.info("查询物流轨迹，校验物流状态变更，物流订单id:{}、运单号:{}、轨迹物流状态:{}、轨迹数据其中有空，不处理",logisticsOrderId,waybillId,Optional.ofNullable(latestDeliveryPath).map(DeliveryPathResData::getLogisticsStatus).orElse(null));
            return;
        }
        LogisticsOrder logisticsOrder = logisticsOrderService.getOne(Wrappers.<LogisticsOrder>lambdaQuery().eq(LogisticsOrder::getId, Long.parseLong(logisticsOrderId))
                .eq(LogisticsOrder::getDeleteStatus, DeleteStatusEnum.NO.getCode()).last(" limit 1"));
        if(Objects.isNull(logisticsOrder)){
            log.info("查询物流轨迹，校验物流状态变更，有效订单信息为空，logisticsOrderId = {}",logisticsOrderId);
            return;
        }
        LogisticsDelivery logisticsDelivery = logisticsDeliveryService.getOne(Wrappers.<LogisticsDelivery>lambdaQuery()
                .eq(LogisticsDelivery::getLogisticsOrderId, logisticsOrder.getId())
                .eq(LogisticsDelivery::getDeliveryNo, waybillId)
                .eq(LogisticsDelivery::getDeleteStatus, DeleteStatusEnum.NO.getCode())
                .eq(LogisticsDelivery::getIfCancel, 0).last(" limit 1"));
        if(Objects.isNull(logisticsDelivery)){
            log.info("查询物流轨迹，校验物流状态变更，有效运单信息为空，logisticsOrderId = {},logisticsOrderId = {},waybillId = {}",logisticsOrderId,logisticsOrder.getChildOrderId(),waybillId);
            return;
        }
        Integer oldLogisticsDeliveryStatus = logisticsDelivery.getLogisticsStatus();
        Integer oldLogisticsOrderStatus = logisticsOrder.getGoodsOrderStatus();
        log.info("查询物流轨迹，校验物流状态变更，老物流状态，logisticsStatus = {},logisticsOrderStatus = {}",logisticsDelivery.getLogisticsStatus(),logisticsOrder.getGoodsOrderStatus());
        if(oldLogisticsDeliveryStatus.equals(DeliveryLogisticsStatusEnum.SIGNED.getCode()) && oldLogisticsOrderStatus.equals(OrderStatusEnum.IS_SIGNED.getCode())){
            return;
        }
        //取最新一条物流轨迹的时间节点状态
        Long latestPathTime = Optional.ofNullable(latestDeliveryPath).map(path -> ListUtils.emptyIfNull(path.getPathItemList()).stream().findFirst().orElse(null)).map(DeliveryPathResData.PathItem::getPathTime).orElse(null);
        if(!latestDeliveryPath.getLogisticsStatus().equals(oldLogisticsDeliveryStatus)){
            log.info("查询物流轨迹，校验物流状态变更，物流状态变化，oldLogisticsStatus = {},newLogisticsStatus = {}",logisticsDelivery.getLogisticsStatus(),latestDeliveryPath.getLogisticsStatus());
            LogisticsDelivery deliveryEntity = new LogisticsDelivery();
            deliveryEntity.setId(logisticsDelivery.getId());
            deliveryEntity.setLogisticsStatus(latestDeliveryPath.getLogisticsStatus());
            deliveryEntity.setUpdateAccount(CommonConstant.SYSTEM_USER);
            deliveryEntity.setUpdateName(CommonConstant.SYSTEM_USER);
            deliveryEntity.setUpdateTime(new Date());
            if(latestDeliveryPath.getLogisticsStatus().equals(DeliveryLogisticsStatusEnum.RECEIVED.getCode())){
                //已揽收
                deliveryEntity.setCollectTime(DateUtil.date(latestPathTime) );
            }
            if(latestDeliveryPath.getLogisticsStatus().equals(DeliveryLogisticsStatusEnum.SIGNED.getCode())){
                //已签收
                deliveryEntity.setSignTime(DateUtil.date(latestPathTime));
            }
            logisticsDeliveryService.updateById(deliveryEntity);
            log.info("查询物流轨迹，校验物流状态变更，更新运单信息完成，deliveryEntity = {}",JSON.toJSONString(deliveryEntity));

            if(latestDeliveryPath.getLogisticsStatus().equals(DeliveryLogisticsStatusEnum.SIGNED.getCode()) && StringUtils.isNotBlank(logisticsDelivery.getGoodsIds())){
                List<Long> logisticsOrderGoodsIds = Arrays.stream(logisticsDelivery.getGoodsIds().split(",")).map(Long::parseLong).collect(Collectors.toList());
                List<LogisticsOrderGoods> logisticsOrderGoodsList = logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                        .in(LogisticsOrderGoods::getId,logisticsOrderGoodsIds)
                        .eq(LogisticsOrderGoods::getDeleteStatus,DeleteStatusEnum.NO.getCode()));
                if(CollectionUtils.isNotEmpty(logisticsOrderGoodsList)){
                    //异步短信签收通知
                    pushLogisticsSignedNotify(logisticsOrder,logisticsOrderGoodsList,logisticsDelivery);
                }
            }
        }
        //判断订单状态是否变化
        if(latestDeliveryPath.getOrderStatus().equals(oldLogisticsOrderStatus)){
            log.info("查询物流轨迹，校验订单状态变更，无变化，oldLogisticsOrderStatus = {},newLogisticsOrderStatus = {}",oldLogisticsOrderStatus,latestDeliveryPath.getOrderStatus());
            return;
        }
        //订单状态已签收
        if(latestDeliveryPath.getOrderStatus().equals(OrderStatusEnum.IS_SIGNED.getCode())){
            log.info("查询物流轨迹，校验订单状态变更，订单状态变化，oldOrderStatus = {},newOrderStatus = {}",logisticsOrder.getGoodsOrderStatus(),latestDeliveryPath.getOrderStatus());
            List<LogisticsOrderGoods> logisticsOrderGoodsList = logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                    .eq(LogisticsOrderGoods::getLogisticsOrderId, logisticsOrder.getId())
                    .eq(LogisticsOrderGoods::getDeleteStatus,DeleteStatusEnum.NO.getCode()));
            if(CollectionUtils.isEmpty(logisticsOrderGoodsList)){
                log.info("查询物流轨迹，校验订单状态变更，订单商品为空，不处理，logisticsOrderId = {}",logisticsOrder.getId());
                return;
            }
            List<Long> noSendGoodsIdList = ListUtils.emptyIfNull(logisticsOrderGoodsList)
                    .stream()
                    .filter(item -> Objects.isNull(item.getLogisticsDeliveryId()))
                    .map(LogisticsOrderGoods::getId)
                    .collect(Collectors.toList());
            //存在未发货的商品，不更新订单状态
            if(CollectionUtils.isNotEmpty(noSendGoodsIdList)){
                log.info("查询物流轨迹，校验订单状态变更，订单状态变化，不处理，存在未发货的商品,logisticsGoodsId = {}",noSendGoodsIdList);
                return;
            }
            //查询订单下的所有运单
            List<LogisticsDelivery> noSinedDeliveryList = logisticsDeliveryService.list(Wrappers.<LogisticsDelivery>lambdaQuery()
                    .eq(LogisticsDelivery::getLogisticsOrderId, logisticsOrder.getId())
                    .eq(LogisticsDelivery::getDeleteStatus, DeleteStatusEnum.NO.getCode())
                    .ne(LogisticsDelivery::getDeliveryNo,waybillId)
                    .ne(LogisticsDelivery::getLogisticsStatus,DeliveryLogisticsStatusEnum.SIGNED.getCode())
                    .eq(LogisticsDelivery::getIfCancel, 0));
            if(CollectionUtils.isNotEmpty(noSinedDeliveryList)){
                log.info("查询物流轨迹，校验订单状态变更，订单状态变化，不处理，存在未签收的运单,deliveryNos = {}",noSinedDeliveryList.stream().map(LogisticsDelivery::getDeliveryNo).collect(Collectors.toList()));
                return;
            }
        }

        OrderStatusChangePushDTO pushDto = new OrderStatusChangePushDTO();
        pushDto.setOrderStatus(latestDeliveryPath.getOrderStatus());
        pushDto.setChildOrderId(logisticsOrder.getChildOrderId());
        pushDto.setLogisticsId(logisticsOrder.getId());
        //正向流程
        pushDto.setLogisticsType(CommonConstant.CONS1);
        if (CommonConstant.CONS2.equals(logisticsOrder.getSendReturn())) {
            pushDto.setLogisticsType(CommonConstant.CONS2);
            pushDto.setReturnOrderId(logisticsOrder.getChildOrderId());
        }
        pushDto.setStatusTime(LocalDateTimeUtil.of(latestPathTime));
        log.info("查询物流轨迹，校验物流状态变更，订单状态变化，发送状态变更消息,pushDto = {}",JSON.toJSONString(pushDto));
        logisticsStatusChangeProducer.send(pushDto);

        //更新logistics_order商品订单状态
        LogisticsOrder logisticsOrderUpdate = new LogisticsOrder();
        logisticsOrderUpdate.setId(logisticsOrder.getId());
        logisticsOrderUpdate.setGoodsOrderStatus(latestDeliveryPath.getOrderStatus());
        logisticsOrderUpdate.setUpdateName(CommonConstant.SYSTEM_USER);
        logisticsOrderUpdate.setUpdateAccount(CommonConstant.SYSTEM_USER);
        logisticsOrderUpdate.setUpdateTime(DateUtil.date());
        logisticsOrderService.updateById(logisticsOrderUpdate);
        log.info("查询物流轨迹，校验物流状态变更，订单状态变化，更新订单状态完成,updateOrderStatus = {}",latestDeliveryPath.getOrderStatus());
    }

    @Override
    public DeliveryPrintResData getPrintData(DeliveryPrintReqData deliveryPrintReqData) throws LopException,UnsupportedEncodingException{
        DeliveryPrintResData deliveryPrintResData = getApiPrintData(deliveryPrintReqData);
        return deliveryPrintResData;
    }

    /**
     * 查询预计送达时间
     * @param deliverTimeReqData
     * @return
     */
    @Override
    public String getDeliverTime(DeliverTimeReqData deliverTimeReqData){
        String chcheKey = new StringBuilder(DELIVERY_NO_TIME_CACHE_KEY).append(deliverTimeReqData.getWaybillId()).toString();
        return Optional.ofNullable(redisTemplate.opsForValue().get(chcheKey))
                .map(String::valueOf)
                .orElseGet(() -> {
                    String deliveryTIme = getApiDeliverTime(deliverTimeReqData);
                    if(StringUtils.isNotBlank(deliveryTIme)){
                        redisTemplate.opsForValue().set(chcheKey,deliveryTIme,24, TimeUnit.HOURS);
                    }
                    return deliveryTIme;
                });
    }


    /**
     * 获取商户物流配置
     * @param shopLogisticsId 商户物流配置id
     * @return ShopLogisticsDTO
     */
    public ShopLogisticsDTO checkAndGetShopLogisticsConfig(Integer shopLogisticsId){
        if(Objects.isNull(shopLogisticsId)){
            return null;
        }
        ShopLogistics shopLogistics = Optional.ofNullable(shopLogisticsId)
                .map(x -> shopLogisticsMapper.selectById(x))
                .orElseThrow(() -> new BusinessException("物流供应商配置信息不存在"));
        return Optional.ofNullable(shopLogisticsConfigMapper.selectOne(Wrappers.<ShopLogisticsConfig>lambdaQuery().eq(ShopLogisticsConfig::getId,shopLogistics.getShopLogisticsConfigId()).last(" limit 1")))
                .map(x -> new ShopLogisticsDTO(shopLogisticsId,x.getLogisticsCode(),x.getLogisticsName(),x.getTypeCode(),x.getTypeName(),x.getUseDefault() != 1 ? shopLogistics.getAccount() : x.getAccount(),x.getUseDefault() != 1 ? shopLogistics.getAppKey() :x.getAppKey(),x.getUseDefault() != 1 ? shopLogistics.getExpressType() : shopLogistics.getExpressType()))
                .orElseThrow(() -> new BusinessException("物流供应商配置模版信息不存在"));
    }

    /**
     * 无轨迹结果缓存
     * @return
     */
    protected void cacheNoDataDeliveryPath(String logisticsOrderId,DeliveryPathReqData deliveryPathReqData){
        try{
            log.info("查询物流轨迹，查询结果为空，开始缓存无轨迹结果，logisticsOrderId = {},companyCode = {},deliveryNo = {}",logisticsOrderId,deliveryPathReqData.getCompanyCode(),deliveryPathReqData.getWaybillId());
            DeliveryPathResData deliveryPathResData = new DeliveryPathResData();
            deliveryPathResData.setWaybillId(deliveryPathReqData.getWaybillId());
            deliveryPathResData.setCompanyCode(deliveryPathReqData.getCompanyCode());
            deliveryPathResData.setPathItemList(null);
            redisTemplate.opsForValue().set(getDeliveryPathCacheKey(logisticsOrderId,deliveryPathResData.getWaybillId()),JSON.toJSONString(deliveryPathResData),getDeliveryPathCacheExpiredTime(TimeUnit.MINUTES), TimeUnit.MINUTES);
            log.info("查询物流轨迹，查询结果为空，开始缓存无轨迹结果完成，缓存失效时间 = {}",getDeliveryPathCacheExpiredTime(TimeUnit.MINUTES)+"M");
        }catch (Exception ex){
            log.error("查询物流轨迹，缓存物流轨迹异常",ex);
        }
    }

    /**
     * 缓存物流轨迹到redis
     */
    protected void cacheDeliveryPath(String logisticsOrderId,DeliveryPathResData deliveryPathResData){
        try{
            log.info("查询物流轨迹，开始缓存结果，companyCode = {},pathState = {},logisticsStatus = {}",deliveryPathResData.getCompanyCode(),deliveryPathResData.getPathState(),deliveryPathResData.getLogisticsStatus());
            if(Objects.isNull(deliveryPathResData) || StringUtils.isBlank(deliveryPathResData.getWaybillId()) || CollectionUtils.isEmpty(deliveryPathResData.getPathItemList())){
                log.info("查询物流轨迹，运单或轨迹不存在，无需缓存");
                return;
            }
            if(Objects.nonNull(deliveryPathResData.getLogisticsStatus()) && DeliveryLogisticsStatusEnum.SIGNED.getCode() == deliveryPathResData.getLogisticsStatus()){
                redisTemplate.opsForValue().set(getDeliveryPathCacheKey(logisticsOrderId,deliveryPathResData.getWaybillId()),JSON.toJSONString(deliveryPathResData),getDeliveryPathCacheExpiredTime(TimeUnit.DAYS), TimeUnit.DAYS);
                log.info("查询物流轨迹，缓存物流轨迹成功，缓存失效时间 = {}",getDeliveryPathCacheExpiredTime(TimeUnit.DAYS)+"D");
            }else{
                redisTemplate.opsForValue().set(getDeliveryPathCacheKey(logisticsOrderId,deliveryPathResData.getWaybillId()),JSON.toJSONString(deliveryPathResData),getDeliveryPathCacheExpiredTime(TimeUnit.HOURS), TimeUnit.HOURS);
                log.info("查询物流轨迹，缓存物流轨迹成功，缓存失效时间 = {}",getDeliveryPathCacheExpiredTime(TimeUnit.HOURS)+"H");
            }
        }catch (Exception ex){
            log.error("查询物流轨迹，缓存物流轨迹异常",ex);
        }
    }

    /**
     * 更新物流订单
     */
    protected void updateLogisticsOrder(AmUserInfo userInfo, LogisticsOrder logisticsOrder, String sendGoodsIds){
        LogisticsOrder logisticsOrderUpdate = new LogisticsOrder();
        logisticsOrderUpdate.setId(logisticsOrder.getId());
        boolean isPartSendFlag = ListUtils.emptyIfNull(logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                        .eq(LogisticsOrderGoods::getLogisticsOrderId, logisticsOrder.getId())
                        .eq(LogisticsOrderGoods::getDeleteStatus, DeleteStatusEnum.NO.getCode())))
                .stream()
                .filter(x -> !sendGoodsIds.contains(String.valueOf(x.getId())))
                .filter(x -> Objects.isNull(x.getLogisticsDeliveryId()))
                .findAny()
                .isPresent();
        logisticsOrderUpdate.setGoodsOrderStatus(isPartSendFlag ? OrderStatusEnum.WAIT_RECEIPT.getCode() : GoodsOrderStatusEnum.SECTION_DELIVERED.getCode());
        if(Objects.nonNull(userInfo)){
            logisticsOrderUpdate.setUpdateAccount(userInfo.getUserName());
            logisticsOrderUpdate.setUpdateName(userInfo.getRealName());
        };
        logisticsOrderUpdate.setUpdateTime(DateUtil.date());
        logisticsOrderService.updateById(logisticsOrderUpdate);
    }
    /**
     * 更新订单商品运单id
     * @param addOrderReqData
     * @param deliveryEntity
     */
    protected void updateLogisticsGoods(AddOrderListReqData addOrderReqData, LogisticsDelivery deliveryEntity,AmUserInfo amUserInfo) {
        LogisticsOrderGoods logisticsOrderGoods = new LogisticsOrderGoods();
        logisticsOrderGoods.setLogisticsDeliveryId(deliveryEntity.getId());
        logisticsOrderGoods.setUpdateTime(DateUtil.date());
        Optional.ofNullable(amUserInfo).ifPresent(userInfo -> {
            logisticsOrderGoods.setUpdateAccount(userInfo.getUserName());
            logisticsOrderGoods.setUpdateName(userInfo.getRealName());
        });
        logisticsOrderGoodsService.update(logisticsOrderGoods,new LambdaUpdateWrapper<LogisticsOrderGoods>().in(LogisticsOrderGoods::getId,addOrderReqData.getGoodsIds()));
    }


    private LogisticsDelivery buildSaveOrUpdateDeliveryEntity(AddOrderListReqData addOrderReqData, List<LogisticsOrderGoods> orderGoodsEntityList, LogisticsOrder logisticsOrder, LogisticsShipAddress logisticsShipAddress, ShopLogisticsDTO shopLogisticsDTO, AmUserInfo amUserInfo) {
        //构建运单信息
        LogisticsDelivery logisticsDelivery = buildDeliveryEntity(addOrderReqData, orderGoodsEntityList, logisticsOrder, logisticsShipAddress, shopLogisticsDTO,amUserInfo);
        if(StringUtils.isNotBlank(addOrderReqData.getDeliveryNo())){
            //快递100发货
            LogisticsDelivery existLogisticsDeliver = logisticsDeliveryService.getOne(Wrappers.<LogisticsDelivery>lambdaQuery()
                    .eq(LogisticsDelivery::getDeliveryNo, addOrderReqData.getDeliveryNo())
                    .eq(LogisticsDelivery::getIfCancel, 0)
                    .eq(LogisticsDelivery::getLogisticsOrderId, logisticsOrder.getId())
                    .last(" limit 1"));
            if(Objects.isNull(existLogisticsDeliver)){
                logisticsDelivery.setVersion(1);
                logisticsDelivery.setDeliveryTime(DateUtil.date());
                logisticsDelivery.setRecordDeliveryNoTime(DateUtil.date());
                logisticsDelivery.setLogisticsStatus(DeliveryLogisticsStatusEnum.SENDED.getCode());
                logisticsDelivery.setCreateTime(DateUtil.date());
                return logisticsDelivery;
            }
            //商品合并
            logisticsDelivery.setId(existLogisticsDeliver.getId());
            Optional.ofNullable(StringUtils.isBlank(existLogisticsDeliver.getGoodsIds()) ? "" : existLogisticsDeliver.getGoodsIds()).ifPresent(goodsIds -> {
                logisticsDelivery.setGoodsIds(Stream.of(goodsIds.split(","),logisticsDelivery.getGoodsIds().split(",")).flatMap(Arrays::stream).distinct().collect(Collectors.joining(",")));
            });
            logisticsDelivery.setUpdateTime(DateUtil.date());
            logisticsDelivery.setCreatorAccount(null);
            logisticsDelivery.setCreatorName(null);
            return logisticsDelivery;
        }
        //平台发货
        Optional<LogisticsDelivery> first = ListUtils.emptyIfNull(logisticsDeliveryService.list(Wrappers.<LogisticsDelivery>lambdaQuery()
                        .eq(LogisticsDelivery::getLogisticsOrderId, logisticsOrder.getId())
                        .eq(LogisticsDelivery::getIfCancel, 0)))
                .stream()
                .filter(x -> x.getGoodsIds().contains(logisticsDelivery.getGoodsIds()))
                .findFirst();
        if(first.isPresent()){
            logisticsDelivery.setId(first.get().getId());
            logisticsDelivery.setCreatorAccount(null);
            logisticsDelivery.setCreatorName(null);
        }else{
            logisticsDelivery.setDeliveryTime(DateUtil.date());
            logisticsDelivery.setLogisticsStatus(DeliveryLogisticsStatusEnum.SENDED.getCode());
        }
        return logisticsDelivery;
    }
    /**
     * 添加或保存运单信息
     * @param addOrderApiResData
     * @param deliveryEntity
     */
    private void saveOrUpdateDeliveryEntity(AddOrderApiResData addOrderApiResData, LogisticsDelivery deliveryEntity) {
        Optional.ofNullable(addOrderApiResData).map(AddOrderApiResData::getWaybillId).ifPresent(deliveryNo ->{
            deliveryEntity.setDeliveryNo(deliveryNo);
            deliveryEntity.setDeliveryTime(DateUtil.date());
            deliveryEntity.setRecordDeliveryNoTime(DateUtil.date());
            deliveryEntity.setUpdateTime(DateUtil.date());
        });
        logisticsDeliveryService.saveOrUpdate(deliveryEntity);
    }

    protected Pair<List<LogisticsOrderGoods>,LogisticsOrder> checkAndGetOrderGoodsEntities(String orderNo,List<Long> goodsIds,List<String> shopIds) {
        if(StringUtils.isBlank(orderNo)){
            throw new BusinessException("订单号不能为空");
        }
        LogisticsOrder logisticsOrder = ListUtils.emptyIfNull(logisticsOrderService.list(Wrappers.<LogisticsOrder>lambdaQuery()
                        .in(LogisticsOrder::getChildOrderId, orderNo)
                        .eq(LogisticsOrder::getDeleteStatus,DeleteStatusEnum.NO.getCode())))
                .stream().findFirst().orElseThrow(() -> new BusinessException(StrUtil.format("订单号{}不存在或此订单为需求单作废中不允许发货",orderNo)));
        // 如果是企业订单， 则判断订单是否作废和作废中
        if(ChannelBizCodeEnum.c10_ORDER_ADMIN.getCode().equals(logisticsOrder.getBizChannelCode())){
            Request<DemandOrderQueryByChildOrderIdReqDto> request = new Request<>();
            DemandOrderQueryByChildOrderIdReqDto dto = new DemandOrderQueryByChildOrderIdReqDto();
            dto.setChildOrderId(logisticsOrder.getChildOrderId());
            request.setData(dto);
            Response<DemandOrderQueryResDto> response = demandOrderQueryClient.detailByChildOrderId(request);
            DemandOrderQueryResDto data = response.getData();
            if(data != null){
                if(DemandOrderStatusEnum.INVALIDING.getCode().equals(data.getDemandOrderStatus())){
                    throw new BusinessException("发货失败，采购需求单作废中");
                }
                if(DemandOrderStatusEnum.INVALID.getCode().equals(data.getDemandOrderStatus())){
                    throw new BusinessException("发货失败，采购需求单已作废");
                }
            }
        }

        log.info("物流下单，根据商品id = {}、订单编号 = {},查询 logisticsOrderid = {}",goodsIds,orderNo,logisticsOrder.getId());
        LambdaQueryWrapper<LogisticsOrderGoods> LogisticsOrderGoodsQueryWrapper = new LambdaQueryWrapper<>();
        LogisticsOrderGoodsQueryWrapper.in(LogisticsOrderGoods::getId, goodsIds);
        LogisticsOrderGoodsQueryWrapper.eq(LogisticsOrderGoods::getLogisticsOrderId,logisticsOrder.getId());
        LogisticsOrderGoodsQueryWrapper.isNull(LogisticsOrderGoods::getLogisticsDeliveryId);
        LogisticsOrderGoodsQueryWrapper.eq(LogisticsOrderGoods::getDeleteStatus,DeleteStatusEnum.NO.getCode());
        List<LogisticsOrderGoods> LogisticsOrderGoodsList = logisticsOrderGoodsService.list(LogisticsOrderGoodsQueryWrapper);
        Optional.ofNullable(CollectionUtils.isEmpty(LogisticsOrderGoodsList) ? null : LogisticsOrderGoodsList).orElseThrow(() -> new BusinessException("未找到可用的商品"));
        log.info("物流下单，查询的商品信息条数 = {}",LogisticsOrderGoodsList.size());

        return new Pair(LogisticsOrderGoodsList,logisticsOrder);
    }

    /**
     * 校验并获取发货地址
     */
    protected LogisticsShipAddress checkAndGetSenderAddress(AddOrderListReqData addOrderReqData){
        LogisticsShipAddress logisticsShipAddress = null;
        if (Objects.nonNull(addOrderReqData.getShopLogisticsId())) {
            //获取发货地址
            logisticsShipAddress = logisticsShipAddressMapper.selectById(addOrderReqData.getSendAddressId());
            Assert.notNull(logisticsShipAddress, "未找到可用的发货地址");
        }
        return logisticsShipAddress;
    }

    /**
     * 校验并获取物流订单信息
     * @param logisticsOrderId 物流订单id
     * @return
     */
    protected LogisticsOrder checkAndGetLogisticsOrder(Long logisticsOrderId){
        LogisticsOrder logisticsOrder = logisticsOrderService.getById(logisticsOrderId);
        Assert.notNull(logisticsOrder,"未找到可用的物流订单");
        return logisticsOrder;
    }

    /**
     * 构建物流api下单请求参数
     */
    protected AddOrderApiReqData buildAddOrderApiReqData(AddOrderListReqData addOrderListReqData,List<LogisticsOrderGoods> LogisticsOrderGoodsList, LogisticsOrder logisticsOrder, LogisticsDelivery deliveryEntity,LogisticsShipAddress shipAddressEntity,ShopLogisticsDTO shopLogisticsDTO) {
        AddOrderApiReqData addOrderReqData = new AddOrderApiReqData();
        addOrderReqData.setDeliveryNo(addOrderListReqData.getDeliveryNo());
        addOrderReqData.setOrderId(String.valueOf(deliveryEntity.getId()));
        Optional.ofNullable(shopLogisticsDTO).ifPresent(item -> {
            addOrderReqData.setExpressType(item.getExpressType());
            addOrderReqData.setAccount(item.getAccount());
        });
        //发货人
        AddOrderApiReqData.Sender sender = new AddOrderApiReqData.Sender();
        if (Objects.nonNull(shipAddressEntity)) {
            sender.setName(shipAddressEntity.getAddresser());
            sender.setMobile(shipAddressEntity.getPhoneNumber());
            sender.setProvince(shipAddressEntity.getProvinceName());
            sender.setCity(shipAddressEntity.getCityName());
            sender.setArea(shipAddressEntity.getAreaName());
            sender.setAddress(StringUtils.isBlank(shipAddressEntity.getStreetName()) ? shipAddressEntity.getAddressDetail() : shipAddressEntity.getStreetName() + shipAddressEntity.getAddressDetail());
        }
        addOrderReqData.setSender(sender);
        //收货人
        AddOrderApiReqData.Receiver receiver = new AddOrderApiReqData.Receiver();
        receiver.setName(logisticsOrder.getReceiptName());
        receiver.setMobile(logisticsOrder.getReceiptPhone());
        receiver.setProvince(logisticsOrder.getReceiptProvince());
        receiver.setCity(logisticsOrder.getReceiptCity());
        receiver.setArea(logisticsOrder.getReceiptArea());
        receiver.setAddress(logisticsOrder.getReceiptAddress());
        addOrderReqData.setReceiver(receiver);
        //商品信息
        AddOrderApiReqData.Shop shop = new AddOrderApiReqData.Shop();
        shop.setGoodsCount(LogisticsOrderGoodsList.size());
        //设置寄托物
        shop.setGoodsName(LogisticsOrderGoodsList
                .stream()
                .map(goodsInfo -> new StringBuilder(goodsInfo.getGoodsName()).append("/").append(goodsInfo.getSkuSpecs()).append("/").append(goodsInfo.getGoodsNum()).toString())
                .collect(Collectors.joining("\n")));
        addOrderReqData.setShop(shop);
        //保价信息
        AddOrderApiReqData.Insured insured = new AddOrderApiReqData.Insured();
        insured.setUseInsured(0);
        addOrderReqData.setInsured(insured);
        //包裹信息
        AddOrderApiReqData.Cargo cargo = new AddOrderApiReqData.Cargo();
        cargo.setCount(1);
        addOrderReqData.setCargo(cargo);
        String skuSpecs = LogisticsOrderGoodsList.stream().map(LogisticsOrderGoods::getSkuSpecs).collect(Collectors.joining(";"));
        addOrderReqData.setCustomRemark(logisticsOrder.getDeliveryRemarks() == null ? skuSpecs : logisticsOrder.getDeliveryRemarks() + ";" + skuSpecs);
        return addOrderReqData;
    }

    /**
     * 构建运单信息
     */
    protected LogisticsDelivery buildDeliveryEntity(AddOrderListReqData addOrderReqData, List<LogisticsOrderGoods> LogisticsOrderGoodsList, LogisticsOrder logisticsOrder, LogisticsShipAddress shipAddressEntity, ShopLogisticsDTO shopLogisticsDTO,AmUserInfo amUserInfo) {
        LogisticsDelivery logisticsDelivery = new LogisticsDelivery();
        //生成唯一id
        logisticsDelivery.setId(IdUtil.getSnowflake().nextId());
        Optional.ofNullable(addOrderReqData).ifPresent(req -> {
            logisticsDelivery.setDeliveryNo(addOrderReqData.getDeliveryNo());
            logisticsDelivery.setCompanyCode(addOrderReqData.getCompanyCode());
            logisticsDelivery.setShopLogisticsId(String.valueOf(addOrderReqData.getShopLogisticsId()));
        });
        Optional.ofNullable(amUserInfo).ifPresent(userInfo -> {
            logisticsDelivery.setCreatorAccount(userInfo.getUserName());
            logisticsDelivery.setCreatorName(userInfo.getRealName());
            logisticsDelivery.setCreateTime(DateUtil.date());
            logisticsDelivery.setUpdateAccount(userInfo.getUserName());
            logisticsDelivery.setUpdateName(userInfo.getRealName());
            logisticsDelivery.setUpdateTime(DateUtil.date());
            logisticsDelivery.setDeleteStatus(0);
        });
        Optional.ofNullable(shipAddressEntity).ifPresent(address -> {
            //发货地址
            logisticsDelivery.setSendAddressId(String.valueOf(address.getId()));
            logisticsDelivery.setSendAddress(StringUtils.isBlank(address.getStreetName()) ? address.getAddressDetail() : address.getStreetName() + address.getAddressDetail());
            logisticsDelivery.setSendName(address.getAddresser());
            logisticsDelivery.setSendPhone(address.getPhoneNumber());
            logisticsDelivery.setSendProvince(address.getProvinceName());
            logisticsDelivery.setSendCity(address.getCityName());
            logisticsDelivery.setSendArea(address.getAreaName());
        });
        Optional.ofNullable(logisticsOrder).ifPresent(order -> {
            //收货地址
            logisticsDelivery.setReceiptAddressId(order.getReceiptAddressId());
            logisticsDelivery.setReceiptName(order.getReceiptName());
            logisticsDelivery.setReceiptPhone(order.getReceiptPhone());
            logisticsDelivery.setReceiptProvince(order.getReceiptProvince());
            logisticsDelivery.setReceiptCity(order.getReceiptCity());
            logisticsDelivery.setReceiptArea(order.getReceiptArea());
            logisticsDelivery.setReceiptAddress(order.getReceiptAddress());
            logisticsDelivery.setLogisticsOrderId(order.getId());
        });
        logisticsDelivery.setCompanyCode(addOrderReqData.getCompanyCode());
        logisticsDelivery.setShopLogisticsId(Optional.ofNullable(addOrderReqData.getShopLogisticsId()).map(String::valueOf).orElse(null));
        String goodsIds = LogisticsOrderGoodsList.stream().map(x -> x.getId()).map(String::valueOf).collect(Collectors.joining(","));
        logisticsDelivery.setGoodsIds(goodsIds);
        logisticsDelivery.setIfCancel(0);
        logisticsDelivery.setLogisticsType(Optional.ofNullable(shopLogisticsDTO)
                .map(x -> StrUtil.emptyToDefault(x.getTypeCode(),null))
                .map(Integer::parseInt)
                //此处数据库0为商家平台，所以做此判断
                .map(x -> x ==1 ? 1 : 2)
                .orElse(LogisticsOrderGoodsList.get(0).getLogisticsType()));
        return logisticsDelivery;
    }

    /**
     * 发送物流变更消息
     */
    protected void sendLogisticsStatusChangeMq(String childOrderId,Long logisticsOrderId,OrderStatusEnum orderStatusEnum){
        LogisticsOrder logisticsOrder = logisticsOrderService.getById(logisticsOrderId);
        //发送商品mq通知,已发货
        OrderStatusChangePushDTO pushDto = new OrderStatusChangePushDTO();
        pushDto.setOrderStatus(orderStatusEnum.getCode());
        pushDto.setChildOrderId(Optional.ofNullable(logisticsOrder).map(LogisticsOrder::getChildOrderId).orElse(null));
        pushDto.setLogisticsId(logisticsOrderId);
        pushDto.setLogisticsType(1);
        pushDto.setStatusTime(LocalDateTime.now());
        logisticsStatusChangeProducer.send(pushDto);
    }

    /**
     * 校验并获取运单信息
     * @param logisticsDeliveryId
     * @return
     */
    protected LogisticsDelivery getAndCheckDelivery(Long logisticsDeliveryId){
        return Optional.ofNullable(logisticsDeliveryService.getOne(Wrappers.<LogisticsDelivery>lambdaQuery()
                        .eq(LogisticsDelivery::getId,logisticsDeliveryId)
                        .last(" limit 1")))
                .orElseThrow(() -> new BusinessException("运单不存在"));
    }

    /**
     * 批量校验并获取运单信息
     * @param logisticsDeliveryId
     * @return
     */
    protected List<LogisticsDelivery> getAndCheckDeliveryByNos(List<String> deliveryNos,List<String> shopIds) {
        List<LogisticsDelivery> deliveryLists = logisticsDeliveryService.list(Wrappers.<LogisticsDelivery>lambdaQuery()
                .in(LogisticsDelivery::getDeliveryNo, deliveryNos)
                .in(LogisticsDelivery::getLogisticsStatus, Arrays.asList(DeliveryLogisticsStatusEnum.SENDED.getCode(), DeliveryLogisticsStatusEnum.TO_RECEIVED.getCode()))
                .eq(LogisticsDelivery::getIfCancel, 0));
        if(CollectionUtils.isEmpty(deliveryLists)){
            throw  new BusinessException("运单不存在");
        }
        return deliveryLists;
    }

    /**
     * 校验运单取消物流状态
     * @param logisticsStatus
     */
    protected void checkLogisticsDeliveryStatusForCancel(Integer logisticsStatus){
        //已下单、待揽收、下单后超七天无物流轨迹导致的派件异常、平台取消   可取消订单
        Optional.ofNullable(logisticsStatus)
                .map(DeliveryLogisticsStatusEnum::fromCode)
                .filter(x -> x.equals(DeliveryLogisticsStatusEnum.SENDED)
                        || x.equals(DeliveryLogisticsStatusEnum.TO_RECEIVED)
                        || x.equals(DeliveryLogisticsStatusEnum.DELIVERY_ERROR)
                        || x.equals(DeliveryLogisticsStatusEnum.PLATFORM_CANCEL))
                .orElseThrow(() -> new BusinessException("非已下单、待揽收、派送异常、平台取消的物流单不可以取消发货"));
    }

    /**
     * 校验物流接口返回状态是否成功
     * @param companyCodeEnum
     * @param code
     * @return
     */
    protected boolean checkCancelSuccess(CompanyCodeEnum companyCodeEnum, Integer code){
        Boolean sfSuccess = CompanyCodeEnum.SF.equals(companyCodeEnum) && Objects.equals(code, CommonConstant.SF_API_SUCCESS_CODE);
        Boolean jdSuccess = CompanyCodeEnum.JD.equals(companyCodeEnum) && Objects.equals(code, CommonConstant.JD_API_SUCCESS_CODE);
        Boolean kuaidi100Success = CompanyCodeEnum.KD100.equals(companyCodeEnum) && Objects.equals(code, CommonConstant.KUAIDI100_API_SUCCESS_CODE);
        return sfSuccess || jdSuccess || kuaidi100Success;
    }

    /**
     * 获取物流状态码和物流订单状态映射map
     *
     * @return
     */
    private Map<String,Pair<Integer,Integer>> getStatusMappingMap(){
        List<LogisticsOrderStatusMapping> logisticsOrderStatusMappings = Optional.ofNullable(redisTemplate.opsForValue().get(ORDER_STATUS_MAPPING_KEY))
                .map(String::valueOf)
                .map(x -> JSONArray.parseArray(x, LogisticsOrderStatusMapping.class))
                .orElseGet(() -> {
                    List<LogisticsOrderStatusMapping> orderStatusMappingLists = logisticsOrderStatusMappingService.list(Wrappers.<LogisticsOrderStatusMapping>lambdaQuery().eq(LogisticsOrderStatusMapping::getDeleteStatus, DeleteStatusEnum.NO.getCode()));
                    if(CollectionUtils.isNotEmpty(orderStatusMappingLists)){
                        redisTemplate.opsForValue().set(ORDER_STATUS_MAPPING_KEY,JSON.toJSONString(orderStatusMappingLists),24,TimeUnit.HOURS);
                    }
                    return orderStatusMappingLists;
                });
        return ListUtils.emptyIfNull(logisticsOrderStatusMappings)
                .stream()
                .collect(Collectors.toMap(x -> new StringBuilder(x.getCompanyCode()).append("|").append(x.getOperationCode()).toString(),y -> new Pair<>(y.getLogisticsStatus(),y.getOrderStatus()),(a,b) -> a));
    }

    /**
     * 转换物流轨迹状态码到映射状态码
     * @return
     */
    @Override
    public Pair<Integer,Integer> getFromPathState(String pathState){

        return Optional.ofNullable(getStatusMappingMap())
                .map(x -> x.get(new StringBuilder(getSourceCompanyCode().getCode()).append("|").append(pathState).toString()))
                .orElse(null);
    }
}

package com.longfor.c10.lzyx.logistics.core.service.merchant.impl;


import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import cn.afterturn.easypoi.excel.entity.result.ExcelVerifyHandlerResult;
import cn.afterturn.easypoi.handler.inter.IExcelVerifyHandler;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.longfor.c10.lzyx.logistics.core.mq.producer.LogisticsStatusChangeProducer;
import com.longfor.c10.lzyx.logistics.core.mq.producer.LogisticsTouchProducer;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonDeliveryService;
import com.longfor.c10.lzyx.logistics.core.service.impl.delivery.KuaiDi100ServiceImpl;
import com.longfor.c10.lzyx.logistics.core.service.merchant.ILogisticsMerchantDeliveryService;
import com.longfor.c10.lzyx.logistics.core.util.PushSmsUtil;
import com.longfor.c10.lzyx.logistics.core.util.SendCommonHandler;
import com.longfor.c10.lzyx.logistics.dao.service.*;
import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryCompanyListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryNoSendDetailReq;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliveryNoSendDetailVO;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliveryNoSendListDetailVO;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.AmUserInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.SubLogisticsPathReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.kuaidi100.AutoNumberDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.*;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.OrderStatusChangePushDTO;
import com.longfor.c10.lzyx.logistics.entity.entity.*;
import com.longfor.c10.lzyx.logistics.entity.enums.DeleteStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeliveryLogisticsStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.GoodsOrderStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.LogisticsTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c10.lzyx.order.entity.enums.TocuhSmsParamEnum;
import com.longfor.c10.lzyx.order.entity.enums.TouchSmsTypeEnum;
import com.longfor.c10.lzyx.touch.entity.bo.yuntusuo.TouchBotCardMessageBO;
import com.longfor.c10.lzyx.touch.entity.dto.mq.TouchMqMessageDTO;
import com.longfor.c10.lzyx.touch.entity.enums.TouchCustomParamEnum;
import com.longfor.c10.lzyx.touch.entity.enums.TouchSystemCodeEnum;
import com.longfor.c2.ryh.order.entity.mq.enums.OrderStatusEnum;
import com.longfor.c2.starter.data.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaoyl
 * @date 2022/3/29 下午2:18
 * @since 1.0
 */
@Service
@Slf4j
public class LogisticeMerchantDeliveryServiceImpl implements ILogisticsMerchantDeliveryService {
    //mysql批量操作条数
    private static final int MYSQL_BATCH_OPERATOR_SIZE = 100;

    @Autowired
    private IShopLogisticsService shopLogisticsService;

    @Autowired
    private IShopLogisticsConfigService shopLogisticsConfigService;

    @Autowired
    private ILogisticsDeliveryCompanyService logisticsDeliveryCompanyService;

    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    private ILogisticsCommonDeliveryService logisticsCommonDeliveryService;

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;

    @Resource
    private KuaiDi100ServiceImpl kuaiDi100Service;

    @Autowired
    private LogisticsStatusChangeProducer logisticsStatusChangeProducer;

    @Autowired
    private LogisticsTouchProducer logisticsTouchProducer;

    @Autowired
    private PushSmsUtil pushSmsUtil;
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
    @Resource
    private SendCommonHandler sendCommonHandler;

    @Override
    public Response<String> noSendImport(MultipartFile file, BaseReqData baseReqData) {
        List<PendingImportDTO> importData = getAndCheckImportData(file,baseReqData);
        //去掉运单号空格
        importData.stream().forEach(item -> {
            item.setDeliveryNo(StrUtil.cleanBlank(item.getDeliveryNo()));
        });
        Pair<Boolean, String> pairResult = pendingResourceImport(importData,baseReqData);
        if (pairResult.getKey()) {
            return Response.ok(pairResult.getValue());
        } else {
            return Response.fail(pairResult.getValue());
        }
    }
    private List<PendingImportDTO> getAndCheckImportData(MultipartFile file, BaseReqData baseReqData){
        InputStream inputStream = null;
        Optional.ofNullable(file)
                .filter(f -> f.getSize() > 0)
                .orElseThrow(() -> new BusinessException("上传文件不能为空"));
        AmUserInfo amUserInfo = Optional.ofNullable(baseReqData)
                .map(BaseReqData::getAmUserInfo)
                .orElseThrow(() -> new BusinessException("商户登陆信息为空"));
        List<String> shopIds = Optional.ofNullable(baseReqData)
                .map(BaseReqData::getShopIds)
                .orElseThrow(() -> new BusinessException("商户登陆信息为空"));
        if(!checkImportShop(shopIds)){
            throw new BusinessException("当前供应商配置的物流信息不支持导入");
        }
        try{
            log.info("待发货列表导入，上传文件 = {}",file.getOriginalFilename());
            Optional.ofNullable(file.getOriginalFilename())
                    .map(name -> name.substring(name.lastIndexOf(".")+1))
                    .filter(extension -> Arrays.asList("xls","xlsx","XLS","XLSX").contains(extension))
                    .orElseThrow(() -> new BusinessException("文件格式不正确"));
            inputStream = file.getInputStream();
            ImportParams importParams = new ImportParams();
            importParams.setNeedVerify(true);
            importParams.setHeadRows(1);
            /*importParams.setVerifyHandler(new IExcelVerifyHandler<PendingImportDTO>() {
                @Override
                public ExcelVerifyHandlerResult verifyHandler(PendingImportDTO obj) {
                    ExcelVerifyHandlerResult result=new ExcelVerifyHandlerResult(true);
                    if (ObjectUtil.isNotNull(obj)){
                        //判断对象属性是否全部为空
                        boolean b = Objects.isNull(obj) || obj.checkAllNull();
                        result.setSuccess(!b);
                    }
                    return result;
                }
            });*/
            ExcelImportResult<PendingImportDTO> importResult = ExcelImportUtil.importExcelMore(inputStream, PendingImportDTO.class, importParams);
            if(Objects.isNull(importResult) || (CollectionUtils.isEmpty(importResult.getFailList()) && CollectionUtils.isEmpty(importResult.getList()))){
                throw new BusinessException("导入文件内容为空");
            }
            if(importResult.getFailList().size() + importResult.getList().size() > 200){
                throw new BusinessException("导入文件条数不能超过200条");
            }
            log.info("待发货列表导入，校验成功条数 = {}，校验失败条数 = {}",Optional.ofNullable(importResult.getList()).map(List::size).orElse(0),Optional.ofNullable(importResult.getFailList()).map(List::size).orElse(0));
            List<PendingImportDTO> failList = importResult.getFailList();
            if(!CollectionUtils.isEmpty(failList)){
                throw new BusinessException(new StringBuilder("导入失败：")
                        .append("存在空值或格式错误：").append(failList.size()).append("条；")
//                        .append("错误信息：<br/>").append(ListUtils.emptyIfNull(failList).stream().map(x -> new StringBuilder("行号："+ (x.getRowNum() + 1)).append("，").append(x.getErrorMsg()).toString()).collect(Collectors.joining("；<br/>")))
                        .append("<br/>错误信息：<br/>").append(ListUtils.emptyIfNull(failList).stream().map(x -> new StringBuilder(x.getErrorMsg()).toString()).collect(Collectors.joining("；<br/>")))
                        .toString());
            }
            List<PendingImportDTO> importDataList = importResult.getList().stream().map(data -> {
                //字符串去除前后空格
                Arrays.stream(data.getClass().getDeclaredFields()).forEach(field -> {
                    if(field.getType().toString().equals("class java.lang.String")){
                        Object fieldValue = ReflectUtil.getFieldValue(data, field);
                        ReflectUtil.setFieldValue(data,field,String.valueOf(fieldValue).trim());
                    }
                });
                return data;
            }).collect(Collectors.toList());
            log.info("待发货列表导入，去除空格后的数据 = {}", JSON.toJSONString(importDataList));
            return importDataList;
        }catch (Exception ex){
            ex.printStackTrace();
            if(ex instanceof BusinessException){
                throw new BusinessException(ex.getMessage());
            }
            throw new BusinessException("系统异常");
        }finally {
            if(Objects.nonNull(inputStream)){
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }
    private boolean checkImportShop(List<String> shopIds){
        log.info("待发货列表导入，校验登陆用户商户配置开始");
        List<ShopLogistics> shopLogistics = shopLogisticsService.list(Wrappers.<ShopLogistics>lambdaQuery()
                .in(ShopLogistics::getShopId,shopIds)
                .eq(ShopLogistics::getDeleteStatus, DeleteStatusEnum.NO.getCode())
                .eq(ShopLogistics::getChoose,1));
        log.info("待发货列表导入，根据商户id查询到的非删除的商户配置信息 = {}",JSON.toJSONString(shopLogistics));
        if(CollectionUtils.isEmpty(shopLogistics)){
            throw new BusinessException("当前供应商未配置物流信息");
        }
        List<Integer> shopLogisticConfigIds = shopLogistics.stream().map(ShopLogistics::getShopLogisticsConfigId).distinct().collect(Collectors.toList());
        log.info("待发货列表导入，商户配置模版配置id集合 = {}",JSON.toJSONString(shopLogisticConfigIds));
        //商户物流配置
        List<ShopLogisticsConfig> shopLogisticsConfigs = shopLogisticsConfigService.list(Wrappers.<ShopLogisticsConfig>lambdaQuery()
                .in(ShopLogisticsConfig::getId, shopLogisticConfigIds)
                .eq(ShopLogisticsConfig::getDeleteStatus, DeleteStatusEnum.NO.getCode()));
        log.info("待发货列表导入，根据模版配置id查询到的非删除配置信息 = {}",JSON.toJSONString(shopLogisticsConfigs));
        if(CollectionUtils.isEmpty(shopLogisticsConfigs)){
            throw new BusinessException("当前供应商未配置物流信息");
        }
        boolean otherExist = shopLogisticsConfigs.stream().anyMatch(config -> config.getLogisticsCode().equals("other"));
        log.info("待发货列表导入，校验商户是否有other类型的物流配置，校验结果 = {}",otherExist);
        return otherExist;
    }

    @Transactional(rollbackFor = Exception.class)
    public Pair<Boolean,String> pendingResourceImport(List<PendingImportDTO> dataList,BaseReqData baseReqData){
        //返回提示信息
        StringBuilder rSb = new StringBuilder();
        //校验订单
        CheckImportOrderDataResultDTO orderCheckResult = checkImportOrderData(dataList);
        if(!CollectionUtil.isEmpty(orderCheckResult.getShippedDataList())){
            rSb.append("存在已发货数据").append(orderCheckResult.getShippedDataList().size()).append("条，已自动过滤；");
        }
        if(orderCheckResult.isNoData()){
            return Pair.of(true,rSb.insert(rSb.length(),"操作成功0条；").toString());
        }
        //物流校验
        Pair<Map<String, AutoNumberDTO>, Object> deliverCheckResult = checkImportDeliveryData(dataList);
        //订单信息
        Map<String, LogisticsOrder> orderIdAndMap = orderCheckResult.getOrderIdAndMap();
        //物流订单id和商品信息map
        Map<Long, List<LogisticsOrderGoods>> logisticOrderIdAndGoodsListMap = orderCheckResult.getLogisticOrderIdAndGoodsListMap();
        //订单id商品sku和发货信息map
        Map<Long,Map<String, LogisticsDelivery>> goodsIdAndDeliverStatusMap = orderCheckResult.getGoodsIdAndDeliverStatusMap();
        //订单id和运单编号分组运单信息map
        Map<String, LogisticsDelivery> orderIdAnddeliverNoAndMap = orderCheckResult.getOrderIdAnddeliverNoAndMap();
        //订单id和运单信息map
        Map<Long,List<LogisticsDelivery>> logisticOrderIdAnDeliverListMap = orderCheckResult.getLogisticOrderIdAnDeliverListMap();
        //商品id和信息map
        Map<Long,LogisticsOrderGoods> goodsIdAndMap = orderCheckResult.getGoodsIdAndMap();
        //配送公司信息
        Map<String, AutoNumberDTO> deliveryCompanyCodeAndMap = deliverCheckResult.getKey();
        //商品订单运单信息
        Map<Long,Long> goodsIdAndDelvierIdMap = new HashMap<>(32);

        log.info("待发货列表导入，查询运单编号和运单信息映射 = {}",JSON.toJSONString(orderIdAnddeliverNoAndMap));

        //数据按照订单和运单编号分组
        Map<String, List<PendingImportDTO>> dataGroupByOrderNoAndDeliverNoMap = dataList.stream().collect(Collectors.groupingBy(x -> orderIdAndMap.get(x.getOrderNo()).getId() + "|" + x.getDeliveryNo()));

        //添加或更新运单信息
        List<LogisticsDelivery> insertOrUpdateList = saveOrUpdateDelivery(orderIdAndMap, orderIdAnddeliverNoAndMap, goodsIdAndMap, deliveryCompanyCodeAndMap, goodsIdAndDelvierIdMap, dataGroupByOrderNoAndDeliverNoMap,baseReqData);

        List<Long> subErrorDeliverIds = new ArrayList<>();

        //订阅快递100物流轨迹
        subLogisticsPath(insertOrUpdateList, subErrorDeliverIds);

        //更新订单信息
        updateOrder(dataList, orderIdAndMap, logisticOrderIdAndGoodsListMap, goodsIdAndDelvierIdMap,baseReqData);

        //更新订单商品
        updateOrderGoods(dataList, orderIdAndMap, logisticOrderIdAndGoodsListMap, goodsIdAndMap, goodsIdAndDelvierIdMap,baseReqData);

        //发送物流状态变更消息
        pushLogisticsStatusChange(dataList, orderIdAndMap);

        //异步短信通知
        pushSendNotify(dataList,orderIdAndMap,logisticOrderIdAndGoodsListMap,deliveryCompanyCodeAndMap);
        return Pair.of(true,rSb.insert(rSb.length(),new StringBuilder("操作成功").append(dataList.size()).append("条；").toString()).toString());
    }

    //快递100订阅物流轨迹
    private void subLogisticsPath(List<LogisticsDelivery> insertOrUpdateList, List<Long> subErrorDeliverIds) {
        insertOrUpdateList.stream().forEach(deliveryInfo ->{
            SubLogisticsPathReqData subLogisticsPathReqData = new SubLogisticsPathReqData();
            subLogisticsPathReqData.setCompanyCode(deliveryInfo.getCompanyCode());
            subLogisticsPathReqData.setDeliverNo(deliveryInfo.getDeliveryNo());
            boolean result = kuaiDi100Service.subLogisticsPath(subLogisticsPathReqData);
            if(!result){
                subErrorDeliverIds.add(deliveryInfo.getId());
            }
        });
        log.info("待发货列表导入，订阅快递100物流轨迹完成，订阅失败的运单id集合", JSON.toJSONString(subErrorDeliverIds));
    }

    private void pushLogisticsStatusChange(List<PendingImportDTO> dataList, Map<String, LogisticsOrder> orderIdAndMap) {
        //发送商品mq通知12:已发货
        List<OrderStatusChangePushDTO> logisticStatusChangePushList = dataList.stream().map(info -> {
            OrderStatusChangePushDTO pushDto = new OrderStatusChangePushDTO();
            pushDto.setOrderStatus(OrderStatusEnum.WAIT_RECEIPT.getCode());
            pushDto.setChildOrderId(info.getOrderNo());
            pushDto.setLogisticsId(orderIdAndMap.get(info.getOrderNo()).getId());
            pushDto.setLogisticsType(1);//正向流程
            pushDto.setStatusTime(LocalDateTime.now());
            return pushDto;
        }).collect(Collectors.toList());
        logisticStatusChangePushList.stream().forEach(data -> logisticsStatusChangeProducer.send(data));
        log.info("待发货列表导入，发送物流状态变更mq完成，发送参数", JSON.toJSONString(logisticStatusChangePushList));
    }

    private void updateOrderGoods(List<PendingImportDTO> dataList, Map<String, LogisticsOrder> orderIdAndMap, Map<Long, List<LogisticsOrderGoods>> logisticOrderIdAndGoodsListMap, Map<Long, LogisticsOrderGoods> goodsIdAndMap, Map<Long, Long> goodsIdAndDelvierIdMap,BaseReqData baseReqData) {
        //更新订单商品信息
        List<LogisticsOrderGoods> orderGoodsUpdateList = dataList.stream().map(info -> {
            Long logisticOrderId = orderIdAndMap.get(info.getOrderNo()).getId();
            LogisticsOrderGoods orderGoodsEntity = new LogisticsOrderGoods();
            //商品主键id
            orderGoodsEntity.setId(logisticOrderIdAndGoodsListMap.get(logisticOrderId).stream().filter(x -> x.getSkuId().equals(info.getSkuId())).map(LogisticsOrderGoods::getId).findFirst().orElse(null));
            //物流订单主键id
            orderGoodsEntity.setLogisticsDeliveryId(goodsIdAndDelvierIdMap.get(getGoodsIdFromSku(goodsIdAndMap,logisticOrderId,info.getSkuId())));
            orderGoodsEntity.setUpdateTime(DateUtil.date());
            Optional.ofNullable(baseReqData).map(BaseReqData::getAmUserInfo).ifPresent(userInfo -> {
                orderGoodsEntity.setUpdateAccount(userInfo.getUserName());
                orderGoodsEntity.setUpdateName(userInfo.getRealName());
            });
            return orderGoodsEntity;
        }).collect(Collectors.toList());
        Lists.partition(orderGoodsUpdateList,MYSQL_BATCH_OPERATOR_SIZE).stream().forEach(list -> {
            logisticsOrderGoodsService.updateBatchById(list);
        });
        log.info("待发货列表导入，物流订单商品运单信息完成，更新参数", JSON.toJSONString(orderGoodsUpdateList));
    }

    //更新物流订单信息
    private void updateOrder(List<PendingImportDTO> dataList, Map<String, LogisticsOrder> orderIdAndMap, Map<Long, List<LogisticsOrderGoods>> logisticOrderIdAndGoodsListMap, Map<Long, Long> goodsIdAndDelvierIdMap,BaseReqData baseReqData) {
        List<LogisticsOrder> logisticOrderUpdateList = dataList.stream().map(info -> {
            Long logisticOrderId = orderIdAndMap.get(info.getOrderNo()).getId();
            LogisticsOrder orderEntity = new LogisticsOrder();
            orderEntity.setId(logisticOrderId);
            //判断是否全部发货
            boolean ifAllSended = Optional.ofNullable(logisticOrderIdAndGoodsListMap.get(logisticOrderId))
                    .map(goodsList -> {
                        if(ListUtils.emptyIfNull(goodsList).stream().filter(x -> Objects.isNull(x.getLogisticsDeliveryId())).allMatch(x -> goodsIdAndDelvierIdMap.containsKey(x.getId()))){
                            return true;
                        }
                        return false;
                    })
                    .orElse(false);
            orderEntity.setGoodsOrderStatus(Boolean.TRUE.equals(ifAllSended) ? OrderStatusEnum.WAIT_RECEIPT.getCode() : GoodsOrderStatusEnum.SECTION_DELIVERED.getCode());
            Optional.ofNullable(baseReqData).map(BaseReqData::getAmUserInfo).ifPresent(userInfo -> {
                orderEntity.setUpdateAccount(userInfo.getUserName());
                orderEntity.setUpdateName(userInfo.getRealName());
            });
            orderEntity.setUpdateTime(DateUtil.date());
            return orderEntity;
        }).collect(Collectors.toList());
        Lists.partition(logisticOrderUpdateList,MYSQL_BATCH_OPERATOR_SIZE).stream().forEach(list -> {
            logisticsOrderService.updateBatchById(list);
        });
        log.info("待发货列表导入，更新物流订单表订单状态完成，更新参数", JSON.toJSONString(logisticOrderUpdateList));
    }

    //添加或更新运单信息
    private List<LogisticsDelivery> saveOrUpdateDelivery(Map<String, LogisticsOrder> orderIdAndMap, Map<String, LogisticsDelivery> orderIdAnddeliverNoAndMap, Map<Long, LogisticsOrderGoods> goodsIdAndMap, Map<String, AutoNumberDTO> deliveryCompanyCodeAndMap, Map<Long, Long> goodsIdAndDelvierIdMap, Map<String, List<PendingImportDTO>> dataGroupByOrderNoAndDeliverNoMap,BaseReqData baseReqData) {
        List<LogisticsDelivery> insertOrUpdateList = dataGroupByOrderNoAndDeliverNoMap.entrySet().stream().map(entry -> {
            LogisticsDelivery deliveryEntity = new LogisticsDelivery();
            AtomicBoolean existFlag = new AtomicBoolean(false);
            String[] array = entry.getKey().split("\\|");
            Long logisticOrderId = Long.parseLong(array[0]);
            String deliverNo = array[1];
            String orderNo = orderIdAndMap.entrySet().stream().filter(e -> e.getValue().getId().equals(logisticOrderId)).map(Map.Entry::getKey).findFirst().orElse(null);
            List<String> goodsIds = ListUtils.emptyIfNull(entry.getValue()).stream().map(x -> getGoodsIdFromSku(goodsIdAndMap, logisticOrderId, x.getSkuId())).map(String::valueOf).collect(Collectors.toList());
            LogisticsOrder orderEntity = orderIdAndMap.get(orderNo);
            Optional.ofNullable(orderIdAnddeliverNoAndMap.get(entry.getKey())).ifPresent(deliverInfo -> {
                existFlag.set(true);
                deliveryEntity.setId(deliverInfo.getId());
                deliveryEntity.setUpdateTime(DateUtil.date());
                deliveryEntity.setGoodsIds(StringUtils.isNotBlank(deliverInfo.getGoodsIds()) ? Arrays.stream(ArrayUtil.addAll(deliverInfo.getGoodsIds().split("\\,"), goodsIds.stream().toArray(String[]::new))).distinct().collect(Collectors.joining(",")) : goodsIds.stream().collect(Collectors.joining(",")));
                Optional.ofNullable(baseReqData).map(BaseReqData::getAmUserInfo).ifPresent(userInfo -> {
                    deliveryEntity.setUpdateAccount(userInfo.getUserName());
                    deliveryEntity.setUpdateName(userInfo.getRealName());
                });
                deliveryEntity.setUpdateTime(DateUtil.date());
            });
            if(!existFlag.get()){
                //不存在
                deliveryEntity.setGoodsIds(goodsIds.stream().collect(Collectors.joining(",")));
                Optional.ofNullable(baseReqData).map(BaseReqData::getAmUserInfo).ifPresent(userInfo -> {
                    deliveryEntity.setCreatorAccount(userInfo.getUserName());
                    deliveryEntity.setCreatorName(userInfo.getRealName());
                    deliveryEntity.setUpdateAccount(userInfo.getUserName());
                    deliveryEntity.setUpdateName(userInfo.getRealName());
                });
                deliveryEntity.setCreateTime(DateUtil.date());
                deliveryEntity.setUpdateTime(DateUtil.date());
            }
            //物流订单id
            deliveryEntity.setLogisticsOrderId(logisticOrderId);
            //运单编号
            deliveryEntity.setDeliveryNo(deliverNo);
            //商家物流
            deliveryEntity.setLogisticsType(LogisticsTypeEnum.SHOP.getCode());
            //物流状态
            deliveryEntity.setLogisticsStatus(DeliveryLogisticsStatusEnum.SENDED.getCode());
            deliveryEntity.setIfCancel(0);
            deliveryEntity.setReceiptProvince(orderEntity.getReceiptProvince());
            deliveryEntity.setReceiptCity(orderEntity.getReceiptCity());
            deliveryEntity.setReceiptArea(orderEntity.getReceiptArea());
            deliveryEntity.setReceiptAddress(orderEntity.getReceiptAddress());
            deliveryEntity.setReceiptAddressId(orderEntity.getReceiptAddressId());
            deliveryEntity.setReceiptName(orderEntity.getReceiptName());
            deliveryEntity.setReceiptPhone(orderEntity.getReceiptPhone());
            Optional.ofNullable(deliveryCompanyCodeAndMap.get(deliverNo)).ifPresent(r -> {
                deliveryEntity.setCompanyCode(r.getComCode());
//                deliveryEntity.setCompanyName(r.getDeliveryCompanyName());
            });
            deliveryEntity.setDeliveryTime(DateUtil.date());
            deliveryEntity.setLogisticsStatus(DeliveryLogisticsStatusEnum.SENDED.getCode());
            deliveryEntity.setDeleteStatus(DeleteStatusEnum.NO.code());
            deliveryEntity.setRecordDeliveryNoTime(DateUtil.date());
            return deliveryEntity;
        }).collect(Collectors.toList());
        log.info("待发货列表导入，组装运单插入或更新数据完成 = {}", JSON.toJSONString(insertOrUpdateList));
        //更新或者保存运单信息
        Lists.partition(insertOrUpdateList,MYSQL_BATCH_OPERATOR_SIZE).stream().forEach(list -> {
            logisticsDeliveryService.saveOrUpdateBatch(list);
            list.stream().forEach(info -> {
                Arrays.stream(info.getGoodsIds().split("\\,")).forEach(goodsId -> {
                    goodsIdAndDelvierIdMap.put(Long.parseLong(goodsId),info.getId());
                });
            });
        });
        log.info("待发货列表导入，运单信息更新完成，商品id和运单id映射信息 = {}",JSON.toJSONString(goodsIdAndDelvierIdMap));
        return insertOrUpdateList;
    }

    //发货通知
    private void pushSendNotify(List<PendingImportDTO> dataList, Map<String, LogisticsOrder> orderIdAndMap, Map<Long, List<LogisticsOrderGoods>> logisticOrderIdAndGoodsListMap, Map<String, AutoNumberDTO> deliveryCompanyCodeAndMap) {
        ThreadUtil.execute(() -> {
            log.info("待发货列表导入,发送物流发货通知开始");
            dataList.stream().forEach(item -> {
                LogisticsOrder orderEntity = orderIdAndMap.get(item.getOrderNo());
                List<LogisticsOrderGoods> orderGoodsEntityList = logisticOrderIdAndGoodsListMap.get(orderEntity.getId());
                AutoNumberDTO deliveryCompany = deliveryCompanyCodeAndMap.get(item.getDeliveryNo());
                LogisticsDeliveryCompany deliveryCompanyEntity = logisticsDeliveryCompanyService.getOne(Wrappers.<LogisticsDeliveryCompany>lambdaQuery().in(LogisticsDeliveryCompany::getCompanyCode, deliveryCompany.getComCode()).last(" limit 1"));
                TouchMqMessageDTO touchMqMessageDTO = new TouchMqMessageDTO();
                touchMqMessageDTO.setMessageSystemCode(TouchSystemCodeEnum.C10_LOGISTICS.getCode());
                touchMqMessageDTO.setTouchTaskCode(SpringUtil.getProperty("logistics.delivery.send.taskCode"));
                touchMqMessageDTO.setTouchTaskModeCode(TouchSmsTypeEnum.SHORT_MESSAGE.getTouchType());
                Map<String, String> smsParamsMap = new HashMap(32);
                smsParamsMap.put(TouchCustomParamEnum.LONGFOR_USERID.getCode(),Optional.ofNullable(orderEntity).map(LogisticsOrder::getUserId).orElse(null));
                smsParamsMap.put(TouchCustomParamEnum.CYCLE_TIMEOUT.getCode(),SpringUtil.getProperty("logistics.delivery.cycleTimeout"));
                smsParamsMap.put(TouchCustomParamEnum.CYCLE_UNIT.getCode(),SpringUtil.getProperty("logistics.delivery.cycleUnit"));
                smsParamsMap.put(TouchCustomParamEnum.MAX_SEND_NUM.getCode(),SpringUtil.getProperty("logistics.delivery.maxSendNum"));
                smsParamsMap.put(TocuhSmsParamEnum.PARAM_ONE.getCode(), pushSmsUtil.handelGoodsName(orderGoodsEntityList));
                smsParamsMap.put(TocuhSmsParamEnum.PARAM_TWO.getCode(), Optional.ofNullable(deliveryCompanyEntity).map(LogisticsDeliveryCompany::getCompanyName).orElse(""));
                smsParamsMap.put(TocuhSmsParamEnum.PARAM_THREE.getCode(), item.getDeliveryNo());
                touchMqMessageDTO.setCustomParam(smsParamsMap);
                log.info("待发货列表导入，物流发货短信通知，发送参数 = {}", JSON.toJSONString(touchMqMessageDTO));
                if(smsLogisticsSendSwitch){
                    List<Object> userIds = new ArrayList<>();
                    userIds.add(Optional.of(orderEntity).map(LogisticsOrder::getUserId).orElse(null));
                    if(!userIds.isEmpty()){
                        Map<String, Object> configParams = new HashMap<>();
                        configParams.put("goodsName", pushSmsUtil.handelGoodsName(orderGoodsEntityList));
                        configParams.put("companyName", Optional.ofNullable(deliveryCompanyEntity).map(LogisticsDeliveryCompany::getCompanyName).orElse(""));
                        configParams.put("deliveryNo", item.getDeliveryNo());
                        sendCommonHandler.sendLmIdSms(configParams, userIds, smsLogisticsSendTemplateId);
                    }
                } else {
                    logisticsTouchProducer.send(Arrays.asList(touchMqMessageDTO));
                }

                TouchMqMessageDTO touchMqMessageDTO1 = new TouchMqMessageDTO();
                touchMqMessageDTO1.setMessageSystemCode(TouchSystemCodeEnum.C10_LOGISTICS.getCode());
                touchMqMessageDTO1.setTouchTaskCode(SpringUtil.getProperty("logistics.delivery.send.push.taskCode"));
                touchMqMessageDTO1.setTouchTaskModeCode(TouchSmsTypeEnum.PUSSH.getTouchType());
                Map<String, String> pushParamsMap = new HashMap(32);
                pushParamsMap.put(TouchCustomParamEnum.YUNTUSUO_TARGET.getCode(),Optional.ofNullable(pushSmsUtil.getOaNumber(orderEntity.getUserId(),null)).orElse(null));
                pushParamsMap.put(TouchCustomParamEnum.CYCLE_TIMEOUT.getCode(),SpringUtil.getProperty("logistics.delivery.cycleTimeout"));
                pushParamsMap.put(TouchCustomParamEnum.CYCLE_UNIT.getCode(),SpringUtil.getProperty("logistics.delivery.cycleUnit"));
                pushParamsMap.put(TouchCustomParamEnum.MAX_SEND_NUM.getCode(),SpringUtil.getProperty("logistics.delivery.maxSendNum"));
                pushParamsMap.put(TocuhSmsParamEnum.PARAM_ONE.getCode(), pushSmsUtil.handelGoodsName(orderGoodsEntityList));
                pushParamsMap.put(TocuhSmsParamEnum.PARAM_TWO.getCode(), Optional.ofNullable(deliveryCompanyEntity).map(LogisticsDeliveryCompany::getCompanyName).orElse(""));
                pushParamsMap.put(TocuhSmsParamEnum.PARAM_THREE.getCode(), item.getDeliveryNo());
                pushParamsMap.put(TocuhSmsParamEnum.PARAM_FOUR.getCode(), orderEntity.getOrderId());
                pushParamsMap.put(TocuhSmsParamEnum.PARAM_FIVE.getCode(), orderEntity.getChildOrderId());
                touchMqMessageDTO1.setCustomParam(pushParamsMap);
                log.info("待发货列表导入，物流发货Push通知，发送参数 = {}", JSON.toJSONString(touchMqMessageDTO1));
                if(pushLogisticsSendSwitch){
                    List<Object> userIds = new ArrayList<>();
                    String oaNumber = pushSmsUtil.getOaNumber(orderEntity.getUserId(),null);
                    log.info("根据lmId-{}获取oaNumber-{}", orderEntity.getUserId(), oaNumber);
                    if(StringUtils.isNotBlank(oaNumber)){
                        userIds.add(oaNumber);
                        String content = pushLogisticsSendTemplate;
                        content = content.replace(TocuhSmsParamEnum.PARAM_ONE.getCode(), pushSmsUtil.handelGoodsName(orderGoodsEntityList));
                        content = content.replace(TocuhSmsParamEnum.PARAM_TWO.getCode(), Optional.ofNullable(deliveryCompanyEntity).map(LogisticsDeliveryCompany::getCompanyName).orElse(""));
                        content = content.replace(TocuhSmsParamEnum.PARAM_THREE.getCode(), item.getDeliveryNo());
                        sendCommonHandler.sendPushSingleCard(userIds, buildLogisticsSignOrSend(content, null, pushLogisticsSendTitle));
                    }
                } else {
                    logisticsTouchProducer.send(Arrays.asList(touchMqMessageDTO1));
                }
            });

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
    /**
     * 校验物流相关数据
     * @param dataList
     */
    private Pair<Map<String, AutoNumberDTO>,Object> checkImportDeliveryData(List<PendingImportDTO> dataList) {
        log.info("待发货列表导入，开始校验导入的物流相关数据是否合法");
        //校验发货时间不能早于当前时间
       /* List<PendingImportDTO> deliverTimeInvalidDatas = dataList.stream().filter(x -> DateUtil.compare(new Date(),DateUtil.parseDateTime(x.getDeliveryTime())) < 0).collect(Collectors.toList());
        log.info("待发货列表导入，发货时间晚于当前时间的数据 = {}",JSON.toJSONString(deliverTimeInvalidDatas));
        if(!CollectionUtils.isEmpty(deliverTimeInvalidDatas)){
            throw new BusinessException(getErrorMsg(deliverTimeInvalidDatas,"发货时间错误",Arrays.asList("deliveryTime")));
        }*/
        List<String> deliveryNos = dataList.stream().map(PendingImportDTO::getDeliveryNo).distinct().collect(Collectors.toList());
        log.info("待发货列表导入，导入的快递运单编码集合 = {}",JSON.toJSONString(deliveryNos));
        Map<String, AutoNumberDTO> deliverCompanyCodeAndNameMap = new HashMap<>(16);
        deliveryNos.forEach(no -> {
            try{
                AutoNumberDTO autoNumberDTO = kuaiDi100Service.autoNumber(no);
                if(Objects.nonNull(autoNumberDTO)){
                    deliverCompanyCodeAndNameMap.put(no,autoNumberDTO);
                }
            }catch (Exception ex){
            }
        });
        log.info("待发货列表导入，运单号和快递公司编码名称映射 = {}",JSON.toJSONString(deliverCompanyCodeAndNameMap));
        //过滤掉运单号不正确的数据
        List<PendingImportDTO> deliverNoInvalidList = dataList.stream().filter(x -> Objects.isNull(deliverCompanyCodeAndNameMap.get(x.getDeliveryNo()))).collect(Collectors.toList());
        log.info("待发货列表导入，运单号填写错误或不支持的数据 = {}",JSON.toJSONString(deliverNoInvalidList));
        if(!CollectionUtils.isEmpty(deliverNoInvalidList)){
            throw new BusinessException(getErrorMsg(deliverNoInvalidList,"当前运单号填写错误或不支持当前物流公司",Arrays.asList("deliveryNo")));
        }

        return Pair.of(deliverCompanyCodeAndNameMap,null);
    }

    /**
     * 校验导入数据
     * @param dataList
     */
    private CheckImportOrderDataResultDTO checkImportOrderData(List<PendingImportDTO> dataList) {
        log.info("待发货列表导入，开始校验导入的订单商品数据是否合法");
        //校验订单id和运单号一致的数据快递公司编号是否都一致
        /*Map<String, Pair<Boolean,List<PendingImportDTO>>> orderNoAndDeliverNoAndCompanyCodeIsSameMap = dataList.stream().collect(Collectors.groupingBy(data -> data.getOrderNo() + "|" + data.getDeliveryNo(), Collectors.collectingAndThen(Collectors.toList(), list -> {
            if (ListUtils.emptyIfNull(list).stream().map(PendingImportDTO::getCompanyCode).distinct().count() != 1) {
                return Pair.of(true,list);
            }
            return Pair.of(false,list);
        })));
        List<PendingImportDTO> companyCodeNotSameList = orderNoAndDeliverNoAndCompanyCodeIsSameMap.values().stream().filter(p -> p.getKey()).flatMap(x -> x.getValue().stream()).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(companyCodeNotSameList)){
            throw new BusinessException(getErrorMsg(companyCodeNotSameList,"快递公司编码不一致",Arrays.asList("orderNo","deliveryNo","companyCode")));
        }*/
        //订单编号集合
        List<String> orderIds = dataList.stream().map(PendingImportDTO::getOrderNo).distinct().collect(Collectors.toList());
        log.info("待发货列表导入，订单编号集合 = {}",JSON.toJSONString(orderIds));
        //订单id和map集合
        Map<String, LogisticsOrder> orderIdAndMap = ListUtils.emptyIfNull(logisticsOrderService.list(Wrappers.<LogisticsOrder>lambdaQuery().in(LogisticsOrder::getChildOrderId, orderIds)))
                .stream()
                .collect(Collectors.toMap(LogisticsOrder::getChildOrderId, Function.identity(), (a, b) -> a));
        log.info("待发货列表导入，订单编号和订单信息映射信息 = {}",JSON.toJSONString(orderIdAndMap));
        //过滤掉订单号不存在的记录
        List<PendingImportDTO> orderNoNotExistList = dataList.stream().filter(x -> !orderIdAndMap.containsKey(x.getOrderNo())).collect(Collectors.toList());
        log.info("待发货列表导入，校验订单编号不存在的导入数据 = {}",JSON.toJSONString(orderNoNotExistList));
        if(!CollectionUtils.isEmpty(orderNoNotExistList)){
            throw new BusinessException(getErrorMsg(orderNoNotExistList,"订单号不存在",Arrays.asList("orderNo")));
        }

        //物流订单id集合
        List<Long> logisticOrderIds = orderIdAndMap.values().stream().map(LogisticsOrder::getId).distinct().collect(Collectors.toList());
        log.info("待发货列表导入，物流订单id集合 = {}",JSON.toJSONString(logisticOrderIds));
        List<LogisticsOrderGoods> orderGoodsList = logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery().in(LogisticsOrderGoods::getLogisticsOrderId, logisticOrderIds).eq(LogisticsOrderGoods::getDeleteStatus, 0));
        log.info("待发货列表导入，根据物流订单id查询到的非删除的商品集合 = {}",JSON.toJSONString(orderGoodsList));
        //商品id和map信息
        Map<Long,LogisticsOrderGoods> goodsIdAndMap = ListUtils.emptyIfNull(orderGoodsList).stream().collect(Collectors.toMap(LogisticsOrderGoods::getId,Function.identity(),(a,b) -> a));
        log.info("待发货列表导入，物流商品id和商品集合映射 = {}",JSON.toJSONString(goodsIdAndMap));
        //订单id和商品id映射map
        Map<Long,List<LogisticsOrderGoods>> orderIdAndGoodsIdMap = ListUtils.emptyIfNull(orderGoodsList)
                .stream()
                .collect(Collectors.groupingBy(LogisticsOrderGoods::getLogisticsOrderId));
        log.info("待发货列表导入，物流订单id和商品信息映射 = {}",JSON.toJSONString(orderIdAndGoodsIdMap));
        //校验商品订单ID和商品id是否一致
        List<PendingImportDTO> orderAndGoodsNoneMatchList = dataList.stream().filter(data -> {
            Long logisticOrderId = orderIdAndMap.get(data.getOrderNo()).getId();
            if (!orderIdAndGoodsIdMap.containsKey(logisticOrderId)) {
                return true;
            }
            if (orderIdAndGoodsIdMap.get(logisticOrderId).stream().noneMatch(order -> order.getSkuId().equals(data.getSkuId()))) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        log.info("待发货列表导入，校验订单和商品不对应的导入信息 = {}",JSON.toJSONString(orderAndGoodsNoneMatchList));
        if(!CollectionUtils.isEmpty(orderAndGoodsNoneMatchList)){
            throw new BusinessException(getErrorMsg(orderAndGoodsNoneMatchList,"订单和商品不匹配或商品不存在",Arrays.asList("orderNo","skuId")));
        }
        //不支持发货的商品列表，平台物流类型不支持
        List<PendingImportDTO> noSupportGoodsList = dataList.stream().filter(data -> {
            Long logisticOrderId = orderIdAndMap.get(data.getOrderNo()).getId();
            return orderIdAndGoodsIdMap.get(logisticOrderId).stream().filter(orderGoods -> orderGoods.getSkuId().equals(data.getSkuId()) && orderGoods.getLogisticsType().intValue() != LogisticsTypeEnum.SHOP.getCode()).findAny().isPresent();
        }).collect(Collectors.toList());
        log.info("待发货列表导入，校验商品物流类型为非商户类型的导入信息 = {}",JSON.toJSONString(noSupportGoodsList));
        if(!CollectionUtils.isEmpty(noSupportGoodsList)){
            throw new BusinessException(getErrorMsg(noSupportGoodsList,"存在非商户类型的商品",Arrays.asList("orderNo","goodsId","skuId")));
        }
        //物流订单商品配送状态map
        List<LogisticsDelivery> deliverInfos = logisticsDeliveryService.list(Wrappers.<LogisticsDelivery>lambdaQuery().in(LogisticsDelivery::getLogisticsOrderId, logisticOrderIds)
                .eq(LogisticsDelivery::getDeleteStatus, 0)
                .eq(LogisticsDelivery::getIfCancel,0));
        log.info("待发货列表导入，根据物流订单id查询到的非删除的运单信息 = {}",JSON.toJSONString(deliverInfos));
        //订单物流商品id和发货信息集合
        Map<Long,Map<String,LogisticsDelivery>> goodsIdAndDeliverStatusMap = ListUtils.emptyIfNull(deliverInfos)
                .stream()
                .collect(Collectors.groupingBy(LogisticsDelivery::getLogisticsOrderId,Collectors.collectingAndThen(Collectors.toList(),list -> {
                    return ListUtils.emptyIfNull(list)
                            .stream()
                            .filter(x -> StringUtils.isNoneBlank(x.getGoodsIds()))
                            .flatMap(x -> Arrays.stream(x.getGoodsIds().split("\\,")).map(goodsId -> new Pair<>(goodsId, x)))
                            .collect(Collectors.toMap(Pair::getKey,Pair::getValue,(a,b) -> a));
                })));
        log.info("待发货列表导入，按照物流订单统计订单下每个商品的发货信息 = {}",JSON.toJSONString(goodsIdAndDeliverStatusMap));

        //查询运单信息,按照订单id和运单号分组
        Map<String, LogisticsDelivery> orderIdAnddeliverNoAndMap = ListUtils.emptyIfNull(deliverInfos).stream().collect(Collectors.toMap(x -> x.getLogisticsOrderId() + "|" + x.getDeliveryNo(), Function.identity(), (a, b) -> a));
        log.info("待发货列表导入，按照订单id和运单编号分组统计信息map = {}",JSON.toJSONString(goodsIdAndDeliverStatusMap));

        //物流订单id和运单信息map
        Map<Long,List<LogisticsDelivery>> logisticOrderIdAnDeliverListMap = ListUtils.emptyIfNull(deliverInfos)
                .stream()
                .collect(Collectors.groupingBy(LogisticsDelivery::getLogisticsOrderId));
        log.info("待发货列表导入，物流订单id和运单信息映射 = {}",JSON.toJSONString(logisticOrderIdAnDeliverListMap));
        //校验配送状态，去掉已配送的数据
        List<PendingImportDTO> shippedDataList = new ArrayList<>();
        Iterator<PendingImportDTO> iterator = dataList.iterator();
        while (iterator.hasNext()){
            PendingImportDTO pendingImportDTO = iterator.next();
            LogisticsOrder orderEntity = orderIdAndMap.get(pendingImportDTO.getOrderNo());
            Optional.ofNullable(goodsIdAndDeliverStatusMap.get(orderEntity.getId()))
                    .map(x -> x.get(String.valueOf(getGoodsIdFromSku(goodsIdAndMap,orderEntity.getId(),pendingImportDTO.getSkuId()))))
                    .filter(x -> !x.getLogisticsStatus().equals(DeliveryLogisticsStatusEnum.TO_SEND.getCode()) && x.getIfCancel() == 0)
                    .ifPresent(x -> {
                        shippedDataList.add(pendingImportDTO);
                        iterator.remove();
                        log.info("待发货列表导入，重复发货数据，已被删除 = {}",JSON.toJSONString(pendingImportDTO));
                    });
        }
        //是否无数据
        boolean noData = CollectionUtils.isEmpty(dataList);
        return new CheckImportOrderDataResultDTO(orderIdAndMap,orderIdAndGoodsIdMap,goodsIdAndDeliverStatusMap,orderIdAnddeliverNoAndMap,goodsIdAndMap,logisticOrderIdAnDeliverListMap,shippedDataList,noData);
    }
    private String getErrorMsg(List<PendingImportDTO> errorDataList,String errorMsg,List<String> fields){
        if(CollectionUtils.isEmpty(errorDataList)){
            return "";
        }
        StringBuilder errorSb = new StringBuilder("导入失败：<br/>").append(errorMsg).append(errorDataList.size()).append("条；<br/>");
        errorSb.append("<br/>错误信息：<br/>");
        String detailErrorInfo = ListUtil.sub(errorDataList, 0, Math.min(errorDataList.size(), 1000))
                .stream()
                .map(data -> {
                    StringBuilder detailSb = new StringBuilder();
//                    detailSb.append("行号：").append((data.getRowNum() + 1)).append("，");
                    String fieldMsg = ListUtils.emptyIfNull(fields).stream().map(field -> {
                        StringBuilder fileldSb = new StringBuilder();
                        Object fieldValue = ReflectUtil.getFieldValue(data, field);
                        fileldSb.append(Optional.ofNullable(ReflectUtil.getField(data.getClass(), field)).map(f -> f.getAnnotation(Excel.class)).map(x -> x.name()).orElse(field));
                        fileldSb.append("：");
                        fileldSb.append(fieldValue);
                        return fileldSb.toString();
                    }).collect(Collectors.joining("，"));
                    detailSb.append(fieldMsg);
                    return detailSb.toString();
                }).collect(Collectors.joining("；<br/>"));
        errorSb.append(detailErrorInfo);
        if(errorSb.length() > 30001){
            return StrUtil.sub(errorMsg.toString(),0,3000);
        }
        return errorSb.toString();
    }

    /**
     *
     * 查询商品id和实体映射map
     * @param goodsIds 商品id集合
     * @return
     */
    private Map<String,LogisticsOrderGoods> getGoodsIdAndMap(List<String> goodsIds){
        return Optional.ofNullable(CollectionUtils.isEmpty(goodsIds) ? null : goodsIds)
                .map(ids -> {
                    return ListUtils.emptyIfNull(logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery().in(LogisticsOrderGoods::getSkuId,ids)
                                    .eq(LogisticsOrderGoods::getDeleteStatus,0)))
                            .stream()
                            .collect(Collectors.toMap(LogisticsOrderGoods::getSkuId,Function.identity(),(a,b) -> a));
                }).orElse(Collections.emptyMap());
    }

    private Long getGoodsIdFromSku(Map<Long,LogisticsOrderGoods> goodsIdAndMap,Long logisticOrderId,String skuId){
        return goodsIdAndMap.entrySet().stream()
                .filter(entry -> entry.getValue().getSkuId().equals(skuId) && entry.getValue().getLogisticsOrderId().equals(logisticOrderId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }



    @Override
    public Response<List<DeliveryCompanyResData>> getDeliveryCompanyList(DeliveryCompanyListReqData req) {
        return Response.ok(ListUtils.emptyIfNull(logisticsDeliveryCompanyService.list(Wrappers.<LogisticsDeliveryCompany>lambdaQuery()
                .like(StringUtils.isNotBlank(req.getDeliveryCompanyCode()),LogisticsDeliveryCompany::getCompanyCode,req.getDeliveryCompanyCode())
                .like(StringUtils.isNotBlank(req.getDeliveryCompanyName()),LogisticsDeliveryCompany::getCompanyName,req.getDeliveryCompanyName())))
                .stream()
                .map(info -> new DeliveryCompanyResData(info.getCompanyCode(),info.getCompanyName(),info.getInitials(),null))
                .collect(Collectors.toList()));
    }

    @Override
    public Response<List<BatchReadySendOrderDetailResData>> queryBatchReadySendOrderList(BatchReadySendOrderDetailReqData req) {
        List<DeliveryNoSendDetailVO> deliveryNoSendDetailVOList = ListUtils.emptyIfNull(req.getChildOrderIdList())
                .stream()
                .map(childOrderId -> {
                    DeliveryNoSendDetailReq deliveryNoSendDetailReq = new DeliveryNoSendDetailReq();
                    deliveryNoSendDetailReq.setChildOrderId(childOrderId);
                    deliveryNoSendDetailReq.setDesensitizedFlag(false);
                    return Optional.ofNullable(logisticsCommonDeliveryService.getNoSendDetail(deliveryNoSendDetailReq)).map(Response::getData).orElse(null);
                })
                .filter(info -> Objects.nonNull(info))
                .collect(Collectors.toList());
        Map<String, DeliveryNoSendDetailVO> childOrderIdAndDeliveryNoSendDetailVOMap = ListUtils.emptyIfNull(deliveryNoSendDetailVOList).stream().collect(Collectors.toMap(DeliveryNoSendDetailVO::getChildOrderId, Function.identity(), (a, b) -> a));
        return Response.ok(ListUtils.emptyIfNull(deliveryNoSendDetailVOList)
                .stream()
                .flatMap(info -> info.getList().stream())
                .collect(Collectors.groupingBy(DeliveryNoSendListDetailVO::getLogisticsTypeCode, Collectors.collectingAndThen(Collectors.toList(), list -> {
                    return ListUtils.emptyIfNull(list)
                            .stream()
                            .map(info -> {
                                BatchReadySendOrderDetailListVO batchReadySendOrderDetailListVO = new BatchReadySendOrderDetailListVO();
                                batchReadySendOrderDetailListVO.setChildOrderId(info.getChildOrderId());
                                batchReadySendOrderDetailListVO.setReceiveName(info.getReceiveName());
                                batchReadySendOrderDetailListVO.setReceiveAddress(info.getReceiveAddress());
                                batchReadySendOrderDetailListVO.setReceivePhone(info.getReceivePhone());
                                batchReadySendOrderDetailListVO.setShopId(info.getShopId());
                                batchReadySendOrderDetailListVO.setOrderCreateTime(info.getOrderCreateTime());
                                Optional.ofNullable(childOrderIdAndDeliveryNoSendDetailVOMap).map(map -> map.get(info.getChildOrderId())).ifPresent(deliveryNoSendDetailVO -> {
                                    batchReadySendOrderDetailListVO.setSellType(deliveryNoSendDetailVO.getSellType());
                                    batchReadySendOrderDetailListVO.setSellTypeShow(deliveryNoSendDetailVO.getSellTypeShow());
                                    batchReadySendOrderDetailListVO.setOrderDesc(deliveryNoSendDetailVO.getOrderDesc());
                                });
                                batchReadySendOrderDetailListVO.setSendGoodList(info.getSendGoodList());
                                return batchReadySendOrderDetailListVO;
                            })
                            .collect(Collectors.toList());
                })))
                .entrySet()
                .stream()
                .map(entity -> {
                    BatchReadySendOrderDetailResData batchReadySendOrderDetailResData = new BatchReadySendOrderDetailResData();
                    boolean isAllSend = entity.getValue().stream()
                            .flatMap(x -> ListUtils.emptyIfNull(x.getSendGoodList()).stream())
                            .noneMatch(x -> 0 == Optional.ofNullable(x.getLogisticsStatusCode()).orElse(0));
                    batchReadySendOrderDetailResData.setIsAllSend(isAllSend);
                    batchReadySendOrderDetailResData.setLogisticsTypeCode(entity.getKey());
                    batchReadySendOrderDetailResData.setLogisticsTypeName(Optional.ofNullable(LogisticsTypeEnum.fromCode(entity.getKey())).map(LogisticsTypeEnum::getDesc).orElse(null));
                    batchReadySendOrderDetailResData.setOrderGoodsList(entity.getValue());
                    return batchReadySendOrderDetailResData;
                }).collect(Collectors.toList()));
    }
}

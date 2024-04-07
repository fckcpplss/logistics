package com.longfor.c10.lzyx.logistics.core.service.merchant.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.longfor.c10.lzyx.logistics.core.factory.DeliveryFactory;
import com.longfor.c10.lzyx.logistics.core.service.IDeliveryService;
import com.longfor.c10.lzyx.logistics.core.service.merchant.ILogisticsMerchantPrintService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsEbillService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.DeliveryPrintReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.DeliveryPrintResData;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.ExpressEBillReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.ExpressEBillResData;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsEbill;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeliveryLogisticsStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.response.Response;
import com.lop.open.api.sdk.LopException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商户端打印接口实现类
 * @author zhaoyl
 * @date 2022/4/29 下午6:50
 * @since 1.0
 */
@Slf4j
@Service
public class LogisticsMerchantPrintServiceImpl implements ILogisticsMerchantPrintService {

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;

    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    private ILogisticsEbillService logisticsEbillService;

    @Autowired
    private DeliveryFactory deliveryFactory;
    @Override
    public List<ExpressEBillResData> print(ExpressEBillReqData data){
        log.info("打印电子面单，请求参数, params = {}", JSON.toJSONString(data));
        List<ExpressEBillResData> resData = new ArrayList<>();
        List<LogisticsDelivery> deliveryList = getAndCheckDeliveryByNos(data.getDeliveryNos(),data.getShopIds());
        deliveryList.stream().collect(Collectors.groupingBy(LogisticsDelivery::getCompanyCode)).forEach((k,v) -> {
            IDeliveryService deliveryService = deliveryFactory.getService(CompanyCodeEnum.fromCode(k));
            DeliveryPrintReqData deliveryPrintReqData = new DeliveryPrintReqData();
            deliveryPrintReqData.setDeliveryList(v);
            deliveryPrintReqData.setAmUserInfo(data.getAmUserInfo());
            DeliveryPrintResData deliveryPrintResData = null;
            try {
                deliveryPrintResData = deliveryService.getPrintData(deliveryPrintReqData);
            } catch (LopException e) {
                log.error("打印电子面单，查询打印数据异常,{}",e);
            } catch (UnsupportedEncodingException e) {
                log.error("打印电子面单，查询打印数据异常,{}",e);
            }
            if(Objects.isNull(deliveryPrintResData)){
                return;
            }
            ExpressEBillResData expressEBillResData = new ExpressEBillResData();
            BeanUtils.copyProperties(deliveryPrintResData,expressEBillResData);
            resData.add(expressEBillResData);
        });
        return resData;
    }

    @Override
    public Response<List<LogisticsEbill>> pdf(ExpressEBillReqData req) {
        List<LogisticsDelivery> printData = getAndCheckDeliveryByNos(req.getDeliveryNos(),req.getShopIds());
        return Response.ok(logisticsEbillService.list(Wrappers.<LogisticsEbill>lambdaQuery()
                .eq(LogisticsEbill::getStatus, 2)
                .in(LogisticsEbill::getDeliveryNo, printData.stream().map(LogisticsDelivery::getDeliveryNo).collect(Collectors.toList()))));
    }

    private List<LogisticsDelivery> getAndCheckDeliveryByNos(List<String> deliveryNos,List<String> shopIds) {
        List<LogisticsDelivery> deliveryLists = logisticsDeliveryService.list(Wrappers.<LogisticsDelivery>lambdaQuery()
                .in(LogisticsDelivery::getDeliveryNo, deliveryNos)
                .in(LogisticsDelivery::getLogisticsStatus, Arrays.asList(DeliveryLogisticsStatusEnum.SENDED.getCode(), DeliveryLogisticsStatusEnum.TO_RECEIVED.getCode()))
                .eq(LogisticsDelivery::getIfCancel, 0));
        if(CollectionUtils.isEmpty(deliveryLists)){
            throw  new BusinessException("运单不存在");
        }
        List<Long> logisticsOrderId = deliveryLists.stream().map(LogisticsDelivery::getLogisticsOrderId).distinct().collect(Collectors.toList());
        Map<Long, LogisticsOrder> logisticsOrderIdAndMap = ListUtils.emptyIfNull(logisticsOrderService.listByIds(logisticsOrderId)).stream().collect(Collectors.toMap(LogisticsOrder::getId, Function.identity(), (a, b) -> a));
        return deliveryLists.stream()
                .map(item -> {
                    Optional.ofNullable(logisticsOrderIdAndMap.get(item.getLogisticsOrderId())).ifPresent(order -> {
                        item.setChildOrderId(order.getChildOrderId());
                    });
                    return item;
                })
                .collect(Collectors.toList());
    }

}

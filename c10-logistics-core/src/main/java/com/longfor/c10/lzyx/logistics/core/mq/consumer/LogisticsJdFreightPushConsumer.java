package com.longfor.c10.lzyx.logistics.core.mq.consumer;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.longfor.c10.lzyx.logistics.core.config.RedisDistributedLocker;
import com.longfor.c10.lzyx.logistics.core.factory.DeliveryFactory;
import com.longfor.c10.lzyx.logistics.core.service.IDeliveryService;
import com.longfor.c10.lzyx.logistics.dao.service.*;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.FeeResultReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.FeeResultResData;
import com.longfor.c10.lzyx.logistics.entity.dto.jingdong.JdFeeReqData;
import com.longfor.c10.lzyx.logistics.entity.entity.*;
import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeleteStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.UpdateFeeStatusEnum;
import com.longfor.c2.starter.rocketmq.annotation.MessageListener;
import com.longfor.c2.starter.rocketmq.annotation.RocketListener;
import com.longfor.c2.starter.rocketmq.message.ReceivedMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * 读取京东费率并落库
 * 由job发起调用
 * @author zhaoyalong
 */
@Slf4j
@Component
@RocketListener(groupID = "GID_c10_logistics_update_jd_fee")
public class LogisticsJdFreightPushConsumer {

    @Autowired
    private ILogisticsFeeService logisticsFeeService;

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;

    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    IShopLogisticsService shopLogisticsService;

    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Value("${jd.delivery.discount:0.5}")
    private Double discount;

    @Value("${jd.delivery.sellerNo:010K1435980}")
    private String sellerNo;

    @Autowired
    private DeliveryFactory deliveryFactory;

    @Autowired
    private RedisDistributedLocker distributedLocker;

    @MessageListener(topic = "c10_logistics_update_jd_fee", orderConsumer = true)
    public void jdFreightPushMq(ReceivedMessage message) {
        log.info("京东费率更新，调用参数 = {}", JSON.toJSONString(message));
        LogisticsDelivery logisticsDelivery = JSONObject.parseObject(message.getBody(), LogisticsDelivery.class);
        if (Objects.isNull(logisticsDelivery) || logisticsDelivery.getIfCancel() == 1 || logisticsDelivery.getUpdateFeeStatus() == UpdateFeeStatusEnum.UPDATE_SUC.getCode()) {
            log.info("京东费率更新，运单不存在或已取消或费用已更新，无需处理");
            return;
        }
        //运单信息
        if(!distributedLocker.tryLock(logisticsDelivery.getDeliveryNo(),true)){
            log.info("京东费率更新,加锁失败");
            return;
        }
        try {
            //获取京东物流服务
            IDeliveryService deliveryService = deliveryFactory.getService(CompanyCodeEnum.JD);
            //查询物流运费信息
            LogisticsFee logisticsFee = Optional.ofNullable(logisticsFeeService.getOne(Wrappers.<LogisticsFee>lambdaQuery()
                    .eq(LogisticsFee::getDeliveryNo,logisticsDelivery.getDeliveryNo())
                    .eq(LogisticsFee::getDeleteStatus,0)
                    .last(" limit 1"))).orElseGet(() ->{
                        LogisticsFee lf = new LogisticsFee();
                        lf.setCreateTime(DateUtil.date());
                        lf.setCreatorAccount("system");
                        lf.setCreatorName("system");
                        return lf;
                    });
            log.info("京东费率更新，查询存在的费率信息 = {}", JSON.toJSONString(logisticsFee));

            //查询物流订单信息
            LogisticsOrder logisticsOrder = logisticsOrderService.getById(logisticsDelivery.getLogisticsOrderId());
            if(Objects.isNull(logisticsOrder)){
                log.info("京东费率更新，订单信息不存在，logisticsOrderId= {}",logisticsDelivery.getLogisticsOrderId());
            }
            //查询物流订单商品
            LogisticsOrderGoods logisticsOrderGoods = logisticsOrderGoodsService.getOne(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                    .eq(LogisticsOrderGoods::getLogisticsDeliveryId,logisticsDelivery.getId())
                    .last(" limit 1"));
            log.info("京东费率更新，订单商品信息 = {}", JSON.toJSONString(logisticsOrderGoods));
            logisticsFee.setDeliveryNo(logisticsDelivery.getDeliveryNo());
            logisticsFee.setLogisticsStatus(logisticsDelivery.getLogisticsStatus());
            logisticsFee.setShipAddress(StringUtils.join(logisticsDelivery.getSendProvince(), logisticsDelivery.getSendCity(), logisticsDelivery.getSendArea(), logisticsDelivery.getSendAddress()));
            logisticsFee.setDeliveryAddress(StringUtils.join(logisticsDelivery.getReceiptProvince(), logisticsDelivery.getReceiptCity(), logisticsDelivery.getReceiptArea(), logisticsDelivery.getReceiptAddress()));
            logisticsFee.setLogisticsAccount(getShopAccount(logisticsDelivery.getShopLogisticsId()));
            Optional.ofNullable(logisticsOrderGoods).ifPresent(goods -> {
                logisticsFee.setGoodsName(goods.getGoodsName());
                logisticsFee.setFeeBearer(goods.getLogisticsType());
            });
            Optional.ofNullable(logisticsOrder).ifPresent(order -> {
                logisticsFee.setChildOrderId(order.getChildOrderId());
                logisticsFee.setOrgId(order.getOrgId());
                logisticsFee.setOrgName(order.getOrgName());
                logisticsFee.setShopId(order.getShopId());
            });

            FeeResultResData feeResultResData = deliveryService.queryFeeResult(new FeeResultReqData(logisticsDelivery.getDeliveryNo(),String.valueOf(logisticsDelivery.getId())));
            Optional.ofNullable(feeResultResData)
                    .map(FeeResultResData::getFeeInfoList)
                    .map(feeInfos -> {
                        return ListUtils.emptyIfNull(feeInfos).stream().findFirst().orElse(null);
                    })
                    .ifPresent(feeInfo -> {
                        logisticsFee.setStandardFee(feeInfo.getStandardAmount());
                        if("YF".equals(feeInfo.getCostClassify())){
                            logisticsFee.setDiscountFee(feeInfo.getActualAmount().multiply(BigDecimal.valueOf(discount)));
                        }else{
                            logisticsFee.setDiscountFee(feeInfo.getStandardAmount());
                        }
                    });
            logisticsFee.setLogisticsCompanyCode(CompanyCodeEnum.JD.getCode());
            logisticsFee.setLogisticsCompanyName(CompanyCodeEnum.JD.getDesc());
            logisticsFee.setUpdateAccount("system");
            logisticsFee.setUpdateName("system");
            logisticsFee.setUpdateTime(DateUtil.date());
            //固定 月结、寄付
            logisticsFee.setPaymentType(1);
            logisticsFee.setSettlementType(2);
            logisticsFee.setDeleteStatus(DeleteStatusEnum.NO.getCode());
            log.info("京东费率更新，添加或更新数据 = {}", JSON.toJSONString(logisticsFee));
            logisticsFeeService.saveOrUpdate(logisticsFee);

            LogisticsDelivery deliveryUpdate = new LogisticsDelivery();
            deliveryUpdate.setId(logisticsDelivery.getId());
            deliveryUpdate.setUpdateFeeStatus(UpdateFeeStatusEnum.UPDATE_SUC.getCode());
            logisticsDeliveryService.updateById(deliveryUpdate);
            log.info("京东费率更新，更新运单表费用更新状态成功");
        } catch (Exception e) {
            Optional.ofNullable(logisticsDelivery).map(LogisticsDelivery::getId).ifPresent(deliveryId -> {
                LogisticsDelivery deliveryUpdate = new LogisticsDelivery();
                deliveryUpdate.setId(deliveryId);
                deliveryUpdate.setUpdateFeeStatus(UpdateFeeStatusEnum.UPDATE_FAIL.getCode());
                logisticsDeliveryService.updateById(deliveryUpdate);
            });
            log.info("京东费率更新，更新运单表费用更新状态失败",e);
        } finally {
            distributedLocker.unlock(logisticsDelivery.getDeliveryNo());
            log.info("京东费率更新，解锁成功");
        }
    }

    /**
     * 获取供应商账号信息
     * @param shopLogisticsId 商户物流配置id
     * @return ShopLogisticsDTO
     */
    private String getShopAccount(String shopLogisticsId){
        return Optional.ofNullable(StrUtil.emptyToDefault(shopLogisticsId,null))
                .map(x -> shopLogisticsService.getById(x))
                .map(ShopLogistics::getAccount)
                .orElse(null);
    }
}

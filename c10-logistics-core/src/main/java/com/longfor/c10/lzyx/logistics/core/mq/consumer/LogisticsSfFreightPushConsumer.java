package com.longfor.c10.lzyx.logistics.core.mq.consumer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.longfor.c10.lzyx.logistics.core.config.RedisDistributedLocker;
import com.longfor.c10.lzyx.logistics.core.service.impl.delivery.SfDeliveryServiceImpl;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsFeeService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderGoodsService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.shunfeng.FeeInfoDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.shunfeng.SFFreightPushDTO;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsFee;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import com.longfor.c10.lzyx.order.entity.enums.ReceiptTypeEnum;
import com.longfor.c2.starter.rocketmq.annotation.MessageListener;
import com.longfor.c2.starter.rocketmq.annotation.RocketListener;
import com.longfor.c2.starter.rocketmq.message.ReceivedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * 顺丰费用推送
 */
@Slf4j
@Component
@RocketListener(groupID = "GID_c10_logistics_sf_freight_push")
public class LogisticsSfFreightPushConsumer {
    @Autowired
    private ILogisticsFeeService logisticsFeeService;
    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;
    @Autowired
    private SfDeliveryServiceImpl sfDeliveryService;
    @Autowired
    private ILogisticsOrderService logisticsOrderService;
    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Autowired
    private RedisDistributedLocker distributedLocker;

    @Value("${sf.delivery.discount:1}")
    private Double discount;

    @MessageListener(topic = "c10_logistics_sf_freight_push", orderConsumer = true)
    public void sfFreightPushMq(ReceivedMessage message) {
        log.info("顺丰费用更新，调用参数 = {}",JSON.toJSONString(message));
        SFFreightPushDTO sfFreightPushDTO = JSONObject.parseObject(message.getBody(), SFFreightPushDTO.class);
        if (Objects.isNull(sfFreightPushDTO) || CollectionUtils.isEmpty(sfFreightPushDTO.getFeeList())) {
            log.info("顺丰费用更新，费用信息为空 = {}",JSON.toJSONString(sfFreightPushDTO));
            return;
        }
        if(!distributedLocker.tryLock(sfFreightPushDTO.getWaybillNo(),true)){
            log.info("顺丰费用更新,加锁失败");
            return;
        }
        try {
            //查询运单信息
            LogisticsDelivery logisticsDelivery = logisticsDeliveryService.getById(sfFreightPushDTO.getOrderNo());
            log.info("顺丰费用更新，运单信息， logisticsDelivery= {}",JSON.toJSONString(logisticsDelivery));
            if(Objects.isNull(logisticsDelivery) || logisticsDelivery.getIfCancel() == 1){
                log.error("顺丰费用更新，运单不存在或已取消， deliveryNo= {}",sfFreightPushDTO.getOrderNo());
                return;
            }
            //查询物流订单
            LogisticsOrder logisticsOrder = logisticsOrderService.getById(logisticsDelivery.getLogisticsOrderId());
            log.info("顺丰费用更新，运单信息， logisticsFee= {}",JSON.toJSONString(logisticsOrder));
            if(Objects.isNull(logisticsOrder)){
                log.error("顺丰费用更新，物流订单不存在， logisticsOrderId= {}",logisticsDelivery.getLogisticsOrderId());
                return;
            }

            LogisticsFee logisticsFee = logisticsFeeService.getOne(Wrappers.<LogisticsFee>lambdaQuery().eq(LogisticsFee::getDeliveryNo,sfFreightPushDTO.getWaybillNo()).eq(LogisticsFee::getDeleteStatus,0));
            log.info("顺丰费用更新，运费信息， logisticsFee= {}",JSON.toJSONString(logisticsFee));
            logisticsFee = ObjectUtil.defaultIfNull(logisticsFee,new LogisticsFee());
            //查询费用

            //标准费用
            double standardFee = sfFreightPushDTO.getFeeList().stream().mapToDouble(FeeInfoDTO::getFeeAmt).count();
            //折扣费用
            double discountFee = sfFreightPushDTO.getFeeList().stream().mapToDouble(dto -> {
                if(ReceiptTypeEnum.PLATFORM_LOGISTICS.getCode().equals(logisticsDelivery.getLogisticsType())){
                    return dto.getFeeAmt() * discount;
                }else{
                    return dto.getFeeAmt();
                }
            }).count();

            LogisticsOrderGoods logisticsOrderGoods = logisticsOrderGoodsService.getOne(Wrappers.<LogisticsOrderGoods>lambdaQuery().eq(LogisticsOrderGoods::getLogisticsDeliveryId,logisticsDelivery.getId()).last(" limit 1"));
            if (Objects.nonNull(logisticsOrderGoods)) {
                logisticsFee.setGoodsName(logisticsOrderGoods.getGoodsName());
                logisticsFee.setFeeBearer(logisticsOrderGoods.getLogisticsType());
            }

            logisticsFee.setChildOrderId(logisticsOrder.getChildOrderId());
            logisticsFee.setLogisticsCompanyCode(CompanyCodeEnum.SF.getCode());
            logisticsFee.setLogisticsCompanyName(CompanyCodeEnum.SF.getDesc());
            logisticsFee.setStandardFee(BigDecimal.valueOf(standardFee));
            logisticsFee.setDiscountFee(BigDecimal.valueOf(discountFee));
            logisticsFee.setPaymentType(Optional.ofNullable(StrUtil.emptyToDefault(sfFreightPushDTO.getFeeList().get(0).getPaymentTypeCode(),null)).map(Integer::parseInt).orElse(null));
            logisticsFee.setSettlementType(Optional.ofNullable(StrUtil.emptyToDefault(sfFreightPushDTO.getFeeList().get(0).getSettlementTypeCode(),null)).map(Integer::parseInt).orElse(null));
            logisticsFee.setLogisticsStatus(logisticsDelivery.getLogisticsStatus());
            logisticsFee.setLogisticsAccount(sfFreightPushDTO.getCustomerAcctCode());
            // TODO: 2022/1/21 地址加密，许解密
            logisticsFee.setShipAddress(sfFreightPushDTO.getConsignorAddr());//发件人
            logisticsFee.setDeliveryAddress(sfFreightPushDTO.getAddresseeAddr());//收货人
            logisticsFee.setOrgId(logisticsOrder.getOrgId());
            logisticsFee.setOrgName(logisticsOrder.getOrgName());
            logisticsFee.setShopId(logisticsOrder.getShopId());
            logisticsFee.setCreatorAccount("system");
            logisticsFee.setCreatorName("system");
            logisticsFee.setUpdateAccount("system");
            logisticsFee.setUpdateName("system");
            logisticsFee.setUpdateTime(DateUtil.date());
            log.info("顺丰费用更新，添加或更新数据， logisticsFee= {}",JSON.toJSONString(logisticsFee));
            logisticsFeeService.saveOrUpdate(logisticsFee);
        } catch (Exception e) {
            log.error("运费消费任务异常: ", e);
        } finally {
            distributedLocker.unlock(sfFreightPushDTO.getWaybillNo());
            log.info("顺丰费用更新，解锁成功");

        }
    }
}

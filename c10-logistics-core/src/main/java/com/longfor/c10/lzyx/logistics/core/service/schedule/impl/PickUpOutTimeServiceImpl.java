package com.longfor.c10.lzyx.logistics.core.service.schedule.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.longfor.c10.lzyx.logistics.core.mq.producer.LogisticsStatusChangeProducer;
import com.longfor.c10.lzyx.logistics.core.service.schedule.IPickUpOutTimeService;
import com.longfor.c10.lzyx.logistics.dao.mapper.LogisticsVerifyOrderMapper;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsVerifyOrderGoodsService;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.OrderStatusChangePushDTO;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsVerifyOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsVerifyOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author lanxiaolong
 * @Date 2022/4/18 2:07 下午
 */
@Service
@Slf4j
public class PickUpOutTimeServiceImpl implements IPickUpOutTimeService {
    @Resource
    private ILogisticsVerifyOrderGoodsService logisticsVerifyOrderGoodsService;

    @Resource
    private LogisticsVerifyOrderMapper logisticsVerifyOrderMapper;

    @Resource
    private LogisticsStatusChangeProducer logisticsStatusChangeProducer;

    @Override
    public void dealPickUpOutTime() {
        LambdaQueryWrapper<LogisticsVerifyOrderGoods> orderGoodsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderGoodsLambdaQueryWrapper.le(LogisticsVerifyOrderGoods::getPickupEndTime, new Date());
        orderGoodsLambdaQueryWrapper.eq(LogisticsVerifyOrderGoods::getVerifyStatus, LogisticsVerifyVerifyStatusEnum.VERIFY_NO.getCode());
        orderGoodsLambdaQueryWrapper.eq(LogisticsVerifyOrderGoods::getRefundStatus, LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode());
        List<LogisticsVerifyOrderGoods> orderGoodsList = logisticsVerifyOrderGoodsService.list(orderGoodsLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(orderGoodsList)) {
            return;
        }

        for (LogisticsVerifyOrderGoods orderGoods : orderGoodsList) {
            LambdaUpdateWrapper<LogisticsVerifyOrderGoods> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(LogisticsVerifyOrderGoods::getVerifyStatus, LogisticsVerifyVerifyStatusEnum.VERIFY_TIMEOUT.getCode());
            updateWrapper.eq(LogisticsVerifyOrderGoods::getId, orderGoods.getId());
            LogisticsVerifyOrderGoods entity = new LogisticsVerifyOrderGoods();
            entity.setUpdateTime(new Date());
            logisticsVerifyOrderGoodsService.update(entity, updateWrapper);

            LambdaQueryWrapper<LogisticsVerifyOrder> orderLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderLambdaQueryWrapper.eq(LogisticsVerifyOrder::getChildOrderId, orderGoods.getChildOrderId());
            orderLambdaQueryWrapper.last("limit 1");
            LogisticsVerifyOrder order = logisticsVerifyOrderMapper.selectOne(orderLambdaQueryWrapper);
            if (order == null || LogisticsVerifyVerifyStatusEnum.VERIFY_TIMEOUT.getCode().equals(order.getVerifyStatus())) {
                continue;
            }

            LambdaUpdateWrapper<LogisticsVerifyOrder> orderUpdateWrapper = new LambdaUpdateWrapper<>();
            orderUpdateWrapper.set(LogisticsVerifyOrder::getVerifyStatus, LogisticsVerifyVerifyStatusEnum.VERIFY_TIMEOUT.getCode());
            orderUpdateWrapper.eq(LogisticsVerifyOrder::getChildOrderId, orderGoods.getChildOrderId());
            LogisticsVerifyOrder orderEntity = new LogisticsVerifyOrder();
            orderEntity.setUpdateTime(new Date());
            int orderUpdateCount = logisticsVerifyOrderMapper.update(orderEntity, orderUpdateWrapper);
            if (orderUpdateCount > 0) {
                //发送自提订单待自提消息通知
                OrderStatusChangePushDTO orderStatusChangePushDTO = new OrderStatusChangePushDTO();
                orderStatusChangePushDTO.setChildOrderId(orderGoods.getChildOrderId());
                orderStatusChangePushDTO.setLogisticsType(LogisticsStatusChangeTypeEnum.FORWARD.getCode());
                if (GoodsTypeEnum.GOODS.getCode().equals(orderGoods.getGoodsType())) {
                    //商品类型待自提
                    orderStatusChangePushDTO.setOrderStatus(VerifyOrderStatusEnum.PICKUP_TIMEOUT.getCode());
                } else {
                    //卡券类型待核销
                    orderStatusChangePushDTO.setOrderStatus(VerifyOrderStatusEnum.VERIFY_TIMEOUT.getCode());
                }
                orderStatusChangePushDTO.setStatusTime(LocalDateTimeUtil.now());
                logisticsStatusChangeProducer.send(orderStatusChangePushDTO);
            }
        }
    }

}

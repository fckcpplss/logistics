package com.longfor.c10.lzyx.logistics.core.schedule;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.logistics.core.mq.producer.UpdateJdFeeProducer;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderService;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeleteStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeliveryLogisticsStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.UpdateFeeStatusEnum;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 京东费用更新
 * @author zhaoyalong
 */
@Slf4j
@Component
@JobHandler(value = "jdFeeUpdateHandler")
public class JdFeeUpdateHandler extends IJobHandler {

    @Autowired
    ILogisticsOrderService logisticsOrderService;

    @Autowired
    ILogisticsDeliveryService logisticsDeliveryService;

    @Autowired
    UpdateJdFeeProducer updateJdFeeProducer;


    //每页查询大小
    private static final int PER_QUERY_SIZE = 1000;

    @Override
    public ReturnT<String> execute(String s) throws Exception{
        log.info("京东费用更新，定时任务开始执行");
        LambdaQueryWrapper<LogisticsDelivery> lambdaQueryWrapper =  buildLambdaQueryWrapper();
        log.info("京东费用更新,构造查询参数,lambdaQueryWrapper = {}", JSON.toJSONString(lambdaQueryWrapper));
        Integer total = logisticsDeliveryService.count(lambdaQueryWrapper);
        log.info("京东费用更新，待更新条数，total = {}",total);
        if (total == 0){
            return SUCCESS;
        }
        long times = (total + PER_QUERY_SIZE - 1) / PER_QUERY_SIZE;
        //批量查询运单信息
        List<LogisticsDelivery> deliverInfos = IntStream.range(1, (int) times + 1)
                .mapToObj(index -> {
                    Page<LogisticsDelivery> pageList = logisticsDeliveryService.page(new Page<>(index, PER_QUERY_SIZE), lambdaQueryWrapper);
                    return pageList.getRecords();
                })
                .filter(list -> !CollectionUtils.isEmpty(list))
                .flatMap(list -> {
                    return list.stream();
                })
                .collect(Collectors.toList());
        List<String> deliverNos = ListUtils.emptyIfNull(deliverInfos).stream().map(LogisticsDelivery::getDeliveryNo).distinct().collect(Collectors.toList());
        log.info("京东费用更新，待更新运单编号集合，deliverNos = {}",JSON.toJSONString(deliverNos));
        deliverInfos.forEach(deliveryEntity -> {
            updateJdFeeProducer.sendMessage(deliveryEntity);
        });
        return SUCCESS;
    }

    //构造查询参数
    private LambdaQueryWrapper<LogisticsDelivery> buildLambdaQueryWrapper() {
        LambdaQueryWrapper<LogisticsDelivery> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(LogisticsDelivery::getCompanyCode, CompanyCodeEnum.JD.getCode());
        lambdaQueryWrapper.in(LogisticsDelivery::getUpdateFeeStatus,Arrays.asList(UpdateFeeStatusEnum.NEVER_UPDATE.getCode(), UpdateFeeStatusEnum.UPDATEING.getCode()));
        lambdaQueryWrapper.eq(LogisticsDelivery::getIfCancel,0);
        lambdaQueryWrapper.eq(LogisticsDelivery::getDeleteStatus, DeleteStatusEnum.NO.getCode());
        lambdaQueryWrapper.in(LogisticsDelivery::getLogisticsStatus,Arrays.asList(
                DeliveryLogisticsStatusEnum.RECEIVED.getCode(),
                DeliveryLogisticsStatusEnum.TRANSPORTING.getCode(),
                DeliveryLogisticsStatusEnum.SENDING.getCode(),
                DeliveryLogisticsStatusEnum.SIGNED.getCode(),
                DeliveryLogisticsStatusEnum.SIGNED_FAIL.getCode()));
        lambdaQueryWrapper.last(" and delivery_no != '' and delivery_no is not null and shop_logistics_id is not null");
        return lambdaQueryWrapper;
    }
}

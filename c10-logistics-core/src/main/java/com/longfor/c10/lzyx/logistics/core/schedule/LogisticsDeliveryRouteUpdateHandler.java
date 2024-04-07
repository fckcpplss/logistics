package com.longfor.c10.lzyx.logistics.core.schedule;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.logistics.core.mq.producer.UpdateDeliverRouteProducer;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryService;
import com.longfor.c10.lzyx.logistics.entity.constant.CommonConstant;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.DeliveryDTO;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeleteStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeliveryLogisticsStatusEnum;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 物流轨迹更新handler
 * @author heandong
 */
@JobHandler(value = "logisticsDeliveryRouteUpdateHandler")
@Component
@Slf4j
public class LogisticsDeliveryRouteUpdateHandler extends IJobHandler {
    @Resource
    ILogisticsDeliveryService logisticsDeliveryService;
    @Resource
    UpdateDeliverRouteProducer updateDeliverRouteProducer;
    //每页查询大小
    private static final int PER_QUERY_SIZE = 1000;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        log.info("物流轨迹定时更新,开始执行...");
        LambdaQueryWrapper<LogisticsDelivery> lambdaQueryWrapper = buildLambdaQueryWrapper();
        long total = logisticsDeliveryService.count(lambdaQueryWrapper);
        log.info("物流轨迹定时更新,待更新条数，total = {}",total);
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
        ListUtils.emptyIfNull(deliverInfos).stream().forEach(item -> {
            DeliveryDTO deliveryDTO = new DeliveryDTO();
            deliveryDTO.setSource(CommonConstant.XXL_JOB_ROUT_SOURCE);
            deliveryDTO.setLogisticsDeliveryId(String.valueOf(item.getId()));
            deliveryDTO.setDeliveryNo(item.getDeliveryNo());
            deliveryDTO.setShopLogisticsId(item.getShopLogisticsId());
            deliveryDTO.setCompanyCode(item.getCompanyCode());
            if (StringUtils.isBlank(item.getShopLogisticsId())) {
                deliveryDTO.setSourceCompany(CompanyCodeEnum.KD100.getCode());
            } else {
                deliveryDTO.setSourceCompany(item.getCompanyCode());
            }
            updateDeliverRouteProducer.sendMessage(deliveryDTO);
        });
        log.info("物流轨迹定时更新，执行完成");
        return SUCCESS;
    }
    //构造查询参数
    private LambdaQueryWrapper<LogisticsDelivery> buildLambdaQueryWrapper() {
        LambdaQueryWrapper<LogisticsDelivery> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(LogisticsDelivery::getIfCancel,0);
        lambdaQueryWrapper.eq(LogisticsDelivery::getDeleteStatus, DeleteStatusEnum.NO.getCode());
        lambdaQueryWrapper.in(LogisticsDelivery::getLogisticsStatus,Arrays.asList(
                DeliveryLogisticsStatusEnum.SENDED.getCode(),
                DeliveryLogisticsStatusEnum.TO_RECEIVED.getCode(),
                DeliveryLogisticsStatusEnum.RECEIVED.getCode(),
                DeliveryLogisticsStatusEnum.TRANSPORTING.getCode(),
                DeliveryLogisticsStatusEnum.SENDING.getCode()));
        lambdaQueryWrapper.ge(LogisticsDelivery::getUpdateTime,DateUtil.offsetDay(DateUtil.date(),-15));
        lambdaQueryWrapper.le(LogisticsDelivery::getUpdateTime,DateUtil.offsetDay(DateUtil.date(),-1));
        return lambdaQueryWrapper;
    }
}

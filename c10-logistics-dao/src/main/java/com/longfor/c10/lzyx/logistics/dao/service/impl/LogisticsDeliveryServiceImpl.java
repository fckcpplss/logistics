package com.longfor.c10.lzyx.logistics.dao.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsDeliveryOvertimeSignedDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsDeliveryOvertimeSignedReqData;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.dao.mapper.LogisticsDeliveryMapper;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 物流订单运单表 服务实现类
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
@Service
public class LogisticsDeliveryServiceImpl extends ServiceImpl<LogisticsDeliveryMapper, LogisticsDelivery> implements ILogisticsDeliveryService {
    @Autowired
    private LogisticsDeliveryMapper logisticsDeliveryMapper;

    @Override
    public IPage<LogisticsDeliveryOvertimeSignedDTO> getOvertimeSignedList(IPage<LogisticsDeliveryOvertimeSignedDTO> page,LogisticsDeliveryOvertimeSignedReqData req) {
        return logisticsDeliveryMapper.getOvertimeSignedList(page,req);
    }

    @Override
    public long getOvertimeSignedCount(){
        return logisticsDeliveryMapper.getOvertimeSignedCount();
    }
}

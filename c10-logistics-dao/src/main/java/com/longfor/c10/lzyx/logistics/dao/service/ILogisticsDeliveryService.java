package com.longfor.c10.lzyx.logistics.dao.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsDeliveryOvertimeSignedDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsDeliveryOvertimeSignedReqData;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 物流订单运单表 服务类
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
public interface ILogisticsDeliveryService extends IService<LogisticsDelivery> {

    /**
     * 获取物流超期签收数据
     * @return
     */
    IPage<LogisticsDeliveryOvertimeSignedDTO> getOvertimeSignedList(IPage<LogisticsDeliveryOvertimeSignedDTO> page,LogisticsDeliveryOvertimeSignedReqData req);

    /**
     * 获取物流超期签收总数
     * @return
     */
    long getOvertimeSignedCount();
}

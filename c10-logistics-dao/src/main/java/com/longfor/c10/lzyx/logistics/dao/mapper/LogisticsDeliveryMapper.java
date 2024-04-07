package com.longfor.c10.lzyx.logistics.dao.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsDeliveryOvertimeSignedDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsDeliveryOvertimeSignedReqData;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliveryNoSendListReq;
import com.longfor.c10.lzyx.logistics.entity.dto.DeliverySendListReq;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliveryNoSendListVO;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliverySendListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 物流订单运单表 Mapper 接口
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
public interface LogisticsDeliveryMapper extends BaseMapper<LogisticsDelivery> {
    /**
     * 运营端待发货列表
     * @param page
     * @param req
     * @return
     */
    IPage<DeliveryNoSendListVO> selectAdminNoSendList(IPage<DeliveryNoSendListVO> page, @Param("req") DeliveryNoSendListReq req);

    /**
     * 运营端已发货列表
     * @param page
     * @param req
     * @return
     */
    IPage<DeliverySendListVO> selectAdminSendList(IPage<DeliverySendListVO> page, @Param("req") DeliverySendListReq req);

    /**
     * 获取物流超期签收数据
     * @return
     */
    IPage<LogisticsDeliveryOvertimeSignedDTO> getOvertimeSignedList(IPage<LogisticsDeliveryOvertimeSignedDTO> page, @Param("req") LogisticsDeliveryOvertimeSignedReqData req);
    /**
     * 获取物流超期签收总数
     * @return
     */
    long getOvertimeSignedCount();
}

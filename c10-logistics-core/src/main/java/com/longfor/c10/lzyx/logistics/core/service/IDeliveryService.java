package com.longfor.c10.lzyx.logistics.core.service;

import cn.hutool.core.lang.Pair;
import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.*;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.lop.open.api.sdk.LopException;

import java.io.UnsupportedEncodingException;

/**
 * 物流服务接口类
 * @author zhaoyl
 * @date 2022/1/11 下午2:28
 * @since 1.0
 */
public interface IDeliveryService {
    boolean addOrder(AddOrderListReqData addOrderReqData, BaseReqData baseReqData);

    /**
     * 取消发货
     * @param cancelOrderReqData
     * @return
     */
    void cancelOrder(CancelOrderReqData cancelOrderReqData);

    /**
     * 校验是否可以取消
     * @param cancelOrderReqData
     * @return
     */
    LogisticsDelivery checkCanCancelOrder(CancelOrderReqData cancelOrderReqData);

    /**
     * 查询物流轨迹
     * @param deliveryPathReqData
     * @return
     */
    DeliveryPathResData getDeliveryPath(DeliveryPathReqData deliveryPathReqData);

    /**
     * 查询物流费率
     * @param feeResultReqData
     * @return
     */
    FeeResultResData queryFeeResult(FeeResultReqData feeResultReqData);

    /**
     * 订阅物流轨迹
     * @return
     */
    boolean subLogisticsPath(SubLogisticsPathReqData subLogisticsPathReqData);

    /**
     * 查询预计送达时间
     * @param req
     * @return
     */
    String getDeliverTime(DeliverTimeReqData deliverTimeReqData);

    /**
     * 运单打印
     */
    DeliveryPrintResData getPrintData(DeliveryPrintReqData deliveryPrintReqData) throws LopException, UnsupportedEncodingException;

    /**
     * 获取物流状态
     * @param sourceCompanyCode
     * @param pathState
     * @return
     */
    Pair<Integer,Integer> getFromPathState(String pathState);

}
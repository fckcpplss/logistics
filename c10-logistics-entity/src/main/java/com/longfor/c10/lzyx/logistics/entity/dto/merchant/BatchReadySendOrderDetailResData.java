package com.longfor.c10.lzyx.logistics.entity.dto.merchant;

import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliveryNoSendDetailVO;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliveryNoSendListDetailVO;
import lombok.Data;

import java.util.List;

/**
 * 待发货订单详情批量返回报文格式
 * @author zhaoyl
 */
public class BatchReadySendOrderDetailResData {
    private boolean isAllSend = false;
    /**
     * 运费承担方编码
     */
    Integer logisticsTypeCode;
    /**
     * 运费承担方名称
     */
    String logisticsTypeName;

    List<BatchReadySendOrderDetailListVO> orderGoodsList;

    public boolean getIsAllSend() {
        return isAllSend;
    }

    public void setIsAllSend(boolean isAllSend) {
        this.isAllSend = isAllSend;
    }

    public Integer getLogisticsTypeCode() {
        return logisticsTypeCode;
    }

    public void setLogisticsTypeCode(Integer logisticsTypeCode) {
        this.logisticsTypeCode = logisticsTypeCode;
    }

    public String getLogisticsTypeName() {
        return logisticsTypeName;
    }

    public void setLogisticsTypeName(String logisticsTypeName) {
        this.logisticsTypeName = logisticsTypeName;
    }

    public List<BatchReadySendOrderDetailListVO> getOrderGoodsList() {
        return orderGoodsList;
    }

    public void setOrderGoodsList(List<BatchReadySendOrderDetailListVO> orderGoodsList) {
        this.orderGoodsList = orderGoodsList;
    }
}

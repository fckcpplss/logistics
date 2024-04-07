package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.Data;

/**
 * 根据物流公司找供应商物流列表
 * @author zhaoyalong
 */
@Data
public class ShopLogisticsByCompanyRep {
    /**
     * 供应商物流ID
     */
    Integer id;
    /**
     * 供应商物流名称
     */
    String logisticsName;
    /**
     * 排序号
     */
    Integer orderNumber;
}

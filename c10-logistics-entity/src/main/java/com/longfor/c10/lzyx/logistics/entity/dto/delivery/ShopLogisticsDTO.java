package com.longfor.c10.lzyx.logistics.entity.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 供应商物流配置
 * @author heandong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopLogisticsDTO {
    /**
     * 数据id，更新时带上这个标记
     */
    Integer id;
    /**
     * 物流code
     */
    String logisticsCode;
    /**
     * 物流名称
     */
    String logisticsName;
    /**
     * 类型code
     */
    String typeCode;
    /**
     * 类型名称
     */
    String typeName;
    /**
     * 商户物流账户
     */
    String account;
    /**
     * appKey，顺丰接口使用
     */
    String appKey;
    /**
     * 物流商品类型
     */
    String expressType;
}

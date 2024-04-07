package com.longfor.c10.lzyx.logistics.client.entity.param;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 供应商物流配置返回参数
 *
 * @author zhaoyalong
 */
@Data
@Builder
public class ShopLogisticsRep {
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
     * 商户顺丰产品类型
     */
    String expressType;
    /**
     * 是否被选中
     */
    boolean choose;
    /**
     * 是否使用默认的账号和appKey
     */
    boolean useDefault;

    /**
     * 顺丰产品类型
     */
    private Map<String, String> sfProductTypes;
}

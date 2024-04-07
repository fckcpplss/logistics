package com.longfor.c10.lzyx.logistics.entity.dto.tools;

import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsFee;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrderGoods;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * 模块表映射枚举类
 * @author zhaoyl
 * @date 2021/11/15 上午9:40
 * @since 1.0
 */
@AllArgsConstructor
public enum TableModuleEnum {
    MODULE_LOGISTIC_ORDER("moduleLogisticOrder","logistics_order", OrderFieldsDTO.class, LogisticsOrder.class,"物流订单"),
    MODULE_LOGISTIC_ORDER_GOODS("moduleLogisticOrderGoods","logistics_order_goods", OrderGoodsFieldsDTO.class, LogisticsOrderGoods.class,"物流订单商品"),
    MODULE_LOGISTIC_DELIVER("moduleLogisticDeliver","logistics_delivery", DeliveryFieldsDTO.class, LogisticsDelivery.class,"物流运单"),
    MODULE_LOGISTIC_FEE("moduleLogisticFee","logistics_fee", FeeFieldsDTO.class, LogisticsFee.class,"运单费用"),
    ;
    /**
     * 模块名称
     */

    @Setter
    @Getter
    private String moduleName;

    /**
     * 表名
     */
    @Setter
    @Getter
    private String tableName;

    /**
     * 可更新字段实体类
     */
    @Setter
    @Getter
    private Class fieldEntityClass;

    /**
     * 可更新字段实体类
     */
    @Setter
    @Getter
    private Class dbEntityClass;

    /**
     * 模块描述
     */
    @Setter
    @Getter
    private String moduleDesc;
    /**
     * 根据模块名称获取表名
     */
    public static String getTableName(String moduleName){
        return Arrays.stream(TableModuleEnum.values())
                .filter(x -> x.getModuleName().equals(moduleName))
                .findFirst()
                .map(TableModuleEnum::getTableName)
                .orElse(null);
    }
    /**
     * 根据模块名称获取表名
     */
    public static Class getFieldEntityClass(String moduleName){
        return Arrays.stream(TableModuleEnum.values())
                .filter(x -> x.getModuleName().equals(moduleName))
                .findFirst()
                .map(TableModuleEnum::getFieldEntityClass)
                .orElse(null);
    }
    /**
     * 根据模块名称获取表名
     */
    public static Class getDbEntityClass(String moduleName){
        return Arrays.stream(TableModuleEnum.values())
                .filter(x -> x.getModuleName().equals(moduleName))
                .findFirst()
                .map(TableModuleEnum::getDbEntityClass)
                .orElse(null);
    }
}

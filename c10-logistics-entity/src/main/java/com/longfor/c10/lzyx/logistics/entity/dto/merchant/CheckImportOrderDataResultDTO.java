package com.longfor.c10.lzyx.logistics.entity.dto.merchant;

import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrderGoods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 校验excel订单数据结果dto
 * @author zhaoyl
 * @date 2021/12/8 下午4:04
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckImportOrderDataResultDTO {
    /**
     * 订单编号和订单信息map
     */
    Map<String, LogisticsOrder> orderIdAndMap;
    /**
     * 订单id和商品信息map
     */
    Map<Long, List<LogisticsOrderGoods>> logisticOrderIdAndGoodsListMap;

    /**
     * 物流订单商品id和发货状态map
     */
    Map<Long,Map<String, LogisticsDelivery>> goodsIdAndDeliverStatusMap;

    /**
     * 订单id和运单编码分组运单信息map
     */
    Map<String, LogisticsDelivery> orderIdAnddeliverNoAndMap;

    /**
     * 商品id和商品信息map
     */
    Map<Long,LogisticsOrderGoods> goodsIdAndMap;
    /**
     * 订单id和运单信息map
     */
    Map<Long,List<LogisticsDelivery>> logisticOrderIdAnDeliverListMap;

    /**
     * 已发货数据
     */
    List<PendingImportDTO> shippedDataList;

    /**
     * 是否无数据
     */
    boolean noData;
}

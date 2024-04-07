package com.longfor.c10.lzyx.logistics.client.entity.param.internal;

import com.longfor.c10.lzyx.logistics.client.entity.vo.admin.GoodsVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物流订单-sku状态dto
 * @author zhaoyl
 * @date 2022/4/27 上午9:59
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsOrderSkuStatusDTO  extends GoodsVO {

    /**
     * 物流状态
     */
    private Integer logisticsStatus;

    /**
     * 收货类型
     */
    private Integer receiptType;

}

package com.longfor.c10.lzyx.logistics.client.entity.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoyalong
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryCancelResData {

    /**
     * 1 退货退款 (仅本地取消发货，调用物流取消失败) 2 仅退款(调用物流取消发货成功且本地也取消发货)
     */
    private Integer successType;

    /**
     * 失败信息
     */
    private String msg;
}

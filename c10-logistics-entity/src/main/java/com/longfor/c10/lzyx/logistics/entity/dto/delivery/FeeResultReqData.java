package com.longfor.c10.lzyx.logistics.entity.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 查询标价运费
 * @author zhaoyalong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeResultReqData implements Serializable {

    /**
     * 运单号
     */
    private String businessNo;

    /**
     * 订单ID，顺丰只能通过订单ID查询
     */
    private String orderId;
}

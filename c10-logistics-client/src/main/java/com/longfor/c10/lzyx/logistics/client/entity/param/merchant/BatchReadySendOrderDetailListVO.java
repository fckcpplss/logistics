package com.longfor.c10.lzyx.logistics.client.entity.param.merchant;

import com.longfor.c10.lzyx.logistics.client.entity.vo.admin.DeliveryNoSendListDetailVO;
import lombok.Data;

/**
 * 批量发货详情VO
 * @author zhaoyl
 * @date 2022/4/6 下午2:51
 * @since 1.0
 */
@Data
public class BatchReadySendOrderDetailListVO extends DeliveryNoSendListDetailVO {
    /**
     * 销售模式
     */
    private Integer sellType;
    /**
     * 销售模式展示
     */
    private String sellTypeShow;

    /**
     * 订单描述
     */
    private String orderDesc;
}

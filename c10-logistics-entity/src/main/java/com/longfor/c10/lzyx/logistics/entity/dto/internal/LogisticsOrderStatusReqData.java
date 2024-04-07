package com.longfor.c10.lzyx.logistics.entity.dto.internal;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author zhaoyl
 * @date 2022/4/27 上午10:51
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsOrderStatusReqData extends BaseReqData {
    /**
     * 子单号集合
     */
    @NotEmpty(message = "订单号不能为空")
    private String childOrderId;
}

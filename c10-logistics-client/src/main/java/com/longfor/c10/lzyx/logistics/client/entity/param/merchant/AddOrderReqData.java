package com.longfor.c10.lzyx.logistics.client.entity.param.merchant;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 发货请求参数类
 * @author zhaoyl
 * @date 2022/4/2 下午4:20
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddOrderReqData extends BaseReqData {
    /**
     * 发货参数
     */
    private List<AddOrderListReqData> addOrderListReqData;
}

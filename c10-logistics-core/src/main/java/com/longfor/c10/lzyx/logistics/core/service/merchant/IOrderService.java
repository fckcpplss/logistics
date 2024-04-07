package com.longfor.c10.lzyx.logistics.core.service.merchant;

import com.longfor.c10.lzyx.logistics.entity.dto.auth.AmUserInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.AddOrderListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.AddOrderReqData;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 物流订单接口类
 * @author zhaoyl
 * @date 2022/1/19 下午12:34
 * @since 1.0
 */
public interface IOrderService {
    /**
     * 商户端发货
     * @param addOrderReqDatas
     * @return
     */
    Response<String> doSendOrder(@RequestBody AddOrderReqData addOrderReqDatas);
}

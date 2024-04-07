package com.longfor.c10.lzyx.logistics.entity.dto.delivery;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.AmUserInfo;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 运单打印请求参数类
 * @author zhaoyl
 * @date 2022/4/29 下午7:46
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPrintReqData{
    private AmUserInfo amUserInfo;
    private List<LogisticsDelivery> deliveryList;
}

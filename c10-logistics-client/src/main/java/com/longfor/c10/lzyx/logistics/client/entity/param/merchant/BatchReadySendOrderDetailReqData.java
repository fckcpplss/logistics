package com.longfor.c10.lzyx.logistics.client.entity.param.merchant;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 待发货详情批量请求
 * @author zhoayl
 */
@Data
public class BatchReadySendOrderDetailReqData {
    @NotEmpty(message = "子订单编号列表不能为空")
    List<String> childOrderIdList;
}

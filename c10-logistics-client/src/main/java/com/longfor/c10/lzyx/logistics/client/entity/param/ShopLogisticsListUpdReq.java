package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author zhaoyalong
 */
@Data
public class ShopLogisticsListUpdReq extends BaseReqData {
    @NotEmpty(message = "list列表不能为空")
    List<ShopLogisticsUpdReq> list;
    /**
     * 供应商ID
     */
    @NotBlank(message = "id不能为空")
    String shopId;
}

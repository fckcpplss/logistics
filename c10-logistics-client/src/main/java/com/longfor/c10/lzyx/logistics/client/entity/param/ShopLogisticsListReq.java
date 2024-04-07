package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 获取商户物流配置列表的请求参数
 * @author zhaoyalong
 */
@Data
public class ShopLogisticsListReq extends BaseReqData {
    /**
     * 供应商id
     */
    @NotBlank(message = "shopId不能为空")
    String shopId;

    /**
     * 物流公司ID
     */
    String companyId;
    /**
     * 运费承担方类型
     */
    Integer logisticsTypeCode;
}

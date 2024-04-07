package com.longfor.c10.lzyx.logistics.entity.dto;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LogisticsAddressListReq  extends BaseReqData {
    /**
     * 地址id
     */
    private String addressId;

    /**
     * 所属id
     */
    private String ownerId;

    /**
     * 地址类型：0 用户收货地址；1 商铺发货地址; 2 项目发货地址；3 商铺退货地址；4 项目退货地址
     */
    private Integer ownerType;

    /**
     * 地址模糊查询关键字
     */
    private String addressQueryKey;

    /**
     * 渠道id
     */
    private String channelId;
    /**
     * 是否默认
     */
    private Integer isDefault;

}

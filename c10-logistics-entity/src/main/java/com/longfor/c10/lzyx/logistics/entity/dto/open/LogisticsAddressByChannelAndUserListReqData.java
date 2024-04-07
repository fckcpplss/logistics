package com.longfor.c10.lzyx.logistics.entity.dto.open;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 物流地址查询请求对象
 * @author zhaoyl
 */
@Data
public class LogisticsAddressByChannelAndUserListReqData extends BaseReqData {

    /**
     * 用户id
     */
    @NotBlank(message = "用户id不能为空")
    private String userId;

    /**
     * 用户渠道id
     */
    @NotBlank(message = "用户渠道id不能为空")
    private String channelId;
}

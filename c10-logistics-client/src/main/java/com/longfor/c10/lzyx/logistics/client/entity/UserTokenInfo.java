package com.longfor.c10.lzyx.logistics.client.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoyl
 * @date 2022/2/11 下午3:15
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenInfo {
    private String lmId;
    private String token;
    private String mobile;
    private Long expire;
    private String prefix;
    private String thirdPartyId;
    private String utmMedium;
    private String utmSource;
    private String entrySource;
    private String channelId;
}

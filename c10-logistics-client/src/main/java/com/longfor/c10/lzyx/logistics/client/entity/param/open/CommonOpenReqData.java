package com.longfor.c10.lzyx.logistics.client.entity.param.open;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 开放平台请求参数
 * @author zhaoyl
 * @date 2022/5/10
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonOpenReqData {
    private String body;
    private String receiveAppId;
    private String timestamp;
    private String sign;

}

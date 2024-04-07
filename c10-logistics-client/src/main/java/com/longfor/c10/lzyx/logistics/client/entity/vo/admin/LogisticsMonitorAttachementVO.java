package com.longfor.c10.lzyx.logistics.client.entity.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物流运单监控列表返回VO
 * @author zhaoyl
 * @date 2022/2/17 下午3:52
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsMonitorAttachementVO {
    /**
     * 附件名称
     */
    private String name;
    /**
     * 附件地址
     */
    private String url;

}

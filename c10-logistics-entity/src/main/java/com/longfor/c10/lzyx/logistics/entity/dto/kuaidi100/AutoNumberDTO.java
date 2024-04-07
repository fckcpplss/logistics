package com.longfor.c10.lzyx.logistics.entity.dto.kuaidi100;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoyl
 * @date 2022/1/13
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoNumberDTO {
    /**
     * 单号长度
     */
    private String lengthPre;

    /**
     * 快递公司编码
     */
    private String comCode;

    /**
     * 快递公司名称
     */
    private String name;
}

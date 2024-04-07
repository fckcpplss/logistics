package com.longfor.c10.lzyx.logistics.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单号识别结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoNumberVO {
    /**
     * 物流公司编码
     */
    private String companyCode;
    /**
     * 物流公司名称
     */
    private String companyName;
    /**
     * 首字母
     */
    private String initial;
    /**
     *单号长度
     */
    private String lengthPre;
}

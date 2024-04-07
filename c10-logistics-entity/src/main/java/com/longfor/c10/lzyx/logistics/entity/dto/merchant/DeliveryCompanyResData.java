package com.longfor.c10.lzyx.logistics.entity.dto.merchant;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.value.qual.ArrayLen;

/**
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryCompanyResData {
    /**
     * 物流公司编码
     */
    private String deliveryCompanyCode;
    /**
     * 物流公司名称
     */
    private String deliveryCompanyName;
    /**
     * 首字母
     */
    private String initial;
    /**
     *单号长度
     */
    private Integer lengthPre;
}

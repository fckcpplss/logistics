package com.longfor.c10.lzyx.logistics.client.entity.dto.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用字典项DTO
 * @author zhaoyl
 * @date 2021/11/15 上午9:16
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDictDTO {
    /**
     * 字典名称
     */
    @ApiModelProperty(value = "字典名称")
    private String key;

    /**
     * 字典值
     */
    @ApiModelProperty(value = "字典值")
    private String value;
}

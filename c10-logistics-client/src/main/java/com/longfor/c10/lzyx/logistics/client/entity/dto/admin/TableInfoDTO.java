package com.longfor.c10.lzyx.logistics.client.entity.dto.admin;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 运维工具表信息DTO
 * @author zhaoyl
 * @date 2021/11/15 上午9:13
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableInfoDTO extends BaseReqData {
    /**
     * 更新id集合
     */
    @NotNull
    private List<String> id;

    /**
     * 模块名称
     */
    @NotBlank
    private String moduleName;

    /**
     * 表更新字段
     */
    private List<String> fields;

    /**
     * 更新数据
     */
    @NotNull
    private List<CommentDictDTO> data;
}

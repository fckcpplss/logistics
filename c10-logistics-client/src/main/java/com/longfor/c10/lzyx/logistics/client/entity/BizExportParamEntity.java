package com.longfor.c10.lzyx.logistics.client.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 业务到处参数DTO
 * @author zhaoyl
 * @date 2021/10/16
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BizExportParamEntity implements Serializable {
    private static final long serialVersionUID = -2090974086272780654L;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 导出参数，json格式
     */
    private String exportParam;

    /**
     * 组织信息
     */
    private List<String> orgIdList;
}

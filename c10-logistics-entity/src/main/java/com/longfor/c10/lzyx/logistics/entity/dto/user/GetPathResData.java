package com.longfor.c10.lzyx.logistics.entity.dto.user;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetPathResData implements Serializable {
    private Integer code;
    private String msg;
    /**
     * 运单ID
     */
    private String waybillId;
    /**
     * 轨迹节点列表
     */
    private List<PathItem> pathItemList;
    @Data
    @Builder
    public static class PathItem implements Serializable {
        /**
         * 轨迹节点 Unix 时间戳
         */
        private Long pathTime;
        /**
         * 轨迹节点描述
         */
        private String pathDes;
        /**
         * 	轨迹节点详情
         */
        private String pathMsg;
        /**
         * 轨迹节点状态
         */
        private Integer pathState;
        /**
         * 物流中心状态
         */
        private String logisticsCenterStateName;
    }
}

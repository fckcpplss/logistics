package com.longfor.c10.lzyx.logistics.entity.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class LogisticsPathResData {
    /**
     * 预计到达时间
     */
    private String deliverTime;

    /**
     * 物流轨迹节点信息
     */
    private List<PathItemResData> pathItemList;

}

package com.longfor.c10.lzyx.logistics.entity.dto.kuaidi100;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.value.qual.ArrayLen;

import java.util.List;

/**
 * @描述:
 * @author: lizhexun
 * @date: 2021-11-22
 */
@Data
public class Kuaidi100MaptrackDTO {

    private String message;
    /**
     * 快递单当前状态，包括0在途，1揽收，2疑难，3签收，4退签，5派件，6退回，7转单，10待清关，11清关中，12已清关，13清关异常，14收件人拒签等13个状态
     */
    private String state;
    /**
     * 快递单明细状态标记，暂未实现，请忽略
     */
    private String condition;
    /**
     * 是否签收标记，请忽略，明细状态请参考state字段
     */
    private Integer ischeck;
    /**
     * 快速公司编码
     */
    private String com;
    /**
     * 单号
     */
    private String nu;
    /**
     * 轨迹地图链接
     */
    private String trailUrl;
    /**
     * 预计到达时间
     */
    private String arrivalTime;
    /**
     * 平均耗时
     */
    private String totalTime;
    /**
     * 到达还需多少时间
     */
    private String remainTime;
    /**
     * 最新查询结果，数组，包含多项，全量，倒序（即时间最新的在最前），每项都是对象，对象包含字段请展开
     */
    private List<MaptrackData> data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaptrackData {
        private String context;
        /**
         * 时间，原始格式
         */
        private String time;
        /**
         * 格式化后时间
         */
        private String ftime;
        private String areaCode;
        private String areaName;
        private String status;
    }

}

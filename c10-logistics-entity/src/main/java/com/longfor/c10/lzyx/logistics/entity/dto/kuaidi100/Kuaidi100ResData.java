package com.longfor.c10.lzyx.logistics.entity.dto.kuaidi100;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @描述:
 * @author: zhaoyalong
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Kuaidi100ResData {
    /**
     * 消息体，请忽略
     */
    private String message;
    /**
     * 快递单当前状态，包括0在途，1揽收，2疑难，3签收，4退签，5派件，6退回，7转单，10待清关，11清关中，12已清关，13清关异常，14收件人拒签等13个状态
     */
    private Integer state;
    /**
     * 200 	通讯状态，请忽略
     */
    private String status;
    /**
     * F00 	快递单明细状态标记，暂未实现，请忽略
     */
    private String condition;
    /**
     * 0 是否签收标记
     */
    private Integer ischeck;
    /**
     * yuantong 	快递公司编码,一律用小写字母，点击查看快递公司编码
     */
    private String com;
    /**
     * V030344422 	快递单号
     */
    private String nu;
    private List<KuaiDi100Trace> data = Collections.emptyList();
    /**
     * 查询结果：false表示查询失败
     */
    private Boolean result;
    /**
     * 失败的代号
     */
    private String returnCode;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KuaiDi100Trace {
        /**
         * 上海分拨中心 	物流轨迹节点内容
         */
        private String context;
        /**
         * 2012-08-28 16:33:19 	时间，原始格式
         */
        private String time;
        /**
         * 2012-08-28 16:33:19 	格式化后时间
         */
        private String ftime;
        /**
         * 在途 	本数据元对应的签收状态。只有在开通签收状态服务（见上面"status"后的说明）且在订阅接口中提交resultv2标记后才会出现
         */
        private String status;
        /**
         * 310000000000 	本数据元对应的行政区域的编码，只有在开通签收状态服务（见上面"status"后的说明）且在订阅接口中提交resultv2标记后才会出现
         */
        private String areaCode;
        /**
         * 上海市 	本数据元对应的行政区域的名称，开通签收状态服务（见上面"status"后的说明）且在订阅接口中提交resultv2标记后才会出现
         */
        private String areaName;
    }
}

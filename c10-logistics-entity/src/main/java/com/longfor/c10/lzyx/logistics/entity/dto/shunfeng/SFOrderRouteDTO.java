package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 顺丰物流轨迹api返回DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SFOrderRouteDTO implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = 1L;
    /**
     * mailno
     */
    private String mailno;
    /**
     * 接受地址
     */
    @JsonProperty("accept_address")
    private String acceptAddress;
    /**
     * 接受日期
     */
    @JsonProperty("accept_date")
    private String acceptDate;
    /**
     * 备注
     */
    private String remark;
    /**
     * 操作码
     */
    private String opcode;
    /**
     * 接受时间
     */
    @JsonProperty("accept_time")
    private String acceptTime;
    /**
     * 接受totaltime
     */
    @JsonProperty("accept_totaltime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date acceptTotaltime;
}

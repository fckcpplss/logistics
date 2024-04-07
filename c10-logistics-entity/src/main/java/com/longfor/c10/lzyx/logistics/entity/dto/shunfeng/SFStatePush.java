package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 顺丰状态推送
 * @author liuqinglin
 * @date 2021/11/4
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SFStatePush {
    /**
     * 客户订单号
     */
    private String orderNo;

    /**
     * 顺丰运单号
     */
    private String waybillNo;

    /**
     * 订单状态
     */
    private String orderStateCode;

    /**
     * 订单状态描述
     */
    private String orderStateDesc;

    /**
     * 收件员工工号
     */
    private String empCode;

    /**
     * 收件员手机号
     */
    private String empPhone;

    /**
     * 网点
     */
    private String netCode;

    /**
     * 最晚上门时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastTime;

    /**
     * 客户预约时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bookTime;

    /**
     * 承运商代码(SF)
     */
    private String carrierCode;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTm;
}

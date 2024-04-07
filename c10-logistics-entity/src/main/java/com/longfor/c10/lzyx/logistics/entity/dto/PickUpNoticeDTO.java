package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author lanxiaolong
 * @Date 2022/4/14 10:32 上午
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PickUpNoticeDTO implements Serializable {
    private String childOrderId;
    private String orderId;
    private String pickupAddress;
    private String goodsName;
    private Date pickupEndTime;
    private String pickupSpot;
    private String lmId;
    private Boolean last;
}

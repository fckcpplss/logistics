package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 物流地址信息对象
 * @author admin
 */
@Data
public class LogisticsAddressInfo {
    /**
     * 地址id
     */
    private String addressId;

    /**
     * 所属id
     */
    private String ownerId;

    /**
     * 渠道id
     */
    private String channelId;

    /**
     * 收件人姓名
     */
    @JSONField(name = "personName")
    private String recipientName;

    /**
     * 收件人电话
     */
    @JSONField(name = "personPhone")
    private String recipientPhone;

    /**
     * 收件人电话脱敏
     */
    private String recipientPhoneFull;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 省编码
     */
    private String provinceCode;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 市编码
     */
    private String cityCode;

    /**
     * 区县名称
     */
    private String areaName;

    /**
     * 区县编码
     */
    private String areaCode;

    /**
     * 详细地址
     */
    private String addressDetail;

    /**
     * 是否为默认地址：1、默认地址；2、非默认
     */
    @JSONField(name = "isDefault")
    private Integer isDef;

    /**
     * 创建时间
     */
    private Date createTime;
}

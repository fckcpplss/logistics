package com.longfor.c10.lzyx.logistics.entity.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.longfor.c10.lzyx.logistics.entity.constant.CommonConstant;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 发货地址
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
@Getter
@Setter
@TableName("logistics_ship_address")
public class LogisticsShipAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 发件人
     */
    private String addresser;

    /**
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 省编码
     */
    private String provinceCode;

    /**
     * 发货地址-省
     */
    private String provinceName;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 发货地址-市
     */
    private String cityName;

    /**
     * 区 编码
     */
    private String areaCode;

    /**
     * 发货地址-区
     */
    private String areaName;

    /**
     * 乡镇街 编码
     */
    private String streetCode;

    /**
     * 发货地址-乡镇街
     */
    private String streetName;

    /**
     * 发货地址-详细地址
     */
    private String addressDetail;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 是否是默认地址1.默认2.非默认;  默认否
     */
    private Integer isDefault;

    /**
     * 是否删除 0.未1.删除
     */
    private Integer isDelete;

    /**
     * 供应商id
     */
    private String sprId;

    /**
     * 创建人
     */
    private String creator;


    public String convertAddressName() {
        return StringUtils.join(
                StringUtils.defaultString(this.getProvinceName(), CommonConstant.HYPHEN),
                CommonConstant.RIGHT_SLASH,
                StringUtils.defaultString(this.getCityName(), CommonConstant.HYPHEN),
                CommonConstant.RIGHT_SLASH,
                StringUtils.defaultString(this.getAreaName(), CommonConstant.HYPHEN),
                CommonConstant.BLANK_ONE,
                StringUtils.defaultString(this.getStreetName(), CommonConstant.HYPHEN),
                CommonConstant.BLANK_ONE,
                StringUtils.defaultString(this.getAddressDetail(), CommonConstant.HYPHEN));
    }


}

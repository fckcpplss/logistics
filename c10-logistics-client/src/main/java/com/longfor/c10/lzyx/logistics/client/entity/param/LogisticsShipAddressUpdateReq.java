package com.longfor.c10.lzyx.logistics.client.entity.param;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

/**
 * 发货地址修改请求对象
 * @author zhaoyl
 */
@Data
public class LogisticsShipAddressUpdateReq  extends BaseReqData {

    /**
     * 地址id
     */
    @NotNull(message = "地址id不能为空")
    private Integer id;

    /**
     * 收件人
     */
    @NotBlank(message = "收件人不能为空")
    @Size(max = 25,min = 1,message = "收件人姓名最多25个字")
    private String addresser;

    /**
     * 电话号码
     */
    @NotBlank(message = "电话号码不能为空")
    private String phoneNumber;

    /**
     * 省名称
     */
    @NotBlank(message = "省名称不能为空")
    private String provinceName;

    /**
     * 省编码
     */
    @NotBlank(message = "省编码不能为空")
    private String provinceCode;

    /**
     * 市名称
     */
    @NotBlank(message = "市名称不能为空")
    private String cityName;

    /**
     * 市编码
     */
    @NotBlank(message = "市编码不能为空")
    private String cityCode;

    /**
     * 区县名称
     */
    @NotBlank(message = "区县名称不能为空")
    private String areaName;

    /**
     * 区县编码
     */
    @NotBlank(message = "区县编码不能为空")
    private String areaCode;

    /**
     * 乡镇街编码
     */
    @NotBlank(message = "乡镇街编码不能为空")
    private String streetCode;

    /**
     * 乡镇街名称
     */
    @NotBlank(message = "乡镇街名称不能为空")
    private String streetName;

    /**
     * 详细地址
     */
    @Size(max = 50,min = 1,message = "详情地址最多50个字")
    @NotBlank(message = "详细地址能为空")
    private String addressDetail;

    /**
     * 是否是默认地址1.默认2.非默认  默认的是非默认
     */
    private Integer isDefault;
}

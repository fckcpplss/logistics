package com.longfor.c10.lzyx.logistics.client.entity.dto.admin;

import com.longfor.c10.lzyx.logistics.client.entity.param.admin.AttachmentLinkDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 评价运维工具列表DTO
 * @author zhaoyl
 * @date 2021/11/15 上午9:23
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticDeliverToolRecordDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 物流订单ID
     */
    @ApiModelProperty(value = "物流订单id")
    private Long childOrderId;

    /**
     * 所属商铺id
     */
    @ApiModelProperty(value = "所属商户id")
    private String shopId;

    /**
     * 商铺名称
     */
    @ApiModelProperty(value = "商户名称")
    private String shopName;

    /**
     * 老快递公司编码
     */
    @ApiModelProperty(value = "老快递公司编码")
    private String oldCompanyCode;

    /**
     * 老快递公司编码
     */
    @ApiModelProperty(value = "老快递公司名称")
    private String oldCompanyName;

    /**
     * 新快递公司名称
     */
    @ApiModelProperty(value = "新快递公司编码")
    private String newCompanyCode;

    /**
     * 新快递公司名称
     */
    @ApiModelProperty(value = "新快递公司名称")
    private String newCompanyName;

    /**
     * 旧物流运费承担方 (1：平台物流，2：商家物流）
     */
    @ApiModelProperty(value = "运费承担方")
    private Integer oldLogisticsType;

    /**
     * 新物流运费承担方 (1：平台物流，2：商家物流）
     */
    @ApiModelProperty(value = "新物流运费承担方")
    private Integer newLogisticsType;

    /**
     * 旧运单号
     */
    @ApiModelProperty(value = "旧运单号")
    private String oldDeliveryNo;

    /**
     * 新运单号
     */
    @ApiModelProperty(value = "新运单号")
    private String newDeliveryNo;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 物流异常附件
     */
    @ApiModelProperty(value = "物流异常附件")
    private List<AttachmentLinkDto> attachmentList;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    /**
     * 创建人账号
     */
    @ApiModelProperty(value = "创建人oa")
    private String creatorOa;

    /**
     * 创建人姓名
     */
    @ApiModelProperty(value = "创建人姓名")
    private String creatorName;

    /**
     * 更新人账号
     */
    @ApiModelProperty(value = "更新人oa")
    private String updateOa;

    /**
     * 更新人姓名
     */
    @ApiModelProperty(value = "更新人姓名")
    private String updateName;

}

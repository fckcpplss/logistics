package com.longfor.c10.lzyx.logistics.entity.dto.tools;

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
    private Long childOrderId;

    /**
     * 所属商铺id
     */
    private String shopId;

    /**
     * 商铺名称
     */
    private String shopName;

    /**
     * 老快递公司编码
     */
    private String oldCompanyCode;

    /**
     * 老快递公司编码
     */
    private String oldCompanyName;

    /**
     * 新快递公司名称
     */
    private String newCompanyCode;

    /**
     * 新快递公司名称
     */
    private String newCompanyName;

    /**
     * 旧物流运费承担方 (1：平台物流，2：商家物流）
     */
    private Integer oldLogisticsType;

    /**
     * 新物流运费承担方 (1：平台物流，2：商家物流）
     */
    private Integer newLogisticsType;

    /**
     * 旧运单号
     */
    private String oldDeliveryNo;

    /**
     * 新运单号
     */
    private String newDeliveryNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 物流异常附件
     */
    private List<AttachmentLinkDto> attachmentList;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人账号
     */
    private String creatorOa;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 更新人账号
     */
    private String updateOa;

    /**
     * 更新人姓名
     */
    private String updateName;

}

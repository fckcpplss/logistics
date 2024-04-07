package com.longfor.c10.lzyx.logistics.entity.dto.delivery;

import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPathReqData {
    /** 必填
     * 订单 ID，需保证全局唯一
     */
    @NotBlank
    private String logisticsOrderId;

    /**
     * 子单id
     */
    private String childOrderId;
    /**
     * 用户openid
     */
    private String openid;

    /**
     * 运单ID
     */
    @NotBlank
    private String waybillId;

    /**
     * 快递公司编码
     */
    private String companyCode;

    /**
     * 快递100 查询 收、寄件人的电话号码（手机和固定电话均可，只能填写一个，顺丰速运和丰网速运必填，其他快递公司选填。如座机号码有分机号，分机号无需上传）
     */
    private String phone;

    /**
     * 物流平台
     */
    private CompanyCodeEnum sourceCompany;

    /**
     * 是否查询最新
     */
    private boolean queryLatest = false;

    /**
     * 路由推送轨迹数据,不为空切queryLates = true以此路由为准，不实时api查询
     */
    private String routePathData;
}

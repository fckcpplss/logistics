package com.longfor.c10.lzyx.logistics.entity.dto.user;

import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPathReqData {
    /** 必填
     * 订单 ID，需保证全局唯一
     */
    private String orderId;
    /**
     * 用户openid
     */
    private String openid;
    /** 必填
     * 运单ID
     */
    private String waybillId;
    /** 必填
     * 所选类型
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
}

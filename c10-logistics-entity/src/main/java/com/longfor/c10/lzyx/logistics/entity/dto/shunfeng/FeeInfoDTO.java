package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 费用信息DTO
 * @author zhaoyalong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeInfoDTO implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = 1L;
    /**
     * 交款人
     */
    private String gatherEmpCode;
    /**
     * 交款时间
     */
    private long inputTm;
    /**
     * 收付款网点
     */
    private String gatherZoneCode;
    /**
     * 付款类型：1-寄付；2-到付；3-第三方付；
     */
    private String paymentTypeCode;
    /**
     * 个性化费用
     */
    private int feeAmtInd;
    /**
     * 个性费用类型
     */
    private int feeIndType;
    /**
     * 标准费用
     */
    private double feeAmt;
    /**
     * 费用类型：1-主运费；其它-增值服务费
     */
    private String feeTypeCode;
    /**
     * 付款变更类型
     */
    private String paymentChangeTypeCode;
    /**
     * 结算类型：1-现结；2-月结；
     */
    private String settlementTypeCode;
    /**
     * 版本号：当红冲后版本号会增加
     */
    private int versionNo;
    /**
     * 运单关联标记，可忽略
     */
    private int waybillId;
    /**
     * 业务所属地区编码
     */
    private String bizOwnerZoneCode;
    /**
     * 币别
     */
    private String currencyCode;
    /**
     * 运单母单号
     */
    private String waybillNo;
}

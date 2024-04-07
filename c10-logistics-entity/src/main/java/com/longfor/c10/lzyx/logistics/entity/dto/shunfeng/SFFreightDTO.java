package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 顺丰价格DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SFFreightDTO implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = 1L;
    /**
     * 返回计费重量
     */
    private String meterageWeightQty;
    /**
     * 返回客户订单号
     */
    private String orderId;
    /**
     * 返回时效类型
     */
    private String limitTypeCode;
    /**
     * 收件详细地址
     */
    private String addresseeAddr;
    /**
     * 声明价值币种
     */
    private String consValueCurrencyCode;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 收件人跟名字
     */
    private String addresseeContName;
    /**
     * 发货人省
     */
    private String consignorProvince;
    /**
     * 发货人电话
     */
    private String consignorMobile;
    /**
     * 收件人省
     */
    private String addresseeProvince;
    /**
     * 收件人的电话
     */
    private String addresseePhone;
    /**
     * 返回月结帐号
     */
    private String customerAcctCode;
    /**
     * 声明价值
     */
    private String consValue;
    /**
     * 收件人城市
     */
    private String addresseeCity;
    /**
     * 寄件详细地址
     */
    private String consignorAddr;
    /**
     * 发货人的电话
     */
    private String consignorPhone;
    /**
     * 发货人的城市
     */
    private String consignorCity;
    /**
     * 返回子单号
     */
    private String waybillChilds;
    /**
     * 收件人手机
     */
    private String addresseeMobile;
    /**
     * 产品类型
     */
    private String expressTypeCode;
    /**
     * 发货人跟名字
     */
    private String consignorContName;
    /**
     * 返回运单号
     */
    private String waybillNo;
    /**
     * 费用集合
     */
    private List<SFFeeDTO> feeList;
}

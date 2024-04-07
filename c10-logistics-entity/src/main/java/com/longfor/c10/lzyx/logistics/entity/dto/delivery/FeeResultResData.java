package com.longfor.c10.lzyx.logistics.entity.dto.delivery;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 描述: 标价运费返回结果
 *
 * @author wanghai03
 * @date 2021/10/15 下午3:52
 */
@Data()
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class FeeResultResData{
    /**
     * 编码 默认 0
     */
    private Integer code = 0;
    /**
     * 信息 默认 OK
     */
    private String msg = "ok";

    /**
     * 运单号
     */
    private String businessNo;

    /**
     * 计费重量
     */
    private BigDecimal calWeight;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 体积
     */
    private BigDecimal volume;

    /**
     * 发货地址
     */
    private String senderAddress;

    /**
     * 付费帐号
     */
    private String customerAcctCode;

    /**
     * 收货地址
     */
    private String receiverAddress;

    /**
     * 费用列表
     */
    private List<FeeInfo> feeInfoList;

    @Data
    public static class FeeInfo implements Serializable {
        /**
         * 费用编码
         */
        private String costNo;

        /**
         * 费用名称：快递运费、快递保价费
         */
        private String costName;

        /**
         * 支付类型
         */
        private Byte paymentType;

        /**
         * 结算类型
         */
        private Byte settlementType;

        /**
         * 费用类型：YF、BF
         */
        private String costClassify;

        /**
         * 标价金额(无折扣)
         */
        private BigDecimal standardAmount;

        /**
         * 实际金额
         */
        private BigDecimal actualAmount;

    }
}

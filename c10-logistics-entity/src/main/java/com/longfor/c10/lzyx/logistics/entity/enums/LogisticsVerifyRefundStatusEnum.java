package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 自提/核销退单状态
 *
 * @author zhaoyalong
 */
@Getter
@AllArgsConstructor
public enum LogisticsVerifyRefundStatusEnum {

    REFUND_NO(0, "未退款"),
    REFUND_ON(1, "退款中"),
    REFUND_SUCCESS(2, "已退款"),
    REFUND_PART(3, "部分退款"),
    REFUND_FAIL(4, "退款失败");

    private final Integer code;

    private final String desc;

    public static LogisticsVerifyRefundStatusEnum fromCode(int code) {
        return Arrays.stream(LogisticsVerifyRefundStatusEnum.values()).filter(x -> x.getCode() == code).findFirst().orElse(null);
    }
    public static Integer fromOrderRefundStatus(Integer refundStatus){
        return Arrays.stream(VerifyRefundStatusMapEnum.values()).filter(x -> x.getOrderRefundStatus().equals(refundStatus)).map(VerifyRefundStatusMapEnum::getVerifyRefundStatus).findFirst().orElse(null);
    }
    @Getter
    @AllArgsConstructor
    enum VerifyRefundStatusMapEnum{
        REFUND_ON(62,LogisticsVerifyRefundStatusEnum.REFUND_ON.getCode()),
        REFUND_SUCCESS(63, LogisticsVerifyRefundStatusEnum.REFUND_SUCCESS.getCode()),
        REFUND_FAIL(64, LogisticsVerifyRefundStatusEnum.REFUND_FAIL.getCode());
        private final Integer orderRefundStatus;

        private final Integer verifyRefundStatus;
    }
}

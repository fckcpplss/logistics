package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 快递100节点物流状态
 * 作为标准物流轨迹状态)
 * @author zhaoyalong
 */
@Getter
@AllArgsConstructor
public enum LogisticsKuaidi100PathStateEnum {
    ON_THE_WAY(0, "在途",0,"在途"),
    ON_THE_WAY_ARRIVE_CITY(0, "在途",1001,"到达派件城市"),
    ON_THE_WAY_ARTERY(0, "在途",1002,"干线"),
    ON_THE_WAY_FORWARDED(0, "在途",1003,"转递"),
    COLLECT(1, "揽收",1, "揽收"),
    COLLECT_ORDER(1, "揽收",101, "已下单"),
    COLLECT_RECEIVE_NO(1, "揽收",102, "待揽收"),
    COLLECT_RECEIVED(1, "揽收",103, "已揽收"),
    DIFFICULT(2, "疑难",2, "疑难"),
    DIFFICULT_OVERTIME_SIGN_NO(2, "疑难",201, "超时未签收"),
    DIFFICULT_OVERTIME_UPDATE_NO(2, "疑难",202, "超时未更新"),
    DIFFICULT_RECEIVE_REJECT(2, "疑难",203, "拒收"),
    DIFFICULT_DELIVERY_FAIL(2, "疑难",204, "派件异常"),
    DIFFICULT_OVERTIME_GET(2, "疑难",205, "柜或驿站超时未取"),
    DIFFICULT_CANNOT_CONTACT(2, "疑难",206, "无法联系"),
    DIFFICULT_SUPERZONE(2, "疑难",207, "超区"),
    DIFFICULT_RETENTION(2, "疑难",208, "滞留"),
    DIFFICULT_DAMAGED(2, "疑难",209, "破损"),
    SIGN(3, "签收",3, "签收"),
    SIGN_OWNER(3, "签收",301, "本人签收"),
    SIGN_WITH_DELIVERY_FAIL(3, "签收",302, "派件异常后签收"),
    SIGN_AGENCY(3, "签收",303, "代签"),
    SIGN_CONTAINER_OR_STAGE(3, "签收",304, "投柜或驿站签收"),
    WITHDRAWAL(4, "退签",4, "退签"),
    WITHDRAWAL_CANCEL_ORDER(4, "退签",401, "已销单"),
    WITHDRAWAL_RECEIVE_REJECT(4, "退签",14, "拒签"),
    DISPATCH(5, "派件",5, "派件"),
    DISPATCH_CONTAINER_OR_STAGE(5, "派件",501, "投柜或驿站"),
    DISPATCH_SIGN(5, "派件",3, "签收"),
    GO_BACK(6, "退回",6, "退回"),
    TRANSFER_ORDER(7, "转投", 7,"转投"),
    TO_BE_CLEARED(8, "清关",8, "清关"),
    TO_BE_CLEARED_NO(8, "清关",10	, "待清关"),
    TO_BE_CLEARED_ON(8, "清关",11, "清关中"),
    TO_BE_CLEARED_SUCCESS(8, "清关",12, "已清关"),
    TO_BE_CLEARED_FAIL(8, "清关",13, "清关异常"),
    REFUSED_TO_SIGN(14, "拒签",14, "拒签"),
    NONE(99, "未知",99, "未知");

    private Integer code;
    private String desc;
    private Integer subCode;
    private String subDesc;

    public static LogisticsKuaidi100PathStateEnum fromCode(int code) {
        return Arrays.stream(LogisticsKuaidi100PathStateEnum.values())
                .filter(x -> x.getCode() == code)
                .findAny()
                .orElse(null);
    }

    public static LogisticsKuaidi100PathStateEnum fromDesc(String desc) {
        return Arrays.stream(LogisticsKuaidi100PathStateEnum.values())
                .filter(x -> x.getDesc().equals(desc))
                .findAny()
                .orElse(null);
    }
    public static LogisticsKuaidi100PathStateEnum fromSubCode(Integer subCode){
        return Arrays.stream(LogisticsKuaidi100PathStateEnum.values())
                .filter(x -> x.getSubCode().equals(subCode))
                .findAny()
                .orElse(null);
    }
    public static LogisticsKuaidi100PathStateEnum fromSubDesc(String subDesc){
        return Arrays.stream(LogisticsKuaidi100PathStateEnum.values())
                .filter(x -> x.getSubDesc().equals(subDesc))
                .findAny()
                .orElse(null);
    }

    
}

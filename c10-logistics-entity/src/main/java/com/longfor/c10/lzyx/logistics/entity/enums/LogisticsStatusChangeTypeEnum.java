package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物流状态变更消息类型
 * @author zhaoyalong
 */
@AllArgsConstructor
@Getter
public enum LogisticsStatusChangeTypeEnum {

    //正向
    FORWARD(1),

    //未删除
    REVERSE(2);

    private int code;

    public int code(){
        return code;
    }

}

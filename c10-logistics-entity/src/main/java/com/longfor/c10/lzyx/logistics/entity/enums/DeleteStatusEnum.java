package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 删除状态
 * @author zhaoyalong
 */
@AllArgsConstructor
@Getter
public enum DeleteStatusEnum {

    //已删除
    YES(1),

    //未删除
    NO(0);

    private int code;

    public int code(){
        return code;
    }

}

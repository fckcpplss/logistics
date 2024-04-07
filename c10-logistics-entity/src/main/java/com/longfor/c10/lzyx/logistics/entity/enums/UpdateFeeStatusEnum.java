package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum UpdateFeeStatusEnum {

    NEVER_UPDATE(0, "没有更新过"),
    UPDATEING(1, "正在更新"),
    UPDATE_FAIL(2, "更新失败"),
    UPDATE_SUC(3, "更新成功");
    private final int code;
    private final String desc;

    public static UpdateFeeStatusEnum fromCode(int code) {
        return Arrays.stream(UpdateFeeStatusEnum.values())
                .filter(x -> x.getCode() == code)
                .findAny()
                .orElse(null);
    }
}

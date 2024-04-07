package com.longfor.c10.lzyx.logistics.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.ListUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 销售模式类型枚举类
 * @author zhaoyl
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum SellTypeEnum {
    WHOLESALE(1,"批发",new String[]{ChannelBizCodeEnum.c10_ORDER_ADMIN.getCode()}),
    RETAIL(2,"零售",new String[]{ChannelBizCodeEnum.c10_SHOPPING_CART.getCode(),ChannelBizCodeEnum.C10_ORDER_PROPOSE.getCode()});

    private final Integer code;
    private final String desc;
    private final String[] channels;


    public static SellTypeEnum fromCode(Integer code) {
        return Arrays.stream(SellTypeEnum.values()).filter(e -> e.getCode().equals(code)).findFirst()
                .orElse(null);
    }

    public static SellTypeEnum fromChannelCode(String channelCode){
        return Arrays.stream(SellTypeEnum.values())
                .filter(x -> Objects.nonNull(x) && Arrays.stream(x.getChannels()).anyMatch(y -> y.equals(channelCode)))
                .findFirst()
                .orElse(null);
    }

    public static String descFromChannelCode(String channelCode){
        return Arrays.stream(SellTypeEnum.values())
                .filter(x -> Objects.nonNull(x) && Arrays.stream(x.getChannels()).anyMatch(y -> y.equals(channelCode)))
                .findFirst()
                .map(SellTypeEnum::getDesc)
                .orElse(null);
    }
}


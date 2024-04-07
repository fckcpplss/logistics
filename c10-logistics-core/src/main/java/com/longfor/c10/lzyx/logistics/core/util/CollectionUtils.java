package com.longfor.c10.lzyx.logistics.core.util;

import cn.hutool.core.collection.CollectionUtil;

import java.util.Collection;

/**
 * @author zhaoyl
 * @date 2022/4/21 上午9:55
 * @since 1.0
 */
public class CollectionUtils extends CollectionUtil {
    /**
     * 集合空转null
     */
    public static <T extends Collection<E>, E> T nullIfEmpty(T collection) {
        return isEmpty(collection) ? null : collection;
    }
}

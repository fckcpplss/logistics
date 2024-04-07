package com.longfor.c10.lzyx.logistics.entity.annotation;

import java.lang.annotation.*;

/**
 * 权限校验
 * @author zhaoyalong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
@Documented
public @interface LoginVerify {
    boolean orgIdNeed() default true;
    boolean shopIdNeed() default false;
}

package com.longfor.c10.lzyx.logistics.entity.annotation;

import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;

import java.lang.annotation.*;

/**
 * 公司类型
 * @author zhaoyalong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
public @interface DeliveryCompanyType {
    CompanyCodeEnum code() default CompanyCodeEnum.KD100;
}
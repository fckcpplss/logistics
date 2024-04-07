package com.longfor.c10.lzyx.logistics.core.aop;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.longfor.c10.lzyx.logistics.entity.annotation.LoginVerify;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * 用户登陆校验
 * @author zhaoyl
 * @date 2022/2/22 上午11:44
 * @since 1.0
 */
@Slf4j
//@Aspect
@Component
public class LoginVerifyAspect {
    @Pointcut("@annotation(com.longfor.c10.lzyx.logistics.entity.annotation.LoginVerify)")
    public void loginVerifyAspect() throws Exception {

    }
    @Before("loginVerifyAspect()")
    public void before(JoinPoint point) throws Throwable{
        Object amUserInfoObject = null;
        try{
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            //获取自定义注解
            LoginVerify loginVerify = method.getAnnotation(LoginVerify.class);
            if(Objects.isNull(loginVerify)){
                return;
            }
            Object paramsObject = Arrays.stream(point.getArgs()).findFirst().orElse(null);
            if(Objects.isNull(paramsObject)){
                return;
            }
            amUserInfoObject = ReflectUtil.getFieldValue(ReflectUtil.getFieldValue(paramsObject,"data"), "amUserInfo");
        }catch (Exception ex){
            ex.printStackTrace();
            log.error("LoginVerifyAspect 处理异常",ex);
        }
        if(Objects.isNull(amUserInfoObject)){
            throw new BusinessException("用户登陆信息为空");
        }
        System.out.println(JSON.toJSONString(amUserInfoObject));

    }
}


package com.longfor.c10.lzyx.logistics.core.exception;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 统一异常处理
 * @author zhaoyalong
 * @date 2021-09-24 10:55
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Object baseExceptionnHandler(Exception e) {
        return Response.fail(e.getMessage());
    }

    /**
     * 校验错误拦截处理
     *
     * @param exception 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Object validationBodyException(MethodArgumentNotValidException exception){
        BindingResult result = exception.getBindingResult();
        String errorMsg = "参数错误";
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            for (ObjectError p : errors) {
                FieldError error = (FieldError) p;
                errorMsg = error.getDefaultMessage();
                break;
            }
        }
        return Response.fail(errorMsg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object exceptionHandler(Exception e) {
        return Response.fail("系统异常");
    }
}

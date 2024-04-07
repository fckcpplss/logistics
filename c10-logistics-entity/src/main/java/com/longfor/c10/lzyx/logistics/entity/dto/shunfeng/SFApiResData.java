package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 顺丰接口返回
 * @author zhaoyl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SFApiResData<T>{

    /**
     * 否	信息	提示信息（如下微派单有异常在此提示）
     */
    private String msg;
    /**
     * 是	状态码	返回成功或失败信息(ok/fail)
     */
    private String succ;
    /**
     * 是	数据	数据
     */
    private T result;
}

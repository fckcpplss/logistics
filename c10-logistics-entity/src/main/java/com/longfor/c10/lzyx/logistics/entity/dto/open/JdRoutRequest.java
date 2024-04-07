package com.longfor.c10.lzyx.logistics.entity.dto.open;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * 描述:京东路由推送
 *
 * @author wanghai03
 * @date 2021/10/19 下午2:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdRoutRequest implements Serializable {

    private String messageId;
    private String token;
    private String format;
    private JdRoutRequestBody requestBody;
    /**
     * yyyy-mm-dd hh:mi:ss
     */
    private String timestamp;

    public String bodyToJsonStr() {
        return JSON.toJSONString(requestBody);
    }

    public Boolean checkData(String encryptKey, String requestBody) {
        return Objects.equals(token, SecureUtil.md5(requestBody + timestamp + encryptKey));
    }

    /**
     * 描述: jackson实例内部类需要static
     *
     * @author wanghai03
     * @date 2021/10/28 下午4:45
     */
    @Data
    @NoArgsConstructor
    public static class JdRoutRequestBody implements Serializable {
        /**
         * 商家编码
         */
        private String vendorCode;

        /**
         * 商家名称
         */
        private String vendorName;

        /**
         * 运单号
         */
        private String waybillCode;

        /**
         * 物流订单号
         */
        private String orderId;

        /**
         * 操作节点
         */
        private String traceNode;

        /**
         * 操作节点编码
         */
        private String traceCode;

        /**
         * 操作描述
         */
        private String traceMark;

        /**
         * 操作员
         */
        private String operator;

        /**
         * 操作时间：2016-11-11 11:01:11
         */
        private String operateTime;
    }
}

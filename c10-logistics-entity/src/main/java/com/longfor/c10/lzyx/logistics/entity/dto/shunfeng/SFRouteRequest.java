package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * sf请求
 *
 * @author bomg
 * @date 2021/09/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SFRouteRequest implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = 1L;
    /**
     * 路由节点信息编号，每一个id代表一条不同的路由节点信息。
     */
    private String id;
    /**
     * 顺丰运单号
     */
    private String mailno;
    /**
     * 客户订单号
     */
    private String orderid;
    /**
     * 路由节点产生的时间，格式：YYYY-MM-DD HH24:MM:SS，示例：2012-7-30 09:30:00。
     */
    private String acceptTime;
    /**
     * 路由节点发生的城市
     */
    private String acceptAddress;
    /**
     * 路由节点具体描述
     */
    private String remark;
    /**
     * 路由节点操作码
     */
    private String opCode;

    /**
     * 密文
     */
    private String params;
}

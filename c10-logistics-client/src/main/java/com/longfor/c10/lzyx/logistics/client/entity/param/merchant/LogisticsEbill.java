package com.longfor.c10.lzyx.logistics.client.entity.param.merchant;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 电子面单
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
@Getter
@Setter
public class LogisticsEbill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 运单号
     */
    private String deliveryNo;

    /**
     * 快递公司编码
     */
    private String companyCode;

    /**
     * 面单状态，1，打印请求，2，生成pdf
     */
    private Integer status;

    /**
     * pdf流
     */
    private byte[] content;

    /**
     * 图片路径
     */
    private String url;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人账号
     */
    private String creatorAccount;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 更新人账号
     */
    private String updateAccount;

    /**
     * 更新人姓名
     */
    private String updateName;

    /**
     * 删除标志 0:未删除 1:已删除
     */
    private Integer deleteStatus;

    /**
     * 数据版本号
     */
    private Integer version;

    /**
     * 请求id
     */
    private String requestId;


}

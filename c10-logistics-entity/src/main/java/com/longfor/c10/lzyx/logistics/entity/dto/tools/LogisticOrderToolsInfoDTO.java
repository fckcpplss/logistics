package com.longfor.c10.lzyx.logistics.entity.dto.tools;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价运维工具列表DTO
 * @author zhaoyl
 * @date 2021/11/15 上午9:23
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticOrderToolsInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 物流订单信息
     */
    private LogisticOrderInfoDTO orderInfo;

    /**
     * 物流运单信息
     */
    private List<LogisticDeliverInfoDTO> deliverInfos;

    /**
     * 运单费用信息
     */
    private List<LogisticFeeInfoDTO> logisticFeeInfos;

    /**
     * 表信息集合
     */
    private List<TableInfoDTO> tableInfos;

    /**
     * 主键，雪花算法生成
     */
    private Long id;

    /**
     * 物流单主键
     */
    private String logisticsOrderId;

    /**
     * 运单表主键
     */
    private String logisticsDeliveryId;

    /**
     * 商品类型：1、商品, 2、卡券
     */
    private Integer goodsType;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品描述
     */
    private String goodsDesc;

    /**
     * 商品数量
     */
    private Integer goodsNum;

    /**
     * 商品图片地址
     */
    private String goodsImgUrl;

    /**
     * 商品规格编号
     */
    private String skuId;

    /**
     * 商品级物流费用承担方 1、本组织 2、供应商
     */
    private Byte logisticsType;

    /**
     * 商品规格
     */
    private String skuSpecs;

    /**
     * 删除标志 0:未删除 1:已删除
     */
    protected Byte deleteStatus;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected LocalDateTime updateTime;

    /**
     * 创建人账号
     */
    protected String creatorAccount;

    /**
     * 创建人姓名
     */
    protected String creatorName;

    /**
     * 更新人账号
     */
    protected String updateAccount;

    /**
     * '更新人姓名'
     */
    protected Integer version;
}

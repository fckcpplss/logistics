package com.longfor.c10.lzyx.logistics.entity.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物流运单监控列表返回VO
 * @author zhaoyl
 * @date 2022/2/17 下午3:52
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsMonitorListGodsVO {
    /**
     * 商品skuId
     */
    private String skuId;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品数量
     */
    private Integer goodsNum;
    /**
     * 商品图片地址
     */
    private String goodsImgUrl;
    /**
     * 商品规格
     */
    private String skuSpecs;
    /**
     * 商品类型
     */
    private Integer goodsType;
    /**
     * 商品描述
     */
    private String goodsDesc;

    /**
     * 业务类型
     */
    private Integer businessType;

    /**
     * 业务类型展示
     */
    private String businessTypeShow;

    /**
     * 商品备注
     */
    private String remark;

}

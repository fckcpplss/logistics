package com.longfor.c10.lzyx.logistics.client.entity.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 待发货、已发货列表商品vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVO {

    /**
     * 商品id
     */
    private String goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品数量
     */
    private Integer goodsNum;

    /**
     * 商品图片url
     */
    private String goodsImgUrl;

    /**
     * 商品规格编号
     */
    private String skuId;

    /**
     * 商品规格
     */
    private String skuSpecs;
}

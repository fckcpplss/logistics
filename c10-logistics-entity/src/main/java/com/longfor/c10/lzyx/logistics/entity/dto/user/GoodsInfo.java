package com.longfor.c10.lzyx.logistics.entity.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 商品信息
 * @author: jiamingqiang
 * @date: 2021/10/27
 */
@Data
@ApiModel("商品信息")
public class GoodsInfo {
    @ApiModelProperty(value = "商品类型：1、商品, 2、卡券")
    private String goodsType;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品描述")
    private String goodsDesc;

    @ApiModelProperty(value = "商品数量")
    private Integer goodsNum;

    @ApiModelProperty(value = "商品图片地址")
    private String goodsImgUrl;

    @ApiModelProperty(value = "商品规格编号")
    private String skuId;

    @ApiModelProperty(value = "物流类型 1:平台物流 2:商户物流")
    private String logisticsType;

    @ApiModelProperty(value = "商品规格")
    private String skuSpecs;
}

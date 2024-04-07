package com.longfor.c10.lzyx.logistics.entity.dto.admin;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
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
    @Excel(name = "商品编号",width= 20,height = 25,orderNum = "3")
    private String goodsId;

    /**
     * 商品名称
     */
    @Excel(name = "商品名称",width= 20,height = 25,orderNum = "4")
    private String goodsName;

    /**
     * 商品数量
     */
    @Excel(name = "商品数量",width= 20,height = 25,orderNum = "5")
    private Integer goodsNum;

    /**
     * 商品图片url
     */
    @ExcelIgnore
    private String goodsImgUrl;

    /**
     * 商品规格编号
     */
    @Excel(name = "sku编号",width= 20,height = 25,orderNum = "6")
    private String skuId;

    /**
     * 商品规格
     */
    @Excel(name = "商品规格",width= 20,height = 25,orderNum = "7")
    private String skuSpecs;

    /**
     * 商品类型
     */
    @ExcelIgnore
    private String goodsType;

    /**
     * 商品描述
     */
    @ExcelIgnore
    private String goodsDesc;
}

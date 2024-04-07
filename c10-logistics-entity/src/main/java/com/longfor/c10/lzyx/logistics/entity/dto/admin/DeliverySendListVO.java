package com.longfor.c10.lzyx.logistics.entity.dto.admin;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 已发货列表vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ColumnWidth(20)
public class DeliverySendListVO  extends GoodsVO{
    /**
     * 运营组织名称
     */
    @Excel(name = "运营组织",width= 20,orderNum = "0")
    private String operOrgName;

    @Excel(name = "运营组织",width= 20,orderNum = "0")
    private String operOrgId;

    /**
     * 供应商名称
     */
    @Excel(name = "供应商名称",width= 20,orderNum = "1")
    private String shopName;
    /**
     * 快递ID
     */
    @ExcelIgnore
    private String deliveryId;
    /**
     * 订单编号
     */
    @Excel(name = "订单编号",width= 20,orderNum = "2")
    private String orderNo;


    /**
     * 快递公司编码
     */
    @ExcelIgnore
    private String companyCode;

    /**
     * 快递公司名称
     */
    @Excel(name = "快递公司",width= 20,orderNum = "8")
    private String companyCodeShow;


    /**
     * 运单编号
     */
    @Excel(name = "运单号",width= 20,orderNum = "9")
    private String deliveryNo;

    /**
     * 订单创建时间
     */
    @Excel(name = "订单创建时间",width= 20,orderNum = "10")
    private String orderCreateTime;

    /**
     * 发货时间
     */
    @Excel(name = "发货时间",width= 20,orderNum = "11")
    private String deliveryTime;

    /**
     * 签收时间
     */
    @Excel(name = "签收时间",width= 20,orderNum = "12")
    private String signTime;

    /**
     * 物流状态
     */
    @ExcelIgnore
    private Integer logisticsStatus;
    /**
     * 物流状态展示
     */
    @Excel(name = "物流状态",width= 20,orderNum = "13")
    private String logisticsStatusShow;

    /**
     * 该运单中包含的商品
     */
    @ExcelIgnore
    private List<GoodsVO> goodsList;

    /**
     * 收货人姓名
     */
    @Excel(name = "用户姓名",width= 20,orderNum = "14")
    private String receiptName;

    /**
     * 收货人手机号
     */
    @Excel(name = "手机号",width= 20,orderNum = "15")
    private String receiptPhoneNumber;

    /**
     * 收货人地址
     */
    @Excel(name = "收货地址",width= 20,orderNum = "16")
    private String receiptAddress;

    /**
     * 物流类型
     */
    @ExcelIgnore
    private Integer logisticsType;

    /**
     * 物流类型中文展示
     */
    @Excel(name = "物流类型",width= 20,orderNum = "17")
    private String logisticsTypeShow;

    /**
     * 商家物流id
     */
    @ExcelIgnore
    private Integer shopLogisticsId;

    /**
     * 物流商品id
     */
    @ExcelIgnore
    private String goodsIds;

    /**
     * 航道编码
     */
    @ExcelIgnore
    private String bizChannelCode;

    /**
     * 销售模式
     */
    @ExcelIgnore
    private Integer sellType;
    /**
     * 销售模式展示
     */
    @Excel(name = "销售模式",width= 20,orderNum = "18")
    private String sellTypeShow;

    /**
     * 订单备注
     */
    @Excel(name = "订单备注",width= 20,orderNum = "19")
    private String orderDesc;
}

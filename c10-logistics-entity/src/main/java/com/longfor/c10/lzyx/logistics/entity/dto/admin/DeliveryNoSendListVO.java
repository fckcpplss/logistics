package com.longfor.c10.lzyx.logistics.entity.dto.admin;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 *  待发货列表vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryNoSendListVO extends GoodsVO{

    /**
     * 订单编号
     */
    @Excel(name = "订单编号",width= 20,orderNum = "0")
    private String orderNo;

    @Excel(name = "运营组织id",width= 20,orderNum = "1")
    private String orgId;

    /**
     * 运营组织名称
     */
    @Excel(name = "运营组织",width= 20,orderNum = "1")
    private String orgName;

    /**
     * 供应商名称
     */
    @Excel(name = "供应商名称",width= 20,orderNum = "2")
    private String shopName;

    /**
     * 商品信息列表
     */
    @ExcelIgnore
    private List<GoodsVO> goodsVos;

    /**
     * 商品id集合字符串
     */
    @ExcelIgnore
    private String goodsIds;

    /**
     * 收货人姓名
     */
    @ExcelProperty(value = {"用户姓名"},index = 9)
    @Excel(name = "用户姓名",width= 20,orderNum = "9")
    private String receiptName;

    /**
     * 收货人地址
     */
    @Excel(name = "收货地址",width= 20,orderNum = "10")
    private String receiptAddress;

    /**
     * 收货人手机号
     */
    @Excel(name = "手机号码",width= 20,orderNum = "11")
    private String receiptPhoneNumber;

    /**
     * 物流类型
     */
    @ExcelIgnore
    private Integer logisticsType;

    /**
     * 物流类型展示
     */
    @Excel(name = "运费承担方",width= 20,orderNum = "12")
    private String logisticsTypeShow;

    /**
     * 订单创建时间
     */
    @Excel(name = "订单创建时间",width= 20,orderNum = "13")
    private String orderCreateTime;

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
    @Excel(name = "销售模式",width= 20,orderNum = "14")
    private String sellTypeShow;

    /**
     * 订单备注
     */
    @Excel(name = "订单备注",width= 20,orderNum = "15")
    private String orderDesc;

    /**
     * 运单号
     */
    @Excel(name = "运单号",width= 20,orderNum = "16")
    private String deliveryNo;
}

package com.longfor.c10.lzyx.logistics.client.entity.param.merchant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 物流下单
 * @author zhaoyl
 * @date 2022/1/13
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddOrderListReqData implements Serializable {
    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 商品主键列表
     */
    @NotEmpty(message = "商品主键不能为空！")
    private List<Long> goodsIds;

    /**
     * 快递公司编码
     */
    @NotNull(message = "快递公司编码不能为空！")
    private String companyCode;

    /**
     * 商户物流账号配置表主键,在公司选择其他时，非必填
     */
    private Integer shopLogisticsId;

    /**
     * 快递公司运单号，在公司选择其他时，非空
     */
    private String deliveryNo;

    /**
     * 商户发货信息维护表主键
     */
    private Long sendAddressId;

    /**
     * 所属商铺id,用于做数据隔离，仅能发自己商铺的货品
     */
    private List<String> shopIds;
}

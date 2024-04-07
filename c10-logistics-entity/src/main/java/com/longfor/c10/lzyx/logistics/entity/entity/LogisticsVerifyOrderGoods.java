package com.longfor.c10.lzyx.logistics.entity.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 自提/核销订单商品表
 * </p>
 *
 * @author zhaoyl
 * @since 2022-04-14
 */
@Getter
@Setter
@TableName("logistics_verify_order_goods")
public class LogisticsVerifyOrderGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
      private Long id;

    /**
     * 子订单id
     */
    private String childOrderId;

    /**
     * 商品id
     */
    private String goodsId;

    /**
     * 父订单id
     */
    private String orderId;

    /**
     * 组织id
     */
    private String orgId;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 商户id
     */
    private String shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 所属主体类型(1：组织， 2：商户)
     */
    private Integer ownerType;

    /**
     * 商品类型（1.实体商品 2.虚拟商品）
     */
    private Integer goodsType;

    /**
     * 商品子类型 (暂定)
     */
    private Integer goodsSubType;

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
     * 商品图片地址(仅一个地址)
     */
    private String goodsImgUrl;

    /**
     * sku_id
     */
    private String skuId;

    /**
     * 商品规格
     */
    private String skuSpecs;

    /**
     * 商品市场单价（单位：元）
     */
    private BigDecimal goodsPrice;

    /**
     * 商品售卖单价（单位：元）
     */
    private BigDecimal actualPrice;

    /**
     * 成本单价（单位：元）
     */
    private BigDecimal costPrice;

    /**
     * 珑珠售卖单价,（单位：角）
     */
    private BigDecimal lzActualPrice;

    /**
     * 收货类型(1：门店自提，2：平台物流，3：商家物流)
     */
    private Integer receiptType;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
      @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 营销账号类型
     */
    private Integer marketAccountType;

    /**
     * 商品状态(1、未使用 2、使用 3、已超期 4、退款中 5、已退款)
     */
    private Integer goodsStatus;

    /**
     * 一级编号
     */
    private String oneLevelId;

    /**
     * 二级编号
     */
    private String twoLevelId;

    /**
     * 三级编号
     */
    private String threeLevelId;

    /**
     * 现金实付总金额（单位：元）
     */
    private BigDecimal payActualAmount;

    /**
     * 龙珠实付总金额,（单位：角）
     */
    private BigDecimal lzPayActualAmount;

    /**
     * 龙珠总金额,（单位：角）
     */
    private BigDecimal lzTotalAmount;

    /**
     * 龙珠优惠总金额,（单位：角）
     */
    private BigDecimal lzDiscountAmount;

    /**
     * 龙珠退款实际金额,（单位：角）
     */
    private BigDecimal lzRefundActualAmount;

    /**
     * 龙珠退款退回优惠总金额,（单位：角）
     */
    private BigDecimal lzRefundDiscountAmount;

    /**
     * 退款的商品数量
     */
    private Integer refundGoodsNum;

    /**
     * 现金退款金额（单位：元）
     */
    private BigDecimal refundAmount;

    /**
     * 龙珠退款金额,（单位：角）
     */
    private BigDecimal lzRefundAmount;

    /**
     * 人民币认定单价（单位：元）
     */
    private BigDecimal rmbCognizancePrice;

    /**
     * 商品备注
     */
    private String goodsRemark;

    /**
     * 一级类目名称
     */
    private String oneLevelName;

    /**
     * 二级类目名称
     */
    private String twoLevelName;

    /**
     * 三级类目名称
     */
    private String threeLevelName;

    /**
     * 商品备注
     */
    private String goodsDescribe;

    /**
     * 自提电话
     */
    private String pickupPhone;

    /**
     * 自提点
     */
    private String pickupSpot;

    /**
     * 自提地址
     */
    private String pickupAddress;

    /**
     * 自提地址ID
     */
    private String pickupAddressId;

    /**
     * 自提开始时间
     */
    private Date pickupStartTime;

    /**
     * 自提结束时间
     */
    private Date pickupEndTime;

    /**
     * 自提/核销码
     */
    private String pickupCode;

    /**
     * 自提码路径/核销码路径
     */
    private String pickupQrcodeUrl;

    /**
     * 自提描述
     */
    private String pickupDesc;

    /**
     * 核销商户id
     */
    private String verifyMerchantId;

    /**
     * 核销供应商id
     */
    private String verifyShopId;

    /**
     * 核销供应商名称
     */
    private String verifyShopName;

    /**
     * 核销运营组织编号
     */
    private String verifyOrgId;

    /**
     * 核销运营组织名称
     */
    private String verifyOrgName;

    /**
     * 核销用户id
     */
    private String verifyUserId;

    /**
     * 核销用户账号
     */
    private String verifyUserAccount;

    /**
     * 核销用户名称
     */
    private String verifyUserName;

    /**
     * 核销单号
     */
    private String verifyNo;

    /**
     * 核销码
     */
    private String verifyCode;

    /**
     * 核销类型0、用户端核销，1.运营端核销
     */
    private Integer verifyType;

    /**
     * 自提/核销时间
     */
    private Date verifyTime;

    /**
     * 航道编码
     */
    private String bizChannelCode;

    /**
     * 航道名称
     */
    private String bizChannelName;

    /**
     * 批发价
     */
    private BigDecimal wholesalePrice;

    /**
     * 业务使用税率
     */
    private BigDecimal bizTaxRate;

    /**
     * 核销状态：0,待核销，1.核销成功，2.核销失败，3.核销超期
     */
    private Integer verifyStatus;

    /**
     * 提款状态：0.未退款，1.退款中，2.已退款
     */
    private Integer refundStatus;

    /**
     * 订单创建时间
     */
    private Date orderCreateTime;

    /**
     * 0,未删除，1.删除
     */
    private Integer isDelete;

    /**
     * 珑民手机号
     */
    private String lmPhone;

    /**
     * 珑民昵称
     */
    private String lmNickname;

    /**
     * 珑民id
     */
    private String lmId;


}

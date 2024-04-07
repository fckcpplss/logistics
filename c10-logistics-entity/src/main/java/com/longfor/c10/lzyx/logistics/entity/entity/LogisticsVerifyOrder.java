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
 * 自提/核销子订单记录表
 * </p>
 *
 * @author liuxin41
 * @since 2022-04-18
 */
@Getter
@Setter
@TableName("logistics_verify_order")
public class LogisticsVerifyOrder implements Serializable {

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
     * 订单id
     */
    private String orderId;

    /**
     * 商铺ID
     */
    private String shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 所属主体类型 1、商场 2、商户
     */
    private Integer ownerType;

    /**
     * 商品类型（1.商品 2.卡券 3.券包 4.停车充值）
     */
    private Integer goodsType;

    /**
     * 支付单号
     */
    private String payOrderId;

    /**
     * 渠道应用id (1:珑珠商城H5（龙信入口） 2:日月湖微信小程序 3:日月湖支付宝小程序)
     */
    private String channelId;

    /**
     * 渠道应用业务编码 (LZSC:珑珠H5商城商品 C2JFSC:日月湖商城商品 C2QB:日月湖券包 C2KQ:日月湖卡券 )
     */
    private String channelBizCode;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户/会员等级(小程序会员等级：1:普卡2:银卡3:金卡4:黑卡)
     */
    private String userGrade;

    /**
     * 支付方式(1：珑珠支付 2：电银微信JSAPI支付 3：免支付 ）
     */
    private Integer payType;

    /**
     * 龙珠总金额,（单位：角）
     */
    private BigDecimal lzTotalAmount;

    /**
     * 龙珠实付总金额,（单位：角）
     */
    private BigDecimal lzPayActualAmount;

    /**
     * 龙珠优惠总金额,（单位：角）
     */
    private BigDecimal lzDiscountAmount;

    /**
     * 现金退款金额（单位：元）
     */
    private BigDecimal refundAmount;

    /**
     * 现金总金额（单位：元）
     */
    private BigDecimal rmbTotalAmount;

    /**
     * 现金实付总金额（单位：元）
     */
    private BigDecimal rmbPayActualAmount;

    /**
     * 现金优惠总金额（单位：元）
     */
    private BigDecimal rmbDiscountAmount;

    /**
     * 现金运费实付总金额（单位：元）
     */
    private BigDecimal rmbFreightActualAmount;

    /**
     * 龙珠退款金额,（单位：角）
     */
    private BigDecimal lzRefundAmount;

    /**
     * 珑珠运费总金额,（单位：角）
     */
    private BigDecimal lzFreightActualAmount;

    /**
     * 龙珠退款实际金额,（单位：角）
     */
    private BigDecimal lzRefundActualAmount;

    /**
     * 龙珠退款退回优惠总金额,（单位：角）
     */
    private BigDecimal lzRefundDiscountAmount;

    /**
     * 订单失效时间
     */
    private Date expireTime;

    /**
     * 支付成功时间
     */
    private Date paySuccessTime;

    /**
     * 订单备注
     */
    private String orderDesc;

    /**
     * 自提点
     */
    private String pickupSpot;

    /**
     * 自提地址，默认所属主体一致，可为空
     */
    private String pickupAddress;

    /**
     * 自提地址ID
     */
    private String pickupAddressId;

    /**
     * 自提描述
     */
    private String pickupDesc;

    /**
     * 自提开始日期
     */
    private Date pickupStartTime;

    /**
     * 自提结束日期/签收截止日期
     */
    private Date pickupEndTime;

    /**
     * 自提码路径/核销码路径
     */
    private String pickupQrcodeUrl;

    /**
     * 自提码/核销码
     */
    private String pickupCode;

    /**
     * 核销类型0、用户端核销，1.运营端核销
     */
    private Integer verifyType;

    /**
     * 核销商户id
     */
    private String verifyMerchantId;

    /**
     * 核销运营组织编号
     */
    private String verifyOrgId;

    /**
     * 核销运营组织名称
     */
    private String verifyOrgName;

    /**
     * 核销商户编号
     */
    private String verifyShopId;

    /**
     * 核销供应商名称
     */
    private String verifyShopName;

    /**
     * 核销时间
     */
    private Date verifyTime;

    /**
     * 核销单号
     */
    private String verifyNo;

    /**
     * 核销码
     */
    private String verifyCode;

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
     * 订单创建时间
     */
    private Date orderCreateTime;

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
     * 收款账号
     */
    private String accountNo;

    /**
     * 订单类型（1商品类订单、2停车类订单、3卡券类订单、4券包类订单）
     */
    private Integer orderType;

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

    /**
     * 附加数据
     */
    private String extraInfo;

    /**
     * 人民币认定总金额（单位：元）
     */
    private BigDecimal rmbCognizanceAmount;

    /**
     * 现金部分支付工具(1:微信 2: 支付宝)
     */
    private Integer payToolRmb;

    /**
     * 珑珠部分支付工具(3:珑珠, 4: 珑珠实时立减)
     */
    private Integer payToolLz;

    /**
     * 组织id
     */
    private String orgId;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 经营模式(1：自营 2：联营)
     */
    private Integer managementModel;

    /**
     * utm媒介
     */
    private String utmMedium;

    /**
     * utm来源
     */
    private String utmSource;

    /**
     * 商户收款账号
     */
    private String merId;

    /**
     * 商户支付流水号
     */
    private String merTransNo;

    /**
     * 端来源
     */
    private String entrySourse;

    /**
     * 分佣路由状态，1.是，0.否
     */
    private Integer benefitRouteStatus;

    /**
     * 核销状态：0,待核销，1.核销成功，2.核销失败，3.核销超期
     */
    private Integer verifyStatus;

    /**
     * 退款状态：0.未退款，1.退款中，2.已退款，3.部分退款，4.退款失败
     */
    private Integer refundStatus;

    /**
     * 0,未删除，1.删除
     */
    private Integer isDelete;


}

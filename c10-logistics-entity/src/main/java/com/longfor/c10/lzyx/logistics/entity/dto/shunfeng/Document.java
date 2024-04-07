package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import lombok.Data;

import java.util.List;

/**
 * @description: 业务数据Dto
 * @author: zhaoyalong
 */
@Data
public class Document {
    /**
     * 主运单号
     */
    private String masterWaybillNo;
    /**
     * 子运单号（单票运单时不填）
     */
    private String branchWaybillNo;
    /**
     * 签回单号（签回单时，masterWayBillNo和branchWaybillNo不填，寄件人，收件人等信息需要自己转换）
     */
    private String backWaybillNo;
    /**
     * 该运单号打印时对应的顺序号（母运单序号=1，子运单号时递增该值，单票运单时不填）
     */
    private String seq;
    /**
     * 子母件运单总数（单票运单时不填）
     */
    private String sum;
    /**
     * 如热敏贴纸上已印刷LOGO及服务电话则不需打印。如未印刷则需打印。 true:热敏纸上无印刷需要打印（不需要打印则不填）
     */
    private String isPrintLogo;
    /**
     * 固定传值：scp，选择V1.0版本时必填
     */
    private String systemSource;
    /**
     * 打印次数，国际面单可不填，选择V1.0版本时必填
     */
    private String printNum;
    /**
     * 打印时间<格式: YYYY-MM-DD hh:mm:ss>*（包牌模板时间格式：MM/DD hh:mm）*，选择V1.0版本时必填
     */
    private String printDateTime;
    /**
     * 时效文本类型，特快等等，对应RLS的proCode（入参customerType=1），不传值时则打印agingType对应的时效图标。选择V1.0版本时必填
     */
    private String agingText;
    /**
     * 目的地（路由信息），选择V1.0版本时必填
     */
    private String destRouteLabel;
    /**
     * 单元区域编码，国际面单可不填。选择V1.0版本时必填
     */
    private String destTeamCode;
    /**
     * 寄件人姓名，*如需要脱敏，业务系统需自己处理*
     */
    private String fromName;
    /**
     * 寄件人电话，*如需要脱敏，业务系统需自己处理*，选择V1.0版本时必填
     */
    private String fromPhone;
    /**
     * 寄件人公司名称，*如需要脱敏，业务系统需自己处理*，选择V1.0版本时必填
     */
    private String fromOrgName;
    /**
     * 寄件人地址，*如需要脱敏，业务系统需自己处理*，选择V1.0版本时必填
     */
    private String fromAddress;
    /**
     * 寄件人邮编（国际模板使用），选择V1.0版本时必填
     */
    private String fromPostcode;
    /**
     * 收件人姓名，*如需要脱敏，业务系统需自己处理*，选择V1.0版本时必填
     */
    private String toName;
    /**
     * 收件人电话，*如需要脱敏，业务系统需自己处理*，选择V1.0版本时必填
     */
    private String toPhone;
    /**
     * 收件人公司名称，*如需要脱敏，业务系统需自己处理*
     */
    private String toOrgName;
    /**
     * 收件人地址，*如需要脱敏，业务系统需自己处理*，选择V1.0版本时必填
     */
    private String toAddress;
    /**
     * 收件人邮编（国际模板使用），选择V1.0版本时必填
     */
    private String toPostcode;
    /**
     * 是否代收货款（true，false）
     */
    private String isCod;
    /**
     * 是否回单（true，false）
     */
    private String isPod;
    /**
     * 付款方式（支持选项：寄付月结、寄付转第三方、寄付现结、到付），选择V1.0版本时必填
     */
    private String payment;
    /**
     * 进港映射码，选择V1.0版本时必填
     */
    private String codingMapping;
    /**
     * 二维码信息，选择V1.0版本时必填
     */
    private String twoDimensionCode;
    /**
     * 电子产品类型图标（选项：A、B、E），注意RLS接口有A1值，调云打印需传A。选择V1.0版本时必填	传值需为A、B、E对应，否则为空不显示
     */
    private String abFlag;
    /**
     * 易燃标识（选项A，RE），如果newAbFlag和abFlag同时传递，优先newAbFlag
     */
    private String newAbFlag;
    /**
     * 出港信息，选择V1.0版本时必填
     */
    private String codingMappingOut;
    /**
     * 增值服务
     */
    private String incrementService;
    /**
     * 增值服务明细列表，举例如：[“保价费用:130元”,“包装服务:2元”]
     */
    private List<String> incrementServiceList;
    /**
     * 托寄物
     */
    private String entrustedArticles;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 件数
     */
    private String packageNumber;
    /**
     * 计费重量
     */
    private String chargedWeight;
    /**
     * 实际重量
     */
    private String actualWeight;
    /**
     * 费用合计
     */
    private String costTotal;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否转寄协议客户（支持选项：是、否），国际面单可不填
     */
    private String isForwardAgreement;
    /**
     * 产品类型，顺丰特惠等
     */
    private String productType;
    /**
     * 产品名称，限时KC24等，对应RLS的 proName，选择V1.0版本时必填
     */
    private String productName;
    /**
     * 出港中转场代码，选择V1.0版本时必填
     */
    private String sourceTransferCode;
    /**
     * 收货时间<格式: yyyy-MM-dd hh:mm:ss>
     */
    private String receiptTime;
    /**
     * 图标名称数组（支持选项：逆、轻放/glass、鲜/fresh、蟹/grab、药/medicine、重/hf、Z、包裹/parcel、标快/se、文件/doc 、X1、X2、X3、X4、X6、X7、X8等，明细参考： 图标定义如[“蟹”,“鲜”])，选择V1.0版本时必填	传值需和图标定义中对应，否则为空不显示
     */
    private List<String> printIcons;
    /**
     * 针对存根区如热敏贴纸上已印刷LOGO及服务电话则不需打印。如未印刷则需打印。 true:热敏纸上无印刷需要打印（不需要打印则不填）
     */
    private String isPrintStubLogo;
    /**
     * 运费
     */
    private String transPrice;
    /**
     * 代收货款金额
     */
    private String collectMoney;
    /**
     * 单尺寸（格式：长宽高）
     */
    private String size;
    /**
     * 签单返还金额
     */
    private String signReturn;
    /**
     * 快件为第三方地区产生的金额
     */
    private String thirdAreaPrice;
    /**
     * 快件为易碎件产生的金额
     */
    private String fragilePrice;
    /**
     * 月结账号
     */
    private String monthlyCount;
    /**
     * 否	优惠券产生的价格
     */
    private String discountPrice;
    /**
     * 超长超重产生的价格
     */
    private String beyondPrice;
    /**
     * 包装费用
     */
    private String packPrice;
    /**
     * 保价费用
     */
    private String insurePrice;
    /**
     * 托寄物明细列表，发票联使用，数组类型如：[“file,2,pc,5000,0,AX”,“package,2,pc,5000,0,AX”]，对应含义：Description, QTY, Unit Price, Value, Origin
     */
    private String entrustedList;
    /**
     * 报关批次
     */
    private String cusBatch;
    /**
     * 贸易条件，用于台湾发票等
     */
    private String termsOfTrade;
    /**
     * 是否已实名
     */
    private String realName;
    /**
     * 保鲜费用
     */
    private String freshPrice;
    /**
     * 安装费用
     */
    private String installPrice;
    /**
     * 声报价值
     */
    private String declaredValue;
    /**
     * 大件入户费用
     */
    private String largeSizedEntryPrice;
    /**
     * 目的地地址关键词，打印在主运单收方地址后面	2020年12月新增
     */
    private String destAddrKeyWord;
    /**
     * 声报价值单位
     */
    private String declaredValueUnit;
    /**
     * 运费单位 代收货款，费用合计共用此单位
     */
    private String transPriceUnit;
}
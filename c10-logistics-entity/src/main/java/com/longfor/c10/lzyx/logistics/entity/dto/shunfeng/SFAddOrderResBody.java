package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 顺丰下单请求body
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SFAddOrderResBody implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = 1L;

    /**
     * 不允许重复	否	用户订单号，为空则系统自动生成；不为空则建议自定义订单号前加上客户公司简拼或者英文名称，防止订单号重复
     */
    private String orderId;
    /**
     * mailno
     * 12位或15位的运单号	否	非自定义运单号，只适用于预分配订单客户。SF+13位数字或12位数字，不为空则按照传输运单号下单
     */
    private String mailno;
    /**
     * 发送开始时间
     * YYYY-MM-DD HH24:MM:SS	否	上门取件时间，非必填，要求上门取件开始时间，格式：YYYY-MM-DD HH24:MM:SS，示例：2012-07-30 09:30:00
     */
    private String sendStartTime;
    /**
     * j公司
     * 中文或英文，不支持特殊符号	否	寄件公司
     */
    private String jCompany;
    /**
     * j联系
     * 只支持中文真实姓名	是	寄件人
     */
    private String jContact;
    /**
     * j电话
     * 数字/-	条件	寄件电话，寄件电话与寄件手机必填其一
     */
    private String jTel;
    /**
     * j移动
     * 真实手机号	条件	寄件手机，寄件电话与寄件手机必填其一
     */
    private String jMobile;
    /**
     * j省
     * 寄件方所在省份	否	必须是标准的省名称称谓，如：广东省。如果是直辖市，请直接传北京、上海等。
     */
    private String jProvince;
    /**
     * j市
     * 寄件方所在城市名称	否	必须是标准的城市称谓，如：深圳市。
     */
    private String jCity;
    /**
     * j地址
     * 寄件详细地址	是	如不传寄件省市时需包含省市区详细地址
     */
    private String jAddress;
    /**
     * d公司
     * 中文或英文，不支持特殊符号	否	收件公司
     */
    private String dCompany;
    /**
     * d联系
     * 只支持真实姓名	是	收件人
     */
    private String dContact;
    /**
     * d电话
     * 数字/-	条件	收件电话，收件电话与收件手机必填其一
     */
    private String dTel;
    /**
     * d移动
     * 真实手机号	条件	收件手机，收件电话与收件手机必填其一
     */
    private String dMobile;
    /**
     * d省
     * 到件方所在省份	否	必须是标准的省名称称谓，如：广东省。如果是直辖市，请直接传北京、上海等。
     * 如果此字段与dCity字段都有值，则直接使用这两个值而不是通过对dAddress进行地址识别获取。为避免地址识别不成功的风险，建议传输此字段。
     */
    private String dProvince;
    /**
     * d城市
     * 到件方所在城市名称	否	必须是标准的城市称谓，如：深圳市。如果是直辖市，请直接传北京（或北京市）、上海（或上海市）等。
     * 如果此字段与dProvince字段都有值，则直接使用这两个值而不是对dAddress进行地址识别获取。为避免地址识别不成功的风险，建议传输此字段。
     */
    private String dCity;
    /**
     * d地址
     * 包含省市区详细地址	是	收件详细地址
     */
    private String dAddress;
    /**
     * custid
     * 10位数字	条件	顺丰月结卡号，需确认月结卡号是否可用，是否开通对应产品类型
     */
    private String custid;
    /**
     * 支付方法
     * 约定数字	是	运费付款方式，根据实际情况选择一种付款方式：0-寄付月结；1-寄付现结；2-收方付；3-第三方付；
     */
    private String payMethod;
    /**
     * 表达类型
     * 约定数字	是	快件产品类别，传值产品编码，详见《附录-2.快件产品类别表》，只有在商务上与顺丰约定的类别方可使用
     */
    private String expressType;
    /**
     * 包没有
     * 1~300	否	件数，非必填，包裹件数，一个包裹对应一个运单号，如果是大于1个包裹，则返回则按照子母件的方式返回母运单号和子运单号。填写实际件数限制为1到300之间。默认为1
     */
    private String packagesNo;
    /**
     * depositum信息
     * 限长度	是	托寄物内容，按实际托寄物填写，可填写简称（文件、水果等）
     */
    private String depositumInfo;
    /**
     * depositum没有
     * 限数字，1到10万	是	托寄物数量，指每票快件包含的托寄物总数
     */
    private String depositumNo;
    /**
     * 包裹的重量
     * 限数字，并大于0.001	否	包裹重量，订单货物单位重量，包含子母件，单位千克，精确到小数点后3位，跨境件报关需要填写
     */
    private String parcelWeighs;
    /**
     * 备注
     * 长度限100字符	否	寄方备注
     */
    private String remark;
    /**
     * 是叫
     * 数字0或1	否	是否下call，是否通过手持终端通知顺丰收派员上门收件，支持以下值：1-要求；0-不要求；
     */
    private String isDoCall;
    /**
     * 是收据
     * 数字0或1	否	是否签回单：0-否；1-是；
     */
    private String isReceipt;
    /**
     * 收据
     * 条件	签回单要求，当isReceipt=1时可填写，isReceipt=0时填写无效，非必填(目前该字段用于自己打单时用，不会传给上游系统，如需要求小哥知晓，建议传值到寄件备注中)
     */
    private String receipt;
    /**
     * 是否自取
     * 数字0或1	否	是否自取：0-否；1-是；
     */
    private String isSelfGet;
    /**
     * 新鲜的
     * 数字0或1	否	保鲜服务：0-否；1-是；
     */
    private String fresh;
    /**
     * 保价
     * 范围：1000~500000000	否	保价金额，如需保价时，填写物品声明价值以原寄地所在区域币种为准，如中国大陆为人民币，香港为港币，保留3位小数。没有保价金额则无需填写，限制在1到50万之间
     */
    private String supportValue;
    /**
     * 个性化包装的钱
     * 限数字，大于000元	否	个性化包装金额，非必填，根据实际包装费用收填写，没有个性化包装费用则无需填写
     */
    private String packIndividuationMoney;
    /**
     * 是否代收
     * 数字0或1	否	是否代收：0-否；1-是；
     */
    private String isCollection;
    /**
     * 代收金额
     * 未开通额度：5000~20000000；开通额度：5.000~100000.000	条件	代收金额，当isCollection=1时根据实际代收金额填写，isCollection=0时则无需填写
     */
    private String collectionMoney;
    /**
     * 寄件范围
     * 数字0或1	否	寄件范围：1-内地互寄；0-港澳台+国际。默认“内地互寄”
     */
    private String expressBillType;
    /**
     * 寄件性质
     * 数字0或1	否	寄件性质：0-个人件；1-企业件。默认“企业件”
     */
    private String personalExpress;
    /**
     * 创造者的用户
     * 限长度	否	创建人，如需使用顺丰快递管家进行订单管理，则需要填顺丰快递管家中真实账号，用于订单归属人的判断，否则无要求，如为空，则取公司接口人账号
     */
    private String creatorUser;
    /**
     * 体积
     * 限数字，并大于0000	否	订单货物总体积，单位立方厘米，精确到小数点后3位，会用于计抛(是否计抛具体商务沟通中双方约定)。
     */
    private String volume;
    /**
     * 公司标识
     * 	是	分配客户sendAppId,数值类型
     */
    private String companyId;
    /**
     * 密码标志
     * 数字[0/1/2/3]	否	口令签收：0-否；1-口令；2-身份证；3-口令或身份证，签收时二者选其一。默认0
     */
    private String passwordSign;
    /**
     * 额外的
     * 否	自定义字段1
     */
    private String extra;
    /**
     * 选择自定义field2
     * 否	自定义字段2
     */
    private String customField2;
    /**
     * 产品代码
     * 否	微派产品编码，下微派单必填，需在快递管家项目维护
     */
    private String productCode;
    /**
     * 密码标志值
     * 数字[0-9]{6}	条件	当passwordSign字段为2或3时，传入身份证后6位；
     */
    private String passwordSignValue;
}

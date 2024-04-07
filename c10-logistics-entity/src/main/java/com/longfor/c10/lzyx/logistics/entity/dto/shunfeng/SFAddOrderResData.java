package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 顺丰下单resultDTO
 * @author zhaoyalong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SFAddOrderResData implements Serializable {
    /**
     * 串行版本uid
     */
    private static final long serialVersionUID = 1L;
    /**
     * 客户订单号
     */
    private String orderid;
    /**
     * 顺丰运单号	一个订单只能有一个母单号，如果是子母单的情况，以半角逗号分隔，主单号在第一个位置，如“755123456789，001123456789，002123456789”，可用于顺丰电子面单标签打印。
     */
    private String mailno;
    /**
     * 顺丰签回单服务运单号
     */
    private String return_tracking_no;
    /**
     * 原寄地区域代码	可用于顺丰电子面单标签打印。
     */
    private String origincode;
    /**
     * 目的地区域代码	可用于顺丰电子面单标签打印。
     */
    private String destcode;
    /**
     * 筛单结果	1-人工确认；2-可收派；3-不可以收派；
     */
    private String filter_result;
    /**
     *备注	filter_result=3时必填,不可以收派的原因代码：1-收方超范围；2-派方超范围；3-其它原因；高峰管控提示信息；【数字】:【高峰管控提示信息】(如：4-温馨提示；1-春运延时)
     */
    private String remark;
    /**
     * 原寄地中转场
     */
    private String sourceTransferCode;
    /**
     * 原寄地城市代码
     */
    private String sourceCityCode;
    /**
     * 原寄地网点代码
     */
    private String sourceDeptCode;
    /**
     * 原寄地单元区域
     */
    private String sourceTeamCode;
    /**
     * 目的地城市代码	eg:755
     */
    private String destCityCode;
    /**
     * 目的地网点代码	eg:755AQ
     */
    private String destDeptCode;
    /**
     * 目的地网点代码映射码
     */
    private String destDeptCodeMapping;
    /**
     * 目的地单元区域
     */
    private String destTeamCode;
    /**
     * 目的地单元区域映射码
     */
    private String destTeamCodeMapping;
    /**
     * 目的地中转场
     */
    private String destTransferCode;
    /**
     * 路由标签信息	如果是大网的路由标签,这里的值是目的地网点代码,如果是同城配的路由标签,
     * 这里的值是根据同城配的设置映射出来的值,不同的配置结果会不一样,不能根据-符号切分
     * (如:上海同城配,可能是:集散点-目的地网点-接驳点,也有可能是目的地网点代码-集散点-接驳点)
     */
    private String destRouteLabel;
    /**
     * 产品名称	对应RLS:pro_name
     */
    private String proName;
    /**
     * 快件内容	如:C816、SP601
     */
    private String cargoTypeCode;
    /**
     * 时效代码	如:T4
     */
    private String limitTypeCode;
    /**
     * 产品类型	如:B1
     */
    private String expressTypeCode;
    /**
     * 入港映射码	eg:S10
     */
    private String codingMapping;
    /**
     * 出港映射码
     */
    private String codingMappingOut;
    /**
     * XB标志	0:不需要打印XB，1:需要打印XB
     */
    private String xbFlag;
    /**
     * 打印标志	返回值总共有9位,每位只有0和1两种,0表示按丰密面单默认的规则,1是显示,顺序如下,如111110000表示打印寄方姓名、寄方电话、寄方公司名、寄方地址和重量,收方姓名、收方电话、
     * 收方公司和收方地址按丰密面单默认规则：1-寄方姓名；2-寄方电话；3-寄方公司名；4-寄方地址；5-重量；6-收方姓名；7-收方电话；8-收方公司名；9-收方地址；
     */
    private String printFlag;
    /**
     * 二维码	根据规则生成字符串信息,格式为MMM={‘k1’:’(目的地中转场代码)’,’k2’:’(目的地原始网点代码)’,’k3’:’(目的地单元区域)’,’k4’:’
     * (附件通过三维码(express_type_code、limit_type_code、 cargo_type_code)映射时效类型)’,’k5’:’(运单号)’,’k6’:’(AB标识)’,’k7’:’(校验码)’}
     */
    private String twoDimensionCode;
    /**
     * 时效类型	值为二维码中的K4
     */
    private String proCode;
    /**
     * 打印图标	根据托寄物判断需要打印的图标(重货,蟹类,生鲜,易碎，Z标)返回值有8位，每一位只有0和1两种，0表示按运单默认的规则，1表示显示。后面两位默认0备用。
     * 顺序如下：重货，蟹类，生鲜，易碎，医药类，Z标,酒类，0。如：00000000表示不需要打印重货，蟹类，生鲜，易碎，医药，Z标，酒类，备用
     */
    private String printIcon;
    /**
     * AB标
     */
    private String abFlag;
    /**
     * ab标扩展
     */
    private String newAbflag;
    /**
     * 打印图标扩展
     */
    private String newIcon;
    private String errMsg;
    private String destPortCode;
    private String destCountry;
    private String destPostCode;
    private String goodsValueTotal;
    private String currencySymbol;
    private String goodsNumber;
    private String twoDimensionCode2;
}

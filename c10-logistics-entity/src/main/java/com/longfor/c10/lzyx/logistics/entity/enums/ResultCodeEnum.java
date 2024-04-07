package com.longfor.c10.lzyx.logistics.entity.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author: zhaoyalong
 * @version: 1.0
 */
@Getter
@AllArgsConstructor
public enum ResultCodeEnum {

    /**
     * 服务异常
     */
    PARAM_NULL(20000, "参数不能为空"),
    ILLEGALARGUMENT(20001, "参数错误"),
    BINDFAIL(20002, "绑定失败"),
    UNBINDFAIL(20003, "解绑失败"),
    UNKNOWNERROR(20004, "未知错误"),
    UNAUTHORIZED(56000, "未登录或登录已过期"),
    NOT_EXIST(20005, "物流订单不存在"),
    STATE_INVALID(20006, "已下单和待揽收的物流单可以取消发货"),
    LOGISTICS_ADDRESS_NOT_EXIST(30000, "物流地址不存在"),
    LOGISTICS_ADDRESS_ADD_FAIL(30001, "物流地址新增失败，请稍后再试"),
    LOGISTICS_ADDRESS_ADD_LIMIT_FAIL(30001, "最多10个地址信息"),
    LOGISTICS_ADDRESS_UPDATE_FAIL(30002, "物流地址修改失败，请稍后再试"),
    LOGISTICS_ADDRESS_DELETE_FAIL(30001, "物流地址删除失败，请稍后再试"),
    VERIFICATION_FAIL(40001, "核销码错误"),
    VERIFICATION_SHOP_NON_FAIL(40001, "核销店铺不能为空"),
    VERIFICATION_SHOP_ERROR_FAIL(40001, "非本商店商品提取码，无法核销"),
    VERIFICATION_STORE_NON_FAIL(40001, "核销项目不能为空"),
    VERIFICATION_STORE_ERROR_FAIL(40001, "非本天街商品提取码，无法核销"),
    VERIFICATION_STATUS_INVALID_FAIL(40001, "提取码已失效"),
    VERIFICATION_STATUS_ISREFUND_FAIL(40001, "已退单，无法核销"),
    VERIFICATION_STATUS_EXTRACTED_FAIL(40002, "商品已核销提取，无法再次核销"),
    VERIFICATION_GOODS_ERROR_FAIL(40003, "未找到核销码对应商品"),
    VERIFICATION_TYPE_SHOP_FAIL(40003, "需到商户核销提取"),
    VERIFICATION_TYPE_STORE_FAIL(40004, "本天街商品提取码，请到指定地点核销提取"),
    VERIFICATION_NOT_START_FAIL(40004, "提取时间未开始"),
    VERIFICATION_NOT_END_FAIL(40004, "提取时间已结束"),
    DELIVERY_SEND_FAIL(50001, "发货处理失败，请稍后再试"),
    DELIVERY_SEND_STATUS_FAIL(50002, "发货失败，物流单已发货"),
    DELIVERY_SEND_TYPE_FAIL(50003, "发货失败，非快递物流类型不能操作发货"),
    DELIVERY_CANNEL_FAIL(50004, "取消发货处理失败，请稍后再试"),
    DELIVERY_IS_REFUND_FAIL(50005, "操作失败，订单已退款"),
    DELIVERY_CONFIRM_FAIL(50006, "该物流单非已发货状态不可确认收货"),
    DELIVERY_CONFIRM_NOT_AUTHORITY_FAIL(50007, "该用户无操作权限"),
    DELIVERY_WECHAT_SUCCESS(200, "微信物流操作成功"),
    DELIVERY_JD_SUCCESS(100, "京东物流操作成功"),
    DELIVERY_WULIU100_SUCCESS(200, "物流一百操作成功"),
    DELIVERY_WULIU100_FAILS(60002, "物流一百操作失败"),
    DELIVERY_SFWL_SUCCESS(200, "顺丰物流操作成功"),
    SFWL_QUERY_FAILS(60001, "顺丰物流查询失败"),
    WECHAT_ERR(100001, "请求微信失败"),
    JD_ERR(100002, "请求京东失败[%s]"),
    JD_ACCESSTOKEN_ERR(100003, "京东accessToken获取失败"),
    LOGISTICS_DEFAULT(50000, "默认地址设置失败"),
    LOGISTICS_DELETE(50001, "发货地址删除失败"),
    USER_ERROR(50002, "无效登录信息"),
    ;
    private final int code;

    private final String message;

    public static ResultCodeEnum fromCode(int code) {
        return Arrays.stream(ResultCodeEnum.values())
                .filter(x -> x.getCode() == code)
                .findAny()
                .orElse(null);
    }

}

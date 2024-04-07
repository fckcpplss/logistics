package com.longfor.c10.lzyx.logistics.entity.constant;

/**
 * 常量类
 * @author  zhaoyalong
 */
public interface CommonConstant {
    /**
     * 常量0
     */
    Integer CONS0 = 0;
    /**
     * 常量1
     */
    Integer CONS1 = 1;
    Integer CONS2 = 2;
    Integer CONS100 = 100;
    public static final String HYPHEN = "-";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String RIGHT_SLASH = "/";
    public static final String BLANK_ONE = " ";
    public static final String BLANK = "";
    public static final Integer SF_API_SUCCESS_CODE = 200;
    public static final Integer JD_API_SUCCESS_CODE = 1;
    public static final Integer KUAIDI100_API_SUCCESS_CODE = 1;
    String CHARSET_UTF8 = "UTF-8";
    //自提/核销订单核销单号前缀
    public static final String VERIFY_ORDER_VERIFY_NO_PREFIX = "hx";
    String ROUT_SOURCE_KEY = "source";

    String SF_ROUT_SOURCE = "SF";
    String JD_ROUT_SOURCE = "JD";
    String SF_STATE_SOURCE = "SF_STATE";
    String SF_STATE_TIMESTAMP = "timestamp";
    String KUAIDI100_ROUT_SOURCE = "KUAIDI100";
    String XXL_JOB_ROUT_SOURCE = "XXL_JOB";
    String OK_MSG = "OK";
    String ERROR_MSG = "ERROR";
    String MSG_CHECK_ERROR = "数据验签失败！";
    String NULL_MSG = "参数不能为空！";

    /**
     * 顺丰推送运单路由信息topic
     */
    String C10_LOGISTICS_ROUT_PUSH = "c10_logistics_sf_push";

    /**
     * 顺丰推送运单费用信息topic
     */
    String C10_LOGISTICS_SF_FREIGHT_PUSH = "c10_logistics_sf_freight_push";

    /**
     * 商品订单生成时，推送物流订单请求topic
     */
    String C10_ORDER_CHILD = "c10_order_child";

    String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    String PROFILE_PRD = "prd";

    /**
     * 顺丰打印面单推送topic
     */
    String EBILL_ROUT_PUSH = "logistics_sf_ebill_push";

    String JD_FEE_MQ_TOPIC = "c10_logistics_update_jd_fee";

    String UPT_DELIVERY_ROUTE_TOPIC = "c10_update_delivery_route";

    /**
     * 京东推送路由信息
     */
    String JD_PUSH_ROUTE_MSG = "京东推送运单路由信息:[%s]";

    /**
     * MQ接收到推送运单路由消息体
     */
    String MQ_ALL_PUSH_ROUTE_BODY = "MQ接收到推送运单路由消息体";

    String SYSTEM_USER = "system";

    String ORG_EMPTY_ERROR_STR = "当前用户的供应商信息为空，请检查用户信息";
    /**
     * 商铺IDKey
     */
    String VENDOR = "VENDOR";

    String ERR_NO_SHOP_ID = "您没有绑定商户，请联系运营管理员绑定商户！";
    String UPDATE_TIME = "update_time";

}

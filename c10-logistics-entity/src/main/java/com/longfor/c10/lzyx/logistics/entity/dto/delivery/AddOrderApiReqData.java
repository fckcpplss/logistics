package com.longfor.c10.lzyx.logistics.entity.dto.delivery;

import com.longfor.c10.lzyx.logistics.entity.enums.DeliveryTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AddOrderApiReqData implements Serializable {

    /**
     * 商户物流账号配置表主键
     */
    private Long shopLogisticsId;
    /**
     * 必填
     * 订单号
     */
    private String orderId;

    /**
     * 配送单号，用于快递100发货直接获取使用
     */
    private String deliveryNo;
    /**
     * openId
     */
    private String openId;
    /**
     * 快递备注
     */
    private String customRemark;
    /**
     * 发货时间
     */
    private Integer expectTime;
    /**
     * 必填
     * 发件人
     */
    private Sender sender;
    /**
     * 必填
     * 收件人
     */
    private Receiver receiver;
    /**
     * 必填
     * 包裹信息
     */
    private Cargo cargo;
    /**
     * 必填
     * 商品信息
     */
    private Shop shop;
    /**
     * 必填
     * 保价信息
     */
    private Insured insured;
    /**
     * 期望取件开始时间
     */
    private Integer pickUpStartTime;

    /**
     * 期望取件结束时间
     */
    private Integer pickUpEndTime;
    /**
     * 必填
     * 所选类型
     */
    private DeliveryTypeEnum deliveryTypeEnum;

    /**
     * 商户配置物流类型
     */
    private String expressType;

    /**
     * 商户配置账号
     */
    private String account;

    @Data
    public static class Sender {
        /**
         * 必填
         * 发件人姓名，不超过64字节
         */
        private String name;
        /**
         * 发件人座机号码，若不填写则必须填写 mobile，不超过32字节
         */
        private String tel;
        /**
         * 发件人手机号码，若不填写则必须填写 tel，不超过32字节
         */
        private String mobile;
        /**
         * 发件人公司名称，不超过64字节
         */
        private String company;
        /**
         * 发件人邮编，不超过10字节
         */
        private String postCode;
        /**
         * 发件人国家，不超过64字节
         */
        private String country;
        /**
         * 必填
         * 发件人省份，比如："广东省"，不超过64字节
         */
        private String province;
        /**
         * 必填
         * 发件人市/地区，比如："广州市"，不超过64字节
         */
        private String city;
        /**
         * 必填
         * 发件人区/县，比如："海珠区"，不超过64字节
         */
        private String area;
        /**
         * 必填
         * 发件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节
         */
        private String address;
    }

    @Data
    public static class Receiver {
        /**
         * 必填
         * 收件人姓名，不超过64字节
         */
        private String name;
        /**
         * 收件人座机号码，若不填写则必须填写 mobile，不超过32字节
         */
        private String tel;
        /**
         * 收件人手机号码，若不填写则必须填写 tel，不超过32字节
         */
        private String mobile;
        /**
         * 收件人公司名，不超过64字节
         */
        private String company;
        /**
         * 收件人邮编，不超过10字节
         */
        private String postCode;
        /**
         * 收件人所在国家，不超过64字节
         */
        private String country;
        /**
         * 必填
         * 收件人省份，比如："广东省"，不超过64字节
         */
        private String province;
        /**
         * 必填
         * 收件人地区/市，比如："广州市"，不超过64字节
         */
        private String city;
        /**
         * 必填
         * 收件人区/县，比如："天河区"，不超过64字节
         */
        private String area;
        /**
         * 必填
         * 收件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节
         */
        private String address;
    }

    @Data
    public static class Cargo {
        /**
         * 必填
         * 包裹数量, 需要和detail_listsize保持一致
         */
        private Integer count;
        /**
         * 必填
         * 包裹总重量，单位是千克(kg)
         */
        private Integer weight;
        /**
         * 必填
         * 包裹长度，单位厘米(cm)
         */
        private Integer spaceX;
        /**
         * 必填
         * 包裹宽度，单位厘米(cm)
         */
        private Integer spaceY;
        /**
         * 必填
         * 包裹高度，单位厘米(cm)
         */
        private Integer spaceZ;
        /**
         * 必填
         * 包裹中商品详情列表
         */
        private List<Detail> detail_list;

        @Data
        public static class Detail {
            /**
             * 必填
             * 商品名，不超过128字节
             */
            private String name;
            /**
             * 必填
             * 商品数量
             */
            private Integer count;
        }
    }

    @Data
    public static class Shop {
        /**
         * 必填
         * 商家小程序的路径，建议为订单页面
         */
        private String wxaPath;
        /**
         * 必填
         * 商品缩略图 url
         */
        private String imgUrl;
        /**
         * 必填
         * 商品名称, 不超过128字节
         */
        private String goodsName;
        /**
         * 必填
         * 商品数量
         */
        private Integer goodsCount;
    }

    @Data
    public static class Insured {
        /**
         * 必填
         * 是否保价，0 表示不保价，1 表示保价
         */
        private Integer useInsured;
        /**
         * 必填
         * 保价金额，单位是分，比如: 10000 表示 100 元
         */
        private Integer insuredValue;
    }
}

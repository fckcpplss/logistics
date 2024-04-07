package com.longfor.c10.lzyx.logistics.entity.dto.mq;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BizBaseDataMessage<T> {
        /**
         * 数据类型
         */
        public enum DataTypeEnum {
                //航道
                COURSE,
                //运营组织
                OPER_ORG,
                //渠道
                CHANNEL,
                //商户信息
                MERCHANT,
                //商户地址
                MERCHANT_ADDR
                ;
        }

        /**
         * 操作类型
         */
        public enum OptTypeEnum {
                //更新
                UPD,
                //刪除
                DEL,
                //添加
                ADD;
        }

        /**
         * 操作类型
         */
        private OptTypeEnum optType;
        /**
         * 数据类型
         */
        private DataTypeEnum dataType;
        /**
         * 数据产生时间，时间戳
         */
        private Long prodTime = System.currentTimeMillis();
        /**
         * 业务数据
         */
        private List<T> data;
    }

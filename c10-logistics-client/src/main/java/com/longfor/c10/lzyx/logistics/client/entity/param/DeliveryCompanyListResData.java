package com.longfor.c10.lzyx.logistics.client.entity.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName : DeliveryCompanyResData
 * @Description : 查询物流公司出参
 * @Author : zhaoyl
 * @Date: 2021-05-08 13:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryCompanyListResData {
    /**
     * 首字母
     */
    private String initial;

    /**
     * 物流公司列表
     */
    private List<DeliveryCompanyResData> deliveryCompanyList;

}

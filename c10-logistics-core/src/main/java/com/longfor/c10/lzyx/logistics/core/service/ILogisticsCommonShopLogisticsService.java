package com.longfor.c10.lzyx.logistics.core.service;

import com.longfor.c10.lzyx.logistics.entity.dto.*;

import java.util.List;

/**
 * 商家物流配置接口类
 * @author zhaoyl
 * @date 2022/4/13 上午11:16
 * @since 1.0
 */
public interface ILogisticsCommonShopLogisticsService {

    /**
     * 获取供应商所有的物流配置
     *
     * @param shopId
     * @return
     */
    List<ShopLogisticsRep> getShopLogisticsRepList(ShopLogisticsListReq shopLogisticsListReq);

    /**
     * update单条物流配置
     *
     * @param shopLogisticsListUpdReq
     */
    void updateShopLogisticsReq(ShopLogisticsListUpdReq shopLogisticsListUpdReq);

    /**
     * 根据物流公司获取供应商的物流配置
     *
     * @param shopLogisticsListReq
     * @return
     */
    List<ShopLogisticsByCompanyRep> getShopLogisticsByCompanyRepList(ShopLogisticsListReq shopLogisticsListReq);

    /**
     * 获取供应商可选的物流公司
     *
     * @param shopId
     * @return
     */
    List<CompangConfigListResData> getCompanyConfigByShopId(CompanyConfigListReqData companyConfigListReqData);
}

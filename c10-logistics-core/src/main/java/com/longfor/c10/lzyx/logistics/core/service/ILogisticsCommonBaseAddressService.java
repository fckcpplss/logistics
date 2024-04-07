package com.longfor.c10.lzyx.logistics.core.service;

import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;

import java.util.List;

/**
 * 发用户址接口类
 * @author zhaoyl
 * @date 2022/2/21 上午9:49
 * @since 1.0
 */
public interface ILogisticsCommonBaseAddressService {
    /**
     * 新增发用户址
     */
    Response<Boolean> add(LogisticsAddressAddReqData req);

    /**
     * 修改发用户址
     */
    Response<Boolean> update(LogisticsAddressUpdateReq req);

    /**
     * 查询发用户址集合
     */
    PageResponse<List<LogisticsAddressInfo>> list(LogisticsAddressListReq req, PageInfo pageInfo);

    /**
     * 删除发用户址
     */
    Response<Boolean> delete(LogisticsAddressDelReq req);

    /**
     * 设置默认发用户址
     */
    Response<Boolean> isDefault(LogisticsAddressDefaultReq req);

    /**
     * 详情
     */
    Response<LogisticsAddressInfo> detail(LogisticsAddressDetailReqData req);

    /**
     * 默认地址
     */
    Response<LogisticsAddressInfo> defaultDetail(LogisticsCommonNoParamReqData req);

    /**
     * 查询发用户址集合
     */
    Response<List<LogisticsAddressInfo>> listByUser(LogisticsCommonNoParamReqData req);
}

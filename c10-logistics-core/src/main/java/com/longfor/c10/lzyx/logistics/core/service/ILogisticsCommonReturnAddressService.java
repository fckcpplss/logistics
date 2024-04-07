package com.longfor.c10.lzyx.logistics.core.service;

import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;

import java.util.List;

/**
 * 退货地址接口类
 * @author zhaoyl
 * @date 2022/2/21 上午9:49
 * @since 1.0
 */
public interface ILogisticsCommonReturnAddressService {
    /**
     * 新增发货地址
     */
    Response<Boolean> add(LogisticsReturnAddressAddReq req);

    /**
     * 修改发货地址
     */
    Response<Boolean> update(LogisticsReturnAddressUpdateReq req);

    /**
     * 查询发货地址集合
     */
    PageResponse<List<LogisticsReturnAddressInfo>> list(LogisticsReturnAddressListReq req,PageInfo pageInfo);

    /**
     * 删除发货地址
     */
    Response<Boolean> delete(LogisticsReturnAddressDelReq req);

    /**
     * 设置默认发货地址
     */
    Response<Boolean> isDefault(LogisticsReturnAddressDefaultReq req);

    /**
     * 获取供应商信息
     */
    Response<List<LogisticsReturnSprRes>> getSpr(LogisticsReturnSprReq req);

    Response<LogisticsReturnAddressResData> getReturnAddress(Request<LogisticsReturnAddressReqData> request);
}

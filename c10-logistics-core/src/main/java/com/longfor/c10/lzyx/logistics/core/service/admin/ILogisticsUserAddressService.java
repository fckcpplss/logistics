package com.longfor.c10.lzyx.logistics.core.service.admin;

import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;

import java.util.List;

/**
 * 用户地址接口类
 * @author zhaoyl
 * @date 2022/2/7 下午4:18
 * @since 1.0
 */
public interface ILogisticsUserAddressService {
    Response<LogisticsAddressAddResp> add(LogisticsAddressAddReqData req);

    Response<Void> update(LogisticsAddressUpdateReq req);

    PageResponse<List<LogisticsAddressInfo>> queryList(PageInfo pageInfo, LogisticsAddressListReq req);

    Response<Void> delete(LogisticsAddressDelReq req);
}

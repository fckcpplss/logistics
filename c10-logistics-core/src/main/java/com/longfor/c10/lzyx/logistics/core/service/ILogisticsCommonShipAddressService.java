package com.longfor.c10.lzyx.logistics.core.service;

import com.longfor.c10.lzyx.download.client.entity.BizExportParamEntity;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.FeeVO;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 发货地址接口类
 * @author zhaoyl
 * @date 2022/2/21 上午9:49
 * @since 1.0
 */
public interface ILogisticsCommonShipAddressService {
    /**
     * 新增发货地址
     */
    Response<Boolean> add(LogisticsShipAddressAddReq req);

    /**
     * 修改发货地址
     */
    Response<Boolean> update(LogisticsShipAddressUpdateReq req);

    /**
     * 查询发货地址集合
     */
    PageResponse<List<LogisticsShipAddressInfo>> list(LogisticsShipAddressListReq req,PageInfo pageInfo);

    /**
     * 删除发货地址
     */
    Response<Boolean> delete(LogisticsShipAddressDelReq req);

    /**
     * 设置默认发货地址
     */
    Response<Boolean> isDefault(LogisticsShipAddressDefaultReq req);

    /**
     * 获取供应商信息
     */
    Response<List<LogisticsShipSprRes>> getSpr(LogisticsShipSprReq req);
}

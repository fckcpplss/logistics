package com.longfor.c10.lzyx.logistics.client.api;

import com.longfor.c10.lzyx.logistics.client.entity.param.*;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 发货地址
 * @author zhaoyl
 * @date 2022/3/23 下午1:40
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsCommonShipAddressClient {
    /**
     * 新增发货地址
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/common/address/ship/add")
    Response<Boolean> add(Request<LogisticsShipAddressAddReq> request);

    /**
     * 修改发货地址
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/common/address/ship/update")
    Response<Boolean> update(@RequestBody @Valid Request<LogisticsShipAddressUpdateReq> request);

    /**
     * 查询发货地址集合
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/common/address/ship/list")
    PageResponse<List<LogisticsShipAddressInfo>> list(PageRequest<LogisticsShipAddressListReq> request);

    /**
     * 删除发货地址
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/common/address/ship/delete")
    Response<Boolean> delete(Request<LogisticsShipAddressDelReq> request);

    /**
     * 设置默认发货地址
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/common/address/ship/default")
    Response<Boolean> isDefault(Request<LogisticsShipAddressDefaultReq> request);

    /**
     * 获取供应商信息
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/common/address/ship/getSpr")
    Response<List<LogisticsShipSprRes>> getSpr(Request<LogisticsShipSprReq> request);
}

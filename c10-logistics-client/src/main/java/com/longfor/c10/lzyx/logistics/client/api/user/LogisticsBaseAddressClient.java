package com.longfor.c10.lzyx.logistics.client.api.user;

import com.longfor.c10.lzyx.logistics.client.entity.param.*;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户地址
 * @author zhaoyl
 * @date 2022/3/23 下午1:40
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsBaseAddressClient {
    /**
     * 新增用户地址
     */
    @PostMapping(path = "lzyx/logistics/common/address/base/add")
    Response<Boolean> add(Request<LogisticsAddressAddReqData> request);

    /**
     * 修改用户地址
     */
    @PostMapping(path = "lzyx/logistics/common/address/base/update")
    Response<Boolean> update(Request<LogisticsAddressUpdateReq> request);

    /**
     * 查询用户地址集合
     */
    @PostMapping(path = "lzyx/logistics/common/address/base/list")
    PageResponse<List<LogisticsAddressInfo>> list(PageRequest<LogisticsAddressListReq> request);

    /**
     * 查询用户地址集合
     */
    @PostMapping(path = "lzyx/logistics/common/address/base/list/user")
    Response<List<LogisticsAddressInfo>> listByUser(PageRequest<LogisticsCommonNoParamReqData> request);

    /**
     * 删除用户地址
     */
    @PostMapping(path = "lzyx/logistics/common/address/base/delete")
    Response<Boolean> delete(Request<LogisticsAddressDelReq> request);

    /**
     * 设置默认用户地址
     */
    @PostMapping(path = "lzyx/logistics/common/address/base/default")
    Response<Boolean> isDefault(Request<LogisticsAddressDefaultReq> request);

    /**
     * 地址详情
     */
    @PostMapping(path = "lzyx/logistics/common/address/base/detail")
    Response<LogisticsAddressInfo> detail(Request<LogisticsAddressDetailReqData> request);

    /**
     * 默认地址
     */
    @PostMapping(path = "lzyx/logistics/common/address/base/detail/default")
    Response<LogisticsAddressInfo> getDefault(Request<LogisticsCommonNoParamReqData> request);
}

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 退回地址
 * @author zhaoyl
 * @date 2022/3/23 下午1:40
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsCommonReturnAddressClient {
    /**
     * 新增退回地址
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/common/address/return/add")
    Response<Boolean> add(Request<LogisticsReturnAddressAddReq> request);

    /**
     * 修改退回地址
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/common/address/return/update")
    Response<Boolean> update(Request<LogisticsReturnAddressUpdateReq> request);

    /**
     * 查询退回地址集合
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/common/address/return/list")
    PageResponse<List<LogisticsReturnAddressInfo>> list(PageRequest<LogisticsReturnAddressListReq> request);

    /**
     * 删除退回地址
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/common/address/return/delete")
    Response<Boolean> delete(Request<LogisticsReturnAddressDelReq> request);

    /**
     * 设置默认退回地址
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/common/address/return/default")
    Response<Boolean> isDefault(Request<LogisticsReturnAddressDefaultReq> request);

    /**
     * 获取供应商信息
     * @param request
     * @return
     */
    @PostMapping(path = "lzyx/logistics/common/address/return/getSpr")
    Response<List<LogisticsReturnSprRes>> getSpr(Request<LogisticsReturnSprReq> request);

    /**
     * 描述: 获取退换货地址-第一条为退回地址
     */
    @PostMapping("lzyx/logistics/common/address/return/getReturnAddress")
    Response<LogisticsReturnAddressResData> getReturnAddress(Request<LogisticsReturnAddressReqData> request);
}

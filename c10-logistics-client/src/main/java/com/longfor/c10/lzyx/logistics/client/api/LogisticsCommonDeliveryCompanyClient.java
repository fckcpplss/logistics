package com.longfor.c10.lzyx.logistics.client.api;

import com.longfor.c10.lzyx.logistics.client.entity.param.*;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 配送地址
 * @author zhaoyl
 * @date 2022/3/23 下午1:40
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsCommonDeliveryCompanyClient {
    @PostMapping(path = "lzyx/logistics/common/delivery/company/add")
    Response<Void> add(@RequestBody Request<CompanyAddReq> request);

    @PostMapping(path = "lzyx/logistics/common/delivery/company/update")
    Response<Void> update(@RequestBody Request<CompanyUpdateReq> request);

    @PostMapping(path = "lzyx/logistics/common/delivery/company/delete")
    Response<Void> delete(@RequestBody Request<CompanyDeleteReq> request);

    @PostMapping(path = "lzyx/logistics/common/delivery/company/list")
    Response<List<CompanyListRes>> list(@RequestBody Request<CompanyListReq> request);

    @PostMapping(path = "lzyx/logistics/common/delivery/company/list/page")
    PageResponse<List<CompanyListRes>> listByPage(@RequestBody PageRequest<CompanyListReq> request);
}

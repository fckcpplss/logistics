package com.longfor.c10.lzyx.logistics.client.api.admin;

import com.longfor.c10.lzyx.logistics.client.entity.param.admin.FeeListReq;
import com.longfor.c10.lzyx.logistics.client.entity.vo.admin.FeeVO;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * 物流运费client
 * @author zhaoyl
 * @date 2022/2/7 下午3:10
 * @since 1.0
 */
@FeignClient(value = "c10-logistics-core")
public interface LogisticsFeeClient {
    @PostMapping(path = "lzyx/logistics/common/fee/list")
    PageResponse<List<FeeVO>> list(@Valid @RequestBody PageRequest<FeeListReq> request);

    @PostMapping(path = "lzyx/logistics/common/fee/direct/export")
    Response<String> listExport(@Valid @RequestBody Request<FeeListReq> request);
}

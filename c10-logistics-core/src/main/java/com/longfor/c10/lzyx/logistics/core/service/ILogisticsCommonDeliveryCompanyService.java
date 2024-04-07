package com.longfor.c10.lzyx.logistics.core.service;

import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 配送公司接口类
 * @author zhaoyl
 * @date 2022/4/20 下午1:47
 * @since 1.0
 */
public interface ILogisticsCommonDeliveryCompanyService {
    @PostMapping(path = "add")
    @ApiOperation(value = "新增快递公司", notes = "新增快递公司")
    Response<Void> add(CompanyAddReq req);

    @PostMapping(path = "update")
    @ApiOperation(value = "修改快递公司", notes = "修改快递公司")
    Response<Void> update(CompanyUpdateReq req);

    @PostMapping(path = "delete")
    @ApiOperation(value = "删除快递公司", notes = "删除快递公司")
    Response<Void> delete(CompanyDeleteReq req);

    @PostMapping(path = "list")
    @ApiOperation(value = "获取快递公司", notes = "获取快递公司")
    Response<List<CompanyListRes>> list(CompanyListReq req);

    @PostMapping(path = "listByPage")
    @ApiOperation(value = "分页获取快递公司", notes = "分页获取快递公司")
    PageResponse<List<CompanyListRes>> listByPage(CompanyListReq req, PageInfo pageInfo);
}

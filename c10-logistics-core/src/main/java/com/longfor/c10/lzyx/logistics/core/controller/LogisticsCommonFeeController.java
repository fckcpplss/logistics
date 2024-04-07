package com.longfor.c10.lzyx.logistics.core.controller;

import com.longfor.c10.lzyx.download.client.entity.BizExportParamEntity;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonFeeService;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c10.lzyx.logistics.entity.dto.FeeListReq;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.FeeVO;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 物流运费管理
 * @author zhaoyl
 * @date 2022/2/21 上午9:30
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/common/fee")
public class LogisticsCommonFeeController {

    @Autowired
    private ILogisticsCommonFeeService logisticsCommonFeeService;
    /**
     * 查询费用列表
     */
    @PostMapping(path = "/list")
    public PageResponse<List<FeeVO>> getShopLogisticsFeeList(@RequestBody PageRequest<FeeListReq> request) {
        PageInfo pageInfo = Optional.ofNullable(request)
                .map(PageRequest::getPageInfo)
                .orElseThrow(() -> new BusinessException("分页参数不能为空"));
        FeeListReq req = Optional.ofNullable(request)
                .map(PageRequest::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsCommonFeeService.getLogisticsFeeList(pageInfo,req);
    }

    /**
     * 物流公司快递编码下载
     * @return
     */
    @PostMapping("/direct/export")
    @ApiOperation(value = "物流公司快递编码", notes = "物流公司快递编码")
    public Response<String> feeExport(@RequestBody Request<FeeListReq> request) {
        FeeListReq req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsCommonFeeService.feeExport(req);
    }

    /**
     * 费用列表导出-接下载中心
     */
    @RequestMapping(path = "/export")
    public Response<Boolean> export(@RequestBody Request<BizExportParamEntity> request){
        BizExportParamEntity req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        return logisticsCommonFeeService.export(req);
    }
}

package com.longfor.c10.lzyx.logistics.core.service;

import com.longfor.c10.lzyx.download.client.entity.BizExportParamEntity;
import com.longfor.c10.lzyx.logistics.entity.dto.FeeListReq;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.FeeVO;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;

import java.util.List;

/**
 * 物流运费接口类
 * @author zhaoyl
 * @date 2022/2/21 上午9:49
 * @since 1.0
 */
public interface ILogisticsCommonFeeService {
    /**
     * 查询费用列表
     * @param pageInfo
     * @param logisticsFeeListReq
     * @return
     */
    PageResponse<List<FeeVO>> getLogisticsFeeList(PageInfo pageInfo, FeeListReq req);

    /**
     * 费用导出-下载中心
     * @param req
     * @return
     */
    Response<Boolean> export(BizExportParamEntity req);

    /**
     * 费用导出
     * @param req
     * @return
     */
    Response<String> feeExport(FeeListReq req);
}

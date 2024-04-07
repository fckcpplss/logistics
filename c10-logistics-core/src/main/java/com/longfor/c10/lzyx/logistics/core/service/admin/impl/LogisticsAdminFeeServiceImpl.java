package com.longfor.c10.lzyx.logistics.core.service.admin.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.logistics.core.util.DesensitizedUtils;
import com.longfor.c10.lzyx.logistics.core.util.LogisticsUtil;
import com.longfor.c10.lzyx.logistics.core.service.admin.ILogisticsAdminFeeService;
import com.longfor.c10.lzyx.logistics.dao.mapper.LogisticsFeeMapper;
import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.AmUserInfo;
import com.longfor.c10.lzyx.logistics.entity.enums.DeliveryLogisticsStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.SellTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c10.lzyx.logistics.entity.dto.FeeListReq;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.FeeVO;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 物流运费业务接口实现类
 * @author zhaoyl
 * @date 2022/2/21 上午9:49
 * @since 1.0
 */
@Service
public class LogisticsAdminFeeServiceImpl implements ILogisticsAdminFeeService {
    @Autowired
    private LogisticsFeeMapper logisticsFeeMapper;

    @Override
    public PageResponse<List<FeeVO>> getLogisticsFeeList(PageInfo pageInfo, FeeListReq logisticsFeeListReq) {
        //参数校验
        if(!checkParams(logisticsFeeListReq)){
            return PageResponse.page(Collections.EMPTY_LIST,0L);
        }
        //参数处理
        Optional.ofNullable(logisticsFeeListReq.getSellType()).map(SellTypeEnum::fromCode).map(SellTypeEnum::getChannels).map(Arrays::asList).ifPresent(list -> {
            logisticsFeeListReq.setBizChannelCodes(list);
        });
        IPage<FeeVO> logisticsFeeList = logisticsFeeMapper.getLogisticsFeeList(new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize()), logisticsFeeListReq);
        if(Objects.isNull(logisticsFeeList) || CollectionUtils.isEmpty(logisticsFeeList.getRecords())){
            return PageResponse.page(logisticsFeeList.getRecords(),0L);
        }
        //数据处理
        handelFeeDataList(logisticsFeeList.getRecords());
        return PageResponse.page(logisticsFeeList.getRecords(),logisticsFeeList.getTotal());
    }

    //物流费用列表数据处理
    private void handelFeeDataList(List<FeeVO> records) {
        records.stream().forEach(item -> {
            item.setShipAddress(DesensitizedUtils.maskAddress(item.getShipAddress()));
            item.setDeliveryAddress(DesensitizedUtils.maskAddress(item.getDeliveryAddress()));
            item.setLogisticsAccount(LogisticsUtil.getLogisticsTypeName(item.getCompanyCode(),item.getLogisticsType(),item.getShopLogisticsId()));
            item.setDiscountFee(Optional.ofNullable(item.getDiscountFee()).filter(l -> !l.equals("0.00")).orElse("-"));
            item.setStandardFee(Optional.ofNullable(item.getStandardFee()).filter(l -> !l.equals("0.00")).orElse("-"));
            item.setLogisticsStatusShow(Optional.ofNullable(item.getLogisticsStatus()).map(x -> DeliveryLogisticsStatusEnum.fromCode(x)).map(DeliveryLogisticsStatusEnum::getDesc).orElse(null));
            item.setFeeBearerShow(Objects.nonNull(item.getFeeBearer()) ? item.getFeeBearer() == 1 ? "平台" : item.getFeeBearer()  == 2 ? "商家" : null : null);
            item.setSettlementTypeShow(StringUtils.isNoneBlank(item.getSettlementType()) ? "1".equals(item.getSettlementType()) ? "现金" : "2".equals(item.getSettlementType()) ? "月结" : null : null);
            item.setPaymentTypeShow(StringUtils.isNoneBlank(item.getPaymentType()) ? "1".equals(item.getPaymentType()) ? "寄付" : "2".equals(item.getPaymentType()) ? "到付" : null : null);
            //处理销售模式
            Optional.ofNullable(SellTypeEnum.fromChannelCode(item.getBizChannelCode())).ifPresent(e -> {
                item.setSellType(e.getCode());
                item.setSellTypeShow(e.getDesc());
            });
        });
    }

    //校验am登陆信息
    private boolean checkParams(BaseReqData req) {
        AmUserInfo amUserInfo = Optional.ofNullable(req).map(BaseReqData::getAmUserInfo).orElseThrow(() -> new BusinessException("用户登陆信息为空"));
        if(CollectionUtils.isEmpty(amUserInfo.getOrgIds())){
            throw  new BusinessException("当前用户无有效运营组织信息");
        }
        if(Objects.nonNull(req.getOrgId()) && amUserInfo.getOrgIds().stream().noneMatch(orgId -> orgId.equals(req.getOrgId()))){
            return false;
        }
        req.setOrgIds(amUserInfo.getOrgIds());
        return true;
    }

    @Override
    public Response<List<FeeVO>> exportList(FeeListReq req) {
        checkParams(req);
        //参数处理
        Optional.ofNullable(req.getSellType()).map(SellTypeEnum::fromCode).map(SellTypeEnum::getChannels).map(Arrays::asList).ifPresent(list -> {
            req.setBizChannelCodes(list);
        });
        //查询运费列表
        List<FeeVO> dataList = logisticsFeeMapper.getLogisticsFeeList(req);
        if(CollectionUtils.isEmpty(dataList)){
            return null;
        }
        //数据处理
        handelFeeDataList(dataList);
        return Response.ok(dataList);
    }
}

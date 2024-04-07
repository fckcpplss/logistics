package com.longfor.c10.lzyx.logistics.core.service.admin.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.logistics.core.service.admin.ILogisticsAdminMonitorService;
import com.longfor.c10.lzyx.logistics.dao.mapper.LogisticsMonitorMapper;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderGoodsService;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.enums.*;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorDetailReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorUpdateReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorAttachementVO;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorListGodsVO;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorListResData;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorListResVO;
import com.longfor.c2.starter.common.util.StringUtil;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 物流监控业务实现类
 * @author zhaoyl
 * @date 2022/2/16 下午7:36
 * @since 1.0
 */
@Slf4j
@Service
public class LogisticsAdminMonitorServiceImpl implements ILogisticsAdminMonitorService {
    @Autowired
    private LogisticsMonitorMapper logisticsMonitorMapper;

    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;

    @Override
    public PageResponse<List<LogisticsMonitorListResVO>> deliveryList(IPage<LogisticsMonitorListResData> page, LogisticsMonitorListReqData req) {
        IPage<LogisticsMonitorListResData> pageResult = logisticsMonitorMapper.queryDeliveryList(page, req);
        if(Objects.isNull(pageResult) || CollectionUtils.isEmpty(pageResult.getRecords())){
            return PageResponse.page(Collections.emptyList(),0l);
        }
        //取到分页结果中所有的商品id
        List<Long> logisticsGoodsIds = pageResult.getRecords().stream()
                .map(LogisticsMonitorListResData::getLogisticsGoodsIds)
                .filter(x -> StringUtils.isNoneBlank(x))
                .flatMap(x -> Arrays.stream(x.split(",")))
                .map(Long::valueOf)
                .distinct()
                .collect(Collectors.toList());
        //商品id批量查询商品信息并转商品id和entity对应map
        Map<Long, LogisticsOrderGoods> logisticsGoodsIdAndEntityMap = ListUtils.emptyIfNull(logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                        .in(LogisticsOrderGoods::getId, logisticsGoodsIds)
                        .eq(LogisticsOrderGoods::getDeleteStatus, DeleteStatusEnum.NO.getCode())))
                .stream()
                .collect(Collectors.toMap(LogisticsOrderGoods::getId, Function.identity(), (a, b) -> a));
        return PageResponse.page(ListUtils.emptyIfNull(pageResult.getRecords()).stream().map(res -> {
            LogisticsMonitorListResVO vo = new LogisticsMonitorListResVO();
            BeanUtils.copyProperties(res,vo);
            // TODO: 2022/3/2 快递公司名称处理
            vo.setCompanyCodeShow(res.getCompanyCode());
            vo.setOrderStatusShow(Optional.ofNullable(StrUtil.emptyToDefault(res.getOrderStatus(),null)).map(Integer::parseInt).map(code -> GoodsOrderStatusEnum.fromCode(code)).map(GoodsOrderStatusEnum::getDesc).orElse(null));
            vo.setIfCancelShow(Objects.nonNull(res.getIfCancel()) && res.getIfCancel() == 1 ? "是" : "否");
            vo.setLogisticsStatusShow(Optional.ofNullable(res.getLogisticsStatus())
                    .map(String::valueOf)
                    .map(Byte::valueOf)
                    .map(DeliveryLogisticsStatusEnum::fromCode)
                    .map(DeliveryLogisticsStatusEnum::getDesc)
                    .orElse(null));
            vo.setFeeTypeShow(Optional.ofNullable(res.getFeeType()).map(LogisticsTypeEnum::fromCode).map(LogisticsTypeEnum::getDesc).orElse(null));
            vo.setLogisticsTypeShow(handelLogisticsTypeShow(res.getCompanyCode(), res.getFeeType(), res.getShopLogisticsId()));
            Optional.ofNullable(StrUtil.emptyToDefault(res.getLogisticsGoodsIds(),null)).ifPresent(goodsIdsStr -> {
                //设置商品信息
                vo.setGoodsResList(Arrays.stream(goodsIdsStr.split(","))
                        .map(Long::parseLong)
                        .map(logisticsGoodsIdAndEntityMap::get)
                        .filter(goodsInfo -> Objects.nonNull(goodsInfo))
                        .map(goodsInfo -> {
                            LogisticsMonitorListGodsVO logisticsMonitorListGodsVo = new LogisticsMonitorListGodsVO();
                            BeanUtils.copyProperties(goodsInfo,logisticsMonitorListGodsVo);
                            logisticsMonitorListGodsVo.setBusinessTypeShow(Optional.ofNullable(goodsInfo.getBusinessType()).map(BusinessTypeEnum::fromCode).map(BusinessTypeEnum::getDesc).orElse(null));
                            return logisticsMonitorListGodsVo;
                        })
                        .collect(Collectors.toList()));
            });
            Optional.ofNullable(StrUtil.emptyToDefault(res.getAttachment1(),"[]")).ifPresent(attachmentStr -> {
                vo.setAttachment1s(JSONArray.parseArray(attachmentStr, LogisticsMonitorAttachementVO.class));
            });
            //手机号码脱敏
            Optional.ofNullable(StrUtil.emptyToDefault(res.getReceiptPhone(),null)).ifPresent(phone -> {
                vo.setReceiptPhone(String.valueOf(PhoneUtil.hideBetween(phone)));
            });
            return vo;
        }).collect(Collectors.toList()),pageResult.getTotal());
    }

    @Override
    public Response<LogisticsMonitorListResVO> deliveryDetail(LogisticsMonitorDetailReqData logisticsMonitorDetailReqData) {
        LogisticsMonitorListReqData logisticsMonitorListReqData = new LogisticsMonitorListReqData();
        logisticsMonitorListReqData.setChildOrderId(logisticsMonitorDetailReqData.getChildOrderId());
        logisticsMonitorListReqData.setDeliveryNo(logisticsMonitorDetailReqData.getDeliveryNo());
        PageResponse<List<LogisticsMonitorListResVO>> pageResponse = deliveryList(new Page<>(1, 1), logisticsMonitorListReqData);
        return PageResponse.ok(pageResponse.getData().stream().findFirst().orElseThrow(null));
    }

    @Override
    public Response<Boolean> deliveryUpdate(LogisticsMonitorUpdateReqData logisticsMonitorUpdateReqData) {
        if(StringUtil.isBlank(logisticsMonitorUpdateReqData.getRemark()) && CollectionUtils.isEmpty(logisticsMonitorUpdateReqData.getAttachment1s())){
            return Response.fail("参数不能为空");
        }
        LogisticsDelivery logisticsDelivery = new LogisticsDelivery();
        logisticsDelivery.setId(Long.parseLong(logisticsMonitorUpdateReqData.getLogisticsDeliveryId()));
        Optional.ofNullable(StrUtil.emptyToDefault(logisticsMonitorUpdateReqData.getRemark(),null)).ifPresent(remark -> {
            logisticsDelivery.setRemark(remark);
        });
        if(!CollectionUtils.isEmpty(logisticsMonitorUpdateReqData.getAttachment1s())){
            String attachments = JSON.toJSONString(logisticsMonitorUpdateReqData.getAttachment1s());
            if(attachments.length() > 1000){
                return Response.fail("附件数量过多");
            }
            logisticsDelivery.setAttachment1(attachments);
        }
        logisticsDelivery.setUpdateTime(DateUtil.date());
        logisticsDelivery.setUpdateAccount("system");
        logisticsDelivery.setUpdateName("system");
        logisticsDeliveryService.updateById(logisticsDelivery);
        return Response.ok(true);
    }

    private String handelLogisticsTypeShow(String companyCode, Integer feeType, String shopLogisticsId) {
        if(StringUtils.isBlank(companyCode) || Objects.isNull(feeType)){
            return "其他";
        }
        if("jd".equals(companyCode) && feeType == 1){
            return "平台京东";
        }else if("shunfeng".equals(companyCode) && feeType == 1){
            return "平台顺丰";
        }else if("shunfeng".equals(companyCode) && feeType == 2 && StringUtils.isNoneBlank(shopLogisticsId)){
            return "商家顺丰";
        }else{
            return "其他";
        }
    }
}

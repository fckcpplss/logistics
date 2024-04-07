package com.longfor.c10.lzyx.logistics.core.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.logistics.core.mq.producer.LogisticsStatusChangeProducer;
import com.longfor.c10.lzyx.logistics.core.mq.producer.LogisticsVerifyStatusChangeProducer;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonVerifyOrderService;
import com.longfor.c10.lzyx.logistics.core.util.CommonUtils;
import com.longfor.c10.lzyx.logistics.core.util.LogisticsNoUtil;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsVerifyOrderGoodsService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsVerifyOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.OrderStatusChangePushDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.OrderVerifyStatusChangePushDTO;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsVerifyOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsVerifyOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.enums.*;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.common.domain.IResultCode;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 抽象自提/核销公共接口实现类
 * @author zhaoyl
 * @date 2022/4/13 上午11:17
 * @since 1.0
 */
@Service
public abstract class AbstractCommonVerifyOrderService implements ILogisticsCommonVerifyOrderService {
    @Autowired
    private ILogisticsVerifyOrderService logisticsVerifyOrderService;

    @Autowired
    private ILogisticsVerifyOrderGoodsService logisticsVerifyOrderGoodsService;

    @Autowired
    private LogisticsNoUtil logisticsNoUtil;

    @Autowired
    private LogisticsStatusChangeProducer logisticsStatusChangeProducer;

    /**
     * 核销检验
     * @param req 核销请求参数
     * @param verifyListByOrderNos 核销订单详情
     * @param existOrderNoAndSkuIdMap 核销订单商品映射
     * @return
     */
    public abstract void verifyDataCheck(LogisticsVerifyOrderVerifyReqData req, List<LogisticsVerifyOrderDetailResData> verifyListByOrderNos, Map<String, List<LogisticsVerifyOrderGoodsDTO>> existOrderNoAndSkuIdMap);


    @Override
    public PageResponse<List<LogisticsVerifyOrderListResData>> list(LogisticsVerifyOrderListReqData req, PageInfo pageInfo) {
        req.setVerifyStatuss(Arrays.stream(LogisticsVerifyVerifyStatusEnum.values()).filter(x -> !x.getCode().equals(LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode())).map(LogisticsVerifyVerifyStatusEnum::getCode).collect(Collectors.toList()));
        req.setRefundStatuss(Arrays.asList(LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode(),LogisticsVerifyRefundStatusEnum.REFUND_PART.getCode()));
        LambdaQueryWrapper<LogisticsVerifyOrderGoods> lambdaOrderGoodsQueryWrapper = buildLogisticsVerifyOrderGoodsListQueryWrapper(req);
        lambdaOrderGoodsQueryWrapper.groupBy(LogisticsVerifyOrderGoods::getChildOrderId);
        List<LogisticsVerifyOrderGoods> orderGoodsList = logisticsVerifyOrderGoodsService.list(lambdaOrderGoodsQueryWrapper);
        if(CollectionUtils.isEmpty(orderGoodsList)){
            return  PageResponse.page(Collections.EMPTY_LIST,0L);
        }
        List<String> childOrderIds = orderGoodsList.stream().map(LogisticsVerifyOrderGoods::getChildOrderId).distinct().collect(Collectors.toList());
        req.setOrderNos(childOrderIds);
        LambdaQueryWrapper<LogisticsVerifyOrder> lambdaQueryWrapper = buildLogisticsVerifyOrderListQueryWrapper(req);
        long total = logisticsVerifyOrderService.count(lambdaQueryWrapper);
        if(total == 0){
            return  PageResponse.page(Collections.EMPTY_LIST,0L);
        }
        Page<LogisticsVerifyOrder> page = logisticsVerifyOrderService.page(new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize()),lambdaQueryWrapper);
        List<LogisticsVerifyOrderListResData> logisticsVerifyOrderListResData = handelVerirfyOrderListResData(page.getRecords());
        logisticsVerifyOrderListResData.stream().forEach(item -> {
            item.setGoodsList(item.getGoodsList().stream().filter(x -> x.getVerifyFlag() == 1).collect(Collectors.toList()));
        });
        return PageResponse.page(logisticsVerifyOrderListResData,total);
    }

    @Override
    public PageResponse<List<LogisticsVerifyOrderRecordsListResData>> recordList(LogisticsVerifyOrderListReqData req, PageInfo pageInfo) {
        req.setVerifyStatus(LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode());
        req.setRefundStatus(LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode());
        LambdaQueryWrapper<LogisticsVerifyOrderGoods> lambdaQueryWrapper = buildLogisticsVerifyOrderGoodsListQueryWrapper(req);
        long total = logisticsVerifyOrderGoodsService.count(lambdaQueryWrapper);
        if(total == 0){
            return  PageResponse.page(Collections.EMPTY_LIST,0L);
        }
        Page<LogisticsVerifyOrderGoods> page = logisticsVerifyOrderGoodsService.page(new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize()),lambdaQueryWrapper);
        return PageResponse.page(handelVerifyOrderRecordsListResData(page.getRecords()),total);
    }

    /**
     * 处理自提/核销订单列表
     */
    private List<LogisticsVerifyOrderListResData> handelVerirfyOrderListResData(List<LogisticsVerifyOrder> dataList){
        List<String> childOrderIds = ListUtils.emptyIfNull(dataList).stream().map(LogisticsVerifyOrder::getChildOrderId).distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(childOrderIds)){
            return Collections.emptyList();
        }
        Map<String, List<LogisticsVerifyOrderGoodsDTO>> childOrderIdAndGoodsDTOMap = ListUtils.emptyIfNull(logisticsVerifyOrderGoodsService.list(Wrappers.<LogisticsVerifyOrderGoods>lambdaQuery()
                .in(LogisticsVerifyOrderGoods::getChildOrderId, childOrderIds)
                .eq(LogisticsVerifyOrderGoods::getIsDelete, DeleteStatusEnum.NO.getCode())))
                .stream().collect(Collectors.groupingBy(LogisticsVerifyOrderGoods::getChildOrderId, Collectors.collectingAndThen(Collectors.toList(), list -> {
            return ListUtils.emptyIfNull(list).stream().map(goodsInfo -> {
                LogisticsVerifyOrderGoodsDTO logisticsVerifyOrderGoodsDTO = new LogisticsVerifyOrderGoodsDTO();
                BeanUtils.copyProperties(goodsInfo, logisticsVerifyOrderGoodsDTO);
                logisticsVerifyOrderGoodsDTO.setVerifyStatusShow(Optional.ofNullable(LogisticsVerifyVerifyStatusEnum.fromCode(goodsInfo.getVerifyStatus())).map(LogisticsVerifyVerifyStatusEnum::getDesc).orElse("-"));
                logisticsVerifyOrderGoodsDTO.setRefundStatusShow(Optional.ofNullable(LogisticsVerifyRefundStatusEnum.fromCode(goodsInfo.getRefundStatus())).map(LogisticsVerifyRefundStatusEnum::getDesc).orElse("-"));
                logisticsVerifyOrderGoodsDTO.setVerifyFlag((!goodsInfo.getRefundStatus().equals(LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode()) || goodsInfo.getVerifyStatus().equals(LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode())) ? 0 : 1);
                return logisticsVerifyOrderGoodsDTO;
            }).collect(Collectors.toList());
        })));
        return ListUtils.emptyIfNull(dataList)
                .stream()
                .map(x -> {
                    LogisticsVerifyOrderListResData logisticsVerifyOrderListResData = new LogisticsVerifyOrderListResData();
                    BeanUtils.copyProperties(x, logisticsVerifyOrderListResData);
                    logisticsVerifyOrderListResData.setId(String.valueOf(x.getId()));
                    logisticsVerifyOrderListResData.setLmNickname(DesensitizedUtil.desensitized(x.getLmNickname(), DesensitizedUtil.DesensitizedType.CHINESE_NAME));
                    logisticsVerifyOrderListResData.setVerifyStatusShow(Optional.ofNullable(LogisticsVerifyVerifyStatusEnum.fromCode(x.getVerifyStatus())).map(LogisticsVerifyVerifyStatusEnum::getDesc).orElse("-"));
                    logisticsVerifyOrderListResData.setRefundStatusShow(Optional.ofNullable(LogisticsVerifyRefundStatusEnum.fromCode(x.getRefundStatus())).map(LogisticsVerifyRefundStatusEnum::getDesc).orElse("-"));
                    Optional.ofNullable(childOrderIdAndGoodsDTOMap.get(x.getChildOrderId())).ifPresent(goodsList -> {
                        logisticsVerifyOrderListResData.setGoodsList(goodsList);
                    });
                    return logisticsVerifyOrderListResData;
                }).collect(Collectors.toList());
    }
    /**
     * 处理自提/核销订单核销记录列表
     */
    private List<LogisticsVerifyOrderRecordsListResData> handelVerifyOrderRecordsListResData(List<LogisticsVerifyOrderGoods> dataList){
        return ListUtils.emptyIfNull(dataList)
                .stream()
                .map(x -> {
                    LogisticsVerifyOrderRecordsListResData logisticsVerifyOrderRecordsListResData = new LogisticsVerifyOrderRecordsListResData();
                    BeanUtils.copyProperties(x, logisticsVerifyOrderRecordsListResData);
                    logisticsVerifyOrderRecordsListResData.setId(String.valueOf(x.getId()));
                    logisticsVerifyOrderRecordsListResData.setVerifyUserAccount(StrUtil.concat(true,x.getVerifyUserAccount(),Optional.ofNullable(x.getVerifyUserName()).map(y -> new StringBuilder("|").append(y).toString()).orElse("")));
                    logisticsVerifyOrderRecordsListResData.setLmNickname(DesensitizedUtil.desensitized(x.getLmNickname(), DesensitizedUtil.DesensitizedType.CHINESE_NAME));
                    logisticsVerifyOrderRecordsListResData.setVerifyStatusShow(Optional.ofNullable(LogisticsVerifyVerifyStatusEnum.fromCode(x.getVerifyStatus())).map(LogisticsVerifyVerifyStatusEnum::getDesc).orElse("-"));
                    logisticsVerifyOrderRecordsListResData.setRefundStatusShow(Optional.ofNullable(LogisticsVerifyRefundStatusEnum.fromCode(x.getRefundStatus())).map(LogisticsVerifyRefundStatusEnum::getDesc).orElse("-"));
                    return logisticsVerifyOrderRecordsListResData;
                }).collect(Collectors.toList());
    }
    private LambdaQueryWrapper<LogisticsVerifyOrder> buildLogisticsVerifyOrderListQueryWrapper(LogisticsVerifyOrderListReqData req) {
        LambdaQueryWrapper<LogisticsVerifyOrder> lambdaQueryWrapper = new LambdaQueryWrapper<LogisticsVerifyOrder>();
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getOrgId()),LogisticsVerifyOrder::getOrgId,req.getOrgId());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getVerifyNo()),LogisticsVerifyOrder::getVerifyNo,req.getVerifyNo());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getVerifyCode()),LogisticsVerifyOrder::getPickupCode,req.getVerifyCode());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getPickupAddressId()),LogisticsVerifyOrder::getPickupAddressId,req.getPickupAddressId());
        lambdaQueryWrapper.like(StringUtils.isNotBlank(req.getOrderNo()),LogisticsVerifyOrder::getChildOrderId,req.getOrderNo());
        lambdaQueryWrapper.in(!CollectionUtils.isEmpty(req.getOrderNos()),LogisticsVerifyOrder::getChildOrderId,req.getOrderNos());
        lambdaQueryWrapper.ge(StringUtils.isNotBlank(req.getStartOrderCreateTime()),LogisticsVerifyOrder::getOrderCreateTime, CommonUtils.checkDateShortToLong(req.getStartOrderCreateTime(),true));
        lambdaQueryWrapper.le(StringUtils.isNotBlank(req.getEndOrderCreateTime()),LogisticsVerifyOrder::getOrderCreateTime,CommonUtils.checkDateShortToLong(req.getEndOrderCreateTime(),false));
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getVerifyUserAccount()),LogisticsVerifyOrder::getVerifyUserAccount,req.getVerifyUserAccount());
        lambdaQueryWrapper.eq(Objects.nonNull(req.getVerifyType()),LogisticsVerifyOrder::getVerifyType,req.getVerifyType());
        lambdaQueryWrapper.in(!CollectionUtils.isEmpty(req.getVerifyTypes()),LogisticsVerifyOrder::getVerifyType,req.getVerifyTypes());
        lambdaQueryWrapper.eq(Objects.nonNull(req.getVerifyStatus()),LogisticsVerifyOrder::getVerifyStatus,req.getVerifyStatus());
        lambdaQueryWrapper.in(!CollectionUtils.isEmpty(req.getVerifyStatuss()),LogisticsVerifyOrder::getVerifyStatus,req.getVerifyStatuss());
        lambdaQueryWrapper.eq(Objects.nonNull(req.getRefundStatus()),LogisticsVerifyOrder::getRefundStatus,req.getRefundStatus());
        lambdaQueryWrapper.in(!CollectionUtils.isEmpty(req.getRefundStatuss()),LogisticsVerifyOrder::getRefundStatus,req.getRefundStatuss());
        lambdaQueryWrapper.ge(StringUtils.isNotBlank(req.getStartVerifyTime()),LogisticsVerifyOrder::getVerifyTime,CommonUtils.checkDateShortToLong(req.getStartVerifyTime(),true));
        lambdaQueryWrapper.le(StringUtils.isNotBlank(req.getEndVerifyTime()),LogisticsVerifyOrder::getVerifyTime,CommonUtils.checkDateShortToLong(req.getEndVerifyTime(),false));
        lambdaQueryWrapper.like(StringUtils.isNotBlank(req.getUserPhone()) && !StrUtil.containsOnly(req.getUserPhone(), '*'),LogisticsVerifyOrder::getLmPhone,req.getUserPhone());
        Optional.ofNullable(req.getAmUserInfo()).ifPresent(userInfo -> {
            lambdaQueryWrapper.in(!CollectionUtils.isEmpty(userInfo.getOrgIds()),LogisticsVerifyOrder::getOrgId,userInfo.getOrgIds());
        });
        lambdaQueryWrapper.eq(LogisticsVerifyOrder::getIsDelete, DeleteStatusEnum.NO.getCode());
        lambdaQueryWrapper.orderByDesc(LogisticsVerifyOrder::getCreateTime);
        return lambdaQueryWrapper;
    }
    private LambdaQueryWrapper<LogisticsVerifyOrderGoods> buildLogisticsVerifyOrderGoodsListQueryWrapper(LogisticsVerifyOrderListReqData req) {
        LambdaQueryWrapper<LogisticsVerifyOrderGoods> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getOrgId()),LogisticsVerifyOrderGoods::getOrgId,req.getOrgId());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getVerifyNo()),LogisticsVerifyOrderGoods::getVerifyNo,req.getVerifyNo());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getVerifyCode()),LogisticsVerifyOrderGoods::getPickupCode,req.getVerifyCode());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getPickupAddressId()),LogisticsVerifyOrderGoods::getPickupAddressId,req.getPickupAddressId());
        lambdaQueryWrapper.like(StringUtils.isNotBlank(req.getOrderNo()),LogisticsVerifyOrderGoods::getChildOrderId,req.getOrderNo());
        lambdaQueryWrapper.in(!CollectionUtils.isEmpty(req.getOrderNos()),LogisticsVerifyOrderGoods::getChildOrderId,req.getOrderNos());
        lambdaQueryWrapper.ge(StringUtils.isNotBlank(req.getStartOrderCreateTime()),LogisticsVerifyOrderGoods::getOrderCreateTime, CommonUtils.checkDateShortToLong(req.getStartOrderCreateTime(),true));
        lambdaQueryWrapper.le(StringUtils.isNotBlank(req.getEndOrderCreateTime()),LogisticsVerifyOrderGoods::getOrderCreateTime,CommonUtils.checkDateShortToLong(req.getEndOrderCreateTime(),false));
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getSkuId()),LogisticsVerifyOrderGoods::getGoodsId,req.getSkuId());
        lambdaQueryWrapper.like(StringUtils.isNotBlank(req.getGoodsName()),LogisticsVerifyOrderGoods::getGoodsName,req.getGoodsName());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getVerifyUserAccount()),LogisticsVerifyOrderGoods::getVerifyUserAccount,req.getVerifyUserAccount());
        lambdaQueryWrapper.eq(Objects.nonNull(req.getVerifyType()),LogisticsVerifyOrderGoods::getVerifyType,req.getVerifyType());
        lambdaQueryWrapper.in(!CollectionUtils.isEmpty(req.getVerifyTypes()),LogisticsVerifyOrderGoods::getVerifyType,req.getVerifyTypes());
        lambdaQueryWrapper.eq(Objects.nonNull(req.getVerifyStatus()),LogisticsVerifyOrderGoods::getVerifyStatus,req.getVerifyStatus());
        lambdaQueryWrapper.in(!CollectionUtils.isEmpty(req.getVerifyStatuss()),LogisticsVerifyOrderGoods::getVerifyStatus,req.getVerifyStatuss());
        lambdaQueryWrapper.eq(Objects.nonNull(req.getRefundStatus()),LogisticsVerifyOrderGoods::getRefundStatus,req.getRefundStatus());
        lambdaQueryWrapper.in(!CollectionUtils.isEmpty(req.getRefundStatuss()),LogisticsVerifyOrderGoods::getRefundStatus,req.getRefundStatuss());
        lambdaQueryWrapper.ge(StringUtils.isNotBlank(req.getStartVerifyTime()),LogisticsVerifyOrderGoods::getVerifyTime,CommonUtils.checkDateShortToLong(req.getStartVerifyTime(),true));
        lambdaQueryWrapper.le(StringUtils.isNotBlank(req.getEndVerifyTime()),LogisticsVerifyOrderGoods::getVerifyTime,CommonUtils.checkDateShortToLong(req.getEndVerifyTime(),false));
        lambdaQueryWrapper.like(StringUtils.isNotBlank(req.getUserPhone()) && !StrUtil.containsOnly(req.getUserPhone(), '*'),LogisticsVerifyOrderGoods::getLmPhone,req.getUserPhone());
        Optional.ofNullable(req.getAmUserInfo()).ifPresent(userInfo -> {
            lambdaQueryWrapper.in(!CollectionUtils.isEmpty(userInfo.getOrgIds()),LogisticsVerifyOrderGoods::getOrgId,userInfo.getOrgIds());
        });
        lambdaQueryWrapper.eq(LogisticsVerifyOrderGoods::getIsDelete, DeleteStatusEnum.NO.getCode());
        lambdaQueryWrapper.orderByDesc(LogisticsVerifyOrderGoods::getCreateTime);
        lambdaQueryWrapper.orderByDesc(LogisticsVerifyOrderGoods::getVerifyNo);
        return lambdaQueryWrapper;
    }

    @Override
    public Response<List<LogisticsVerifyOrderDetailResData>> detailList(LogisticsVerifyOrderDetailReqData req) {
        List<LogisticsVerifyOrder> logisticsVerifyOrders = logisticsVerifyOrderService.list(Wrappers.<LogisticsVerifyOrder>lambdaQuery()
                .in(!CollectionUtils.isEmpty(req.getOrderNos()),LogisticsVerifyOrder::getChildOrderId, req.getOrderNos())
                .eq(StringUtils.isNotBlank(req.getOrderNo()),LogisticsVerifyOrder::getChildOrderId,req.getOrderNo()));
        if(CollectionUtils.isEmpty(logisticsVerifyOrders)){
            return Response.ok(Collections.emptyList());
        }
        List<String> existLogisticsVerifyChildOrderIds = logisticsVerifyOrders.stream().map(LogisticsVerifyOrder::getChildOrderId).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(existLogisticsVerifyChildOrderIds)){
            return Response.ok(Collections.emptyList());
        }
        List<LogisticsVerifyOrderListResData> verifyListByOrderNos = getVerifyListByOrderNos(existLogisticsVerifyChildOrderIds);
        Map<String, List<LogisticsVerifyOrderGoodsDTO>> logisticsVerifyChildOrderIdAndMap = ListUtils.emptyIfNull(verifyListByOrderNos)
                .stream()
                .collect(Collectors.toMap(LogisticsVerifyOrderListResData::getChildOrderId,LogisticsVerifyOrderListResData::getGoodsList,(a,b) -> a));
        return Response.ok(logisticsVerifyOrders.stream()
                .map(order -> {
                    LogisticsVerifyOrderDetailResData logisticsVerifyOrderDetailResData = new LogisticsVerifyOrderDetailResData();
                    BeanUtils.copyProperties(order,logisticsVerifyOrderDetailResData);
                    logisticsVerifyOrderDetailResData.setId(String.valueOf(order.getId()));
                    logisticsVerifyOrderDetailResData.setLmNickname(DesensitizedUtil.desensitized(order.getLmNickname(), DesensitizedUtil.DesensitizedType.CHINESE_NAME));
                    Optional.ofNullable(StrUtil.emptyToDefault(order.getChildOrderId(),null)).map(childOrderId -> logisticsVerifyChildOrderIdAndMap.get(childOrderId)).ifPresent(item -> {
                        logisticsVerifyOrderDetailResData.setGoodsList(item.stream().sorted(Comparator.comparing(LogisticsVerifyOrderGoodsDTO::getVerifyFlag).reversed()).collect(Collectors.toList()));
                        Map<Integer, List<LogisticsVerifyOrderGoodsDTO>> groupByVerifyFlag = item.stream().collect(Collectors.groupingBy(LogisticsVerifyOrderGoodsDTO::getVerifyFlag));
                        logisticsVerifyOrderDetailResData.setCanVerifyList(groupByVerifyFlag.get(new Integer(1)));
                        logisticsVerifyOrderDetailResData.setCanNotVerifyList(groupByVerifyFlag.get(new Integer(0)));
                    });
                    logisticsVerifyOrderDetailResData.setVerifyStatusShow(Optional.ofNullable(LogisticsVerifyVerifyStatusEnum.fromCode(order.getVerifyStatus())).map(LogisticsVerifyVerifyStatusEnum::getDesc).orElse("-"));
                    logisticsVerifyOrderDetailResData.setRefundStatusShow(Optional.ofNullable(LogisticsVerifyRefundStatusEnum.fromCode(order.getRefundStatus())).map(LogisticsVerifyRefundStatusEnum::getDesc).orElse("-"));
                    return logisticsVerifyOrderDetailResData;
                })
                .collect(Collectors.toList()));
    }

    private List<LogisticsVerifyOrderListResData> getVerifyListByOrderNos(List<String> orderNos){
        LogisticsVerifyOrderListReqData logisticsVerifyOrderListReqData = new LogisticsVerifyOrderListReqData();
        logisticsVerifyOrderListReqData.setOrderNos(orderNos);
        LambdaQueryWrapper<LogisticsVerifyOrder> lambdaQueryWrapper = buildLogisticsVerifyOrderListQueryWrapper(logisticsVerifyOrderListReqData);
        List<LogisticsVerifyOrder> dataList = logisticsVerifyOrderService.list(lambdaQueryWrapper);
        return handelVerirfyOrderListResData(dataList);
    }

    private List<LogisticsVerifyOrderDetailResData> getDetailListByOrderNos(List<String> orderNos){
        LogisticsVerifyOrderDetailReqData logisticsVerifyOrderDetailReqData = new LogisticsVerifyOrderDetailReqData();
        logisticsVerifyOrderDetailReqData.setOrderNos(orderNos);
        return detailList(logisticsVerifyOrderDetailReqData).getData();
    }
    @Override
    @Transactional
    public Response<String> verify(LogisticsVerifyOrderVerifyReqData req) {
        List<String> orderNos = req.getVerifyList().stream().map(LogisticsVerifyOrderVerifyListDTO::getOrderNo).distinct().collect(Collectors.toList());
        List<LogisticsVerifyOrderDetailResData> verifyListByOrderNos = getDetailListByOrderNos(orderNos);
        Map<String,LogisticsVerifyOrderDetailResData> verifyOrderNoAndMap = ListUtils.emptyIfNull(verifyListByOrderNos).stream().collect(Collectors.toMap(LogisticsVerifyOrderDetailResData::getChildOrderId,Function.identity(),(a,b) -> a));
        Map<String,List<LogisticsVerifyOrderGoodsDTO>> existOrderNoAndSkuIdMap = ListUtils.emptyIfNull(verifyListByOrderNos).stream().collect(Collectors.toMap(LogisticsVerifyOrderDetailResData::getChildOrderId,LogisticsVerifyOrderDetailResData::getGoodsList,(a,b) -> a));
        verifyDataCheck(req, verifyListByOrderNos, existOrderNoAndSkuIdMap);
        String verifyNo = logisticsNoUtil.createVerifyNo();
        req.getVerifyList().stream().forEach(item -> {
            //更新核销订单商品表
            LogisticsVerifyOrderGoods logisticsVerifyOrderGoods = new LogisticsVerifyOrderGoods();
            logisticsVerifyOrderGoods.setVerifyStatus(LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode());
            logisticsVerifyOrderGoods.setVerifyTime(DateUtil.date());
            logisticsVerifyOrderGoods.setUpdateTime(DateUtil.date());
            //核销类型
            logisticsVerifyOrderGoods.setVerifyType(req.getVerifyType().getCode());
            if(req.getVerifyType().equals(LogisticsVerifyVerifyTypeEnum.VERIFY_ADMIN)){
                //运营端核销
                Optional.ofNullable(req.getAmUserInfo()).ifPresent(userInfo -> {
                    logisticsVerifyOrderGoods.setVerifyOrgId(ListUtils.emptyIfNull(userInfo.getOrgIds()).stream().findFirst().orElse(null));
                    logisticsVerifyOrderGoods.setVerifyOrgName(ListUtils.emptyIfNull(userInfo.getOrgNames()).stream().findFirst().orElse(null));
                    logisticsVerifyOrderGoods.setVerifyUserId(userInfo.getUserId());
                    logisticsVerifyOrderGoods.setVerifyUserAccount(userInfo.getUserName());
                    logisticsVerifyOrderGoods.setVerifyUserName(userInfo.getRealName());
                });
            }
            if(req.getVerifyType().equals(LogisticsVerifyVerifyTypeEnum.VERIFY_USER)){
                //用户端核销
                Optional.ofNullable(req.getUserTokenInfo()).ifPresent(userInfo -> {
                    logisticsVerifyOrderGoods.setVerifyUserId(userInfo.getLmId());
                    logisticsVerifyOrderGoods.setVerifyUserAccount(userInfo.getAccount());
                    logisticsVerifyOrderGoods.setVerifyUserName(userInfo.getUserName());
                });
            }
            //核销单号
            logisticsVerifyOrderGoods.setVerifyNo(verifyNo);
            //核销码
            logisticsVerifyOrderGoods.setVerifyCode(req.getPickupCode());
            logisticsVerifyOrderGoodsService.update(logisticsVerifyOrderGoods,Wrappers.<LogisticsVerifyOrderGoods>lambdaUpdate()
                    .eq(LogisticsVerifyOrderGoods::getChildOrderId,item.getOrderNo())
                    .in(LogisticsVerifyOrderGoods::getSkuId,item.getSkuIds()));
            //判断子单sku是否全部核销更改子单状态
            Optional.ofNullable(existOrderNoAndSkuIdMap.get(item.getOrderNo()))
                    .filter(x -> ListUtils.emptyIfNull(x).stream().filter(y -> !y.getVerifyStatus().equals(LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode()) && y.getRefundStatus().equals(LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode())).map(LogisticsVerifyOrderGoodsDTO::getSkuId).distinct().count() == ListUtils.emptyIfNull(item.getSkuIds()).stream().distinct().count())
                    .ifPresent(y -> {
                        //更新核销订单表
                        LogisticsVerifyOrder logisticsVerifyOrder = new LogisticsVerifyOrder();
                        logisticsVerifyOrder.setVerifyStatus(LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode());
                        logisticsVerifyOrder.setVerifyTime(DateUtil.date());
                        logisticsVerifyOrder.setUpdateTime(DateUtil.date());
                        logisticsVerifyOrder.setVerifyType(LogisticsVerifyVerifyTypeEnum.VERIFY_ADMIN.getCode());
                        Optional.ofNullable(req.getAmUserInfo()).ifPresent(userInfo -> {
                            logisticsVerifyOrder.setVerifyOrgId(ListUtils.emptyIfNull(userInfo.getOrgIds()).stream().findFirst().orElse(null));
                            logisticsVerifyOrder.setVerifyOrgName(ListUtils.emptyIfNull(userInfo.getOrgNames()).stream().findFirst().orElse(null));
                            logisticsVerifyOrder.setVerifyUserId(userInfo.getUserId());
                            logisticsVerifyOrder.setVerifyUserAccount(userInfo.getUserName());
                            logisticsVerifyOrder.setVerifyUserName(userInfo.getRealName());
                        });
                        //核销单号
                        logisticsVerifyOrder.setVerifyNo(logisticsNoUtil.createVerifyNo());
                        //核销码
                        logisticsVerifyOrder.setVerifyCode(req.getPickupCode());
                        logisticsVerifyOrderService.update(logisticsVerifyOrder,Wrappers.<LogisticsVerifyOrder>lambdaUpdate().eq(LogisticsVerifyOrder::getChildOrderId,item.getOrderNo()));

                        //推送自提/核销订单状态变更
                        Optional.ofNullable(verifyOrderNoAndMap.get(item.getOrderNo())).ifPresent(orderInfo -> {
                            //发送自提订单待自提消息通知
                            OrderStatusChangePushDTO orderStatusChangePushDTO = new OrderStatusChangePushDTO();
                            orderStatusChangePushDTO.setChildOrderId(orderInfo.getChildOrderId());
                            orderStatusChangePushDTO.setLogisticsId(Long.parseLong(orderInfo.getId()));
                            orderStatusChangePushDTO.setLogisticsType(LogisticsStatusChangeTypeEnum.FORWARD.getCode());
                            if(GoodsTypeEnum.GOODS.getCode().equals(orderInfo.getGoodsType())){
                                //商品类型待自提
                                orderStatusChangePushDTO.setOrderStatus(VerifyOrderStatusEnum.IS_PICKUP.getCode());
                            }else{
                                //卡券类型待核销
                                orderStatusChangePushDTO.setOrderStatus(VerifyOrderStatusEnum.IS_VERIFY.getCode());
                            }
                            orderStatusChangePushDTO.setStatusTime(LocalDateTimeUtil.now());
                            logisticsStatusChangeProducer.send(orderStatusChangePushDTO);
                        });
                    });
        });
        return Response.ok("核销成功");
    }
}

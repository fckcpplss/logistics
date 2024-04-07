package com.longfor.c10.lzyx.logistics.core.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.download.client.client.DownloadTaskClient;
import com.longfor.c10.lzyx.download.client.entity.BizExportParamEntity;
import com.longfor.c10.lzyx.download.client.param.DownloadInfoUpdateParam;
import com.longfor.c10.lzyx.download.entity.enums.TaskStatusEnum;
import com.longfor.c10.lzyx.download.entity.enums.TaskTypeEnum;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonDeliveryService;
import com.longfor.c10.lzyx.logistics.core.util.DesensitizedUtils;
import com.longfor.c10.lzyx.logistics.dao.mapper.LogisticsDeliveryMapper;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryCompanyService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderGoodsService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.*;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.AmUserInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.DeliveryCompanyResData;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDeliveryCompany;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.enums.*;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c10.lzyx.order.entity.enums.OrderStatusEnum;
import com.longfor.c10.starter.aliyun.oss.provider.AliyunOssProvider;
import com.longfor.c2.starter.common.util.JsonUtil;
import com.longfor.c2.starter.data.domain.file.MultipartFileInfo;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 物流运单业务接口实现类
 *
 * @author zhaoyl
 * @date 2022/2/21 上午9:49
 * @since 1.0
 */
@Slf4j
@Service
public class LogisticsCommonDeliveryServiceImpl implements ILogisticsCommonDeliveryService {
    @Autowired
    private LogisticsDeliveryMapper logisticsDeliveryMapper;

    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;

    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Autowired
    private DownloadTaskClient downloadTaskClient;

    @Autowired
    private AliyunOssProvider aliyunOssProvider;

    @Autowired
    private ILogisticsDeliveryCompanyService logisticsDeliveryCompanyService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<Boolean> updateNoSend(Request<List<LogisticOrderUpdateReq>> request) {
        log.info("更新子订单待发货数据请求参数：{}", JSON.toJSONString(request));
        if (request == null || CollectionUtils.isEmpty(request.getData())) {
            log.error("更新待发货列表请求参数不能为空");
            return Response.ok(false);
        }
        List<LogisticOrderUpdateReq> list = request.getData();
        for (LogisticOrderUpdateReq logisticOrderUpdateReq : list) {
            if(StringUtils.isBlank(logisticOrderUpdateReq.getChildOrderId())){
                continue;
            }
            LogisticsOrder logisticsOrder = logisticsOrderService.getOne(new LambdaQueryWrapper<LogisticsOrder>()
                    .eq(LogisticsOrder::getChildOrderId, logisticOrderUpdateReq.getChildOrderId()), false);
            // 只有待发货才隐藏/取消隐藏订单发货信息
            if(OrderStatusEnum.WAIT_DELIVER.getCode().equals(logisticOrderUpdateReq.getOrderStatus())){
                logisticsOrderService.update(new LambdaUpdateWrapper<LogisticsOrder>()
                        .eq(LogisticsOrder::getChildOrderId, logisticOrderUpdateReq.getChildOrderId())
                        .set(LogisticsOrder::getIfRefund, logisticOrderUpdateReq.getUpdateStatus())
                        .set(LogisticsOrder::getDeleteStatus, logisticOrderUpdateReq.getUpdateStatus()));
            }
            /*
             *
             *  更新物流商品 如果是作废的时候传过来是1，取消作废的时候是0
             *  在需求单作废时商户端待发货列表需要隐藏相应未发货商品信息（一个商品一条，并且判断未发货是看delivery_id为空）
             *  因此需求单作废的时候，如果商品的delivery_id为空则默认设置一个值，取消的时候置还原
             *  而运营端物流详情，是根据delivery_id查询运单，如果没查到则表示未发货
             *  因此需求单作废时这里设置一个默认值不影响运营端物流详情，并且能隐藏商户端待发货列表
             *
             */
            if(logisticOrderUpdateReq.getUpdateStatus() == 1){
                logisticsOrderGoodsService.update(new LambdaUpdateWrapper<LogisticsOrderGoods>()
                        .isNull(LogisticsOrderGoods::getLogisticsDeliveryId)
                        .eq(LogisticsOrderGoods::getLogisticsOrderId, logisticsOrder.getId())
                        .eq(LogisticsOrderGoods::getDeleteStatus, 0)
                        .set(LogisticsOrderGoods::getLogisticsDeliveryId, 0));
            } else {
                logisticsOrderGoodsService.update(new LambdaUpdateWrapper<LogisticsOrderGoods>()
                        .eq(LogisticsOrderGoods::getLogisticsDeliveryId, 0)
                        .eq(LogisticsOrderGoods::getLogisticsOrderId, logisticsOrder.getId())
                        .eq(LogisticsOrderGoods::getDeleteStatus, 0)
                        .set(LogisticsOrderGoods::getLogisticsDeliveryId, null));
            }

        }
        return Response.ok(true);
    }

    @Override
    public PageResponse<List<DeliveryNoSendListVO>> getNoSendList(PageInfo pageInfo, DeliveryNoSendListReq req) {
        //参数处理
        Optional.ofNullable(req.getSellType()).map(SellTypeEnum::fromCode).map(SellTypeEnum::getChannels).map(Arrays::asList).ifPresent(list -> {
            req.setBizChannelCodes(list);
        });
        IPage<DeliveryNoSendListVO> pageResult = logisticsDeliveryMapper.selectAdminNoSendList(new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize()), req);
        if (Objects.isNull(pageResult) || CollectionUtils.isEmpty(pageResult.getRecords())) {
            return PageResponse.page(pageResult.getRecords(), 0L);
        }
        //数据处理
        handelNoSendDataList(pageResult.getRecords(), true);
        return PageResponse.page(pageResult.getRecords(), pageResult.getTotal());
    }

    @Override
    public PageResponse<List<DeliverySendListVO>> getSendList(PageInfo pageInfo, DeliverySendListReq req) {
        //参数处理
        Optional.ofNullable(req.getSellType()).map(SellTypeEnum::fromCode).map(SellTypeEnum::getChannels).map(Arrays::asList).ifPresent(list -> {
            req.setBizChannelCodes(list);
        });
        IPage<DeliverySendListVO> pageResult = logisticsDeliveryMapper.selectAdminSendList(new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize()), req);
        if (Objects.isNull(pageResult) || CollectionUtils.isEmpty(pageResult.getRecords())) {
            return PageResponse.page(pageResult.getRecords(), 0L);
        }
        //数据处理
        handelSendDataList(pageResult.getRecords());
        return PageResponse.page(pageResult.getRecords(), pageResult.getTotal());
    }

    @Override
    public Response<DeliveryNoSendDetailVO> getNoSendDetail(DeliveryNoSendDetailReq req) {
        //根据子订单编号获取物流订单号
        LambdaQueryWrapper<LogisticsOrder> lambdaQueryWrapper = Wrappers.<LogisticsOrder>lambdaQuery()
                .eq(LogisticsOrder::getChildOrderId, req.getChildOrderId());
        lambdaQueryWrapper.orderByDesc(LogisticsOrder::getUpdateTime);
        lambdaQueryWrapper.last(" limit 1");
        //物流订单
        LogisticsOrder logisticsOrder = Optional.ofNullable(logisticsOrderService.getOne(lambdaQueryWrapper)).orElseThrow(() -> new BusinessException("物流订单不存在"));
         log.info("物流订单详情：{}", JsonUtil.toJson(logisticsOrder));
        //物流商品
        List<LogisticsOrderGoods> logisticsOrderGoods = logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                .eq(LogisticsOrderGoods::getLogisticsOrderId, logisticsOrder.getId())
                .eq(LogisticsOrderGoods::getDeleteStatus, DeleteStatusEnum.NO.getCode()));
        if (CollectionUtils.isEmpty(logisticsOrderGoods)) {
            throw new BusinessException("物流订单商品不存在");
        }

        //运单id集合
        List<Long> logisticsGoodsDeliveryIds = logisticsOrderGoods.stream().map(LogisticsOrderGoods::getLogisticsDeliveryId).distinct().collect(Collectors.toList());

        //商品运单信息
        Map<Long, LogisticsDelivery> goodsDeliveryIdAndMap = ListUtils.emptyIfNull(logisticsDeliveryService.list(Wrappers.<LogisticsDelivery>lambdaQuery().in(LogisticsDelivery::getId, logisticsGoodsDeliveryIds)))
                .stream()
                .collect(Collectors.toMap(LogisticsDelivery::getId, Function.identity(), (a, b) -> a));
        List<String> companyCodes = CollectionUtil.defaultIfEmpty(goodsDeliveryIdAndMap.values(), Collections.emptyList()).stream().map(LogisticsDelivery::getCompanyCode).distinct().collect(Collectors.toList());
        //快递公司编码和名称map
        Map<String, String> companyCodeAndNameMap = CollectionUtils.isEmpty(companyCodes) ? Collections.emptyMap() : ListUtils.emptyIfNull(logisticsDeliveryCompanyService.list(Wrappers.<LogisticsDeliveryCompany>lambdaQuery().in(LogisticsDeliveryCompany::getCompanyCode, companyCodes).eq(LogisticsDeliveryCompany::getStatus, 1)))
                .stream()
                .collect(Collectors.toMap(LogisticsDeliveryCompany::getCompanyCode, LogisticsDeliveryCompany::getCompanyName, (a, b) -> a));
        List<DeliveryNoSendListDetailVO> deliveryNoSendListDetailVOS = logisticsOrderGoods.stream().collect(Collectors.groupingBy(LogisticsOrderGoods::getLogisticsType, Collectors.collectingAndThen(Collectors.toList(), list -> {
            return ListUtils.emptyIfNull(list).stream()
                    .map(goodsInfo -> {
                        DeliveryNoSendListDetailGoodsVo goodsVo = new DeliveryNoSendListDetailGoodsVo();
                        goodsVo.setGoodsId(String.valueOf(goodsInfo.getId()));
                        goodsVo.setGoodsDesc(goodsInfo.getGoodsDesc());
                        goodsVo.setGoodsName(goodsInfo.getGoodsName());
                        goodsVo.setGoodsImgUrl(goodsInfo.getGoodsImgUrl());
                        goodsVo.setGoodsNum(goodsInfo.getGoodsNum());
                        goodsVo.setSkuId(goodsInfo.getSkuId());
                        goodsVo.setSkuSpecs(goodsInfo.getSkuSpecs());
                        if (Objects.nonNull(goodsInfo.getLogisticsDeliveryId()) && goodsDeliveryIdAndMap.containsKey(goodsInfo.getLogisticsDeliveryId())) {
                            Optional.ofNullable(goodsDeliveryIdAndMap.get(goodsInfo.getLogisticsDeliveryId())).ifPresent(deliveryInfo -> {
                                goodsVo.setCompanyName(Optional.ofNullable(companyCodeAndNameMap).map(x -> x.get(deliveryInfo.getCompanyCode())).orElse("未知"));
                                goodsVo.setCompangCode(deliveryInfo.getCompanyCode());
                                goodsVo.setLogisticsStatusCode(deliveryInfo.getLogisticsStatus());
                                goodsVo.setLogisticsStatusName(Optional.ofNullable(DeliveryLogisticsStatusEnum.fromCode(deliveryInfo.getLogisticsStatus())).map(DeliveryLogisticsStatusEnum::getDesc).orElse(null));
                                goodsVo.setDeliveryNo(deliveryInfo.getDeliveryNo());
                                goodsVo.setSendAddress(DesensitizedUtils.maskAddress(StringUtils.join(deliveryInfo.getSendProvince(), deliveryInfo.getSendCity(), deliveryInfo.getSendArea(), deliveryInfo.getSendAddress())));
                            });
                        } else {
                            goodsVo.setLogisticsStatusCode(DeliveryLogisticsStatusEnum.TO_SEND.getCode());
                            goodsVo.setLogisticsStatusName(DeliveryLogisticsStatusEnum.TO_SEND.getDesc());
                        }
                        return goodsVo;
                    }).collect(Collectors.toList());
        }))).entrySet().stream().map(typeMap -> {
            DeliveryNoSendListDetailVO deliveryNoSendListDetailVO = new DeliveryNoSendListDetailVO();
            deliveryNoSendListDetailVO.setChildOrderId(logisticsOrder.getChildOrderId());
            deliveryNoSendListDetailVO.setShopId(logisticsOrder.getShopId());
            deliveryNoSendListDetailVO.setLogisticsTypeCode(typeMap.getKey());
            deliveryNoSendListDetailVO.setLogisticsTypeName(Optional.ofNullable(LogisticsTypeEnum.fromCode(typeMap.getKey())).map(LogisticsTypeEnum::getDesc).orElse(null));
            deliveryNoSendListDetailVO.setOrderCreateTime(DateUtil.formatDateTime(logisticsOrder.getCreateTime()));
            deliveryNoSendListDetailVO.setReceivePhone(req.isDesensitizedFlag() ? DesensitizedUtils.maskMobilePhone(logisticsOrder.getReceiptPhone()) : logisticsOrder.getReceiptPhone());
            deliveryNoSendListDetailVO.setReceiveName(req.isDesensitizedFlag() ? DesensitizedUtils.maskChineseName(logisticsOrder.getReceiptName()) : logisticsOrder.getReceiptName());
            deliveryNoSendListDetailVO.setReceiveAddress(Optional.ofNullable(StringUtils.join(logisticsOrder.getReceiptProvince(), logisticsOrder.getReceiptCity(), logisticsOrder.getReceiptArea(), logisticsOrder.getReceiptAddress()))
                    .map(address -> req.isDesensitizedFlag() ? DesensitizedUtils.maskChineseName(address) : address)
                    .orElse(null));

            deliveryNoSendListDetailVO.setSendGoodList(typeMap.getValue());
            return deliveryNoSendListDetailVO;
        }).collect(Collectors.toList());
        DeliveryNoSendDetailVO deliveryNoSendDetailVO = new DeliveryNoSendDetailVO();
        deliveryNoSendDetailVO.setOperOrgName(logisticsOrder.getOrgName());
        deliveryNoSendDetailVO.setShopId(logisticsOrder.getShopId());
        deliveryNoSendDetailVO.setShopName(logisticsOrder.getShopName());
        deliveryNoSendDetailVO.setChildOrderId(logisticsOrder.getChildOrderId());
        deliveryNoSendDetailVO.setOrderCreateTime(DateUtil.formatDateTime(logisticsOrder.getCreateTime()));
        deliveryNoSendDetailVO.setList(deliveryNoSendListDetailVOS);
        //处理销售模式
        Optional.ofNullable(SellTypeEnum.fromChannelCode(logisticsOrder.getBizChannelCode())).ifPresent(e -> {
            deliveryNoSendDetailVO.setSellType(e.getCode());
            deliveryNoSendDetailVO.setSellTypeShow(e.getDesc());
        });
        deliveryNoSendDetailVO.setOrderDesc(logisticsOrder.getOrderDesc());
        return Response.ok(deliveryNoSendDetailVO);
    }

    @Override
    public Response<DeliverySendDetailVO> getSendDetail(DeliverySendDetailReq req) {
        log.info("已发货列表详情查询参数,req = {}", JSON.toJSONString(req));

        AmUserInfo userInfo = Optional.ofNullable(req.getAmUserInfo()).orElseThrow(() -> new BusinessException("用户登陆信息为空"));
        log.info("已发货列表详情查询参数,用户信息 = {}", JSON.toJSONString(userInfo));

        LambdaQueryWrapper<LogisticsOrder> lambdaQueryWrapper = Wrappers.<LogisticsOrder>lambdaQuery()
                .eq(LogisticsOrder::getChildOrderId, req.getChildOrderId());
        lambdaQueryWrapper.orderByDesc(LogisticsOrder::getUpdateTime);
        lambdaQueryWrapper.last(" limit 1");
        //物流订单
        LogisticsOrder order = Optional.ofNullable(logisticsOrderService.getOne(lambdaQueryWrapper)).orElseThrow(() -> new BusinessException("物流订单不存在"));
        //物流运单
        LogisticsDelivery delivery = Optional.ofNullable(logisticsDeliveryService.getOne(Wrappers.<LogisticsDelivery>lambdaQuery()
                .eq(LogisticsDelivery::getIfCancel, 0)
                .eq(LogisticsDelivery::getLogisticsOrderId, order.getId())
                .eq(StringUtils.isNoneBlank(req.getDeliveryNo()), LogisticsDelivery::getDeliveryNo, req.getDeliveryNo())
                .last(" limit 1")))
                .orElseThrow(() -> new BusinessException("物流运单不存在"));
        //物流商品
        List<LogisticsOrderGoods> logisticsOrderGoods = logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                .eq(LogisticsOrderGoods::getLogisticsDeliveryId, delivery.getId())
                .eq(LogisticsOrderGoods::getDeleteStatus, DeleteStatusEnum.NO.getCode()));
        if (CollectionUtils.isEmpty(logisticsOrderGoods)) {
            throw new BusinessException("物流订单商品不存在");
        }
        Map<String, String> companyCodeAndNameMap = ListUtils.emptyIfNull(logisticsDeliveryCompanyService.list()).stream().collect(Collectors.toMap(LogisticsDeliveryCompany::getCompanyCode, LogisticsDeliveryCompany::getCompanyName, (a, b) -> a));
        DeliverySendDetailVO resp = new DeliverySendDetailVO();
        BeanUtils.copyProperties(delivery, resp);
        resp.setOrderCreateTime(DateUtil.formatDateTime(order.getCreateTime()));
        resp.setChildOrderId(req.getChildOrderId());
        resp.setDeliveryNo(req.getDeliveryNo());
        //快递公司名称
        resp.setCompany(Optional.ofNullable(companyCodeAndNameMap).map(x -> x.get(delivery.getCompanyCode())).orElse(null));
        //物流类型
        resp.setLogisticsType(Optional.ofNullable(LogisticsTypeEnum.fromCode(delivery.getLogisticsType())).map(LogisticsTypeEnum::getDesc).orElse(null));
        resp.setLogisticsStatus(DeliveryLogisticsStatusEnum.fromCode(delivery.getLogisticsStatus()).getDesc());
        if (CollectionUtils.isNotEmpty(userInfo.getOrgIds())) {
            resp.setSendName(DesensitizedUtils.maskChineseName(resp.getSendName()));
            resp.setSendPhone(DesensitizedUtils.maskMobilePhone(resp.getSendPhone()));
        }
        if (CompanyCodeEnum.JD.getDesc().equals(resp.getCompany()) || CompanyCodeEnum.SF.getDesc().equals(resp.getCompany())) {
            resp.setAccount(LogisticsTypeEnum.fromCode(delivery.getLogisticsType()).getDesc() + resp.getCompany());
        }

        if (Objects.isNull(delivery.getShopLogisticsId())) {
            //快递100发货地址为空
            resp.setSendAddress(null);
        } else {
            String sendAddress = delivery.getSendProvince() + delivery.getSendCity() + delivery.getSendArea() + delivery.getSendAddress();
            resp.setSendAddress(DesensitizedUtils.maskAddress(sendAddress));
        }

        String receiptAddress = delivery.getReceiptProvince() + delivery.getReceiptCity() + delivery.getReceiptArea() + delivery.getReceiptAddress();
        resp.setReceiptAddress(DesensitizedUtils.maskAddress(receiptAddress));
        resp.setReceiptName(DesensitizedUtils.maskChineseName(resp.getReceiptName()));
        resp.setReceiptPhone(DesensitizedUtils.maskMobilePhone(resp.getReceiptPhone()));
        resp.setShopName(order.getShopName());
        resp.setOperOrgName(order.getOrgName());
        //设置商品信息
        List<GoodsVO> logisticsGoodsVOS = logisticsOrderGoods.stream().map(goodsInfo -> {
            GoodsVO dto = new GoodsVO();
            BeanUtils.copyProperties(goodsInfo, dto);
            dto.setGoodsType(GoodsTypeEnum.fromCode(goodsInfo.getGoodsType()).getDesc());
            resp.getOrderGoodsList().add(dto);
            return dto;
        }).collect(Collectors.toList());
        resp.setOrderGoodsList(logisticsGoodsVOS);
        //处理销售模式
        Optional.ofNullable(SellTypeEnum.fromChannelCode(order.getBizChannelCode())).ifPresent(e -> {
            resp.setSellType(e.getCode());
            resp.setSellTypeShow(e.getDesc());
        });
        resp.setOrderDesc(order.getOrderDesc());
        return Response.ok(resp);
    }

    @Override
    public Response<Boolean> exportNoSendList(Request<BizExportParamEntity> request) {
        BizExportParamEntity exportParam = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("下载参数不能为空"));
        InputStream inputStream = null;
        String downloadKey = null;
        String fileName = "";
        long total = 0L;
        int taskStatus = TaskStatusEnum.DEAL_SUCCESS.getValue();
        String taskResult = "下载成功";
        int pageQuerySize = 1000;
        try {
            log.info("待发货列表导出，接受到下载参数：{}", JSON.toJSONString(request));
            DeliveryNoSendListReq reqData = Optional.ofNullable(exportParam).map(x -> JSONObject.parseObject(x.getExportParam(), DeliveryNoSendListReq.class)).orElse(new DeliveryNoSendListReq());
            List<DeliveryNoSendListVO> deliveryNoSendLists = new ArrayList<>();
            boolean isShopExport = false;
            Class exportModelClass = DeliveryNoSendListVO.class;
            fileName = TaskTypeEnum.LOGISTICS_ADMIN_DELIVERY_NO_SEND_EXPORT.getDesc() + "-" + DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss");
            //组织id处理
            if (StringUtils.isNotBlank(reqData.getOrgId())) {
                reqData.setOrgIds(Arrays.stream(reqData.getOrgId().split(",")).collect(Collectors.toList()));
                reqData.setOrgId(null);
            }
            if (StringUtils.isNotBlank(reqData.getShopId())) {
                isShopExport = true;
                reqData.setShopIds(Arrays.stream(reqData.getShopId().split(",")).collect(Collectors.toList()));
                reqData.setShopId(null);
            }
            //参数处理
            Optional.ofNullable(reqData.getSellType()).map(SellTypeEnum::fromCode).map(SellTypeEnum::getChannels).map(Arrays::asList).ifPresent(list -> {
                reqData.setBizChannelCodes(list);
            });
            //计算总条数
            IPage<DeliveryNoSendListVO> pageResult = logisticsDeliveryMapper.selectAdminNoSendList(new Page<>(1, pageQuerySize), reqData);
            total = pageResult.getTotal();
            deliveryNoSendLists.addAll(pageResult.getRecords());
            //计算查询次数 按照每1000条执行一次
            long time = (total + pageQuerySize - 1) / pageQuerySize;
            if (total > 0 && time > 1) {
                deliveryNoSendLists.addAll(IntStream.range(2, (int) time + 1).mapToObj(index -> {
                    return Optional.ofNullable(logisticsDeliveryMapper.selectAdminNoSendList(new Page<>(index, pageQuerySize), reqData)).map(IPage::getRecords).orElse(null);
                }).filter(x -> CollectionUtils.isNotEmpty(x))
                        .flatMap(list -> {
                            return list.stream();
                        }).collect(Collectors.toList()));
            }
            //数据处理
            handelNoSendDataList(deliveryNoSendLists, isShopExport ? false : true);
            //按照sku拆分
            List<DeliveryNoSendListVO> exportDataList = deliveryNoSendLists.stream()
                    .flatMap(listVO -> {
                        return ListUtils.emptyIfNull(listVO.getGoodsVos())
                                .stream()
                                .map(goodsVO -> {
                                    DeliveryNoSendListVO newListVO = new DeliveryNoSendListVO();
                                    BeanUtils.copyProperties(listVO, newListVO);
                                    BeanUtils.copyProperties(goodsVO, newListVO);
                                    return newListVO;
                                });
                    })
                    .collect(Collectors.toList());
            ExportParams exportParams = new ExportParams(null, "待发货列表");
            exportParams.setType(ExcelType.XSSF);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, handelExportModelClass(exportModelClass, isShopExport), exportDataList);
            log.info("共导出:{}条记录", total);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] barray = bos.toByteArray();
            inputStream = new ByteArrayInputStream(barray);
            downloadKey = aliyunOssProvider.upload(inputStream, fileName + ".xlsx");
            log.info("待发货列表导出，上传阿里云key：{}", JSON.toJSONString(downloadKey));
            return Response.ok(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            taskResult = ExceptionUtil.stacktraceToString(ex);
            taskStatus = TaskStatusEnum.DEAL_FAIL.getValue();
        } finally {
            Request<DownloadInfoUpdateParam> downloadInfoUpdateParamRequest = new Request();
            DownloadInfoUpdateParam downloadInfoUpdateParam = new DownloadInfoUpdateParam();
            downloadInfoUpdateParam.setTaskId(exportParam.getTaskId());
            downloadInfoUpdateParam.setTaskStatus(taskStatus);
            downloadInfoUpdateParam.setTaskResult(taskResult);
            if (StringUtils.isNotBlank(downloadKey)) {
                downloadInfoUpdateParam.setDownloadKey(downloadKey);
            }
            downloadInfoUpdateParam.setDownloadTotalNum(total);
            downloadInfoUpdateParam.setFieldCnName(getAnnoationValus(DeliveryNoSendListVO.class));
            downloadInfoUpdateParamRequest.setData(downloadInfoUpdateParam);
            downloadTaskClient.downloadUpdate(downloadInfoUpdateParamRequest);
        }
        return Response.ok(true);
    }

    @Override
    public Response<Boolean> exportSendList(Request<BizExportParamEntity> request) {
        BizExportParamEntity exportParam = Optional.ofNullable(request).map(Request::getData).orElseThrow(() -> new BusinessException("下载参数不能为空"));
        InputStream inputStream = null;
        String downloadKey = null;
        String fileName = "";
        long total = 0L;
        int taskStatus = TaskStatusEnum.DEAL_SUCCESS.getValue();
        String taskResult = "下载成功";
        int pageQuerySize = 1000;
        try {
            log.info("已发货列表导出，接受到下载参数：{}", JSON.toJSONString(request));
            DeliverySendListReq reqData = Optional.ofNullable(exportParam).map(x -> JSONObject.parseObject(x.getExportParam(), DeliverySendListReq.class)).orElse(new DeliverySendListReq());
            List<DeliverySendListVO> deliverySendLists = new ArrayList<>();
            boolean isShopExport = false;
            Class exportModelClass = DeliverySendListVO.class;
            //组织id处理
            if (StringUtils.isNotBlank(reqData.getOrgId())) {
                reqData.setOrgIds(Arrays.stream(reqData.getOrgId().split(",")).collect(Collectors.toList()));
                reqData.setOrgId(null);
            }
            if (StringUtils.isNotBlank(reqData.getShopId())) {
                isShopExport = true;
                reqData.setShopIds(Arrays.stream(reqData.getShopId().split(",")).collect(Collectors.toList()));
                reqData.setShopId(null);
            }
            //参数处理
            Optional.ofNullable(reqData.getSellType()).map(SellTypeEnum::fromCode).map(SellTypeEnum::getChannels).map(Arrays::asList).ifPresent(list -> {
                reqData.setBizChannelCodes(list);
            });
            //计算总条数
            IPage<DeliverySendListVO> pageResult = logisticsDeliveryMapper.selectAdminSendList(new Page<>(1, pageQuerySize), reqData);
            total = pageResult.getTotal();
            deliverySendLists.addAll(pageResult.getRecords());
            //计算查询次数 按照每1000条执行一次
            long time = (total + pageQuerySize - 1) / pageQuerySize;
            if (total > 0 && time > 1) {
                deliverySendLists.addAll(IntStream.range(2, (int) time + 1).mapToObj(index -> {
                    return Optional.ofNullable(logisticsDeliveryMapper.selectAdminSendList(new Page<>(index, pageQuerySize), reqData)).map(IPage::getRecords).orElse(null);
                }).filter(x -> CollectionUtils.isNotEmpty(x))
                        .flatMap(list -> {
                            return list.stream();
                        }).collect(Collectors.toList()));
            }
            //数据处理
            handelSendDataList(deliverySendLists);
            //按照sku拆分
            List<DeliverySendListVO> exportDataList = deliverySendLists.stream()
                    .flatMap(listVO -> {
                        return ListUtils.emptyIfNull(listVO.getGoodsList())
                                .stream()
                                .map(goodsVO -> {
                                    DeliverySendListVO newListVO = new DeliverySendListVO();
                                    BeanUtils.copyProperties(listVO, newListVO);
                                    BeanUtils.copyProperties(goodsVO, newListVO);
                                    return newListVO;
                                });
                    })
                    .collect(Collectors.toList());

            fileName = TaskTypeEnum.LOGISTICS_ADMIN_DELIVERY_SEND_EXPORT.getDesc() + "-" + DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss");
            ExportParams exportParams = new ExportParams(null, "已发货列表");
            exportParams.setType(ExcelType.XSSF);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, handelExportModelClass(exportModelClass, isShopExport), exportDataList);
            log.info("共导出:{}条记录", total);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] barray = bos.toByteArray();
            inputStream = new ByteArrayInputStream(barray);
            downloadKey = aliyunOssProvider.upload(inputStream, fileName + ".xlsx");
            log.info("已发货列表导出，上传阿里云key：{}", JSON.toJSONString(downloadKey));
            return Response.ok(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            taskResult = ExceptionUtil.stacktraceToString(ex);
            taskStatus = TaskStatusEnum.DEAL_FAIL.getValue();
        } finally {
            if (Objects.nonNull(inputStream)) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            Request<DownloadInfoUpdateParam> downloadInfoUpdateParamRequest = new Request();
            DownloadInfoUpdateParam downloadInfoUpdateParam = new DownloadInfoUpdateParam();
            downloadInfoUpdateParam.setTaskId(exportParam.getTaskId());
            downloadInfoUpdateParam.setTaskStatus(taskStatus);
            downloadInfoUpdateParam.setTaskResult(taskResult);
            if (StringUtils.isNotBlank(downloadKey)) {
                downloadInfoUpdateParam.setDownloadKey(downloadKey);
            }
            downloadInfoUpdateParam.setDownloadTotalNum(total);
            downloadInfoUpdateParam.setFieldCnName(getAnnoationValus(DeliverySendListVO.class));
            downloadInfoUpdateParamRequest.setData(downloadInfoUpdateParam);
            downloadTaskClient.downloadUpdate(downloadInfoUpdateParamRequest);
        }
        return Response.ok(true);
    }

    /**
     * 处理商家导出不展示字段处理
     *
     * @param exportModelClass
     * @param isShopExport
     * @return
     */
    private Class<?> handelExportModelClass(Class exportModelClass, boolean isShopExport) {
        if (!isShopExport) {
            return exportModelClass;
        }
        List<String> ignoreFiledNames = Arrays.asList("operOrgName", "orgName");
        ListUtils.emptyIfNull(ignoreFiledNames).stream().forEach(fieldName -> {
            Optional.ofNullable(ReflectUtil.getField(exportModelClass, fieldName))
                    .map(field -> field.getAnnotation(Excel.class))
                    .ifPresent(annotation -> {
                        try {
                            //获取代理
                            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
                            Field excelField = invocationHandler.getClass().getDeclaredField("memberValues");
                            excelField.setAccessible(true);
                            Map memberValues = (Map) excelField.get(invocationHandler);
                            memberValues.put("isColumnHidden", true);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            log.error("根据字段明层获取反射属性异常", e);
                        }
                    });
        });
        return exportModelClass;
    }

    /**
     * 获取导出文档字段
     *
     * @return
     */
    public String getAnnoationValus(Class clazz) {
        return Arrays.stream(ReflectUtil.getFields(clazz)).filter(field -> {
            ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
            if (!Objects.isNull(annotation)) {
                return true;
            }
            return false;
        }).map(x -> {
            StringBuilder sb = new StringBuilder();
            String[] value = x.getAnnotation(ExcelProperty.class).value();
            ListUtils.emptyIfNull(Arrays.asList(value)).stream().forEach(v -> {
                sb.append(v);
            });
            return sb;
        }).collect(Collectors.joining(","));

    }

    /**
     * @param records
     * @param isDesensitized 是否脱敏
     */
    //处理待发货列表数据
    private void handelNoSendDataList(List<DeliveryNoSendListVO> records, boolean isDesensitized) {
        Map<Long, LogisticsOrderGoods> logisticsGoodsIdAndMap = getLogisticsGoodsIdAndMap(ListUtils.emptyIfNull(records).stream().map(DeliveryNoSendListVO::getGoodsIds).distinct().collect(Collectors.toList()));
        records.stream().forEach(item -> {
            item.setReceiptName(!isDesensitized ? item.getReceiptName() : DesensitizedUtils.maskChineseName(item.getReceiptName()));
            item.setReceiptAddress(!isDesensitized ? item.getReceiptAddress() : DesensitizedUtils.maskAddress(item.getReceiptAddress()));
            item.setReceiptPhoneNumber(!isDesensitized ? item.getReceiptPhoneNumber() : DesensitizedUtils.maskMobilePhone(item.getReceiptPhoneNumber()));
            item.setLogisticsTypeShow(Optional.ofNullable(item.getLogisticsType()).map(LogisticsTypeEnum::fromCode).map(LogisticsTypeEnum::getDesc).orElse(null));
            Optional.ofNullable(StrUtil.emptyToDefault(item.getGoodsIds(), null)).ifPresent(goodsIds -> {
                item.setGoodsVos(Arrays.stream(goodsIds.split("\\,"))
                        .map(Long::parseLong)
                        .map(goodsId -> {
                            return Optional.ofNullable(logisticsGoodsIdAndMap.get(goodsId))
                                    .map(goodsEntity -> {
                                        GoodsVO goodsVO = new GoodsVO();
                                        BeanUtils.copyProperties(goodsEntity, goodsVO);
                                        return goodsVO;
                                    })
                                    .orElse(null);
                        }).filter(Objects::nonNull).collect(Collectors.toList()));
            });
            //处理销售模式
            Optional.ofNullable(SellTypeEnum.fromChannelCode(item.getBizChannelCode())).ifPresent(e -> {
                item.setSellType(e.getCode());
                item.setSellTypeShow(e.getDesc());
            });
            if (!SellTypeEnum.WHOLESALE.getCode().equals(item.getSellType())) {
                item.setOrderDesc(null);
            }


        });
    }

    private Map<Long, LogisticsOrderGoods> getLogisticsGoodsIdAndMap(List<String> goodsIds) {
        //查询商品信息
        List<Long> logisticsOrderGoodsIds = ListUtils.emptyIfNull(goodsIds).stream()
                .filter(goodsIdStr -> StringUtils.isNotBlank(goodsIdStr))
                .flatMap(goodsIdStr -> Arrays.stream(goodsIdStr.split("\\,")))
                .map(Long::parseLong)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, LogisticsOrderGoods> logisticsGoodsIdAndMap = CollectionUtils.isEmpty(logisticsOrderGoodsIds) ? Collections.emptyMap() : ListUtils.emptyIfNull(logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                .in(LogisticsOrderGoods::getId, logisticsOrderGoodsIds)
                .eq(LogisticsOrderGoods::getDeleteStatus, DeleteStatusEnum.NO.getCode())))
                .stream()
                .collect(Collectors.toMap(LogisticsOrderGoods::getId, Function.identity(), (a, b) -> a));
        return logisticsGoodsIdAndMap;
    }

    //处理已发货列表数据
    private void handelSendDataList(List<DeliverySendListVO> records) {
        Map<Long, LogisticsOrderGoods> logisticsGoodsIdAndMap = getLogisticsGoodsIdAndMap(ListUtils.emptyIfNull(records).stream().map(DeliverySendListVO::getGoodsIds).collect(Collectors.toList()));
        Map<String, String> companyCodeAndNameMap = ListUtils.emptyIfNull(logisticsDeliveryCompanyService.list()).stream().collect(Collectors.toMap(LogisticsDeliveryCompany::getCompanyCode, LogisticsDeliveryCompany::getCompanyName, (a, b) -> a));
        records.stream().forEach(item -> {
            Optional.ofNullable(StrUtil.emptyToDefault(item.getGoodsIds(), null)).ifPresent(goodsIds -> {
                item.setGoodsList(Arrays.stream(goodsIds.split("\\,"))
                        .map(Long::parseLong)
                        .map(goodsId -> {
                            return Optional.ofNullable(logisticsGoodsIdAndMap.get(goodsId))
                                    .map(goodsEntity -> {
                                        GoodsVO goodsVO = new GoodsVO();
                                        BeanUtils.copyProperties(goodsEntity, goodsVO);
                                        return goodsVO;
                                    })
                                    .orElse(null);
                        }).filter(Objects::nonNull).collect(Collectors.toList()));
                item.setReceiptName(DesensitizedUtils.maskChineseName(item.getReceiptName()));
                item.setReceiptAddress(DesensitizedUtils.maskAddress(item.getReceiptAddress()));
                item.setReceiptPhoneNumber(DesensitizedUtils.maskMobilePhone(item.getReceiptPhoneNumber()));
                item.setLogisticsTypeShow(handelLogisticsTypeShow(item.getCompanyCode(), item.getLogisticsType(), item.getShopLogisticsId()));
                item.setLogisticsStatusShow(Optional.ofNullable(item.getLogisticsStatus()).map(x -> DeliveryLogisticsStatusEnum.fromCode(x)).map(DeliveryLogisticsStatusEnum::getDesc).orElse(null));
                Optional.ofNullable(companyCodeAndNameMap.get(item.getCompanyCode())).ifPresent(companyName -> {
                    item.setCompanyCodeShow(companyName);
                });

                //处理销售模式
                Optional.ofNullable(SellTypeEnum.fromChannelCode(item.getBizChannelCode())).ifPresent(e -> {
                    item.setSellType(e.getCode());
                    item.setSellTypeShow(e.getDesc());
                });
                if (!SellTypeEnum.WHOLESALE.getCode().equals(item.getSellType())) {
                    item.setOrderDesc(null);
                }
            });
        });
    }

    private String handelLogisticsTypeShow(String companyCode, Integer logisticsType, Integer shopLogisticsId) {
        if (StringUtils.isBlank(companyCode) || Objects.isNull(logisticsType)) {
            return "其他";
        }
        if ("jd".equals(companyCode) && logisticsType == 1) {
            return "平台京东";
        } else if ("shunfeng".equals(companyCode) && logisticsType == 1) {
            return "平台顺丰";
        } else if ("shunfeng".equals(companyCode) && logisticsType == 2 && Objects.nonNull(shopLogisticsId)) {
            return "商家顺丰";
        } else {
            return "其他";
        }
    }

    //校验登陆信息
    // TODO: 2022/2/22 抽成公共校验
    private boolean checkParams(BaseReqData req) {
        AmUserInfo amUserInfo = Optional.ofNullable(req).map(BaseReqData::getAmUserInfo).orElseThrow(() -> new BusinessException("用户登陆信息为空"));
        if (CollectionUtils.isEmpty(amUserInfo.getOrgIds())) {
            throw new BusinessException("当前用户无有效运营组织信息");
        }
        if (StringUtils.isNotBlank(req.getOrgId()) && amUserInfo.getOrgIds().stream().noneMatch(orgId -> orgId.equals(req.getOrgId()))) {
            return false;
        }
        req.setOrgIds(amUserInfo.getOrgIds());
        return true;
    }
}

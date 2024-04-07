package com.longfor.c10.lzyx.logistics.core.service.admin.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longfor.c10.lzyx.logistics.core.mq.producer.LogisticsStatusChangeProducer;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonOrderService;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonVerifyOrderService;
import com.longfor.c10.lzyx.logistics.core.service.admin.ILogisticsAdminOrderToolsService;
import com.longfor.c10.lzyx.logistics.core.service.impl.delivery.KuaiDi100ServiceImpl;
import com.longfor.c10.lzyx.logistics.core.util.CommonUtils;
import com.longfor.c10.lzyx.logistics.core.util.JsonUtil;
import com.longfor.c10.lzyx.logistics.core.util.LogisticsNoUtil;
import com.longfor.c10.lzyx.logistics.core.util.ToolsUtil;
import com.longfor.c10.lzyx.logistics.dao.service.*;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticOrderToolsReq;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.AmUserInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.SubLogisticsPathReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.OrderStatusChangePushDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.tools.*;
import com.longfor.c10.lzyx.logistics.entity.entity.*;
import com.longfor.c10.lzyx.logistics.entity.enums.*;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.common.util.StringUtil;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 物流运维工具接口实现类
 *
 * @author zhaoyl
 * @date 2021/12/6 上午11:37
 * @since 1.0
 */
@Slf4j
@Service
public class LogisticsAdminOrderToolsServiceImpl implements ILogisticsAdminOrderToolsService {
    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;

    @Autowired
    private ILogisticsFeeService logisticsFeeService;

    @Autowired
    private LogisticsStatusChangeProducer logisticsStatusChangeProducer;

    @Autowired
    private KuaiDi100ServiceImpl kuaiDi100Service;

    @Autowired
    private ILogisticsCommonOrderService logisticsCommonOrderService;

    @Autowired
    private ILogisticsDeliveryToolRecordService logisticsDeliveryToolRecordService;
    @Autowired
    private ILogisticsDeliveryCompanyService logisticsDeliveryCompanyService;
    @Resource
    private ILogisticsVerifyOrderService logisticsVerifyOrderService;
    @Resource
    private LogisticsNoUtil logisticsNoUtil;
    @Resource
    private ILogisticsVerifyOrderGoodsService logisticsVerifyOrderGoodsService;
    @Resource(name = "logisticsAdminVerifyOrderServiceImpl")
    private ILogisticsCommonVerifyOrderService logisticsCommonVerifyOrderService;
    @Resource
    private ILogisticsVerifyOrderRecordService logisticsVerifyOrderRecordService;
    /**
     * 只处理待核销状态
     */
    @Value("${only.no-verify:true}")
    private boolean onlyNoVerify;
    private static final String BATCH_VERIFY_DEFAULT_CODE = "999999";

    @Override
    public Response<Boolean> update(@Valid Request<List<TableInfoDTO>> request) {
        log.info("物流运维工具，更新参数 = {}", JSON.toJSONString(request));
        List<TableInfoDTO> tableInfoDto = Optional.ofNullable(request).map(Request::getData).map(param -> CollectionUtils.isEmpty(param) ? null : param).orElseThrow(() -> new BusinessException("参数不能为空"));
        //用户信息
        AmUserInfo amUserInfo = tableInfoDto.stream().findFirst().map(x -> ((BaseReqData) x).getAmUserInfo()).orElse(null);
        //转换格式为操作数据库表对应的entity格式
        Map<String, List<Object>> moduleDataMap = ToolsUtil.handelData(tableInfoDto, amUserInfo);
        log.info("物流运维工具，模块和更新数据映射 = {}", JSON.toJSONString(moduleDataMap));
        if (CollectionUtils.isEmpty(moduleDataMap)) {
            throw new BusinessException("参数不能为空");
        }
        //数据更新
        moduleDataMap.forEach((k, v) -> {
            ServiceImpl service = ToolsUtil.getServiceFromTabelName(k);

            if (!CollectionUtils.isEmpty(v) && service.updateBatchById(v)) {
                log.info("物流运维工具，数据更新成功");
                //发送物流状态变更mq
                ThreadUtil.execAsync(() -> handelSendLogisticsChangeMq(v));
            }
        });
        return Response.ok(true);
    }

    @Override
    public Response<List<LogisticDeliverToolRecordDTO>> changeDeliverNoRecord(Request<LogisticsDeliverToolReq> request) {
        LogisticsDeliverToolReq data = request.getData();
        String childOrderId = data.getChildOrderId();
        try {

            if (StringUtil.isAnyBlank(childOrderId)) {
                throw new BusinessException("参数不可为空");
            }
            List<LogisticsDeliveryToolRecord> list = logisticsDeliveryToolRecordService.list(Wrappers.<LogisticsDeliveryToolRecord>lambdaQuery().eq(LogisticsDeliveryToolRecord::getChildOrderId, childOrderId).orderByDesc(LogisticsDeliveryToolRecord::getCreateTime));
            List<LogisticDeliverToolRecordDTO> collect = ListUtils.emptyIfNull(list)
                    .stream()
                    .map(item -> {
                        LogisticDeliverToolRecordDTO recordDTO = new LogisticDeliverToolRecordDTO();
                        BeanUtil.copyProperties(item, recordDTO);
                        recordDTO.setCreateTime(DateUtil.formatDateTime(item.getCreateTime()));
                        recordDTO.setAttachmentList(StringUtils.isEmpty(item.getAttachment()) ? null : JSONObject.parseArray(item.getAttachment(), AttachmentLinkDto.class));
                        return recordDTO;
                    }).collect(Collectors.toList());

            return Response.ok(collect);

        } catch (Exception e) {
            log.info("获取修改记录异常", e);
            return Response.fail(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<LogisticOrderVerifyDTO> batchVerify(Request<LogisticsOrderVerifyReq> request) {
        log.info("运维工具-批量核销参数:{}", JSONObject.toJSONString(request));
        LogisticsOrderVerifyReq req = request.getData();
        AmUserInfo amUserInfo = Optional.ofNullable(req).map(BaseReqData::getAmUserInfo).orElseThrow(() -> new BusinessException("用户登陆信息为空"));
        if (StringUtils.isBlank(req.getOrderFileUrl())) {
            return Response.fail("订单文件url不能为空");
        }
        if (CollectionUtils.isEmpty(req.getVerifyList())) {
            return Response.fail("业务确认截图不能为空");
        }
        // 读取文件
        ReadPair readPair = readAndCheckOrder(req.getOrderFileUrl());
        if (!readPair.success) {
            return Response.fail(readPair.msg);
        }

        // 检查是否有错误订单号
        List<String> list = readPair.orderList;
        Map<String, LogisticsVerifyOrder> orderMap = logisticsVerifyOrderService.list(new LambdaQueryWrapper<LogisticsVerifyOrder>()
                .in(LogisticsVerifyOrder::getChildOrderId, list)
                .eq(LogisticsVerifyOrder::getIsDelete, 0))
                .stream()
                .collect(Collectors.toMap(LogisticsVerifyOrder::getChildOrderId, order -> order, (v1, v2) -> v1));
        List<String> errorOrderList = checkErrorOrder(list, orderMap);
        // 如果没有错误订单， 都是待核销， 则进行批量核销， 并保存操作日志
        if (CollectionUtils.isEmpty(errorOrderList)) {
            startBatchVerify(list, amUserInfo);
            batchSaveRecord(list, req);
            return Response.ok(new LogisticOrderVerifyDTO(1, "成功处理" + list.size() + "条"));
        }
        String res = String.join(",", errorOrderList);
        LogisticOrderVerifyDTO logisticOrderVerifyDTO = new LogisticOrderVerifyDTO(0, res);
        return Response.ok(logisticOrderVerifyDTO);
    }

    @Override
    public Response<LogisticOrderVerifyRecordDTO> verifyRecord(Request<LogisticsOrderVerifyRecordReq> request) {
        LogisticsOrderVerifyRecordReq data = request.getData();
        String childOrderId = data.getChildOrderId();
        LogisticsVerifyOrderRecord record = logisticsVerifyOrderRecordService.getOne(new LambdaQueryWrapper<LogisticsVerifyOrderRecord>()
                .eq(LogisticsVerifyOrderRecord::getChildOrderId, childOrderId), false);
        LogisticOrderVerifyRecordDTO dto = new LogisticOrderVerifyRecordDTO();
        BeanUtils.copyProperties(record, dto);
        List<LogisticOrderVerifyRecordDTO.FilePair> list = new ArrayList<>();
        String verifyPictureInfo = record.getVerifyPictureInfo();
        JSONArray jsonArray = JSONArray.parseArray(verifyPictureInfo);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            LogisticOrderVerifyRecordDTO.FilePair filePair = new LogisticOrderVerifyRecordDTO.FilePair();
            filePair.setName(jsonObject.getString("name"));
            filePair.setUrl(jsonObject.getString("url"));
            list.add(filePair);
        }
        dto.setVerifyList(list);
        return Response.ok(dto);
    }

    @Override
    public PageResponse<List<LogisticsVerifyOrderRecordsListResData>> recordList(@Valid PageRequest<LogisticsVerifyOrderListReqData> request) {
        LogisticsVerifyOrderListReqData req = request.getData();
        PageInfo pageInfo = request.getPageInfo();
        req.setVerifyStatus(LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode());
        req.setRefundStatus(LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode());
        LambdaQueryWrapper<LogisticsVerifyOrderGoods> lambdaQueryWrapper = buildLogisticsVerifyOrderGoodsListQueryWrapper(req);
        long total = logisticsVerifyOrderGoodsService.count(lambdaQueryWrapper);
        if (total == 0) {
            return PageResponse.page(Collections.EMPTY_LIST, 0L);
        }
        Page<LogisticsVerifyOrderGoods> page = logisticsVerifyOrderGoodsService.page(new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize()), lambdaQueryWrapper);
        return PageResponse.page(handelVerifyOrderRecordsListResData(page.getRecords()), total);
    }


    public LambdaQueryWrapper<LogisticsVerifyOrderGoods> buildLogisticsVerifyOrderGoodsListQueryWrapper(LogisticsVerifyOrderListReqData req) {
        LambdaQueryWrapper<LogisticsVerifyOrderGoods> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(LogisticsVerifyOrderGoods::getVerifyCode, BATCH_VERIFY_DEFAULT_CODE);
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getOrgId()), LogisticsVerifyOrderGoods::getOrgId, req.getOrgId());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getVerifyNo()), LogisticsVerifyOrderGoods::getVerifyNo, req.getVerifyNo());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getPickupAddressId()), LogisticsVerifyOrderGoods::getPickupAddressId, req.getPickupAddressId());
        lambdaQueryWrapper.like(StringUtils.isNotBlank(req.getOrderNo()), LogisticsVerifyOrderGoods::getChildOrderId, req.getOrderNo());
        lambdaQueryWrapper.in(!CollectionUtils.isEmpty(req.getOrderNos()), LogisticsVerifyOrderGoods::getChildOrderId, req.getOrderNos());
        lambdaQueryWrapper.ge(StringUtils.isNotBlank(req.getStartOrderCreateTime()), LogisticsVerifyOrderGoods::getOrderCreateTime, CommonUtils.checkDateShortToLong(req.getStartOrderCreateTime(), true));
        lambdaQueryWrapper.le(StringUtils.isNotBlank(req.getEndOrderCreateTime()), LogisticsVerifyOrderGoods::getOrderCreateTime, CommonUtils.checkDateShortToLong(req.getEndOrderCreateTime(), false));
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getSkuId()), LogisticsVerifyOrderGoods::getGoodsId, req.getSkuId());
        lambdaQueryWrapper.like(StringUtils.isNotBlank(req.getGoodsName()), LogisticsVerifyOrderGoods::getGoodsName, req.getGoodsName());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getVerifyUserAccount()), LogisticsVerifyOrderGoods::getVerifyUserAccount, req.getVerifyUserAccount());
        lambdaQueryWrapper.eq(Objects.nonNull(req.getVerifyType()), LogisticsVerifyOrderGoods::getVerifyType, req.getVerifyType());
        lambdaQueryWrapper.in(!CollectionUtils.isEmpty(req.getVerifyTypes()), LogisticsVerifyOrderGoods::getVerifyType, req.getVerifyTypes());
        lambdaQueryWrapper.eq(LogisticsVerifyOrderGoods::getVerifyStatus, LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode());
        lambdaQueryWrapper.ge(StringUtils.isNotBlank(req.getStartVerifyTime()), LogisticsVerifyOrderGoods::getVerifyTime, CommonUtils.checkDateShortToLong(req.getStartVerifyTime(), true));
        lambdaQueryWrapper.le(StringUtils.isNotBlank(req.getEndVerifyTime()), LogisticsVerifyOrderGoods::getVerifyTime, CommonUtils.checkDateShortToLong(req.getEndVerifyTime(), false));
        lambdaQueryWrapper.like(StringUtils.isNotBlank(req.getUserPhone()) && !StrUtil.containsOnly(req.getUserPhone(), '*'), LogisticsVerifyOrderGoods::getLmPhone, req.getUserPhone());
        Optional.ofNullable(req.getAmUserInfo()).ifPresent(userInfo -> lambdaQueryWrapper.in(!CollectionUtils.isEmpty(userInfo.getOrgIds()), LogisticsVerifyOrderGoods::getOrgId, userInfo.getOrgIds()));
        lambdaQueryWrapper.eq(LogisticsVerifyOrderGoods::getIsDelete, DeleteStatusEnum.NO.getCode());
        lambdaQueryWrapper.orderByDesc(LogisticsVerifyOrderGoods::getCreateTime);
        lambdaQueryWrapper.orderByDesc(LogisticsVerifyOrderGoods::getVerifyNo);
        return lambdaQueryWrapper;
    }

    /**
     * 处理自提/核销订单核销记录列表
     */
    private List<LogisticsVerifyOrderRecordsListResData> handelVerifyOrderRecordsListResData(List<LogisticsVerifyOrderGoods> dataList) {
        return ListUtils.emptyIfNull(dataList)
                .stream()
                .map(x -> {
                    LogisticsVerifyOrderRecordsListResData logisticsVerifyOrderRecordsListResData = new LogisticsVerifyOrderRecordsListResData();
                    BeanUtils.copyProperties(x, logisticsVerifyOrderRecordsListResData);
                    logisticsVerifyOrderRecordsListResData.setId(String.valueOf(x.getId()));
                    logisticsVerifyOrderRecordsListResData.setVerifyUserAccount(StrUtil.concat(true, x.getVerifyUserAccount(), Optional.ofNullable(x.getVerifyUserName()).map(y -> new StringBuilder("|").append(y).toString()).orElse("")));
                    logisticsVerifyOrderRecordsListResData.setLmNickname(DesensitizedUtil.desensitized(x.getLmNickname(), DesensitizedUtil.DesensitizedType.CHINESE_NAME));
                    logisticsVerifyOrderRecordsListResData.setVerifyStatusShow(Optional.ofNullable(LogisticsVerifyVerifyStatusEnum.fromCode(x.getVerifyStatus())).map(LogisticsVerifyVerifyStatusEnum::getDesc).orElse("-"));
                    logisticsVerifyOrderRecordsListResData.setRefundStatusShow(Optional.ofNullable(LogisticsVerifyRefundStatusEnum.fromCode(x.getRefundStatus())).map(LogisticsVerifyRefundStatusEnum::getDesc).orElse("-"));
                    return logisticsVerifyOrderRecordsListResData;
                }).collect(Collectors.toList());
    }

    private void batchSaveRecord(List<String> orderList, LogisticsOrderVerifyReq req) {
        AmUserInfo amUserInfo = req.getAmUserInfo();
        List<LogisticsVerifyOrderRecord> list = new ArrayList<>();
        for (String childOrderId : orderList) {
            LogisticsVerifyOrderRecord record = new LogisticsVerifyOrderRecord();
            record.setCreateUser(amUserInfo.getUserName());
            record.setCreateTime(new Date());
            record.setOrderFileName(req.getOrderFileName());
            record.setOrderFileUrl(req.getOrderFileUrl());
            record.setChildOrderId(childOrderId);
            record.setVerifyPictureInfo(JSONObject.toJSONString(req.getVerifyList()));
            list.add(record);
        }
        logisticsVerifyOrderRecordService.saveBatch(list);
    }

    private ReadPair readAndCheckOrder(String orderFileUrl) {
        // 读取文件
        URL url;
        try {
            url = new URL(orderFileUrl);
        } catch (MalformedURLException e) {
            log.error("读取订单上传文件失败", e);
            return new ReadPair(false, "读取订单上传文件失败");
        }
        ImportParams importParams = new ImportParams();
        importParams.setNeedVerify(true);
        importParams.setHeadRows(1);
        try (InputStream in = url.openStream()) {
            ExcelImportResult<BatchVerifyOrderDTO> importResult = ExcelImportUtil.importExcelMore(in, BatchVerifyOrderDTO.class, importParams);
            List<BatchVerifyOrderDTO> importDataList = importResult.getList().stream()
                    .filter(s -> s != null && StringUtils.isNotBlank(s.getOrderNo()))
                    .peek(data -> {
                //字符串去除前后空格
                Arrays.stream(data.getClass().getDeclaredFields()).forEach(field -> {
                    if (field.getType().toString().equals("class java.lang.String")) {
                        Object fieldValue = ReflectUtil.getFieldValue(data, field);
                        ReflectUtil.setFieldValue(data, field, String.valueOf(fieldValue).trim());
                    }
                });
            }).collect(Collectors.toList());

            if (importDataList.size() > 200) {
                return new ReadPair(false, "订单不能超过200个");
            }
            if (importDataList.size() == 0) {
                return new ReadPair(false, "暂无可运维数据");
            }
            Set<String> collect = importDataList.stream()
                    .map(BatchVerifyOrderDTO::getOrderNo)
                    .collect(Collectors.toSet());
            return new ReadPair(true, new ArrayList<>(collect));
        } catch (Exception e) {
            log.error("批量核销文件出错", e);
            return new ReadPair(false, "批量核销文件出错");
        }
    }

    static class ReadPair {
        public boolean success;
        public List<String> orderList;
        public String msg;

        public ReadPair(boolean success, List<String> orderList) {
            this.success = success;
            this.orderList = orderList;
        }

        public ReadPair(boolean success, String msg) {
            this.success = success;
            this.msg = msg;
        }
    }

    /**
     * 检查是否有不符合的订单
     *
     * @param list     导入订单数据
     * @param orderMap orderMap
     * @return List<String>
     */
    private List<String> checkErrorOrder(List<String> list, Map<String, LogisticsVerifyOrder> orderMap) {
        List<String> errorList = new ArrayList<>();
        log.info("批量核销开关:{}", onlyNoVerify);
        for (String childOrderId : list) {
            LogisticsVerifyOrder order = orderMap.get(childOrderId);
            if (order == null) {
                errorList.add(childOrderId);
                continue;
            }
            // 支持 只能是待核销状态批量核销， 待核销/核销失败/核销超时状态批量核销
            if (onlyNoVerify) {
                if (!LogisticsVerifyVerifyStatusEnum.VERIFY_NO.getCode().equals(order.getVerifyStatus())) {
                    errorList.add(childOrderId);
                }
            } else {
                if (LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode().equals(order.getVerifyStatus())) {
                    errorList.add(childOrderId);
                }
            }
        }
        log.info("错误的订单号:{}", errorList);
        return errorList;
    }

    @Transactional(rollbackFor = Exception.class)
    public void startBatchVerify(List<String> orderNos, AmUserInfo amUserInfo) {
        List<LogisticsVerifyOrderDetailResData> verifyListByOrderNos = getDetailListByOrderNos(orderNos);
        Map<String, LogisticsVerifyOrderDetailResData> verifyOrderNoAndMap = ListUtils.emptyIfNull(verifyListByOrderNos).stream().collect(Collectors.toMap(LogisticsVerifyOrderDetailResData::getChildOrderId, Function.identity(), (a, b) -> a));
        String verifyNo = logisticsNoUtil.createVerifyNo();

        // 改成批量更新核销商品信息
        LogisticsVerifyOrderGoods logisticsVerifyOrderGoods = new LogisticsVerifyOrderGoods();
        logisticsVerifyOrderGoods.setVerifyStatus(LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode());
        logisticsVerifyOrderGoods.setVerifyTime(DateUtil.date());
        logisticsVerifyOrderGoods.setUpdateTime(DateUtil.date());
        //核销类型
        logisticsVerifyOrderGoods.setVerifyType(LogisticsVerifyVerifyTypeEnum.VERIFY_ADMIN.getCode());
        //运营端核销
        Optional.ofNullable(amUserInfo).ifPresent(userInfo -> {
            logisticsVerifyOrderGoods.setVerifyOrgId(ListUtils.emptyIfNull(userInfo.getOrgIds()).stream().findFirst().orElse(null));
            logisticsVerifyOrderGoods.setVerifyOrgName(ListUtils.emptyIfNull(userInfo.getOrgNames()).stream().findFirst().orElse(null));
            logisticsVerifyOrderGoods.setVerifyUserId(userInfo.getUserId());
            logisticsVerifyOrderGoods.setVerifyUserAccount(userInfo.getUserName());
            logisticsVerifyOrderGoods.setVerifyUserName(userInfo.getRealName());
        });
        //核销单号
        logisticsVerifyOrderGoods.setVerifyNo(verifyNo);
        //核销码
        logisticsVerifyOrderGoods.setVerifyCode(BATCH_VERIFY_DEFAULT_CODE);
        logisticsVerifyOrderGoodsService.update(logisticsVerifyOrderGoods, new LambdaUpdateWrapper<LogisticsVerifyOrderGoods>()
                .in(LogisticsVerifyOrderGoods::getChildOrderId, orderNos)
                .eq(LogisticsVerifyOrderGoods::getIsDelete, DeleteStatusEnum.NO.getCode()));

        // 改成批量更新核销订单信息
        LogisticsVerifyOrder logisticsVerifyOrder = new LogisticsVerifyOrder();
        logisticsVerifyOrder.setVerifyStatus(LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode());
        logisticsVerifyOrder.setVerifyTime(DateUtil.date());
        logisticsVerifyOrder.setUpdateTime(DateUtil.date());
        logisticsVerifyOrder.setVerifyType(LogisticsVerifyVerifyTypeEnum.VERIFY_ADMIN.getCode());
        Optional.ofNullable(amUserInfo).ifPresent(userInfo -> {
            logisticsVerifyOrder.setVerifyOrgId(ListUtils.emptyIfNull(userInfo.getOrgIds()).stream().findFirst().orElse(null));
            logisticsVerifyOrder.setVerifyOrgName(ListUtils.emptyIfNull(userInfo.getOrgNames()).stream().findFirst().orElse(null));
            logisticsVerifyOrder.setVerifyUserId(userInfo.getUserId());
            logisticsVerifyOrder.setVerifyUserAccount(userInfo.getUserName());
            logisticsVerifyOrder.setVerifyUserName(userInfo.getRealName());
        });
        //核销单号
        logisticsVerifyOrder.setVerifyNo(verifyNo);
        //核销码
        logisticsVerifyOrder.setVerifyCode(BATCH_VERIFY_DEFAULT_CODE);
        logisticsVerifyOrderService.update(logisticsVerifyOrder, new LambdaUpdateWrapper<LogisticsVerifyOrder>()
                .in(LogisticsVerifyOrder::getChildOrderId, orderNos)
                .eq(LogisticsVerifyOrder::getIsDelete, DeleteStatusEnum.NO.getCode()));

        // 发送mq改为批量查询
        List<OrderStatusChangePushDTO> list = new ArrayList<>();
        orderNos.forEach(item -> {
            //推送自提/核销订单状态变更
            Optional.ofNullable(verifyOrderNoAndMap.get(item)).ifPresent(orderInfo -> {
                //发送自提订单待自提消息通知
                OrderStatusChangePushDTO orderStatusChangePushDTO = new OrderStatusChangePushDTO();
                orderStatusChangePushDTO.setChildOrderId(orderInfo.getChildOrderId());
                orderStatusChangePushDTO.setLogisticsId(Long.parseLong(orderInfo.getId()));
                orderStatusChangePushDTO.setLogisticsType(LogisticsStatusChangeTypeEnum.FORWARD.getCode());
                if (GoodsTypeEnum.GOODS.getCode().equals(orderInfo.getGoodsType())) {
                    //商品类型待自提
                    orderStatusChangePushDTO.setOrderStatus(VerifyOrderStatusEnum.IS_PICKUP.getCode());
                } else {
                    //卡券类型待核销
                    orderStatusChangePushDTO.setOrderStatus(VerifyOrderStatusEnum.IS_VERIFY.getCode());
                }
                orderStatusChangePushDTO.setStatusTime(LocalDateTimeUtil.now());
                list.add(orderStatusChangePushDTO);
            });
        });
        logisticsStatusChangeProducer.batchSend(list);
    }

    private List<LogisticsVerifyOrderDetailResData> getDetailListByOrderNos(List<String> orderNos) {
        LogisticsVerifyOrderDetailReqData logisticsVerifyOrderDetailReqData = new LogisticsVerifyOrderDetailReqData();
        logisticsVerifyOrderDetailReqData.setOrderNos(orderNos);
        return logisticsCommonVerifyOrderService.detailList(logisticsVerifyOrderDetailReqData).getData();
    }

    @Override
    public Response<Boolean> changeDeliverNo(@Valid Request<LogisticsDeliverToolReq> request) {

        AmUserInfo amUserInfo = Optional.ofNullable(request).map(Request::getData).map(BaseReqData::getAmUserInfo).orElseThrow(() -> new BusinessException("用户登陆信息为空"));
        LogisticsDeliverToolReq reqData = request.getData();
        try {
            if (StringUtil.isAnyBlank(reqData.getChildOrderId(), reqData.getLogisticsDeliveryId())) {
                throw new BusinessException("参数不可为空");
            }
            //1.查询订单、运单信息
            LogisticsOrder logisticsOrder = logisticsOrderService.getOne(Wrappers.<LogisticsOrder>lambdaQuery().eq(StringUtils.isNotBlank(reqData.getChildOrderId()), LogisticsOrder::getChildOrderId, reqData.getChildOrderId()));
            if (null == logisticsOrder) {
                throw new BusinessException("订单不存在");
            }
            LogisticsDelivery logisticsDelivery = logisticsDeliveryService.getOne(Wrappers.<LogisticsDelivery>lambdaQuery().eq(LogisticsDelivery::getId, reqData.getLogisticsDeliveryId()));
            if (null == logisticsDelivery) {
                throw new BusinessException("运单不存在");
            }
            //2.更新运单、快递公司编码
            updateDeliverInfo(amUserInfo, reqData, logisticsOrder, logisticsDelivery);

            //订阅和拉取物流轨迹
            ThreadUtil.execAsync(() -> subAndGetLogisticsPath(reqData));

            //5.记录操作日志
            insertRecord(amUserInfo, reqData, logisticsOrder, logisticsDelivery);

        } catch (Exception e) {
            log.info("修改运单号异常", e);
            return Response.fail(e.getMessage());
        }
        return Response.ok(true);
    }

    private void subAndGetLogisticsPath(LogisticsDeliverToolReq reqData) {
        try {
            //3.订阅物流轨迹
            SubLogisticsPathReqData subLogisticsPathReqData = new SubLogisticsPathReqData();
            subLogisticsPathReqData.setCompanyCode(reqData.getNewLogisticsCompanyCode());
            subLogisticsPathReqData.setDeliverNo(reqData.getNewDeliverNo());
            kuaiDi100Service.subLogisticsPath(subLogisticsPathReqData);

            //4.拉取物流轨迹
            DeliveryPathReq deliveryPathReq = new DeliveryPathReq();
            deliveryPathReq.setDeliveryNo(reqData.getNewDeliverNo());
            deliveryPathReq.setChildOrderId(reqData.getChildOrderId());
            logisticsCommonOrderService.getPath(deliveryPathReq);
        } catch (Exception e) {
            log.info("物流轨迹异常", e);
        }
    }

    private void insertRecord(AmUserInfo amUserInfo, LogisticsDeliverToolReq reqData, LogisticsOrder logisticsOrder, LogisticsDelivery logisticsDelivery) {
        LogisticsDeliveryToolRecord toolRecord = new LogisticsDeliveryToolRecord();
        toolRecord.setId(IdUtil.getSnowflake().nextId());
        toolRecord.setChildOrderId(Long.valueOf(reqData.getChildOrderId()));
        toolRecord.setShopId(logisticsOrder.getShopId());
        toolRecord.setShopName(logisticsOrder.getShopName());
        toolRecord.setOldCompanyCode(logisticsDelivery.getCompanyCode());
        toolRecord.setNewCompanyCode(reqData.getNewLogisticsCompanyCode());
        toolRecord.setOldLogisticsType(logisticsDelivery.getLogisticsType());
        toolRecord.setNewLogisticsType(reqData.getLogisticsType());
        toolRecord.setOldDeliveryNo(logisticsDelivery.getDeliveryNo());
        toolRecord.setNewDeliveryNo(reqData.getNewDeliverNo());
        toolRecord.setRemark(reqData.getRemark());
        if (CollectionUtil.isNotEmpty(reqData.getAttachmentList())) {
            toolRecord.setAttachment(JSON.toJSONString(reqData.getAttachmentList()));
        }
        toolRecord.setCreateTime(new Date());
        toolRecord.setCreatorOa(amUserInfo.getUserName());
        toolRecord.setCreatorName(amUserInfo.getRealName());
        toolRecord.setUpdateOa(amUserInfo.getUserName());
        toolRecord.setUpdateName(amUserInfo.getRealName());
        logisticsDeliveryToolRecordService.save(toolRecord);
    }

    private void updateDeliverInfo(AmUserInfo amUserInfo, LogisticsDeliverToolReq reqData, LogisticsOrder logisticsOrder, LogisticsDelivery logisticsDelivery) {
        log.info("原有运单信息：{}", JsonUtil.toJson(logisticsDelivery));

        LogisticsDelivery updateDeliveryEntity = new LogisticsDelivery();
        if (StringUtils.isNotBlank(reqData.getNewDeliverNo()) && !StringUtil.equals(reqData.getNewDeliverNo(), logisticsDelivery.getDeliveryNo())) {
            updateDeliveryEntity.setDeliveryNo(reqData.getNewDeliverNo());
        }
        if (StringUtils.isNotBlank(reqData.getNewLogisticsCompanyCode()) && !StringUtil.equals(reqData.getNewLogisticsCompanyCode(), logisticsDelivery.getCompanyCode())) {
            updateDeliveryEntity.setCompanyCode(reqData.getNewLogisticsCompanyCode());
        }
        if (null != reqData.getLogisticsType() && reqData.getLogisticsType() != logisticsDelivery.getLogisticsType()) {
            updateDeliveryEntity.setLogisticsType(reqData.getLogisticsType());
        }
        updateDeliveryEntity.setRemark(reqData.getRemark());
        updateDeliveryEntity.setUpdateAccount(amUserInfo.getRealName());
        updateDeliveryEntity.setUpdateName(amUserInfo.getUserName());
        updateDeliveryEntity.setUpdateTime(DateUtil.date());
        log.info("更新实体：{}", JsonUtil.toJson(updateDeliveryEntity));
        logisticsDeliveryService.update(updateDeliveryEntity, Wrappers.<LogisticsDelivery>lambdaUpdate()
                .eq(LogisticsDelivery::getLogisticsOrderId, logisticsOrder.getId())
                .eq(LogisticsDelivery::getId, logisticsDelivery.getId()));
    }

    /*
      处理签收自动发送物流状态变更消息
     */
    private void handelSendLogisticsChangeMq(List<Object> lists) {
        try {
            log.info("物流运维工具，开始异步处理物流状态");
            List<LogisticsDelivery> signDeliverLists = ListUtils.emptyIfNull(lists)
                    .stream()
                    .map(x -> BeanUtil.toBean(x, LogisticsDelivery.class))
                    .filter(x -> Objects.nonNull(x.getId()) && Objects.nonNull(x.getSignTime()) && Objects.nonNull(x.getLogisticsStatus()) && x.getLogisticsStatus().intValue() == DeliveryLogisticsStatusEnum.SIGNED.getCode())
                    .collect(Collectors.toList());
            log.info("物流运维工具，已签收状态并且签收时间不为空的数据条数 = {}", signDeliverLists.size());
            if (CollectionUtils.isEmpty(signDeliverLists)) {
                return;
            }
            List<Long> logisticsDeliverIds = signDeliverLists.stream().map(LogisticsDelivery::getId).distinct().collect(Collectors.toList());
            log.info("物流运维工具，运单id集合 = {}", JSON.toJSONString(logisticsDeliverIds));
            Map<Long, Long> logisticsDeliverIdAndLogisticsOrderIdMap = ListUtils.emptyIfNull(logisticsDeliveryService.list(Wrappers.<LogisticsDelivery>lambdaQuery().in(LogisticsDelivery::getId, logisticsDeliverIds)))
                    .stream()
                    .collect(Collectors.toMap(LogisticsDelivery::getId, LogisticsDelivery::getLogisticsOrderId, (a, b) -> a));
            log.info("物流运维工具，运单id和物流订单id映射 = {}", JSON.toJSONString(logisticsDeliverIdAndLogisticsOrderIdMap));
            if (CollectionUtils.isEmpty(logisticsDeliverIdAndLogisticsOrderIdMap)) {
                return;
            }
            List<Long> logisticsOrderIds = logisticsDeliverIdAndLogisticsOrderIdMap.values().stream().collect(Collectors.toList());
            log.info("物流运维工具，物流订单id集合 = {}", JSON.toJSONString(logisticsOrderIds));
            Map<Long, String> logisticsOrderIdAndChildOrderIdMap = ListUtils.emptyIfNull(logisticsOrderService.list(Wrappers.<LogisticsOrder>lambdaQuery().in(LogisticsOrder::getId, logisticsOrderIds)))
                    .stream()
                    .collect(Collectors.toMap(LogisticsOrder::getId, LogisticsOrder::getChildOrderId, (a, b) -> a));
            log.info("物流运维工具，物流订单id和订单编号映射 = {}", JSON.toJSONString(logisticsOrderIdAndChildOrderIdMap));
            List<OrderStatusChangePushDTO> logisticsStatusChangeDtos = ListUtils.emptyIfNull(signDeliverLists)
                    .stream()
                    .filter(x -> logisticsDeliverIdAndLogisticsOrderIdMap.containsKey(x.getId()) && logisticsOrderIdAndChildOrderIdMap.containsKey(logisticsDeliverIdAndLogisticsOrderIdMap.get(x.getId())))
                    .map(x -> {
                        OrderStatusChangePushDTO orderStatusChangePushDto = new OrderStatusChangePushDTO();
                        orderStatusChangePushDto.setChildOrderId(logisticsOrderIdAndChildOrderIdMap.get(logisticsDeliverIdAndLogisticsOrderIdMap.get(x.getId())));
                        orderStatusChangePushDto.setLogisticsId(logisticsDeliverIdAndLogisticsOrderIdMap.get(x.getId()));
                        //正向
                        orderStatusChangePushDto.setLogisticsType(1);
                        orderStatusChangePushDto.setOrderStatus(GoodsOrderStatusEnum.SIGNED.getCode());
                        orderStatusChangePushDto.setStatusTime(LocalDateTimeUtil.of(x.getSignTime()));
                        return orderStatusChangePushDto;
                    }).collect(Collectors.toList());
            log.info("物流运维工具，发送物流签收状态变更数据dto = {}", JSON.toJSONString(logisticsStatusChangeDtos));
            if (CollectionUtils.isEmpty(logisticsStatusChangeDtos)) {
                return;
            }
            logisticsStatusChangeDtos.forEach(item -> {
                logisticsStatusChangeProducer.send(item);
                log.info("物流运维工具，物流签收状态变更mq发送成功 = {}", JSON.toJSONString(item));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public PageResponse<List<LogisticOrderToolsInfoDTO>> list(@Valid PageRequest<LogisticOrderToolsReq> request) {
        LogisticOrderToolsReq reqData = Optional.ofNullable(request).map(PageRequest::getData).orElseThrow(() -> new BusinessException("参数错误"));
        Pair<Boolean, LambdaQueryWrapper> lambdaQueryPair = buildQueryWrapper(reqData);
        if (!lambdaQueryPair.getKey()) {
            return PageResponse.page(Collections.emptyList(), 0L);
        }
        Integer total = logisticsOrderGoodsService.count(lambdaQueryPair.getValue());
        if (total == 0) {
            return PageResponse.page(Collections.emptyList(), 0L);
        }
        Page<LogisticsOrderGoods> pageQuery = new Page<>(request.getPageInfo().getPageNum(), request.getPageInfo().getPageSize());
        Page<LogisticsOrderGoods> page = logisticsOrderGoodsService.page(pageQuery, lambdaQueryPair.getValue());
        List<LogisticsOrderGoods> resultList = Optional.ofNullable(page).map(Page::getRecords).orElse(null);
        return PageResponse.page(handelResultDTO(resultList, reqData), total.longValue());
    }

    /**
     * 数据处理
     *
     * @param resultList
     * @return
     */
    private List<LogisticOrderToolsInfoDTO> handelResultDTO(List<LogisticsOrderGoods> resultList, LogisticOrderToolsReq reqData) {
        //物流订单信息
        List<Long> logisticOrderIds = ListUtils.emptyIfNull(resultList).stream().map(LogisticsOrderGoods::getLogisticsOrderId).distinct().collect(Collectors.toList());
        Map<Long, LogisticsOrder> logisticOrderIdAndMap = CollectionUtils.isEmpty(logisticOrderIds) ? Collections.emptyMap() : ListUtils.emptyIfNull(logisticsOrderService.list(Wrappers.<LogisticsOrder>lambdaQuery().in(LogisticsOrder::getId, logisticOrderIds)))
                .stream().collect(Collectors.toMap(LogisticsOrder::getId, Function.identity(), (a, b) -> a));
        //物流运单信息
        Map<Long, List<LogisticsDelivery>> logisticOrderIdAndDeliverInfoMap = CollectionUtils.isEmpty(logisticOrderIds) ? Collections.emptyMap() : ListUtils.emptyIfNull(logisticsDeliveryService.list(Wrappers.<LogisticsDelivery>lambdaQuery().in(LogisticsDelivery::getLogisticsOrderId, logisticOrderIds)))
                .stream().collect(Collectors.groupingBy(LogisticsDelivery::getLogisticsOrderId, Collectors.collectingAndThen(Collectors.toList(), list -> {
                    return ListUtils.emptyIfNull(list).stream().filter(x -> {
                        boolean flag1 = true, flag2 = true, flag3 = true, flag4 = true, flag5 = true, flag6 = true;
                        if (StringUtils.isNotBlank(reqData.getDeliverNo())) {
                            flag1 = x.getDeliveryNo().equals(reqData.getDeliverNo());
                        }
                        if (StringUtils.isNotBlank(reqData.getLogisticStatuss())) {
                            flag2 = reqData.getLogisticStatuss().equals(x.getLogisticsStatus().toString());
                        }
                        if (StringUtils.isNotBlank(reqData.getLogisticsDeliveryId())) {
                            flag3 = reqData.getLogisticsDeliveryId().equals(String.valueOf(x.getId()));
                        }
                        if (Objects.nonNull(reqData.getIsDelete())) {
                            flag4 = reqData.getIsDelete() == x.getDeleteStatus().intValue();
                        }
                        if (Objects.nonNull(reqData.getUpdateFeeStatus())) {
                            flag5 = reqData.getUpdateFeeStatus().equals(x.getUpdateFeeStatus());
                        }
                        if (Objects.nonNull(reqData.getIsCancel())) {
                            flag6 = reqData.getIsCancel().equals(x.getIfCancel());
                        }
                        return flag1 && flag2 && flag3 && flag4 && flag5 && flag6;
                    }).collect(Collectors.toList());
                })));
        //物流运费信息
        List<String> childOrderIds = logisticOrderIdAndMap.values().stream().map(LogisticsOrder::getChildOrderId).distinct().collect(Collectors.toList());
        Map<String, List<LogisticsFee>> childOrderIdAndFeeInfoMap = CollectionUtils.isEmpty(childOrderIds) ? Collections.emptyMap() : ListUtils.emptyIfNull(logisticsFeeService.list(Wrappers.<LogisticsFee>lambdaQuery().in(LogisticsFee::getChildOrderId, childOrderIds)))
                .stream().collect(Collectors.groupingBy(LogisticsFee::getChildOrderId, Collectors.collectingAndThen(Collectors.toList(), list -> {
                    return ListUtils.emptyIfNull(list).stream().filter(x -> {
                        boolean flag1 = true, flag2 = true, flag3 = true, flag4 = true;
                        if (StringUtils.isNotBlank(reqData.getDeliverNo())) {
                            flag1 = x.getDeliveryNo().equals(reqData.getDeliverNo());
                        }
                        if (StringUtils.isNotBlank(reqData.getLogisticStatuss())) {
                            flag1 = reqData.getLogisticStatuss().equals(x.getLogisticsStatus().toString());
                        }
                        if (StringUtils.isNotBlank(reqData.getLogisticsFeeId())) {
                            flag3 = reqData.getLogisticsFeeId().equals(String.valueOf(x.getId()));
                        }
                        if (Objects.nonNull(reqData.getIsDelete())) {
                            flag4 = reqData.getIsDelete() == x.getDeleteStatus().intValue();
                        }
                        return flag1 && flag2 && flag3 && flag4;
                    }).collect(Collectors.toList());
                })));
        return ListUtils.emptyIfNull(resultList).stream().map(info -> {
            LogisticOrderToolsInfoDTO dto = new LogisticOrderToolsInfoDTO();
            BeanUtils.copyProperties(info, dto);
            //处理long转string
            dto.setLogisticsOrderId(Optional.ofNullable(info.getLogisticsOrderId()).map(String::valueOf).orElse(""));
            dto.setLogisticsDeliveryId(Optional.ofNullable(info.getLogisticsDeliveryId()).map(String::valueOf).orElse(""));
            //处理订单商品信息
            Optional.ofNullable(logisticOrderIdAndMap.get(info.getLogisticsOrderId())).ifPresent(orderInfo -> {
                LogisticOrderInfoDTO orderInfoDTO = new LogisticOrderInfoDTO();
                BeanUtils.copyProperties(orderInfo, orderInfoDTO);
                Optional.ofNullable(orderInfoDTO.getGoodsOrderStatus()).ifPresent(orderStatus -> {
                    orderInfoDTO.setGoodsOrderStatusShow(Arrays.stream(GoodsOrderStatusEnum.values()).filter(x -> x.getCode().equals(orderStatus)).map(x -> x.getDesc()).findFirst().orElse(null));
                });
                orderInfoDTO.setIfRefundShow(Optional.ofNullable(orderInfoDTO.getIfRefund()).map(x -> x ? "是" : "否").orElse("否"));
                dto.setOrderInfo(orderInfoDTO);
            });
            //处理运单信息
            Optional.ofNullable(logisticOrderIdAndDeliverInfoMap.get(info.getLogisticsOrderId())).ifPresent(deliverInfos -> {
                dto.setDeliverInfos(deliverInfos.stream()
                        .map(deliverInfo -> {
                            LogisticDeliverInfoDTO deliverInfoDTO = new LogisticDeliverInfoDTO();
                            BeanUtils.copyProperties(deliverInfo, deliverInfoDTO);
                            Optional.ofNullable(deliverInfoDTO.getUpdateFeeStatus()).ifPresent(updateFeeStatus -> {
                                deliverInfoDTO.setUpdateFeeStatusShow(Arrays.stream(UpdateFeeStatusEnum.values()).filter(x -> x.getCode() == updateFeeStatus.intValue()).map(x -> x.getDesc()).findFirst().orElse(null));
                            });
                            Optional.ofNullable(deliverInfoDTO.getLogisticsStatus()).ifPresent(logisticStatus -> {
                                deliverInfoDTO.setLogisticsStatusShow(Arrays.stream(DeliveryLogisticsStatusEnum.values()).filter(x -> x.getCode() == logisticStatus.intValue()).map(x -> x.getDesc()).findFirst().orElse(null));
                            });
                            deliverInfoDTO.setIfCancelShow(Optional.ofNullable(deliverInfoDTO.getIfCancel()).map(x -> x ? "是" : "否").orElse("否"));
                            deliverInfoDTO.setLogisticsTypeShow(Optional.ofNullable(deliverInfoDTO.getLogisticsType()).map(Byte::intValue).map(LogisticsTypeEnum::fromCode).map(LogisticsTypeEnum::getDesc).orElse(null));
                            return deliverInfoDTO;
                        }).collect(Collectors.toList()));
            });
            //处理运费信息
            Optional.ofNullable(dto.getOrderInfo()).map(LogisticOrderInfoDTO::getChildOrderId).map(childOrderId -> childOrderIdAndFeeInfoMap.get(childOrderId)).ifPresent(feeInfos -> {
                dto.setLogisticFeeInfos(feeInfos.stream().map(feeInfo -> {
                    LogisticFeeInfoDTO feeInfoDTO = new LogisticFeeInfoDTO();
                    BeanUtils.copyProperties(feeInfo, feeInfoDTO);
                    return feeInfoDTO;
                }).collect(Collectors.toList()));
            });
            //设置列表对应的table信息
            dto.setTableInfos(Arrays.stream(TableModuleEnum.values())
                    .map(e -> {
                        TableInfoDTO tableInfoDTO = new TableInfoDTO();
                        if (e.getTableName().equals(TableModuleEnum.MODULE_LOGISTIC_ORDER_GOODS.getTableName())) {
                            //商品
                            tableInfoDTO.setId(Arrays.asList(String.valueOf(dto.getId())));
                        } else if (e.getTableName().equals(TableModuleEnum.MODULE_LOGISTIC_ORDER.getTableName())) {
                            Optional.ofNullable(dto.getOrderInfo()).ifPresent(orderInfo -> {
                                //订单
                                tableInfoDTO.setId(Arrays.asList(String.valueOf(orderInfo.getId())));
                            });
                        } else if (e.getTableName().equals(TableModuleEnum.MODULE_LOGISTIC_DELIVER.getTableName())) {
                            Optional.ofNullable(dto.getDeliverInfos()).ifPresent(deliverInfos -> {
                                //运单
                                tableInfoDTO.setId(deliverInfos.stream().map(LogisticDeliverInfoDTO::getId).map(String::valueOf).distinct().collect(Collectors.toList()));
                            });
                        } else if (e.getTableName().equals(TableModuleEnum.MODULE_LOGISTIC_FEE.getTableName())) {
                            Optional.ofNullable(dto.getLogisticFeeInfos()).ifPresent(feeInfos -> {
                                //运费
                                tableInfoDTO.setId(feeInfos.stream().map(LogisticFeeInfoDTO::getId).map(String::valueOf).distinct().collect(Collectors.toList()));
                            });
                        }
                        tableInfoDTO.setModuleName(e.getModuleName());
                        tableInfoDTO.setFields(ToolsUtil.getTableFieldsFrom(e.getModuleName()));
                        return tableInfoDTO;
                    }).filter(x -> !CollectionUtils.isEmpty(x.getId())).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
    }

    private Pair<Boolean, LambdaQueryWrapper> buildQueryWrapper(LogisticOrderToolsReq reqData) {
        LambdaQueryWrapper<LogisticsOrderGoods> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Objects.nonNull(reqData.getIsDelete()), LogisticsOrderGoods::getDeleteStatus, reqData.getIsDelete());
        if (StringUtils.isNotBlank(reqData.getOrderId()) || StringUtils.isNoneBlank(reqData.getChildOrderId()) || StringUtils.isNotBlank(reqData.getOrderStatuss())) {
            List<LogisticsOrder> orderList = logisticsOrderService.list(Wrappers.<LogisticsOrder>lambdaQuery()
                    .eq(StringUtils.isNotBlank(reqData.getOrderId()), LogisticsOrder::getOrderId, reqData.getOrderId())
                    .eq(StringUtils.isNotBlank(reqData.getChildOrderId()), LogisticsOrder::getChildOrderId, reqData.getChildOrderId())
                    .eq(StringUtils.isNotBlank(reqData.getOrderStatuss()), LogisticsOrder::getGoodsOrderStatus, reqData.getOrderStatuss()));
            if (CollectionUtils.isEmpty(orderList)) {
                return Pair.of(false, lambdaQueryWrapper);
            }
            List<Long> logisticOrderIds = orderList.stream().map(LogisticsOrder::getId).distinct().collect(Collectors.toList());
            lambdaQueryWrapper.in(LogisticsOrderGoods::getLogisticsOrderId, logisticOrderIds);
        }
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(reqData.getSkuId()), LogisticsOrderGoods::getSkuId, reqData.getSkuId());
        lambdaQueryWrapper.like(StringUtils.isNotBlank(reqData.getGoodsName()), LogisticsOrderGoods::getGoodsName, reqData.getGoodsName());
        lambdaQueryWrapper.orderByDesc(LogisticsOrderGoods::getUpdateTime);
        return Pair.of(true, lambdaQueryWrapper);
    }
}

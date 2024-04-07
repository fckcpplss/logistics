package com.longfor.c10.lzyx.logistics.core.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.download.client.client.DownloadTaskClient;
import com.longfor.c10.lzyx.download.client.entity.BizExportParamEntity;
import com.longfor.c10.lzyx.download.client.param.DownloadInfoUpdateParam;
import com.longfor.c10.lzyx.download.entity.enums.TaskStatusEnum;
import com.longfor.c10.lzyx.download.entity.enums.TaskTypeEnum;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonFeeService;
import com.longfor.c10.lzyx.logistics.core.util.DesensitizedUtils;
import com.longfor.c10.lzyx.logistics.core.util.LogisticsUtil;
import com.longfor.c10.lzyx.logistics.dao.mapper.LogisticsFeeMapper;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.AmUserInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.LogisticsCompanyInfoExportVO;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDeliveryCompany;
import com.longfor.c10.lzyx.logistics.entity.enums.DeliveryLogisticsStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.SellTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.dto.FeeListReq;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliveryNoSendListVO;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.FeeVO;
import com.longfor.c10.starter.aliyun.oss.provider.AliyunOssProvider;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 物流运费业务接口实现类
 * @author zhaoyl
 * @date 2022/2/21 上午9:49
 * @since 1.0
 */
@Slf4j
@Service
public class LogisticsCommonFeeServiceImpl implements ILogisticsCommonFeeService {
    @Autowired
    private LogisticsFeeMapper logisticsFeeMapper;

    @Autowired
    private DownloadTaskClient downloadTaskClient;

    @Autowired
    private AliyunOssProvider aliyunOssProvider;

    @Override
    public PageResponse<List<FeeVO>> getLogisticsFeeList(PageInfo pageInfo, FeeListReq logisticsFeeListReq) {
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
            if(!SellTypeEnum.WHOLESALE.getCode().equals(item.getSellType())){
                item.setOrderDesc(null);
            }
        });
    }
    private Response<List<FeeVO>> getExportData(FeeListReq req) {
        //参数处理
        Optional.ofNullable(req.getSellType()).map(SellTypeEnum::fromCode).map(SellTypeEnum::getChannels).map(Arrays::asList).ifPresent(list -> {
            req.setBizChannelCodes(list);
        });
        int pageQuerySize = 1000;
        List<FeeVO> feeVOS = new ArrayList<>();
        //计算总条数
        IPage<FeeVO> pageResult = logisticsFeeMapper.getLogisticsFeeList(new Page<>(1,pageQuerySize),req);
        long total = pageResult.getTotal();
        if(total == 0){
            return Response.ok(feeVOS);
        }
        feeVOS.addAll(pageResult.getRecords());
        //计算查询次数 按照每1000条执行一次
        long time = (total + pageQuerySize - 1) / pageQuerySize;
        if(total >0 && time > 1){
            feeVOS.addAll(IntStream.range(2, (int) time + 1).mapToObj(index -> {
                return Optional.ofNullable(logisticsFeeMapper.getLogisticsFeeList(new Page<>(index,pageQuerySize), req)).map(IPage::getRecords).orElse(null);
            }).filter(x -> CollectionUtils.isNotEmpty(x))
                    .flatMap(list -> {
                        return list.stream();
                    }).collect(Collectors.toList()));
        }
        //数据处理
        handelFeeDataList(feeVOS);
        return Response.ok(feeVOS);
    }

    @Override
    public Response<Boolean> export(BizExportParamEntity exportParam) {
        InputStream inputStream = null;
        ExcelWriter excelWriter = null;
        String downloadKey = null;
        String fileName="";
        long total = 0;
        int taskStatus = TaskStatusEnum.DEAL_SUCCESS.getValue();
        String taskResult="下载成功";
        try {
            log.info("费用列表导出，接受到下载参数：{}", JSON.toJSONString(exportParam));
            FeeListReq reqData = Optional.ofNullable(exportParam).map(x -> JSONObject.parseObject(x.getExportParam(),FeeListReq.class)).orElse(new FeeListReq());
            MultipartFileInfo info = new MultipartFileInfo();
            excelWriter = EasyExcel.write(info.getOutputStream(), DeliveryNoSendListVO.class).build();
            fileName = TaskTypeEnum.LOGISTICS_ADMIN_DELIVERY_NO_SEND_EXPORT.getDesc() + "-" + DateUtil.format(DateUtil.date(),"yyyyMMddHHmmss") + ".xlsx";
            //组织id处理
            if(StringUtils.isNotBlank(reqData.getOrgId())){
                reqData.setAmUserInfo(new AmUserInfo(){{
                    setOrgIds(Arrays.stream(reqData.getOrgId().split(",")).collect(Collectors.toList()));
                }});
                reqData.setOrgId(null);
            }
            log.info("费用列表导出，转换后的参数：{}", JSON.toJSONString(reqData));
            List<FeeVO> feeVOS = getExportData(reqData).getData();
            total = feeVOS.size();
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            writeSheet.setSheetName(TaskTypeEnum.LOGISTICS_ADMIN_DELIVERY_NO_SEND_EXPORT.getDesc());
            excelWriter.write(feeVOS, writeSheet);
            excelWriter.finish();
            log.info("费用列表导出,共导出:{}条记录", total);
            inputStream = new ByteArrayInputStream(info.getFileBytes());
            downloadKey = aliyunOssProvider.upload(inputStream, fileName);
            log.info("费用列表导出，上传阿里云key：{}", JSON.toJSONString(downloadKey));
            return Response.ok(true);
        }catch (Exception ex){
            ex.printStackTrace();
            taskResult = ExceptionUtil.stacktraceToString(ex);
            taskStatus = TaskStatusEnum.DEAL_FAIL.getValue();
        }finally {
            Request<DownloadInfoUpdateParam> downloadInfoUpdateParamRequest = new Request();
            DownloadInfoUpdateParam downloadInfoUpdateParam = new DownloadInfoUpdateParam();
            downloadInfoUpdateParam.setTaskId(exportParam.getTaskId());
            downloadInfoUpdateParam.setTaskStatus(taskStatus);
            downloadInfoUpdateParam.setTaskResult(taskResult);
            if(StringUtils.isNotBlank(downloadKey)){
                downloadInfoUpdateParam.setDownloadKey(downloadKey);
            }
            downloadInfoUpdateParam.setDownloadTotalNum(total);
            downloadInfoUpdateParam.setFieldCnName(getAnnoationValus(FeeVO.class));
            downloadInfoUpdateParamRequest.setData(downloadInfoUpdateParam);
            downloadTaskClient.downloadUpdate(downloadInfoUpdateParamRequest);
        }
        return Response.ok(true);
    }

    @Override
    public Response<String> feeExport(FeeListReq req) {
        InputStream inputStream = null;
        try {
            String fileName = new StringBuilder("运费明细-").append(DateUtil.format(DateUtil.date(),"yyyyMMddHHmmss")).append(".xlsx").toString();
            ExportParams exportParams = new ExportParams(null, "运费明细");
            List<FeeVO> exportData = Optional.ofNullable(getExportData(req)).map(Response::getData).orElse(Collections.emptyList());
            exportParams.setType(ExcelType.XSSF);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, FeeVO.class, exportData);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] barray = bos.toByteArray();
            inputStream = new ByteArrayInputStream(barray);
            String downloadUrl = aliyunOssProvider.getDownloadUrlNoKey(inputStream, fileName);
            log.info("运费明细导出,生成下载地址 = {}",downloadUrl);
            return Response.ok(downloadUrl);
        }catch (Exception ex){
            ex.printStackTrace();
            log.error("运费明细，系统异常",ex);
            return Response.fail("系统异常");
        }finally {
            if(Objects.nonNull(inputStream)){
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 获取导出文档字段
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
}

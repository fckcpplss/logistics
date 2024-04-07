package com.longfor.c10.lzyx.logistics.core.service.merchant.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.longfor.c10.lzyx.download.entity.enums.TaskTypeEnum;
import com.longfor.c10.lzyx.logistics.core.service.merchant.ILogisticsMerchantExportService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryCompanyService;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.LogisticsCompanyInfoExportVO;
import com.longfor.c10.lzyx.logistics.entity.dto.merchant.PendingImportDTO;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDeliveryCompany;
import com.longfor.c10.starter.aliyun.oss.provider.AliyunOssProvider;
import com.longfor.c2.starter.data.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;
import org.bouncycastle.asn1.cmc.PendInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 物流订单service实现类
 * @author zhaoyl
 * @date 2022/1/19 上午11:52
 * @since 1.0
 */
@Slf4j
@Service
public class LogisticsMerchantExportServiceImpl implements ILogisticsMerchantExportService {

    @Resource
    private ILogisticsDeliveryCompanyService logisticsDeliveryCompanyService;

    @Resource
    private AliyunOssProvider aliyunOssProvider;

    @Override
    public Response<String> noSendImportTemplate() {
        InputStream inputStream = null;
        try {
            String fileName = "待发货列表导入模版.xlsx";
            ExportParams exportParams = new ExportParams(null, "待发货列表");
            exportParams.setType(ExcelType.XSSF);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, handelExportModelClass(PendingImportDTO.class,true), Collections.emptyList());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] barray = bos.toByteArray();
            inputStream = new ByteArrayInputStream(barray);
            String downloadUrl = aliyunOssProvider.getDownloadUrlNoKey(inputStream, fileName);
            log.info("待发货列表导入模版,生成下载地址 = {}",downloadUrl);
            return Response.ok(downloadUrl);
        }catch (Exception ex){
            ex.printStackTrace();
            log.error("待发货模版导出，系统异常",ex);
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

    @Override
    public Response<String> companyInfo() {
        InputStream inputStream = null;
        try {
            String fileName = "物流公司编码.xlsx";
            ExportParams exportParams = new ExportParams(null, "物流公司编码");
            List<LogisticsCompanyInfoExportVO> logisticsCompanyInfoExportVOS = ListUtils.emptyIfNull(logisticsDeliveryCompanyService.list(Wrappers.<LogisticsDeliveryCompany>lambdaQuery()
                    .eq(LogisticsDeliveryCompany::getStatus, 1))
                    .stream()
                    .map(info -> new LogisticsCompanyInfoExportVO(info.getCompanyCode(), info.getCompanyName()))
                    .collect(Collectors.toList()));
            exportParams.setType(ExcelType.XSSF);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, LogisticsCompanyInfoExportVO.class, logisticsCompanyInfoExportVOS);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] barray = bos.toByteArray();
            inputStream = new ByteArrayInputStream(barray);
            String downloadUrl = aliyunOssProvider.getDownloadUrlNoKey(inputStream, fileName);
            log.info("物流公司编码导出,生成下载地址 = {}",downloadUrl);
            return Response.ok(downloadUrl);
        }catch (Exception ex){
            ex.printStackTrace();
            log.error("物流公司编码，系统异常",ex);
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
    private Class<?> handelExportModelClass(Class exportModelClass, boolean isShopExport){
        if(!isShopExport){
            return exportModelClass;
        }
        List<String> ignoreFiledNames = Arrays.asList("operOrgName","orgName");
        ListUtils.emptyIfNull(ignoreFiledNames).stream().forEach(fieldName -> {

            Optional.ofNullable(ReflectUtil.getField(exportModelClass,fieldName))
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
                            log.error("根据字段明层获取反射属性异常",e);
                        }
                    });
        });
        return exportModelClass;
    }
}

package com.longfor.c10.lzyx.logistics.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonDeliveryCompanyService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryCompanyService;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDeliveryCompany;
import com.longfor.c10.lzyx.logistics.entity.enums.ResultCodeEnum;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.PageRequest;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 配送公司接口实现类
 * @author zhaoyl
 * @date 2022/4/20 下午1:47
 * @since 1.0
 */
@Slf4j
@Service
public class LogisticsCommonDeliveryCompanyServiceImpl implements ILogisticsCommonDeliveryCompanyService {
    @Autowired
    private ILogisticsDeliveryCompanyService logisticsDeliveryCompanyService;

    /**
     * 新增快递公司
     *
     * @param request
     * @return
     */
    @Override
    public Response<Void> add(CompanyAddReq req) {
        //判断快递公司编码是否存在
        LogisticsDeliveryCompany bycodeAndStatus = findBycodeAndStatus(req.getCompanyCode());
        if (bycodeAndStatus != null) {
            return Response.fail("操作失败，该快递公司编码已存在");
        }
        //构建实体
        LogisticsDeliveryCompany company = new LogisticsDeliveryCompany();
        company.setCompanyCode(req.getCompanyCode());
        company.setCompanyName(req.getCompanyName());
        company.setIsSupportDeliveryPath(req.getIsSupportDeliveryPath());
        company.setStatus(1);
        Optional.ofNullable(req.getAmUserInfo()).ifPresent(userInfo -> {
            company.setCreateBy(userInfo.getRealName());
        });
        company.setCreateTime(new Timestamp(System.currentTimeMillis()));

        logisticsDeliveryCompanyService.save(company);
        return Response.ok(null);
    }

    /**
     * 修改快递公司
     *
     * @param request
     * @return
     */
    @Override
    public Response<Void> update(CompanyUpdateReq req) {
        //判断快递公司编码是否存在不包含此修改数据
        LogisticsDeliveryCompany bycodeAndStatus = findBycodeAndStatus(req.getCompanyCode());
        if (bycodeAndStatus != null && !req.getId().equals(bycodeAndStatus.getId().intValue())) {
            return Response.fail("操作失败，该快递公司编码已存在");
        }
        //构建实体
        LogisticsDeliveryCompany company = new LogisticsDeliveryCompany();
        company.setId(Long.valueOf(req.getId()));
        company.setCompanyCode(req.getCompanyCode());
        company.setCompanyName(req.getCompanyName());
        company.setIsSupportDeliveryPath(req.getIsSupportDeliveryPath());
        Optional.ofNullable(req.getAmUserInfo()).ifPresent(userInfo -> {
            company.setUpdateBy(userInfo.getRealName());
        });
        company.setUpdateTime(new Timestamp(System.currentTimeMillis()));

        logisticsDeliveryCompanyService.updateById(company);
        return Response.ok(null);
    }

    /**
     * 删除快递公司
     *
     * @param request
     * @return
     */
    @Override
    public Response<Void> delete(CompanyDeleteReq req) {
        LogisticsDeliveryCompany company = new LogisticsDeliveryCompany();
        company.setId(Long.valueOf(req.getId()));
        company.setStatus(0);
        Optional.ofNullable(req).map(CompanyDeleteReq::getAmUserInfo).ifPresent(userInfo -> {
            company.setUpdateBy(userInfo.getRealName());
        });
        company.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        logisticsDeliveryCompanyService.updateById(company);
        return Response.ok(null);
    }

    /**
     * 查询所有快递公司
     *
     * @param request
     * @return
     */
    @Override
    public Response<List<CompanyListRes>> list(CompanyListReq req) {
        LambdaQueryWrapper<LogisticsDeliveryCompany> lambdaQueryWrapper = buildListQuery(req);
        List<LogisticsDeliveryCompany> list = logisticsDeliveryCompanyService.list(lambdaQueryWrapper);
        List<CompanyListRes> resList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(c -> {
                CompanyListRes deliveryCompany = new CompanyListRes();
                BeanUtils.copyProperties(c, deliveryCompany);
                resList.add(deliveryCompany);
            });
        }
        return Response.ok(resList);
    }

    /**
     * 构造列表查询参数
     * @param req
     * @return
     */
    private LambdaQueryWrapper<LogisticsDeliveryCompany> buildListQuery(CompanyListReq req){
        LambdaQueryWrapper<LogisticsDeliveryCompany> lambdaQueryWrapper = Wrappers.<LogisticsDeliveryCompany>lambdaQuery();
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getCompanyCode()),LogisticsDeliveryCompany::getCompanyCode,req.getCompanyCode());
        lambdaQueryWrapper.like(StringUtils.isNotBlank(req.getCompanyName()),LogisticsDeliveryCompany::getCompanyName,req.getCompanyName());
        lambdaQueryWrapper.eq(LogisticsDeliveryCompany::getStatus,1);
        lambdaQueryWrapper.eq(Objects.nonNull(req.getIsSupportDeliveryPath()),LogisticsDeliveryCompany::getIsSupportDeliveryPath,req.getIsSupportDeliveryPath());
        return lambdaQueryWrapper;
    }
    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    @Override
    public PageResponse<List<CompanyListRes>> listByPage(CompanyListReq req, PageInfo pageInfo) {
        LambdaQueryWrapper<LogisticsDeliveryCompany> lambdaQueryWrapper = buildListQuery(req);
        Page<LogisticsDeliveryCompany> deliverCompanyPage = logisticsDeliveryCompanyService.page(new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize()),lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(deliverCompanyPage.getRecords())) {
            return Response.page(Collections.emptyList(), 0L);
        }
        List<CompanyListRes> listRes = ListUtils.emptyIfNull(deliverCompanyPage.getRecords())
                .stream()
                .map(c -> {
                    CompanyListRes deliveryCompany = new CompanyListRes();
                    BeanUtils.copyProperties(c, deliveryCompany);
                    return deliveryCompany;
                }).collect(Collectors.toList());
        return Response.page(listRes, deliverCompanyPage.getTotal());
    }

    /**
     * 根据编码查询物流公司是否存在
     *
     * @param companyCode
     * @return
     */
    public LogisticsDeliveryCompany findBycodeAndStatus(String companyCode) {
        LogisticsDeliveryCompany company = new LogisticsDeliveryCompany();
        company.setCompanyCode(companyCode);
        company.setStatus(1);
        List<LogisticsDeliveryCompany> list = logisticsDeliveryCompanyService.list(new QueryWrapper<>(company));
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }
}

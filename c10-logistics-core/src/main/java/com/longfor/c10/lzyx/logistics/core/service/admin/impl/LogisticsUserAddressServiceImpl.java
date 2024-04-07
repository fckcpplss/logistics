package com.longfor.c10.lzyx.logistics.core.service.admin.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsBaseAddress;
import com.longfor.c10.lzyx.logistics.core.service.admin.ILogisticsUserAddressService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsBaseAddressService;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户地址接口实现类
 * @author zhaoyl
 * @date 2022/2/7 下午4:18
 * @since 1.0
 */
@Service
public class LogisticsUserAddressServiceImpl implements ILogisticsUserAddressService {
    @Autowired
    private ILogisticsBaseAddressService logisticsBaseAddressService;

    @Override
    public Response<LogisticsAddressAddResp> add(LogisticsAddressAddReqData req) {
        LogisticsBaseAddress logisticsBaseAddress = new LogisticsBaseAddress();
        BeanUtils.copyProperties(req,logisticsBaseAddress);
        Optional.ofNullable(req.getAmUserInfo()).ifPresent(userInfo -> {
            logisticsBaseAddress.setCreateBy(userInfo.getRealName());
            logisticsBaseAddress.setUpdateBy(userInfo.getRealName());
        });
        boolean flag = logisticsBaseAddressService.save(logisticsBaseAddress);
        if(!flag){
            return Response.fail("地址添加失败");
        }
        LogisticsAddressAddResp logisticsAddressAddResp = new LogisticsAddressAddResp();
        logisticsAddressAddResp.setAddressId(logisticsBaseAddress.getAddressId());
        return Response.ok(logisticsAddressAddResp);
    }

    @Override
    public Response<Void> update(LogisticsAddressUpdateReq req) {
        LogisticsBaseAddress logisticsBaseAddress = new LogisticsBaseAddress();
        BeanUtils.copyProperties(req,logisticsBaseAddress);
        Optional.ofNullable(req.getAmUserInfo()).ifPresent(userInfo -> {
            logisticsBaseAddress.setUpdateBy(userInfo.getRealName());
            logisticsBaseAddress.setUpdateTime(DateUtil.date());
        });
        boolean flag = logisticsBaseAddressService.updateById(logisticsBaseAddress);
        if(!flag){
            return Response.fail("地址更新失败");
        }
        return Response.ok(null);
    }

    @Override
    public PageResponse<List<LogisticsAddressInfo>> queryList(PageInfo pageInfo, LogisticsAddressListReq req) {
        LambdaQueryWrapper<LogisticsBaseAddress> lambdaQueryWrapper = buildLogisticsAddressQueryWrapper(req);
        long total = logisticsBaseAddressService.count(lambdaQueryWrapper);
        if(total == 0){
            return  PageResponse.page(Collections.EMPTY_LIST,0L);
        }
        Page<LogisticsBaseAddress> page = logisticsBaseAddressService.page(new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize()),lambdaQueryWrapper);
        List<LogisticsAddressInfo> dataList = ListUtils.emptyIfNull(page.getRecords())
                .stream()
                .map(x -> {
                    LogisticsAddressInfo logisticsAddressInfo = new LogisticsAddressInfo();
                    BeanUtils.copyProperties(x, logisticsAddressInfo);
                    logisticsAddressInfo.setIsDef(x.getIsDefault());
                    return logisticsAddressInfo;
                }).collect(Collectors.toList());
        return PageResponse.page(dataList,total);
    }

    @Override
    public Response<Void> delete(LogisticsAddressDelReq req) {
        LogisticsBaseAddress logisticsBaseAddress = new LogisticsBaseAddress();
        logisticsBaseAddress.setAddressId(req.getAddressId());
        logisticsBaseAddress.setIsDelete(1);
        Optional.ofNullable(req.getAmUserInfo()).ifPresent(userInfo -> {
            logisticsBaseAddress.setUpdateBy(userInfo.getRealName());
            logisticsBaseAddress.setUpdateTime(DateUtil.date());
        });
        boolean flag = logisticsBaseAddressService.updateById(logisticsBaseAddress);
        if(!flag){
            return Response.fail("地址删除失败");
        }
        return Response.ok(null);
    }

    /**
     * 构造列表查询参数
     */
    private LambdaQueryWrapper<LogisticsBaseAddress> buildLogisticsAddressQueryWrapper(LogisticsAddressListReq req) {
        LambdaQueryWrapper<LogisticsBaseAddress> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(LogisticsBaseAddress::getIsDelete,0);
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(req.getOwnerId()),LogisticsBaseAddress::getOwnerId,req.getOwnerId());
        lambdaQueryWrapper.eq(Objects.nonNull(req.getOwnerType()),LogisticsBaseAddress::getOwnerType,req.getOwnerType());

        //最后面添加的自定义sql
        if(StringUtils.isNotBlank(req.getAddressQueryKey())){
            lambdaQueryWrapper.last(" concat(province_name,city_name,area_name,address_detail) like concat('%',"+req.getAddressQueryKey()+",'%')");
        }
        return lambdaQueryWrapper;
    }
}

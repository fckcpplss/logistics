package com.longfor.c10.lzyx.logistics.core.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.logistics.core.mq.producer.BizBaseDataToOrderSyncProducer;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonShipAddressService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsShipAddressService;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.BizBaseDataMessage;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsBaseAddress;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsShipAddress;
import com.longfor.c10.lzyx.logistics.entity.enums.AddressDefaultStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeleteStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 发货地址接口类
 * @author zhaoyl
 * @date 2022/2/21 上午9:49
 * @since 1.0
 */
@Service
public class LogisticsCommonShipAddressServiceImpl implements ILogisticsCommonShipAddressService {

    @Autowired
    private ILogisticsShipAddressService logisticsShipAddressService;

    @Autowired
    private BizBaseDataToOrderSyncProducer bizBaseDataToOrderSyncProducer;

    @Override
    public Response<Boolean> add(LogisticsShipAddressAddReq req) {
        String shopId = Optional.ofNullable(req.getFirstShopId()).orElseThrow(() -> new BusinessException("商户登陆信息为空"));
        LogisticsShipAddress logisticsShipAddress = new LogisticsShipAddress();
        BeanUtils.copyProperties(req,logisticsShipAddress);
        logisticsShipAddress.setSprId(shopId);
        Optional.ofNullable(req.getAmUserInfo()).ifPresent(userInfo -> {
            logisticsShipAddress.setCreator(userInfo.getUserName());
        });
        return addOrUpdate(logisticsShipAddress);
    }

    @Override
    public Response<Boolean> update(LogisticsShipAddressUpdateReq req) {
        Optional.ofNullable(req.getId()).orElseThrow(() -> new BusinessException("更新id为空"));
        String shopId = Optional.ofNullable(req.getFirstShopId()).orElseThrow(() -> new BusinessException("商户登陆信息为空"));
        LogisticsShipAddress logisticsShipAddress = new LogisticsShipAddress();
        BeanUtils.copyProperties(req,logisticsShipAddress);
        logisticsShipAddress.setSprId(shopId);
        return addOrUpdate(logisticsShipAddress);
    }

    @Transactional(rollbackFor = Exception.class)
    public Response<Boolean> addOrUpdate(LogisticsShipAddress req){
        Optional.ofNullable(req.getId()).ifPresent(id -> {
            checkData(id,req.getSprId());
        });
        if(Objects.isNull(req.getId())){
            if(Objects.isNull(req.getIsDefault())){
                req.setIsDefault(AddressDefaultStatusEnum.NO.getCode());
            }
            req.setCreateTime(DateUtil.date());
            req.setIsDelete(DeleteStatusEnum.NO.getCode());
        }
        boolean flag = logisticsShipAddressService.saveOrUpdate(req);
        //默认状态
        Integer isDefault =  Optional.ofNullable(AddressDefaultStatusEnum.fromCode(req.getIsDefault()))
                .map(AddressDefaultStatusEnum::getCode)
                .orElse(2);
        req.setIsDefault(isDefault);
        req.setIsDelete(DeleteStatusEnum.NO.getCode());
        //处理默认地址
        if(isDefault.equals(AddressDefaultStatusEnum.YES.getCode())){
            LogisticsShipAddress updateInfo = new LogisticsShipAddress();
            updateInfo.setIsDefault(AddressDefaultStatusEnum.NO.getCode());
            //更新其他地址为非默认
            logisticsShipAddressService.update(updateInfo,Wrappers.<LogisticsShipAddress>lambdaUpdate()
                    .eq(LogisticsShipAddress::getSprId,req.getSprId())
                    .ne(Objects.nonNull(req.getId()),LogisticsShipAddress::getId,req.getId()));
        }
        if(flag){
            //推送数据
            bizBaseDataToOrderSyncProducer.sendMsg(BizBaseDataMessage.builder()
                    .dataType(BizBaseDataMessage.DataTypeEnum.MERCHANT_ADDR)
                    .optType(BizBaseDataMessage.OptTypeEnum.UPD)
                    .prodTime(System.currentTimeMillis())
                    .data(Collections.singletonList(req)).build());
        }
        return Response.ok(true);
    }

    @Override
    public PageResponse<List<LogisticsShipAddressInfo>> list(LogisticsShipAddressListReq req, PageInfo pageInfo) {
        req = Optional.ofNullable(req).orElse(new LogisticsShipAddressListReq());
        pageInfo = Optional.ofNullable(pageInfo).orElse(new PageInfo());
        String shopId = Optional.ofNullable(req.getFirstShopId()).orElseThrow(() -> new BusinessException("商户登陆信息为空"));
        LambdaQueryWrapper<LogisticsShipAddress> queryWrapper = Wrappers.<LogisticsShipAddress>lambdaQuery();
        queryWrapper.eq(LogisticsShipAddress::getSprId,shopId);
        queryWrapper.eq(LogisticsShipAddress::getIsDelete,DeleteStatusEnum.NO.getCode());
        queryWrapper.eq(StringUtils.isNotBlank(req.getAddresser()),LogisticsShipAddress::getAddresser,req.getAddresser());
        queryWrapper.eq(StringUtils.isNotBlank(req.getPhoneNumber()),LogisticsShipAddress::getPhoneNumber,req.getPhoneNumber());
        queryWrapper.orderByAsc(LogisticsShipAddress::getIsDefault);
        queryWrapper.orderByDesc(LogisticsShipAddress::getCreateTime);
        Page<LogisticsShipAddress> pageResult = logisticsShipAddressService.page(new Page<LogisticsShipAddress>(pageInfo.getPageNum(), pageInfo.getPageSize()), queryWrapper);
        return PageResponse.page(handelResult(pageResult.getRecords()),pageResult.getTotal());
    }

    private List<LogisticsShipAddressInfo> handelResult(List<LogisticsShipAddress> records) {
        return ListUtils.emptyIfNull(records).stream()
                .map(address -> {
                    LogisticsShipAddressInfo addressInfo = new LogisticsShipAddressInfo();
                    BeanUtils.copyProperties(address,addressInfo);
                    return addressInfo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Response<Boolean> delete(LogisticsShipAddressDelReq req) {
        Integer id = Optional.ofNullable(req.getId()).orElseThrow(() -> new BusinessException("更新id为空"));
        String shopId = Optional.ofNullable(req.getFirstShopId()).orElseThrow(() -> new BusinessException("商户登陆信息为空"));
        checkData(id,shopId);
        LogisticsShipAddress logisticsShipAddress = new LogisticsShipAddress();
        logisticsShipAddress.setId(id);
        logisticsShipAddress.setIsDelete(DeleteStatusEnum.YES.getCode());
        logisticsShipAddressService.updateById(logisticsShipAddress);
        return Response.ok(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Boolean> isDefault(LogisticsShipAddressDefaultReq req) {
        Integer id = Optional.ofNullable(req.getId()).orElseThrow(() -> new BusinessException("更新id为空"));
        String shopId = Optional.ofNullable(req.getFirstShopId()).orElseThrow(() -> new BusinessException("商户登陆信息为空"));
        checkData(id,shopId);
        LogisticsShipAddress defaultNoUpdate = new LogisticsShipAddress();
        defaultNoUpdate.setIsDefault(AddressDefaultStatusEnum.NO.getCode());
        logisticsShipAddressService.update(defaultNoUpdate,Wrappers.<LogisticsShipAddress>lambdaUpdate().eq(LogisticsShipAddress::getSprId,shopId));

        LogisticsShipAddress defaultYesUpdate = new LogisticsShipAddress();
        defaultYesUpdate.setId(id);
        defaultYesUpdate.setIsDefault(AddressDefaultStatusEnum.YES.getCode());
        logisticsShipAddressService.updateById(defaultYesUpdate);
        return Response.ok(true);
    }

    private void checkData(Integer id, String shopId) {
        List<LogisticsShipAddress> queryList = logisticsShipAddressService.list(Wrappers.<LogisticsShipAddress>lambdaQuery()
                .eq(LogisticsShipAddress::getId, id)
                .eq(LogisticsShipAddress::getIsDelete,DeleteStatusEnum.NO.getCode())
                .eq(LogisticsShipAddress::getSprId, shopId));
        if(CollectionUtils.isEmpty(queryList)){
            throw new BusinessException("更新数据不存在");
        }
    }

    @Override
    public Response<List<LogisticsShipSprRes>> getSpr(LogisticsShipSprReq req) {
        String shopId = Optional.ofNullable(req.getFirstShopId()).orElseThrow(() -> new BusinessException("商户登陆信息为空"));
        LambdaQueryWrapper<LogisticsShipAddress> queryWrapper = Wrappers.<LogisticsShipAddress>lambdaQuery();
        queryWrapper.eq(LogisticsShipAddress::getSprId,shopId);
        queryWrapper.eq(LogisticsShipAddress::getIsDelete,DeleteStatusEnum.NO.getCode());
        queryWrapper.orderByAsc(LogisticsShipAddress::getIsDefault);
        queryWrapper.orderByDesc(LogisticsShipAddress::getCreateTime);
        List<LogisticsShipAddress> dataList = logisticsShipAddressService.list(queryWrapper);
        return Response.ok(ListUtils.emptyIfNull(dataList).stream().map(address -> {
            LogisticsShipSprRes logisticsShipSprRes = new LogisticsShipSprRes();
            BeanUtils.copyProperties(address,logisticsShipSprRes);
            return logisticsShipSprRes;
        }).collect(Collectors.toList()));
    }
}

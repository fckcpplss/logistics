package com.longfor.c10.lzyx.logistics.core.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.PhoneUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.logistics.core.mq.producer.BizBaseDataToOrderSyncProducer;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonBaseAddressService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsBaseAddressService;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.UserTokenInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.BizBaseDataMessage;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsBaseAddress;
import com.longfor.c10.lzyx.logistics.entity.enums.AddressDefaultStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.AddressOwnerTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeleteStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class LogisticsCommonBaseAddressServiceImpl implements ILogisticsCommonBaseAddressService {

    @Value("${logistics.user.max-address-count:25}")
    private Integer maxAddressCount = 25;

    @Autowired
    private ILogisticsBaseAddressService logisticsBaseAddressService;

    @Autowired
    private BizBaseDataToOrderSyncProducer bizBaseDataToOrderSyncProducer;

    @Override
    public Response<Boolean> add(LogisticsAddressAddReqData req) {
        UserTokenInfo userInfo = getAndCheckUserInfo(req);
        LogisticsBaseAddress logisticsBaseAddress = new LogisticsBaseAddress();
        BeanUtils.copyProperties(req,logisticsBaseAddress);
        logisticsBaseAddress.setOwnerType(AddressOwnerTypeEnum.USER.getCode());
        logisticsBaseAddress.setOwnerId(userInfo.getLmId());
        logisticsBaseAddress.setChannelId(userInfo.getChannelId());
        logisticsBaseAddress.setPersonName(req.getRecipientName());
        logisticsBaseAddress.setPersonName(req.getRecipientName());
        logisticsBaseAddress.setPersonPhone(req.getRecipientPhone());
        logisticsBaseAddress.setIsDelete(DeleteStatusEnum.NO.getCode());
        logisticsBaseAddress.setIsDefault(Optional.ofNullable(req.getIsDef()).orElse(AddressDefaultStatusEnum.NO.getCode()));
        return addOrUpdate(logisticsBaseAddress);
    }

    @Override
    public Response<Boolean> update(LogisticsAddressUpdateReq req) {
        UserTokenInfo userInfo = getAndCheckUserInfo(req);
        Optional.ofNullable(req.getAddressId()).orElseThrow(() -> new BusinessException("地址id为空"));
        LogisticsBaseAddress logisticsBaseAddress = new LogisticsBaseAddress();
        BeanUtils.copyProperties(req,logisticsBaseAddress);
        logisticsBaseAddress.setOwnerId(userInfo.getLmId());
        logisticsBaseAddress.setPersonName(req.getRecipientName());
        logisticsBaseAddress.setPersonPhone(req.getRecipientPhone());
        logisticsBaseAddress.setIsDefault(Optional.ofNullable(req.getIsDef()).orElse(AddressDefaultStatusEnum.NO.getCode()));
        return addOrUpdate(logisticsBaseAddress);
    }

    @Transactional(rollbackFor = Exception.class)
    public Response<Boolean> addOrUpdate(LogisticsBaseAddress req){
        checkData(req.getAddressId(),req.getOwnerId());
        if(Objects.isNull(req.getAddressId())){
            if(Objects.isNull(req.getIsDefault())){
                req.setIsDefault(AddressDefaultStatusEnum.NO.getCode());
            }
            req.setCreateTime(DateUtil.date());
            req.setIsDelete(DeleteStatusEnum.NO.getCode());
        }
        boolean flag = logisticsBaseAddressService.saveOrUpdate(req);
        //默认状态
        Integer isDefault =  Optional.ofNullable(AddressDefaultStatusEnum.fromCode(req.getIsDefault()))
                .map(AddressDefaultStatusEnum::getCode)
                .orElse(2);
        req.setIsDefault(isDefault);
        req.setIsDelete(DeleteStatusEnum.NO.getCode());
        //处理默认地址
        if(isDefault.equals(AddressDefaultStatusEnum.YES.getCode())){
            LogisticsBaseAddress updateInfo = new LogisticsBaseAddress();
            updateInfo.setIsDefault(AddressDefaultStatusEnum.NO.getCode());
            //更新其他地址为非默认
            logisticsBaseAddressService.update(updateInfo,Wrappers.<LogisticsBaseAddress>lambdaUpdate()
                    .eq(LogisticsBaseAddress::getOwnerId,req.getOwnerId())
                    .ne(Objects.nonNull(req.getAddressId()),LogisticsBaseAddress::getAddressId,req.getAddressId()));
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
    public PageResponse<List<LogisticsAddressInfo>> list(LogisticsAddressListReq req, PageInfo pageInfo) {
        UserTokenInfo userInfo = getAndCheckUserInfo(req);
        req = Optional.ofNullable(req).orElse(new LogisticsAddressListReq());
        pageInfo = Optional.ofNullable(pageInfo).orElse(new PageInfo());
        LambdaQueryWrapper<LogisticsBaseAddress> queryWrapper = Wrappers.<LogisticsBaseAddress>lambdaQuery();
        queryWrapper.eq(LogisticsBaseAddress::getOwnerId,userInfo.getLmId());
//        queryWrapper.eq(LogisticsBaseAddress::getChannelId,userInfo.getChannelId());
        queryWrapper.eq(StringUtils.isNotBlank(req.getChannelId()),LogisticsBaseAddress::getChannelId,userInfo.getChannelId());
        queryWrapper.eq(LogisticsBaseAddress::getIsDelete,DeleteStatusEnum.NO.getCode());
        queryWrapper.eq(StringUtils.isNotBlank(req.getAddressId()),LogisticsBaseAddress::getAddressId,req.getAddressId());
        queryWrapper.eq(Objects.nonNull(req.getOwnerType()),LogisticsBaseAddress::getOwnerType,req.getOwnerType());
        queryWrapper.eq(Objects.nonNull(req.getIsDefault()),LogisticsBaseAddress::getIsDefault,req.getIsDefault());
        //多个字段模糊匹配
        LogisticsAddressListReq finalReq = req;
        queryWrapper.and(StringUtils.isNotBlank(finalReq.getAddressQueryKey()),wrapper -> {
            wrapper.like(LogisticsBaseAddress::getProvinceName, finalReq.getAddressQueryKey()).or();
            wrapper.like(LogisticsBaseAddress::getCityName, finalReq.getAddressQueryKey()).or();
            wrapper.like(LogisticsBaseAddress::getAreaName, finalReq.getAddressQueryKey()).or();
            wrapper.like(LogisticsBaseAddress::getAddressDetail, finalReq.getAddressQueryKey());
        });
        queryWrapper.orderByAsc(LogisticsBaseAddress::getIsDefault);
        queryWrapper.orderByDesc(LogisticsBaseAddress::getCreateTime);
        Page<LogisticsBaseAddress> pageResult = logisticsBaseAddressService.page(new Page<LogisticsBaseAddress>(pageInfo.getPageNum(), pageInfo.getPageSize()), queryWrapper);
        return PageResponse.page(handelResult(pageResult.getRecords()),pageResult.getTotal());
    }

    private List<LogisticsAddressInfo> handelResult(List<LogisticsBaseAddress> records) {
        return ListUtils.emptyIfNull(records).stream()
                .map(address -> {
                    LogisticsAddressInfo addressInfo = handelCommonAddressConvert(address);
                    //手机号不脱敏
                    addressInfo.setRecipientPhoneFull(address.getPersonPhone());
                    //手机号码脱敏
                    addressInfo.setRecipientPhone(String.valueOf(PhoneUtil.hideBetween(address.getPersonPhone())));
                    return addressInfo;
                })
                .collect(Collectors.toList());
    }
    private LogisticsAddressInfo handelCommonAddressConvert(LogisticsBaseAddress address){
        LogisticsAddressInfo addressInfo = new LogisticsAddressInfo();
        BeanUtils.copyProperties(address,addressInfo);
        addressInfo.setRecipientName(address.getPersonName());
        addressInfo.setRecipientPhone(address.getPersonPhone());
        addressInfo.setIsDef(address.getIsDefault());
        return addressInfo;
    }
    @Override
    public Response<Boolean> delete(LogisticsAddressDelReq req) {
        UserTokenInfo userInfo = getAndCheckUserInfo(req);
        String addressId = Optional.ofNullable(req.getAddressId()).orElseThrow(() -> new BusinessException("地址id为空"));
        checkData(addressId,userInfo.getLmId());
        LogisticsBaseAddress logisticsBaseAddress = new LogisticsBaseAddress();
        logisticsBaseAddress.setAddressId(addressId);
        logisticsBaseAddress.setIsDelete(DeleteStatusEnum.YES.getCode());
        logisticsBaseAddressService.updateById(logisticsBaseAddress);
        return Response.ok(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Boolean> isDefault(LogisticsAddressDefaultReq req) {
        UserTokenInfo userInfo = getAndCheckUserInfo(req);
        String addressId = Optional.ofNullable(req.getAddressId()).orElseThrow(() -> new BusinessException("地址id为空"));
        checkData(addressId,userInfo.getLmId());
        LogisticsBaseAddress defaultNoUpdate = new LogisticsBaseAddress();
        defaultNoUpdate.setIsDefault(AddressDefaultStatusEnum.NO.getCode());
        logisticsBaseAddressService.update(defaultNoUpdate,Wrappers.<LogisticsBaseAddress>lambdaUpdate().eq(LogisticsBaseAddress::getOwnerId,userInfo.getLmId()));

        LogisticsBaseAddress defaultYesUpdate = new LogisticsBaseAddress();
        defaultYesUpdate.setAddressId(addressId);
        defaultYesUpdate.setIsDefault(AddressDefaultStatusEnum.YES.getCode());
        logisticsBaseAddressService.updateById(defaultYesUpdate);
        return Response.ok(true);
    }

    @Override
    public Response<LogisticsAddressInfo> detail(LogisticsAddressDetailReqData req) {
        UserTokenInfo userInfo = getAndCheckUserInfo(req);
        String addressId = Optional.ofNullable(req.getAddressId()).orElseThrow(() -> new BusinessException("地址id为空"));
        LogisticsBaseAddress logisticsBaseAddress = logisticsBaseAddressService.getOne(new LambdaQueryWrapper<LogisticsBaseAddress>()
                .eq(LogisticsBaseAddress::getAddressId, addressId)
                .eq(LogisticsBaseAddress::getOwnerId, userInfo.getLmId()));
        return Response.ok(Optional.ofNullable(logisticsBaseAddress)
                .map(this::handelCommonAddressConvert)
                .orElse(null));
    }

    @Override
    public Response<LogisticsAddressInfo> defaultDetail(LogisticsCommonNoParamReqData req) {
        UserTokenInfo userInfo = getAndCheckUserInfo(req);
        LogisticsBaseAddress defaultAddress = logisticsBaseAddressService.getOne(Wrappers.<LogisticsBaseAddress>lambdaQuery()
                .in(LogisticsBaseAddress::getIsDefault, AddressDefaultStatusEnum.YES.getCode())
                .in(LogisticsBaseAddress::getOwnerId, userInfo.getLmId())
                .in(LogisticsBaseAddress::getOwnerType, AddressOwnerTypeEnum.USER.getCode())
                .in(LogisticsBaseAddress::getChannelId, userInfo.getChannelId())
                .orderByDesc(LogisticsBaseAddress::getCreateTime)
                .last(" limit 1"));
        return Response.ok(Optional.ofNullable(defaultAddress)
                .map(address -> handelCommonAddressConvert(address))
                .orElse(null));
    }

    @Override
    public Response<List<LogisticsAddressInfo>> listByUser(LogisticsCommonNoParamReqData req) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageSize(maxAddressCount);
        LogisticsAddressListReq defaultReq =  Optional.ofNullable(getAndCheckUserInfo(req))
                .map(userInfo -> {
                    LogisticsAddressListReq r = new LogisticsAddressListReq();
                    r.setUserTokenInfo(userInfo);
                    r.setOwnerId(userInfo.getLmId());
                    r.setOwnerType(AddressOwnerTypeEnum.USER.getCode());
                    r.setChannelId(userInfo.getChannelId());
                    return r;
                })
                .orElse(null);
        return Response.ok(Optional.ofNullable(list(defaultReq,pageInfo))
                .map(PageResponse::getData)
                .orElse(null));
    }

    private UserTokenInfo getAndCheckUserInfo(BaseReqData req){
        UserTokenInfo userInfo = Optional.ofNullable(req).map(BaseReqData::getUserTokenInfo).orElseThrow(() -> new BusinessException("登陆信息为空"));
        if(Objects.isNull(userInfo.getLmId())){
            throw new BusinessException("登陆信息为空");
        }
        return userInfo;
    }
    private void checkData(String addressId,String ownerId) {
        List<LogisticsBaseAddress> queryList = logisticsBaseAddressService.list(Wrappers.<LogisticsBaseAddress>lambdaQuery()
                .eq(StringUtils.isNotBlank(addressId),LogisticsBaseAddress::getAddressId, addressId)
                .eq(LogisticsBaseAddress::getIsDelete,DeleteStatusEnum.NO.getCode())
                .eq(StringUtils.isNotBlank(ownerId),LogisticsBaseAddress::getOwnerId,ownerId));
        if(StringUtils.isNotBlank(addressId) && CollectionUtils.isEmpty(queryList)){
            throw new BusinessException("更新数据不存在");
        }
        if(StringUtils.isBlank(addressId) && !CollectionUtils.isEmpty(queryList) && queryList.size() >= maxAddressCount){
            throw new BusinessException("已达到" + maxAddressCount + "条地址上限，请先删除不常用地址");
        }
    }
}

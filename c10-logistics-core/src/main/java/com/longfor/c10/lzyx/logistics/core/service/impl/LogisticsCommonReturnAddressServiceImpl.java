package com.longfor.c10.lzyx.logistics.core.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longfor.c10.lzyx.logistics.core.mq.producer.BizBaseDataToOrderSyncProducer;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonReturnAddressService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsDeliveryService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsReturnAddressService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsShipAddressService;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.dto.mq.BizBaseDataMessage;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsReturnAddress;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsShipAddress;
import com.longfor.c10.lzyx.logistics.entity.enums.AddressDefaultStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeleteStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.common.util.BeanUtil;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.PageResponse;
import com.longfor.c2.starter.data.domain.response.Response;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 退回地址接口类
 * @author zhaoyl
 * @date 2022/2/21 上午9:49
 * @since 1.0
 */
@Service
public class LogisticsCommonReturnAddressServiceImpl implements ILogisticsCommonReturnAddressService {

    @Autowired
    private ILogisticsReturnAddressService logisticsReturnAddressService;

    @Autowired
    private BizBaseDataToOrderSyncProducer bizBaseDataToOrderSyncProducer;

    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;

    @Autowired
    private ILogisticsShipAddressService logisticsShipAddressService;

    @Override
    public Response<Boolean> add(LogisticsReturnAddressAddReq req) {
        String shopId = Optional.ofNullable(req.getFirstShopId()).orElseThrow(() -> new BusinessException("商户登陆信息为空"));
        LogisticsReturnAddress logisticsReturnAddress = new LogisticsReturnAddress();
        BeanUtils.copyProperties(req,logisticsReturnAddress);
        logisticsReturnAddress.setAddressee(req.getAddresser());
        logisticsReturnAddress.setSprId(shopId);
        logisticsReturnAddress.setIsDelete(DeleteStatusEnum.NO.getCode());
        logisticsReturnAddress.setIsDefault(Optional.ofNullable(req.getIsDefault()).orElse(AddressDefaultStatusEnum.NO.getCode()));
        Optional.ofNullable(req.getAmUserInfo()).ifPresent(userInfo -> {
            logisticsReturnAddress.setCreator(userInfo.getUserName());
        });
        logisticsReturnAddress.setCreateTime(DateUtil.date());
        return addOrUpdate(logisticsReturnAddress);
    }

    @Override
    public Response<Boolean> update(LogisticsReturnAddressUpdateReq req) {
        Integer id = Optional.ofNullable(req.getId()).orElseThrow(() -> new BusinessException("更新id为空"));
        String shopId = Optional.ofNullable(req.getFirstShopId()).orElseThrow(() -> new BusinessException("商户登陆信息为空"));
        LogisticsReturnAddress logisticsReturnAddress = new LogisticsReturnAddress();
        BeanUtils.copyProperties(req,logisticsReturnAddress);
        logisticsReturnAddress.setAddressee(req.getAddresser());
        logisticsReturnAddress.setId(id);
        logisticsReturnAddress.setSprId(shopId);
        return addOrUpdate(logisticsReturnAddress);
    }

    @Transactional(rollbackFor = Exception.class)
    public Response<Boolean> addOrUpdate(LogisticsReturnAddress req){
        Optional.ofNullable(req.getId()).ifPresent(id -> {
            List<LogisticsReturnAddress> queryList = logisticsReturnAddressService.list(Wrappers.<LogisticsReturnAddress>lambdaQuery().eq(LogisticsReturnAddress::getId, id).eq(LogisticsReturnAddress::getSprId, req.getSprId()));
            if(CollectionUtils.isEmpty(queryList)){
                throw new BusinessException("更新数据不存在");
            }
        });
        boolean flag = logisticsReturnAddressService.saveOrUpdate(req);
        //默认状态
        Integer isDefault =  Optional.ofNullable(AddressDefaultStatusEnum.fromCode(req.getIsDefault()))
                .map(AddressDefaultStatusEnum::getCode)
                .orElse(2);
        req.setIsDefault(isDefault);
        req.setIsDelete(DeleteStatusEnum.NO.getCode());
        //处理默认地址
        if(isDefault.equals(AddressDefaultStatusEnum.YES.getCode())){
            LogisticsReturnAddress updateInfo = new LogisticsReturnAddress();
            updateInfo.setIsDefault(AddressDefaultStatusEnum.NO.getCode());
            //更新其他地址为非默认
            logisticsReturnAddressService.update(updateInfo,Wrappers.<LogisticsReturnAddress>lambdaUpdate()
                    .eq(LogisticsReturnAddress::getSprId,req.getSprId())
                    .ne(Objects.nonNull(req.getId()),LogisticsReturnAddress::getId,req.getId()));
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
    public PageResponse<List<LogisticsReturnAddressInfo>> list(LogisticsReturnAddressListReq req, PageInfo pageInfo) {
        req = Optional.ofNullable(req).orElse(new LogisticsReturnAddressListReq());
        pageInfo = Optional.ofNullable(pageInfo).orElse(new PageInfo());
        String shopId = Optional.ofNullable(req.getFirstShopId()).orElseThrow(() -> new BusinessException("商户登陆信息为空"));
        LambdaQueryWrapper<LogisticsReturnAddress> queryWrapper = Wrappers.<LogisticsReturnAddress>lambdaQuery();
        queryWrapper.eq(LogisticsReturnAddress::getIsDelete,DeleteStatusEnum.NO.getCode());
        queryWrapper.eq(LogisticsReturnAddress::getSprId,shopId);
        queryWrapper.eq(StringUtils.isNotBlank(req.getAddresser()),LogisticsReturnAddress::getAddressee,req.getAddresser());
        queryWrapper.eq(StringUtils.isNotBlank(req.getPhoneNumber()),LogisticsReturnAddress::getPhoneNumber,req.getPhoneNumber());
        queryWrapper.orderByAsc(LogisticsReturnAddress::getIsDefault);
        queryWrapper.orderByDesc(LogisticsReturnAddress::getCreateTime);
        Page<LogisticsReturnAddress> pageResult = logisticsReturnAddressService.page(new Page<LogisticsReturnAddress>(pageInfo.getPageNum(), pageInfo.getPageSize()), queryWrapper);
        return PageResponse.page(handelResult(pageResult.getRecords()),pageResult.getTotal());
    }

    private List<LogisticsReturnAddressInfo> handelResult(List<LogisticsReturnAddress> records) {
        return ListUtils.emptyIfNull(records).stream()
                .map(address -> {
                    LogisticsReturnAddressInfo addressInfo = new LogisticsReturnAddressInfo();
                    BeanUtils.copyProperties(address,addressInfo);
                    addressInfo.setAddresser(address.getAddressee());
                    return addressInfo;
                }).collect(Collectors.toList());
    }

    @Override
    public Response<Boolean> delete(LogisticsReturnAddressDelReq req) {
        Integer id = Optional.ofNullable(req.getId()).orElseThrow(() -> new BusinessException("更新id为空"));
        String shopId = Optional.ofNullable(req.getFirstShopId()).orElseThrow(() -> new BusinessException("商户登陆信息为空"));
        List<LogisticsReturnAddress> queryList = logisticsReturnAddressService.list(Wrappers.<LogisticsReturnAddress>lambdaQuery().eq(LogisticsReturnAddress::getId, id).eq(LogisticsReturnAddress::getSprId, shopId));
        if(CollectionUtils.isEmpty(queryList)){
            throw new BusinessException("更新数据不存在");
        }
        LogisticsReturnAddress logisticsReturnAddress = new LogisticsReturnAddress();
        logisticsReturnAddress.setId(id);
        logisticsReturnAddress.setIsDelete(DeleteStatusEnum.YES.getCode());
        logisticsReturnAddressService.updateById(logisticsReturnAddress);
        return Response.ok(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Boolean> isDefault(LogisticsReturnAddressDefaultReq req) {
        Integer id = Optional.ofNullable(req.getId()).orElseThrow(() -> new BusinessException("更新id为空"));
        String shopId = Optional.ofNullable(req.getFirstShopId()).orElseThrow(() -> new BusinessException("商户登陆信息为空"));
        List<LogisticsReturnAddress> queryList = logisticsReturnAddressService.list(Wrappers.<LogisticsReturnAddress>lambdaQuery().eq(LogisticsReturnAddress::getId, id).eq(LogisticsReturnAddress::getSprId, shopId));
        if(CollectionUtils.isEmpty(queryList)){
            throw new BusinessException("更新数据不存在");
        }
        LogisticsReturnAddress defaultNoUpdate = new LogisticsReturnAddress();
        defaultNoUpdate.setIsDefault(AddressDefaultStatusEnum.NO.getCode());
        logisticsReturnAddressService.update(defaultNoUpdate,Wrappers.<LogisticsReturnAddress>lambdaUpdate().eq(LogisticsReturnAddress::getSprId,shopId));

        LogisticsReturnAddress defaultYesUpdate = new LogisticsReturnAddress();
        defaultYesUpdate.setId(id);
        defaultYesUpdate.setIsDefault(AddressDefaultStatusEnum.YES.getCode());
        logisticsReturnAddressService.updateById(defaultYesUpdate);
        return Response.ok(true);
    }

    @Override
    public Response<List<LogisticsReturnSprRes>> getSpr(LogisticsReturnSprReq req) {
        String shopId = Optional.ofNullable(req.getFirstShopId()).orElseThrow(() -> new BusinessException("商户登陆信息为空"));
        LambdaQueryWrapper<LogisticsReturnAddress> queryWrapper = Wrappers.<LogisticsReturnAddress>lambdaQuery();
        queryWrapper.eq(LogisticsReturnAddress::getSprId,shopId);
        queryWrapper.eq(LogisticsReturnAddress::getIsDelete,DeleteStatusEnum.NO.getCode());
        queryWrapper.orderByAsc(LogisticsReturnAddress::getIsDefault);
        queryWrapper.orderByDesc(LogisticsReturnAddress::getCreateTime);
        List<LogisticsReturnAddress> dataList = logisticsReturnAddressService.list(queryWrapper);
        return Response.ok(ListUtils.emptyIfNull(dataList).stream().map(address -> {
            LogisticsReturnSprRes logisticsReturnSprRes = new LogisticsReturnSprRes();
            BeanUtils.copyProperties(address,logisticsReturnSprRes);
            return logisticsReturnSprRes;
        }).collect(Collectors.toList()));
    }

    @Override
    public Response<LogisticsReturnAddressResData> getReturnAddress(Request<LogisticsReturnAddressReqData> request) {
        String childOrderId = Optional.ofNullable(request).map(Request::getData).map(LogisticsReturnAddressReqData::getChildOrderId).orElseThrow(() -> new BusinessException("子订单ID不能为空"));
        List<LogisticsOrder> logisticsOrders = logisticsOrderService.list(Wrappers.<LogisticsOrder>lambdaQuery().eq(LogisticsOrder::getChildOrderId, childOrderId));
        if(CollectionUtils.isEmpty(logisticsOrders)){
            throw new BusinessException("订单不存在");
        }
        String shopId = logisticsOrders.stream().map(LogisticsOrder::getShopId).findFirst().orElseThrow(() -> new BusinessException("订单不存在"));
        List<Long> logisticsOrderIds = logisticsOrders.stream().map(LogisticsOrder::getId).distinct().collect(Collectors.toList());
        Integer sendAddrId = ListUtils.emptyIfNull(logisticsDeliveryService.list(Wrappers.<LogisticsDelivery>lambdaQuery().in(LogisticsDelivery::getLogisticsOrderId, logisticsOrderIds)))
                .stream()
                .filter(x -> Objects.nonNull(x.getSendAddressId()))
                .map(LogisticsDelivery::getSendAddressId)
                .map(Integer::valueOf)
                .findFirst().orElse(null);
        //退回地址
        LogisticsShipAddress logisticsShipAddress = Optional.ofNullable(sendAddrId).map(addrId -> {
            return Optional.ofNullable(logisticsShipAddressService.getOne(Wrappers.<LogisticsShipAddress>lambdaQuery()
                    .eq(LogisticsShipAddress::getId, addrId)
                    .eq(LogisticsShipAddress::getIsDelete, DeleteStatusEnum.NO.getCode())
                    .last(" limit 1"))).orElse(null);
        }).filter(x -> Objects.nonNull(x)).orElse(null);
        String sendAddr = Optional.ofNullable(logisticsShipAddress).map(LogisticsShipAddress::convertAddressName).orElse(null);

        // 获取退货地址
        LogisticsReturnAddress returnAddress = new LogisticsReturnAddress();
        returnAddress.setSprId(shopId);
        returnAddress.setIsDelete(DeleteStatusEnum.NO.getCode());
        List<LogisticsReturnAddress> returnAddressDataList = logisticsReturnAddressService.list(new QueryWrapper<>(returnAddress))
                .stream()
                .filter(returnAddressEntity -> !StringUtils.equals(sendAddr, returnAddressEntity.convertAddressName()))
                .collect(Collectors.toList());
        if (ObjectUtils.isEmpty(logisticsShipAddress) && CollectionUtil.isEmpty(returnAddressDataList)) {
            throw new BusinessException("没有找到退换货地址");
        }
        // 组装返回结果
        LogisticsReturnAddressResData resData = new LogisticsReturnAddressResData();
        Optional.ofNullable(StrUtil.emptyToDefault(sendAddr,null)).ifPresent(str -> {
            resData.setSendAddress(logisticsShipAddress);
            LogisticsReturnAddress send = new LogisticsReturnAddress();
            BeanUtil.copy(logisticsShipAddress, send);
            send.setAddressee(logisticsShipAddress.getAddresser());
            returnAddressDataList.add(0, send);
        });
        resData.setReturnAddressList(returnAddressDataList);
        return Response.ok(resData);
    }
}

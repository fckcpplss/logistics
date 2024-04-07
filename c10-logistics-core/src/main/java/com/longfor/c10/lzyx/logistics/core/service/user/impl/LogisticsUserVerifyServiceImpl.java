package com.longfor.c10.lzyx.logistics.core.service.user.impl;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Preconditions;
import com.longfor.c10.lzyx.dictionary.client.api.function.FunctionListClient;
import com.longfor.c10.lzyx.dictionary.entity.dto.function.FunctionListVo;
import com.longfor.c10.lzyx.dictionary.entity.enums.ListDataTypeEnum;
import com.longfor.c10.lzyx.dictionary.entity.param.function.FunctionListReq;
import com.longfor.c10.lzyx.logistics.core.service.impl.AbstractCommonVerifyOrderService;
import com.longfor.c10.lzyx.logistics.core.service.user.ILogisticsUserVerifyService;
import com.longfor.c10.lzyx.logistics.core.util.QrCodeUtil;
import com.longfor.c10.lzyx.logistics.core.util.RedisUtil;
import com.longfor.c10.lzyx.logistics.dao.service.ICfgVerifyAuthorityService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsVerifyOrderGoodsService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsVerifyOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsVerifyOrderDetailResData;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsVerifyOrderGoodsDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsVerifyOrderVerifyListDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsVerifyOrderVerifyReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.UserTokenInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.verify.VerifyOrderConfirmVO;
import com.longfor.c10.lzyx.logistics.entity.dto.verify.VerifyOrderGoodsVO;
import com.longfor.c10.lzyx.logistics.entity.dto.verify.VerifyPickUpCodeVO;
import com.longfor.c10.lzyx.logistics.entity.dto.verify.VerifyResGoodsVO;
import com.longfor.c10.lzyx.logistics.entity.dto.verify.param.VerifyOrderConfirmReq;
import com.longfor.c10.lzyx.logistics.entity.dto.verify.param.VerifyOrderSearchReq;
import com.longfor.c10.lzyx.logistics.entity.entity.CfgVerifyAuthority;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsVerifyOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsVerifyOrderGoods;
import com.longfor.c10.lzyx.logistics.entity.enums.LogisticsVerifyRefundStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.LogisticsVerifyVerifyStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.LogisticsVerifyVerifyTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c10.starter.aliyun.oss.provider.AliyunOssProvider;
import com.longfor.c2.starter.common.domain.IResultCode;
import com.longfor.c2.starter.common.util.DateUtil;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liuxin41
 * @version 1.0
 * @description:
 * @date 2022/4/14 15:58
 */
@Slf4j
@Service("logisticsUserVerifyServiceImpl")
public class LogisticsUserVerifyServiceImpl extends AbstractCommonVerifyOrderService implements ILogisticsUserVerifyService {

    @Value("${verify.child.order.h5.path}")
    private String orderH5Path;
    @Resource
    private ILogisticsVerifyOrderService verifyOrderService;
    @Resource
    private ILogisticsVerifyOrderGoodsService verifyOrderGoodsService;
    @Resource
    private ICfgVerifyAuthorityService verifyAuthorityService;
    @Resource
    private AliyunOssProvider aliyunOssProvider;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private FunctionListClient functionListClient;

    private final static String PICKUP_CODE_PREFIX = "Verify:order:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Map<String, VerifyPickUpCodeVO>> listVerifyQrCodeUrl(Request<VerifyOrderSearchReq> request) {
        VerifyOrderSearchReq data = request.getData();
        QueryWrapper<LogisticsVerifyOrder> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<LogisticsVerifyOrder> lambda = queryWrapper.lambda();
        lambda.in(LogisticsVerifyOrder::getChildOrderId, data.getChildOrderIds()).eq(LogisticsVerifyOrder::getLmId, data.getLmId());
        List<LogisticsVerifyOrder> verifyOrders = verifyOrderService.list(queryWrapper);
        if(CollectionUtils.isEmpty(verifyOrders)){
            return Response.fail("未查询到订单信息");
        }
        Map<String,VerifyPickUpCodeVO> res = new HashMap<>(verifyOrders.size());
        InputStream inputStream = null;
        try {
            for (LogisticsVerifyOrder verifyOrder : verifyOrders) {
                String qrCodeUrl;
                VerifyPickUpCodeVO vo = new VerifyPickUpCodeVO();
                String childOrderId = verifyOrder.getChildOrderId();
                String pickupQrcodeUrl = verifyOrder.getPickupQrcodeUrl();
                if(StringUtils.isBlank(pickupQrcodeUrl)){
                    String fileName = "Verify_"+ childOrderId + "_" + RandomUtil.randomString(5) + ".jpg";
                    byte[] bytes = QrCodeUtil.createQrcode(orderH5Path + childOrderId);
                    if(bytes == null){
                        return Response.fail("生成核销二维码失败");
                    }
                    inputStream = new ByteArrayInputStream(bytes);
                    String quCodeUrlKey = aliyunOssProvider.upload(inputStream, fileName);
                    qrCodeUrl = aliyunOssProvider.getDownloadUrl(quCodeUrlKey);
                    verifyOrder.setPickupQrcodeUrl(quCodeUrlKey);
                    verifyOrderService.updateById(verifyOrder);
                }else{
                    qrCodeUrl = aliyunOssProvider.getDownloadUrl(pickupQrcodeUrl);
                }
                vo.setPickupQrcodeUrl(qrCodeUrl);
                vo.setPickupCode(verifyOrder.getPickupCode());
                vo.setChildOrderId(childOrderId);
                res.put(childOrderId, vo);
            }
        } catch (IOException e) {
            log.error("获取、刷新 核销二维码异常", e);
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {}
            }
        }
        return Response.ok(res);
    }

    @Override
    public Response<Boolean> confirmVerifyOrder(Request<VerifyOrderConfirmReq> request) {
        VerifyOrderConfirmReq data = request.getData();
        if(Objects.nonNull(data) && StringUtils.isBlank(data.getOaAccount())){
            return Response.fail("核销人oa为空，请重新扫描");
        }
        Request<FunctionListReq> dicReq = new Request<>();
        FunctionListReq dicData = new FunctionListReq();
        dicData.setBizFunction(0);
        dicData.setListType(0);
        dicData.setDataType(ListDataTypeEnum.OA.name());
        dicData.setListData(data.getOaAccount());
        dicReq.setData(dicData);
        Response<List<FunctionListVo>> dicResp = functionListClient.listByCondition(dicReq);
        if(dicResp == null || CollectionUtils.isEmpty(dicResp.getData())){
            return Response.fail("无核销权限");
        }
        // TODO: 2023/3/15 优化 
//        Set<String> authedOas = StreamEx.of(dicResp.getData()).map(FunctionListVo::getListData).toSet();
//        if(CollectionUtils.isEmpty(authedOas) || !authedOas.contains(data.getOaAccount())){
//            return Response.fail("您无核销权限");
//        }
        LogisticsVerifyOrder verifyOrder = verifyOrderService.getOne(Wrappers.<LogisticsVerifyOrder>lambdaQuery().eq(LogisticsVerifyOrder::getChildOrderId, data.getChildOrderId()));
        if(verifyOrder == null){
            return Response.fail("订单不存在");
        }
        if (LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode().equals(verifyOrder.getVerifyStatus())) {
            return Response.fail("当前订单已核销");
        }
        List<LogisticsVerifyOrderGoods> goodsList = verifyOrderGoodsService.list(Wrappers.<LogisticsVerifyOrderGoods>lambdaQuery().eq(LogisticsVerifyOrderGoods::getChildOrderId, data.getChildOrderId()));
        Set<String> skuSets = ListUtils.emptyIfNull(goodsList).stream().map(LogisticsVerifyOrderGoods::getSkuId).collect(Collectors.toSet());
        for (String skuId : data.getSkuIds()) {
            if(!skuSets.contains(skuId)){
                return Response.fail("核销商品有误");
            }
        }
        List<String> orderGoods = ListUtils.emptyIfNull(goodsList).stream().filter(it -> !LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode().equals(it.getVerifyStatus()) && LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode().equals(it.getRefundStatus()))
                .map(LogisticsVerifyOrderGoods::getSkuId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(orderGoods)){
            return Response.fail("当前订单下无可核销的商品");
        }
        LogisticsVerifyOrderVerifyReqData req = new LogisticsVerifyOrderVerifyReqData();
        req.setVerifyType(LogisticsVerifyVerifyTypeEnum.VERIFY_USER);
        req.setVerifyList(Collections.singletonList(new LogisticsVerifyOrderVerifyListDTO(data.getChildOrderId(), orderGoods)));
        req.setPickupCode(verifyOrder.getPickupCode());
        UserTokenInfo info = new UserTokenInfo();
        info.setAccount(data.getOaAccount());
        req.setUserTokenInfo(info);
        Response<String> verify = super.verify(req);
        if(IResultCode.SUCCESS != verify.getCode()){
            return Response.fail(verify.getMsg());
        }
        return Response.ok(Boolean.TRUE);
    }

    @Override
    public Response<VerifyOrderConfirmVO> listChildOrderGoods(Request<VerifyOrderConfirmReq> request) {
        VerifyOrderConfirmReq data = request.getData();

        Request<FunctionListReq> dicReq = new Request<>();
        FunctionListReq dicData = new FunctionListReq();
        dicData.setBizFunction(0);
        dicData.setListType(0);
        dicData.setDataType(ListDataTypeEnum.OA.name());
        dicData.setListData(data.getOaAccount());
        dicReq.setData(dicData);
        Response<List<FunctionListVo>> dicResp = functionListClient.listByCondition(dicReq);
        if(dicResp == null || CollectionUtils.isEmpty(dicResp.getData())){
            return Response.fail("无核销权限");
        }
//        Set<String> authedOas = StreamEx.of(dicResp.getData()).map(FunctionListVo::getListData).toSet();
//        if(CollectionUtils.isEmpty(authedOas) || !authedOas.contains(data.getOaAccount())){
//            return Response.fail("您无核销权限");
//        }
        LogisticsVerifyOrder verifyOrder = verifyOrderService.getOne(Wrappers.<LogisticsVerifyOrder>lambdaQuery().eq(LogisticsVerifyOrder::getChildOrderId, data.getChildOrderId()));
        Preconditions.checkNotNull(verifyOrder, "订单不存在");

        VerifyOrderConfirmVO res = this.convert(verifyOrder);

        List<VerifyResGoodsVO> list = new ArrayList<>(5);

        List<LogisticsVerifyOrderGoods> goodsList = verifyOrderGoodsService.list(Wrappers.<LogisticsVerifyOrderGoods>lambdaQuery().eq(LogisticsVerifyOrderGoods::getChildOrderId, data.getChildOrderId()));

        List<LogisticsVerifyOrderGoods> orderGoodsNormal = StreamEx.of(goodsList).filter(it -> LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode().equals(it.getRefundStatus())).toList();
        List<LogisticsVerifyOrderGoods> orderGoodsUnNormal = StreamEx.of(goodsList).filter(it -> !LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode().equals(it.getRefundStatus())).toList();
        if(CollectionUtils.isNotEmpty(orderGoodsNormal)){
            Map<String, List<VerifyOrderGoodsVO>> listMap = StreamEx.of(orderGoodsNormal).map(this::convert).groupingBy(it -> it.getPickupStartTime() + ":" + it.getPickupEndTime());
            List<VerifyResGoodsVO> normalList = mapSortedByKey(listMap);
            list.addAll(normalList);
        }
        if(CollectionUtils.isNotEmpty(orderGoodsUnNormal)){
            Map<String, List<VerifyOrderGoodsVO>> listMap = StreamEx.of(orderGoodsUnNormal).map(this::convert).groupingBy(it -> it.getPickupStartTime() + ":" + it.getPickupEndTime());
            List<VerifyResGoodsVO> unnormalList = mapSortedByKey(listMap);
            list.addAll(unnormalList);
        }
        res.setPickupGroupList(list);
        return Response.ok(res);
    }

    private VerifyOrderGoodsVO convert(LogisticsVerifyOrderGoods goods){
        VerifyOrderGoodsVO target = new VerifyOrderGoodsVO();
        BeanUtils.copyProperties(goods, target);
        target.setLmNickname(DesensitizedUtil.chineseName(goods.getLmNickname()));
        return target;
    }
    private VerifyOrderConfirmVO convert(LogisticsVerifyOrder order){
        VerifyOrderConfirmVO target = new VerifyOrderConfirmVO();
        BeanUtils.copyProperties(order, target);
        target.setLmNickname(DesensitizedUtil.chineseName(order.getLmNickname()));
        return target;
    }

    List<VerifyResGoodsVO> mapSortedByKey(Map<String, List<VerifyOrderGoodsVO>> param) {
        ArrayList<String> keyList = new ArrayList<>(param.keySet());
        Collections.sort(keyList);
        List<VerifyResGoodsVO> res = new ArrayList<>();
        for (int i = 0; i < keyList.size(); i++) {
            VerifyResGoodsVO vo = new VerifyResGoodsVO();
            List<VerifyOrderGoodsVO> orderGoodsVOS = param.get(keyList.get(i));
            VerifyOrderGoodsVO orderGoodsVO = orderGoodsVOS.get(0);
            BeanUtils.copyProperties(orderGoodsVO, vo);
            vo.setPickupStartTime(orderGoodsVO.getPickupStartTime() == null ? "" : DateUtil.format(orderGoodsVO.getPickupStartTime(), "yyyy-MM-dd HH:mm"));
            vo.setPickupEndTime(orderGoodsVO.getPickupEndTime() == null ? "" : DateUtil.format(orderGoodsVO.getPickupEndTime(), "yyyy-MM-dd HH:mm"));
            vo.setOrderGoodsList(orderGoodsVOS);
            res.add(vo);
        }
        return res;
    }

    @Override
    public void verifyDataCheck(LogisticsVerifyOrderVerifyReqData req, List<LogisticsVerifyOrderDetailResData> verifyListByOrderNos, Map<String, List<LogisticsVerifyOrderGoodsDTO>> existOrderNoAndSkuIdMap) {
        String orderNoInValid = ListUtils.emptyIfNull(req.getVerifyList()).stream().filter(x -> !existOrderNoAndSkuIdMap.containsKey(x.getOrderNo())).map(LogisticsVerifyOrderVerifyListDTO::getOrderNo).collect(Collectors.joining(","));
        if(StringUtils.isNotBlank(orderNoInValid)){
            throw new BusinessException("核销失败，订单号" + orderNoInValid + "不存在");
        }
        for (LogisticsVerifyOrderDetailResData x : ListUtils.emptyIfNull(verifyListByOrderNos)) {
            if (LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode().equals(x.getVerifyStatus())) {
                log.info("订单已核销:{}", x.getChildOrderId());
                throw new BusinessException("订单"+x.getChildOrderId()+"已核销");
            }
            List<LogisticsVerifyOrderGoodsDTO> existSkuInfos = existOrderNoAndSkuIdMap.get(x.getChildOrderId());
            List<LogisticsVerifyOrderGoodsDTO> goodsDTOS = StreamEx.of(ListUtils.emptyIfNull(existSkuInfos)).filter(it -> !LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode().equals(it.getVerifyStatus()) && LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode().equals(it.getRefundStatus())).toList();
            if (CollectionUtils.isEmpty(goodsDTOS)) {
                log.info("订单:{} 的子商品均无法核销{}", x.getChildOrderId(), JSON.toJSONString(existSkuInfos));
                throw new BusinessException("当前订单下无可核销的商品");
            }else{
                log.info("修改只能核销的订单数据goodsDTOS:{}", JSON.toJSONString(goodsDTOS));
                //existOrderNoAndSkuIdMap.put(x.getChildOrderId(), goodsDTOS);
                x.setCanVerifyList(goodsDTOS);
            }
        }
    }
}

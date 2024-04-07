package com.longfor.c10.lzyx.logistics.core.service.admin.impl;

import cn.hutool.core.lang.Pair;
import com.longfor.c10.lzyx.logistics.core.service.admin.ILogisticsAdminVerifyOrderService;
import com.longfor.c10.lzyx.logistics.core.service.impl.AbstractCommonVerifyOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsVerifyOrderDetailResData;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsVerifyOrderGoodsDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsVerifyOrderVerifyListDTO;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsVerifyOrderVerifyReqData;
import com.longfor.c10.lzyx.logistics.entity.enums.LogisticsVerifyRefundStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.LogisticsVerifyVerifyStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.response.Response;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 自提/核销运营端接口实现类
 * @author zhaoyl
 * @date 2022/4/13 上午11:17
 * @since 1.0
 */
@Service("logisticsAdminVerifyOrderServiceImpl")
public class LogisticsAdminVerifyOrderServiceImpl extends AbstractCommonVerifyOrderService implements ILogisticsAdminVerifyOrderService {

    @Override
    public void verifyDataCheck(LogisticsVerifyOrderVerifyReqData req, List<LogisticsVerifyOrderDetailResData> verifyListByOrderNos, Map<String, List<LogisticsVerifyOrderGoodsDTO>> existOrderNoAndSkuIdMap) {
        String orderNoInValid = ListUtils.emptyIfNull(req.getVerifyList()).stream().filter(x -> !existOrderNoAndSkuIdMap.containsKey(x.getOrderNo())).map(LogisticsVerifyOrderVerifyListDTO::getOrderNo).collect(Collectors.joining(","));
        if(StringUtils.isNotBlank(orderNoInValid)){
            throw new BusinessException(new StringBuilder("核销失败，订单号:[").append(orderNoInValid).append("]不存在").toString());
        }
        //订单核销状态map
        Map<String, Pair<Integer,Integer>> childOrderIdAndStatusMap = verifyListByOrderNos.stream().collect(Collectors.toMap(LogisticsVerifyOrderDetailResData::getChildOrderId, x -> new Pair(x.getVerifyStatus(), x.getRefundStatus()), (a, b) -> a));
        String verifySuccessChildOrderIds = childOrderIdAndStatusMap.entrySet().stream().filter(x -> LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getCode().equals(x.getValue().getKey())).map(entity -> entity.getKey()).distinct().collect(Collectors.joining(","));
        if(StringUtils.isNotBlank(verifySuccessChildOrderIds)){
            throw new BusinessException(new StringBuilder("核销失败，订单号:[").append(verifySuccessChildOrderIds).append("]已核销").toString());
        }
        String refundAllChildOrderIds = childOrderIdAndStatusMap.entrySet().stream().filter(x -> !LogisticsVerifyRefundStatusEnum.REFUND_NO.getCode().equals(x.getValue().getValue()) && !LogisticsVerifyRefundStatusEnum.REFUND_PART.getCode().equals(x.getValue().getValue())).map(entity -> entity.getKey()).distinct().collect(Collectors.joining(","));
        if(StringUtils.isNotBlank(refundAllChildOrderIds)){
            throw new BusinessException(new StringBuilder("核销失败，订单号:[").append(verifySuccessChildOrderIds).append("}商品已全部发生退款").toString());
        }
        String errorMsg = ListUtils.emptyIfNull(req.getVerifyList()).stream().map(x -> {
            List<LogisticsVerifyOrderGoodsDTO> existSkuInfos = existOrderNoAndSkuIdMap.get(x.getOrderNo());
            Map<String, String> skuIdAndNameMap = ListUtils.emptyIfNull(existSkuInfos).stream().collect(Collectors.toMap(LogisticsVerifyOrderGoodsDTO::getSkuId, goods -> new StringBuilder("名称:").append(goods.getGoodsName()).append("|规格：").append(goods.getSkuSpecs()).toString(), (a, b) -> a));
            String invalidSkuIds = ListUtils.emptyIfNull(x.getSkuIds()).stream().filter(y -> ListUtils.emptyIfNull(existSkuInfos).stream().noneMatch(z -> z.getSkuId().equals(y))).map(m -> Optional.ofNullable(skuIdAndNameMap.get(m)).orElse(m)).collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(invalidSkuIds)) {
                return new StringBuilder("订单号：").append(x.getOrderNo()).append(",").append("商品:[").append(invalidSkuIds).append("]不存在").toString();
            }
            String invalidVerifyStatusSkuIds = ListUtils.emptyIfNull(x.getSkuIds()).stream().filter(y -> ListUtils.emptyIfNull(existSkuInfos).stream().anyMatch(z -> z.getSkuId().equals(y) && z.getVerifyStatusShow().equals(LogisticsVerifyVerifyStatusEnum.VERIFY_SUCCESS.getDesc()))).map(m -> Optional.ofNullable(skuIdAndNameMap.get(m)).orElse(m)).collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(invalidVerifyStatusSkuIds)) {
                return new StringBuilder("订单号：").append(x.getOrderNo()).append(",").append("商品:[").append(invalidVerifyStatusSkuIds).append("]已核销").toString();
            }
            String invalidVerifyStatusSkuIds1 = ListUtils.emptyIfNull(x.getSkuIds()).stream().filter(y -> ListUtils.emptyIfNull(existSkuInfos).stream().anyMatch(z -> z.getSkuId().equals(y) && !z.getRefundStatusShow().equals(LogisticsVerifyRefundStatusEnum.REFUND_NO.getDesc()))).map(m -> Optional.ofNullable(skuIdAndNameMap.get(m)).orElse(m)).collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(invalidVerifyStatusSkuIds1)) {
                return new StringBuilder("订单号：").append(x.getOrderNo()).append(",").append("商品:[").append(invalidVerifyStatusSkuIds1).append("]已发生退款").toString();
            }
            Map<String, String> existSkuIdsAndShopIdMap = ListUtils.emptyIfNull(existSkuInfos).stream().collect(Collectors.toMap(LogisticsVerifyOrderGoodsDTO::getSkuId, LogisticsVerifyOrderGoodsDTO::getOrgId, (a, b) -> a));

            if (Objects.nonNull(req.getAmUserInfo()) && !CollectionUtils.isEmpty(req.getAmUserInfo().getOrgIds())) {
                String invalidShopSkuIds = ListUtils.emptyIfNull(x.getSkuIds()).stream().map(skuId -> existSkuIdsAndShopIdMap.get(skuId)).filter(loginShopId -> req.getAmUserInfo().getOrgIds().stream().noneMatch(orgId -> loginShopId.equals(orgId))).map(m -> Optional.ofNullable(skuIdAndNameMap.get(m)).orElse(m)).collect(Collectors.joining(","));
                if (StringUtils.isNotBlank(invalidShopSkuIds)) {
                    return new StringBuilder("订单号：").append(x.getOrderNo()).append(",").append("商品:[").append(invalidShopSkuIds).append("]当前用户无权限").toString();
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.joining(";"));
        if(StringUtils.isNotBlank(errorMsg)){
            throw new BusinessException(new StringBuilder("核销失败，").append(errorMsg).toString());
        }
        boolean pickupCodeValid = ListUtils.emptyIfNull(verifyListByOrderNos)
                .stream()
                .flatMap(x -> x.getGoodsList().stream()).map(LogisticsVerifyOrderGoodsDTO::getPickupCode)
                .filter(StringUtils::isNotBlank)
                .map(String::toUpperCase)
                .distinct()
                .anyMatch(code -> code.equals(req.getPickupCode().trim().toUpperCase()));
        if(!pickupCodeValid){
            throw new BusinessException(new StringBuilder("核销失败，").append("核销码").append(req.getPickupCode()).append("不正确").toString());
        }
    }
}

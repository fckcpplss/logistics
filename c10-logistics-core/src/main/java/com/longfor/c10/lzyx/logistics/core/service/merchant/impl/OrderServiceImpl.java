package com.longfor.c10.lzyx.logistics.core.service.merchant.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.longfor.c10.lzyx.logistics.core.factory.DeliveryFactory;
import com.longfor.c10.lzyx.logistics.core.service.merchant.IOrderService;
import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.AddOrderReqData;
import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import com.longfor.c2.starter.common.util.MD5Util;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

/**
 * 物流订单service实现类
 * @author zhaoyl
 * @date 2022/1/19 上午11:52
 * @since 1.0
 */
@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private DeliveryFactory deliveryFactory;

    public static void main(String[] args) {
        String param = "{\"com\":\"sf\",\"num\":\"JDVA10642846749\",\"phone\":\"null\",\"resultv2\":1}";

        String body = param + "KAwhjmrS5849" + "D3365F7FF69723E0E37487E044635AB0";
        String sign = MD5Util.getMD5(body);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("customer", "D3365F7FF69723E0E37487E044635AB0");
        params.add("sign", sign.toUpperCase());
        params.add("param", param);
        System.out.println(JSON.toJSONString(params));
    }

    @Override
    public Response<String> doSendOrder(AddOrderReqData addOrderReqDatas) {
        if(CollectionUtils.isEmpty(addOrderReqDatas.getAddOrderListReqData())){
            return Response.ok("参数为空");
        }
        BaseReqData baseReqData = (BaseReqData)addOrderReqDatas;
        if(Objects.isNull(baseReqData) || CollectionUtils.isEmpty(baseReqData.getShopIds()) || Objects.isNull(baseReqData.getAmUserInfo())){
            return Response.ok("登陆信息为空");
        }
        addOrderReqDatas.getAddOrderListReqData().forEach(addOrderReqData -> {
            //去掉运单号空格
            addOrderReqData.setDeliveryNo(StrUtil.cleanBlank(addOrderReqData.getDeliveryNo()));
            deliveryFactory.getService(null, String.valueOf(ObjectUtil.defaultIfNull(addOrderReqData.getShopLogisticsId(),"")),addOrderReqData.getCompanyCode()).addOrder(addOrderReqData,baseReqData);
        });
        // TODO: 2022/2/8
        return Response.ok(null);
    }
}

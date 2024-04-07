package com.longfor.c10.lzyx.logistics.core.factory;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.longfor.c10.lzyx.logistics.core.service.IDeliveryService;
import com.longfor.c10.lzyx.logistics.entity.annotation.DeliveryCompanyType;
import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.SendReturnTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 物流服务工厂类
 * @author zhaoyl
 */
@Slf4j
@Component
public class DeliveryFactory implements InitializingBean, ApplicationContextAware{
    //物流服务map
    private Map<String, IDeliveryService> deliveryServiceMap = new ConcurrentHashMap<>(16);
    private ApplicationContext applicationContext;
    /**
     * 根据快递公司编码获取服务
     */
    public IDeliveryService getService(CompanyCodeEnum companyCodeEnum){
        IDeliveryService deliveryService = Optional.ofNullable(companyCodeEnum)
                .map(e -> StringUtils.isBlank(e.getCode()) ? null : e.getCode())
                .map(c -> deliveryServiceMap.get(c))
                .orElseThrow(() -> new BusinessException(StrUtil.format("根据快递公司编码[{}]查询物流服务为空", companyCodeEnum.getCode())));
        log.info("当前执行的物流服务,deliveryService = {}",deliveryService);
        return deliveryService;
    }

    /**
     * 根据退货状态、商户id、运单号获取物流服务
     * @param sendReturn
     * @param shopLogisticsId
     * @param companyCode
     * @return
     */
    public IDeliveryService getService(Integer sendReturn,String shopLogisticsId,String companyCode){
        CompanyCodeEnum companyCodeEnum = null;
        //退货物流 或者 发货物流中的"其他"
        if ((Objects.nonNull(sendReturn) && sendReturn == SendReturnTypeEnum.BACKWARD.getCode()) || StringUtils.isBlank(shopLogisticsId)) {
            companyCodeEnum = CompanyCodeEnum.KD100;
        } else {
            //平台物流: 京东 顺丰
            companyCodeEnum = CompanyCodeEnum.fromCode(companyCode);
        }
        //如果退货，直接走快递100
        return getService(companyCodeEnum);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, IDeliveryService> matchBeans = applicationContext.getBeansOfType(IDeliveryService.class);
        matchBeans.forEach((key, value) -> {
            DeliveryCompanyType deliveryCompanyType = AnnotationUtils.findAnnotation(value.getClass(),DeliveryCompanyType.class);
            if(Objects.nonNull(deliveryCompanyType)){
                deliveryServiceMap.put(deliveryCompanyType.code().getCode(), value);
            }
        });
        log.info("spring加载物流服务完成，deliveryServiceMap = {}", JSON.toJSONString(deliveryServiceMap));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
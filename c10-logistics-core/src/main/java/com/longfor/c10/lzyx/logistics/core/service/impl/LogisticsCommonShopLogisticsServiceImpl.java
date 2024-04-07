package com.longfor.c10.lzyx.logistics.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.longfor.c10.lzyx.dictionary.client.api.org.DictionaryOrgClient;
import com.longfor.c10.lzyx.dictionary.client.request.ClientPageRequest;
import com.longfor.c10.lzyx.dictionary.entity.dto.clientdto.OrgConfigDto;
import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonShopLogisticsService;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsOrderService;
import com.longfor.c10.lzyx.logistics.dao.service.IShopLogisticsConfigService;
import com.longfor.c10.lzyx.logistics.dao.service.IShopLogisticsService;
import com.longfor.c10.lzyx.logistics.entity.dto.*;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.AmUserInfo;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsOrder;
import com.longfor.c10.lzyx.logistics.entity.entity.ShopLogistics;
import com.longfor.c10.lzyx.logistics.entity.entity.ShopLogisticsConfig;
import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeleteStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.LogisticsTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.SfProductTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 供应商物流配置接口实现类
 * @author zhaoyl
 * @date 2022/4/13 上午11:16
 * @since 1.0
 */
@Service
@Slf4j
public class LogisticsCommonShopLogisticsServiceImpl implements ILogisticsCommonShopLogisticsService {
    @Autowired
    private IShopLogisticsService shopLogisticsService;

    @Autowired
    private IShopLogisticsConfigService shopLogisticsConfigService;

    @Autowired
    private ILogisticsOrderService logisticsOrderService;

    @Autowired
    private DictionaryOrgClient dictionaryOrgClient;


    @Override
    public List<ShopLogisticsRep> getShopLogisticsRepList(ShopLogisticsListReq shopLogisticsListReq) {

        List<ShopLogistics> shopLogisticsEntityList = shopLogisticsService.list(Wrappers.<ShopLogistics>lambdaQuery()
                .eq(ShopLogistics::getDeleteStatus, DeleteStatusEnum.NO.getCode())
                .eq(ShopLogistics::getShopId,shopLogisticsListReq.getShopId()));

        Map<Integer, ShopLogisticsConfig> configMap = ListUtils.emptyIfNull(shopLogisticsConfigService.list(Wrappers.<ShopLogisticsConfig>lambdaQuery()
                        .eq(ShopLogisticsConfig::getDeleteStatus,DeleteStatusEnum.NO.getCode())))
                .stream().collect(Collectors.toMap(ShopLogisticsConfig::getId, Function.identity(),(a, b) -> a));
        List<ShopLogisticsRep> shopLogisticsRepList = new ArrayList<>();
        configMap.forEach((configId, config) -> {
            ShopLogistics exist = findExist(shopLogisticsEntityList, configId);
            if (exist == null) {
                exist = createNewShopLogistics(shopLogisticsListReq,configId);
                log.info("给供应商：{}创建物流配置：{}成功！", shopLogisticsListReq.getShopId(), config.toString());
            }

            ShopLogisticsRep.ShopLogisticsRepBuilder builder = ShopLogisticsRep.builder()
                    .id(exist.getId())
                    .logisticsCode(config.getLogisticsCode())
                    .logisticsName(config.getLogisticsName())
                    .typeCode(config.getTypeCode())
                    .typeName(config.getTypeName())
                    .choose(exist.getChoose() == 1)
                    .useDefault(config.getUseDefault() == 1);

            if (config.getUseDefault() != 1) {
                builder.account(exist.getAccount()).appKey(exist.getAppKey());
            }

            //商家顺丰可配置产品类别
            if (Objects.equals(CompanyCodeEnum.SF.getCode(), config.getLogisticsCode())
                    && Objects.equals("0", config.getTypeCode())) {
                Map<String, String> sfProductTypes = Maps.newHashMap();
                for (SfProductTypeEnum e : SfProductTypeEnum.values()) {
                    sfProductTypes.put(e.getCode(), e.getDesc());
                }
                builder.sfProductTypes(sfProductTypes);
                builder.expressType(exist.getExpressType());
            }
            shopLogisticsRepList.add(builder.build());

        });

        return shopLogisticsRepList;
    }
    private boolean checkIsC2Order(String childOrderId){
        if(StringUtils.isBlank(childOrderId)){
            return false;
        }
        LogisticsOrder logisticsOrder = logisticsOrderService.getOne(Wrappers.<LogisticsOrder>lambdaQuery().eq(LogisticsOrder::getChildOrderId, childOrderId).last(" limit 1"));
        if(Objects.isNull(logisticsOrder)){
            log.info("订单号不存在，childOrderId = {}",childOrderId);
            return false;
        }
        ClientPageRequest<OrgConfigDto> request = new ClientPageRequest<>();
        OrgConfigDto orgConfigDto = new OrgConfigDto();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(1);
        pageInfo.setPageSize(200);
        orgConfigDto.setBizChannelCode("C2");
        request.setData(orgConfigDto);
        request.setPageInfo(pageInfo);
        return Optional.ofNullable(dictionaryOrgClient.list(request))
                .map(Response::getData)
                .map(list -> {
                    return ListUtils.emptyIfNull(list).stream().anyMatch(x -> x.getOrgId().equals(logisticsOrder.getOrgId()));
                })
                .orElse(false);
    }
    @Override
    public void updateShopLogisticsReq(ShopLogisticsListUpdReq shopLogisticsListUpdReq) {
        ListUtils.emptyIfNull(shopLogisticsListUpdReq.getList()).forEach(shopLogisticsUpdReq -> {
            ShopLogistics shopLogisticsEntity = shopLogisticsService.getOne(Wrappers.<ShopLogistics>lambdaQuery().eq(ShopLogistics::getId,shopLogisticsUpdReq.getId()));
            if (shopLogisticsEntity == null) {
                log.error("找不到对应的配置, id:{}", shopLogisticsUpdReq.getId());
                throw new BusinessException("找不到对应的配置");
            }
            if (!StringUtils.equals(shopLogisticsEntity.getShopId(), shopLogisticsListUpdReq.getShopId())) {
                log.error("对应的配置与供应商ID不符, shopId:{}, shopLogisticsEntity:{}", shopLogisticsListUpdReq.getShopId(), shopLogisticsEntity);
                throw new BusinessException("id对应的配置与供应商id不符");
            }
            ShopLogisticsConfig shopLogisticsConfigEntity = shopLogisticsConfigService.getById(shopLogisticsEntity.getShopLogisticsConfigId());
            if (shopLogisticsConfigEntity == null) {
                log.error("找不到配置对应的模板, configId:{}", shopLogisticsEntity.getShopLogisticsConfigId());
                throw new BusinessException("找不到配置对应的模板");
            }
            if (shopLogisticsConfigEntity.getUseDefault() == 1) {
                if (StringUtils.isNotBlank(shopLogisticsUpdReq.getAccount()) || StringUtils.isNotBlank(shopLogisticsUpdReq.getAppKey())) {
                    throw new BusinessException("当前配置不允许填写账户和appKey");
                }
            }
            shopLogisticsEntity.setAccount(shopLogisticsUpdReq.getAccount());
            shopLogisticsEntity.setAppKey(shopLogisticsUpdReq.getAppKey());
            shopLogisticsEntity.setChoose(shopLogisticsUpdReq.getChoose() ? 1 : 0);

            //商家顺丰可配置产品类别
            if (Objects.equals(CompanyCodeEnum.SF.getCode(), shopLogisticsConfigEntity.getLogisticsCode())
                    && Objects.equals("0", shopLogisticsConfigEntity.getTypeCode())) {
                if(Objects.isNull(shopLogisticsUpdReq.getExpressType())){
                    shopLogisticsEntity.setExpressType(null);
                }else{
                    shopLogisticsEntity.setExpressType(SfProductTypeEnum.fromCode(shopLogisticsUpdReq.getExpressType()).getCode());
                }
            }

            AmUserInfo userInfo = shopLogisticsListUpdReq.getAmUserInfo();
            if (userInfo != null) {
                shopLogisticsEntity.setUpdateAccount(userInfo.getUserName());
                shopLogisticsEntity.setUpdateName(userInfo.getRealName());
            }

            shopLogisticsService.updateById(shopLogisticsEntity);
        });
    }

    @Override
    public List<ShopLogisticsByCompanyRep> getShopLogisticsByCompanyRepList(ShopLogisticsListReq shopLogisticsListReq) {

        if (StringUtils.isBlank(shopLogisticsListReq.getCompanyId())) {
            return new ArrayList<>();
        }

        List<Integer> configIdList = new ArrayList<>();
        Map<Integer, ShopLogisticsConfig> configMap = ListUtils.emptyIfNull(shopLogisticsConfigService.list(Wrappers.<ShopLogisticsConfig>lambdaQuery().eq(ShopLogisticsConfig::getDeleteStatus,DeleteStatusEnum.NO.getCode())))
                .stream().collect(Collectors.toMap(ShopLogisticsConfig::getId, Function.identity(),(a, b) -> a));
        configMap.forEach((id, config) -> {
            if (config.getLogisticsCode().equals(shopLogisticsListReq.getCompanyId())) {
                configIdList.add(id);
            }
        });
        List<ShopLogistics> shopLogisticsEntityList = shopLogisticsService.list(Wrappers.<ShopLogistics>lambdaQuery().eq(ShopLogistics::getShopId,shopLogisticsListReq.getShopId()).in(ShopLogistics::getShopLogisticsConfigId,configIdList));
        return shopLogisticsEntityList.stream().filter(entity -> filter(shopLogisticsListReq.getLogisticsTypeCode(), entity.getShopLogisticsConfigId())).map(entity -> {
            ShopLogisticsConfig config = shopLogisticsConfigService.getById(entity.getShopLogisticsConfigId());
            ShopLogisticsByCompanyRep shopLogisticsByCompanyRep = new ShopLogisticsByCompanyRep();
            shopLogisticsByCompanyRep.setId(entity.getId());
            shopLogisticsByCompanyRep.setOrderNumber(config.getOrderNumber());
            shopLogisticsByCompanyRep.setLogisticsName(config.getTypeName() + config.getLogisticsName());
            return shopLogisticsByCompanyRep;
        }).sorted(Comparator.comparing(ShopLogisticsByCompanyRep::getOrderNumber)).collect(Collectors.toList());
    }

    /**
     * 运费承担方为平台时，不能使用商家物流
     */
    private boolean filter(Integer logisticsTypeCode, Integer shopLogisticsConfigId) {
        if (LogisticsTypeEnum.SELF_PLAT.getCode().equals(logisticsTypeCode)) {
            ShopLogisticsConfig shopLogisticsConfigEntity = shopLogisticsConfigService.getById(shopLogisticsConfigId);
            return !"0".equals(shopLogisticsConfigEntity.getTypeCode());
        }
        return true;
    }

    @Override
    public List<CompangConfigListResData> getCompanyConfigByShopId(CompanyConfigListReqData companyConfigListReqData) {
        if(checkIsC2Order(companyConfigListReqData.getOrderNo())){
            //直接返回其他
            CompangConfigListResData compangConfigRep = new CompangConfigListResData();
            compangConfigRep.setCompanyCode("other");
            compangConfigRep.setCompanyName("其他");
            return Arrays.asList(compangConfigRep);
        }
        List<ShopLogistics> shopLogisticsEntityList = shopLogisticsService.list(Wrappers.<ShopLogistics>lambdaQuery()
                .eq(ShopLogistics::getShopId,companyConfigListReqData.getShopId())
                .eq(ShopLogistics::getDeleteStatus,DeleteStatusEnum.NO.getCode())
                .eq(ShopLogistics::getChoose,1));

        return shopLogisticsEntityList.stream()
                .filter(shopLogisticsEntity -> filter(companyConfigListReqData.getLogisticsTypeCode(), shopLogisticsEntity.getShopLogisticsConfigId()))
                .map(shopLogisticsEntity -> {
                    ShopLogisticsConfig shopLogisticsConfigEntity = shopLogisticsConfigService.getById(shopLogisticsEntity.getShopLogisticsConfigId());
                    return shopLogisticsConfigEntity.getLogisticsCode();
                })
                .distinct()
                .map(code -> {
                    CompangConfigListResData compangConfigRep = new CompangConfigListResData();
                    compangConfigRep.setCompanyCode(code);
                    compangConfigRep.setCompanyName(CompanyCodeEnum.fromCode(code).getDesc());
                    return compangConfigRep;
                })
                .collect(Collectors.toList());
    }

    /**
     * 给供应商创建一条新的物流配置，默认是未勾选
     *
     * @param shopId
     * @param configId
     * @return
     */
    private ShopLogistics createNewShopLogistics(ShopLogisticsListReq shopLogisticsListReq,Integer configId) {
        ShopLogistics shopLogisticsEntity = new ShopLogistics();
        shopLogisticsEntity.setShopId(shopLogisticsListReq.getShopId());
        shopLogisticsEntity.setShopLogisticsConfigId(configId);
        shopLogisticsEntity.setChoose(0);
        AmUserInfo userInfo = shopLogisticsListReq.getAmUserInfo();
        if (userInfo != null) {
            shopLogisticsEntity.setCreatorAccount(userInfo.getUserName());
            shopLogisticsEntity.setCreatorName(userInfo.getRealName());
            shopLogisticsEntity.setUpdateAccount(userInfo.getUserName());
            shopLogisticsEntity.setUpdateName(userInfo.getRealName());
        }
        shopLogisticsEntity.setDeleteStatus(DeleteStatusEnum.NO.getCode());
        shopLogisticsService.save(shopLogisticsEntity);
        return shopLogisticsEntity;
    }

    /**
     * 查找是否存在某条配置
     *
     * @param shopLogisticsEntityList
     * @param configId
     * @return
     */
    private ShopLogistics findExist(List<ShopLogistics> shopLogisticsEntityList, Integer configId) {
        return shopLogisticsEntityList.stream().filter(entity -> configId.equals(entity.getShopLogisticsConfigId())).findFirst().orElse(null);
    }
}

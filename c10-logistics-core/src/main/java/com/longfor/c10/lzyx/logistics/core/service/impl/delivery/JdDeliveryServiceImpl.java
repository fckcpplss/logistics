package com.longfor.c10.lzyx.logistics.core.service.impl.delivery;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.PhoneUtil;
import com.alibaba.fastjson.JSON;
import com.longfor.c10.lzyx.logistics.core.config.EBillJDConfig;
import com.longfor.c10.lzyx.logistics.core.service.IDeliveryService;
import com.longfor.c10.lzyx.logistics.entity.annotation.DeliveryCompanyType;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.*;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsDelivery;
import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeliveryTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.lop.open.api.sdk.DefaultDomainApiClient;
import com.lop.open.api.sdk.LopException;
import com.lop.open.api.sdk.domain.express.FeeQueryApi.FeeQueryParam;
import com.lop.open.api.sdk.domain.express.FeeQueryApi.ServiceResponse;
import com.lop.open.api.sdk.domain.express.GeneralWaybillQueryApi.OrderInfoQueryConditionDTO;
import com.lop.open.api.sdk.domain.express.GeneralWaybillQueryApi.ResponseDTO;
import com.lop.open.api.sdk.domain.express.GeneralWaybillQueryApi.SimpleOrderInfoDTO;
import com.lop.open.api.sdk.domain.express.OrbLsCancelWaybillInterceptService.CancelWaybillInterceptReq;
import com.lop.open.api.sdk.domain.express.Waybill2CTraceApi.BaseResult;
import com.lop.open.api.sdk.domain.express.Waybill2CTraceApi.Waybill2CTraceDto;
import com.lop.open.api.sdk.domain.express.WaybillJosService.WaybillDTO;
import com.lop.open.api.sdk.domain.express.WaybillJosService.WaybillResultInfoDTO;
import com.lop.open.api.sdk.domain.jdcloudprint.PullDataService.*;
import com.lop.open.api.sdk.plugin.LopPlugin;
import com.lop.open.api.sdk.plugin.factory.OAuth2PluginFactory;
import com.lop.open.api.sdk.request.DomainAbstractRequest;
import com.lop.open.api.sdk.request.express.*;
import com.lop.open.api.sdk.request.jdcloudprint.PullDataServiceGetTemplateListLopRequest;
import com.lop.open.api.sdk.request.jdcloudprint.PullDataServicePullDataLopRequest;
import com.lop.open.api.sdk.response.AbstractResponse;
import com.lop.open.api.sdk.response.express.*;
import com.lop.open.api.sdk.response.jdcloudprint.PullDataServiceGetTemplateListLopResponse;
import com.lop.open.api.sdk.response.jdcloudprint.PullDataServicePullDataLopResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 京东物流api
 * @author zhaoyalong
 */
@Slf4j
@Service
@DeliveryCompanyType(code = CompanyCodeEnum.JD)
public class JdDeliveryServiceImpl extends AbstractDeliverService implements IDeliveryService {
    private static final String JD_SALE_PLAT = "0030001";
    @Value("${jd.customer.code}")
    private String jdCustomerCode;
    @Value("${jd.app.key}")
    private String jdAppkey;
    @Value("${jd.app.secret}")
    private String jdAppSecret;
    @Value("${jd.server.url}")
    private String jdServerUrl;
    @Value("${jd.customer.refreshToken}")
    private String refreshToken;
    @Value("${jd.delivery.sellerNo:010K1435980}")
    private String sellerNo;

    @Autowired
    private EBillJDConfig eBillJDConfig;

    //物流公司编码，京东快递：JD
    private static final String CP_CODE = "JD";
    //pin授权码/商家授权pin兼容jospin
    private static final String PIN = "";
    //模板类型
    private static final String TEMPLATE_TYPE = "1";
    //面单类型
    private static final String WAY_TEMPLATE_TYPE = "1";
    //打印类型
    private static final String ORDER_TYPE = "PRE_View";

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //京东请求异常统一错误码
    private static final Integer JD_ERR = 100002;

    private DefaultDomainApiClient client = null;

    @PostConstruct
    void initDomainApiClient() {
        client = new DefaultDomainApiClient(jdServerUrl);
    }

    @Override
    CompanyCodeEnum getSourceCompanyCode() {
        return CompanyCodeEnum.JD;
    }

    @Override
    public AddOrderApiResData addApiOrder(AddOrderApiReqData addOrderApiReqData) {
        log.info("京东下单接口，调用参数：{}",JSON.toJSONString(addOrderApiReqData));
        WaybillJosServiceReceiveOrderInfoLopRequest request = buildAddOrderRequst(addOrderApiReqData);
        log.info("京东下单接口，构建api请求参数：{}",JSON.toJSONString(request));
        //构建响应
        AddOrderApiResData addOrderResData = new AddOrderApiResData();
        try {
            WaybillJosServiceReceiveOrderInfoLopResponse response = client.execute(request);
            log.info("京东下单接口，api返回结果：{}",JSON.toJSONString(response));
            WaybillResultInfoDTO result = response.getResult();
            //失败的请求
            if (result.getResultCode() != 100) {
                throw new BusinessException(String.format("请求京东下单接口失败[%s]", result.getResultMessage()));
            }
            addOrderResData.setOrderId(result.getOrderId());
            addOrderResData.setWaybillId(result.getDeliveryId());
            log.info("京东下单接口，下单成功，运单号 = {}",result.getDeliveryId());
        } catch (Exception e) {
            log.error("京东下单接口,请求api接口失败，error = {}",e);
            throw new BusinessException(String.format("请求京东下单接口失败[%s]", e.getMessage()));
        }
        return addOrderResData;
    }

    @Override
    public CancelOrderResData cancelApiOrder(CancelOrderReqData cancelOrderReqData) {
        log.info("京东取消接口，调用参数 = {}",JSON.toJSONString(cancelOrderReqData));
        cancelOrderReqData.setVendorCode(jdCustomerCode);
        OrbLsCancelWaybillInterceptServiceCancelOrderLopRequest request = new OrbLsCancelWaybillInterceptServiceCancelOrderLopRequest();
        CancelWaybillInterceptReq cancelRequest = new CancelWaybillInterceptReq();
        BeanUtils.copyProperties(cancelOrderReqData,cancelRequest);
        cancelRequest.setCancelTime(new Date());
        request.setCancelRequest(cancelRequest);
        setLopPlugin(request);
        //构建响应
        CancelOrderResData cancelOrderResData = new CancelOrderResData();
        log.info("京东取消接口，请求api参数 = {}",JSON.toJSONString(request));
        try {
            OrbLsCancelWaybillInterceptServiceCancelOrderLopResponse response = client.execute(request);
            log.info("京东取消接口，api返回结果 = {}",JSON.toJSONString(response));
            cancelOrderResData.setCode(response.getResult().getStateCode());
            cancelOrderResData.setMsg(response.getResult().getStateMessage());
        } catch (Exception e) {
            log.error("京东取消接口，api请求失败，errorMsg = {}",e);
            //请求京东失败
            cancelOrderResData.setCode(JD_ERR);
            cancelOrderResData.setMsg(e.getMessage());
        }
        log.info("京东取消接口，返回结果 = {}",JSON.toJSONString(cancelOrderResData));
        return cancelOrderResData;
    }

    @Override
    public boolean subLogisticsPath(SubLogisticsPathReqData subLogisticsPathReqData) {
        return true;
    }

    @Override
    public String getApiDeliverTime(DeliverTimeReqData deliverTimeReqData) {
        log.info("京东查询预计送达时间，调用参数 = {}",JSON.toJSONString(deliverTimeReqData));
        GeneralwaybillqueryapiQueryorderinfobyconditionLopRequest request = new GeneralwaybillqueryapiQueryorderinfobyconditionLopRequest();
        OrderInfoQueryConditionDTO orderInfoQueryConditionDTO = new OrderInfoQueryConditionDTO();
        orderInfoQueryConditionDTO.setCustomerCode(jdCustomerCode);
        orderInfoQueryConditionDTO.setDeliveryId(deliverTimeReqData.getWaybillId());
        //动态时间
        orderInfoQueryConditionDTO.setDynamicTimeFlag(1);
        request.setOrderInfoQueryConditionDTO(orderInfoQueryConditionDTO);
        setLopPlugin(request);
        log.info("京东查询预计送达时间，api请求参数 = {}",JSON.toJSONString(request));
        GeneralwaybillqueryapiQueryorderinfobyconditionLopResponse response = null;
        try {
            response = client.execute(request);
        } catch (Exception e) {
            log.error("京东查询预计送达时间，api请求失败",e);
        }
        log.info("京东查询预计送达时间，api请求结果 = {}",JSON.toJSONString(response));
        String deliveryPromiseTime = Optional.ofNullable(response)
                .map(GeneralwaybillqueryapiQueryorderinfobyconditionLopResponse::getResult)
                .map(ResponseDTO::getData)
                .map(SimpleOrderInfoDTO::getDeliveryPromiseTime)
                .map(d -> DateUtil.formatDateTime(d))
                .orElse(null);
        log.info("京东查询预计送达时间，返回结果 = {}",deliveryPromiseTime);
        return deliveryPromiseTime;
    }

    /**
     * 获取打印模板
     *
     * @return
     * @throws Exception
     */
    private GetTemplateListRespDTO getPrintTemplate() throws LopException {
        log.info("京东云打印，api获取打印模版，开始");
        DefaultDomainApiClient client = new DefaultDomainApiClient(eBillJDConfig.getServerUrl());
        PullDataServiceGetTemplateListLopRequest lopRequest = new PullDataServiceGetTemplateListLopRequest();

        //组装获取打印模板列表参数
        GetTemplateListReqDTO getTemplateListReqDTO = new GetTemplateListReqDTO();
        getTemplateListReqDTO.setCpCode(CP_CODE);
        getTemplateListReqDTO.setPin(PIN);
        getTemplateListReqDTO.setTemplateType(TEMPLATE_TYPE);
        getTemplateListReqDTO.setWayTempleteType(WAY_TEMPLATE_TYPE);

        //调用网关做报文的签名
        lopRequest.setGetTemplateListReqDTO(getTemplateListReqDTO);

        //物流云的oauth验证
        LopPlugin oauthPlugin = OAuth2PluginFactory.produceLopPlugin(client.getServerUrl(), eBillJDConfig.getAppKey(),
                eBillJDConfig.getAppSecret(), eBillJDConfig.getRefreshToken());
        lopRequest.addLopPlugin(oauthPlugin);
        log.info("京东云打印，api获取打印模版，请求参数，lopRequest = {}",JSON.toJSONString(lopRequest));
        PullDataServiceGetTemplateListLopResponse response = client.execute(lopRequest);
        GetTemplateListRespDTO getTemplateListRespDTO = response.getResult();
        log.info("京东云打印，api获取打印模版，请求结果，getTemplateListRespDTO = {}",JSON.toJSONString(getTemplateListRespDTO));
        return getTemplateListRespDTO;
    }

    /**
     * 获取打印数据
     *
     * @return
     * @throws Exception
     */
    private PullDataRespDTO getPrintData(List<LogisticsDelivery> deliveries) throws LopException {
        log.info("京东云打印，api获取打印数据，开始");
        DefaultDomainApiClient client = new DefaultDomainApiClient(eBillJDConfig.getServerUrl());
        PullDataServicePullDataLopRequest lopRequest = new PullDataServicePullDataLopRequest();

        //组装获取打印数据响应DTO参数
        PullDataReqDTO pullDataReqDTO = new PullDataReqDTO();
        pullDataReqDTO.setCpCode(CP_CODE);
        //objectId不超过20位
        pullDataReqDTO.setObjectId(System.currentTimeMillis() + "");
        Map<String, String> parameters = new HashMap<>(1);
        parameters.put("ewCustomerCode", eBillJDConfig.getCustomerCode());
        pullDataReqDTO.setParameters(parameters);
        List<WayBillInfo> wayBillInfos = new ArrayList<>();

        //添加非pop运单
        deliveries.forEach(deliveryEntity -> {
            WayBillInfo wayBillInfo = new WayBillInfo();
            wayBillInfo.setOrderNo(deliveryEntity.getChildOrderId());
            wayBillInfo.setJdWayBillCode(deliveryEntity.getDeliveryNo());
            wayBillInfos.add(wayBillInfo);
        });

        pullDataReqDTO.setWayBillInfos(wayBillInfos);
        //调用网关做报文的签名
        lopRequest.setPullDataReqDTO(pullDataReqDTO);
        //物流云的oauth验证
        LopPlugin oauthPlugin  = OAuth2PluginFactory.produceLopPlugin(client.getServerUrl(), eBillJDConfig.getAppKey(),
                eBillJDConfig.getAppSecret(), eBillJDConfig.getRefreshToken());
        lopRequest.addLopPlugin(oauthPlugin);
        log.info("京东云打印，api获取打印数据，请求数据，lopRequest = {}",JSON.toJSONString(lopRequest));

        //请求网关获取网关响应数据
        PullDataServicePullDataLopResponse response = null;
        try {
            response = client.execute(lopRequest);
        } catch (LopException e) {
            log.error("京东云打印,api请求异常，{}",e);
        }
        PullDataRespDTO pullDataRespDTO = response.getResult();
        log.info("京东云打印，api获取打印数据，查询结果，pullDataRespDTO = {}",JSON.toJSONString(pullDataRespDTO));
        return pullDataRespDTO;
    }

    @Override
    DeliveryPrintResData getApiPrintData(DeliveryPrintReqData deliveryPrintReqData) throws LopException {
        log.info("京东云打印，获取打印数据开始");
        //请求网关获取打印数据
        PullDataRespDTO pullDataRespDTO = getPrintData(deliveryPrintReqData.getDeliveryList());
        //请求网关获取打印模板列表数据
        GetTemplateListRespDTO getTemplateListRespDTO = getPrintTemplate();

        TemplateDTO templateDTO = Objects.requireNonNull(getTemplateListRespDTO).getDatas();
        List<StandardTemplate> standardTemplate = templateDTO.getSDatas();
        StandardTemplateDTO standardTemplateDTO = standardTemplate.get(0).getStandardTemplates().get(0);
        //组装请求WebSocket参数
        PullDataServiceDTO pullDataServiceDTO = new PullDataServiceDTO();
        Map<String, Object> parameters = new HashMap<>(8);
        pullDataServiceDTO.setOrderType(ORDER_TYPE);
        pullDataServiceDTO.setPin(PIN);
        parameters.put("tempUrl", standardTemplateDTO.getStandardTemplateUrl());
        List<String> printDataList = new ArrayList<>();
        for (PrePrintDataInfo prePrintDataInfo : Objects.requireNonNull(pullDataRespDTO).getPrePrintDatas()) {
            printDataList.add(prePrintDataInfo.getPerPrintData());
        }
        parameters.put("offsetTop", "10mm");
        parameters.put("offsetLeft", "10mm");
        parameters.put("printData", printDataList);
        parameters.put("tradeCode", eBillJDConfig.getCustomerCode());
        pullDataServiceDTO.setParameters(parameters);
        DeliveryPrintResData deliveryPrintResData = new DeliveryPrintResData();
        deliveryPrintResData.setApiResultData(JSON.toJSONString(pullDataServiceDTO));
        return deliveryPrintResData;
    }

    /**
     * costClassify：
     * 快递运费：YF
     * 代收手续费：DSSXF
     * 保费：BF
     * 签单返还：QDFHF
     * 包装费：BZF
     *
     * costNo
     * QIPSF-快递运费
     * QLBJ-快递保价费
     * QLDS-快递代收手续费
     * QLQDFH-快递签单返还
     * QLJZD-京尊达服务费
     * QLHCF-快递耗材
     * QLDZQD-快递电子签单
     */
    @Override
    public FeeResultResData queryFeeResult(FeeResultReqData feeResultReqData) {
        log.info("京东查询费率，调用参数 = {}",JSON.toJSONString(feeResultReqData));
        QueryWaybillfeeLopRequest request = new QueryWaybillfeeLopRequest();
        FeeQueryParam feeQueryParam = new FeeQueryParam();
        feeQueryParam.setBusinessNo(feeResultReqData.getBusinessNo());
        feeQueryParam.setSellerNo(sellerNo);
        request.setFeeQueryParam(feeQueryParam);
        setLopPlugin(request);
        log.info("京东查询费率，api请求参数 = {}",JSON.toJSONString(request));
        QueryWaybillfeeLopResponse response = null;
        try {
            response = client.execute(request);
        } catch (LopException e) {
            log.error("京东查询费率，接口请求错误，errorMsg = {}",e);
        }
        log.info("京东查询费率，api请求结果 = {}",JSON.toJSONString(response));
        FeeResultResData resultResData = Optional.ofNullable(response).map(QueryWaybillfeeLopResponse::getResult).map(ServiceResponse::getData).map(data -> {
            FeeResultResData fr  = new FeeResultResData();
            fr.setBusinessNo(feeResultReqData.getBusinessNo());
            fr.setCalWeight(data.getCalWeight());
            fr.setWeight(data.getWeight());
            fr.setVolume(data.getVolume());
            return fr;
        }).orElseGet(() -> {
            FeeResultResData fr = new FeeResultResData();
            fr.setCode(JD_ERR);
            fr.setMsg("京东查询费率失败");
            return fr;
        });
        log.info("京东查询费率，返回结果 = {}",JSON.toJSONString(resultResData));
        return resultResData;
    }

    /**
     * 构建京东下单请求参数
     */
    private WaybillJosServiceReceiveOrderInfoLopRequest buildAddOrderRequst(AddOrderApiReqData addOrderApiReqData){
        WaybillJosServiceReceiveOrderInfoLopRequest request = new WaybillJosServiceReceiveOrderInfoLopRequest();
        WaybillDTO waybillDTO = new WaybillDTO();
        request.setWaybillDTO(waybillDTO);
        //发货人
        String senderMobile = addOrderApiReqData.getSender().getMobile();
        String senderTel = addOrderApiReqData.getSender().getTel();
        if (StringUtils.isNotBlank(senderMobile) && StringUtils.isBlank(senderTel) && !PhoneUtil.isMobile(senderMobile)) {
            // 如果senderMobile填的是座机号
            senderTel = senderMobile;
            senderMobile = null;
        }
        AddOrderApiReqData.Sender sender = addOrderApiReqData.getSender();
        waybillDTO.setSenderName(sender.getName());
        waybillDTO.setSenderTel(senderTel);
        waybillDTO.setSenderMobile(senderMobile);

        String senderAddres = sender.getProvince() + sender.getCity() + sender.getArea() + sender.getAddress();
        waybillDTO.setSenderAddress(senderAddres);
        //收货人
        AddOrderApiReqData.Receiver receiver = addOrderApiReqData.getReceiver();
        waybillDTO.setReceiveName(receiver.getName());
        waybillDTO.setReceiveTel(receiver.getTel());
        waybillDTO.setReceiveMobile(receiver.getMobile());
        waybillDTO.setProvince(receiver.getProvince());
        waybillDTO.setCity(receiver.getCity());
        String receiveAddress = receiver.getProvince() + receiver.getCity() + receiver.getArea() + receiver.getAddress();
        waybillDTO.setReceiveAddress(receiveAddress);
        //包裹信息
        AddOrderApiReqData.Cargo cargo = addOrderApiReqData.getCargo();
        waybillDTO.setPackageCount(cargo.getCount());
        waybillDTO.setWeight(cargo.getWeight() != null ? cargo.getWeight().doubleValue() : 0);
        waybillDTO.setVloumLong(cargo.getSpaceX() != null ? cargo.getWeight().doubleValue() : 0);
        waybillDTO.setVloumWidth(cargo.getSpaceY() != null ? cargo.getWeight().doubleValue() : 0);
        waybillDTO.setVloumHeight(cargo.getSpaceZ() != null ? cargo.getWeight().doubleValue() : 0);
        //体积默认
        waybillDTO.setVloumn(1.0);
        //商品
        AddOrderApiReqData.Shop shop = addOrderApiReqData.getShop();
        waybillDTO.setGoods(shop.getGoodsName());
        waybillDTO.setGoodsCount(shop.getGoodsCount());
        //保价服务
        AddOrderApiReqData.Insured insured = addOrderApiReqData.getInsured();
        waybillDTO.setGuaranteeValue(insured.getUseInsured());
        if (insured.getUseInsured() == 1) {
            waybillDTO.setGuaranteeValueAmount(insured.getInsuredValue().doubleValue() / 100);
        }
        //运单备注
        waybillDTO.setRemark(addOrderApiReqData.getCustomRemark());
        //取件时间
        if (addOrderApiReqData.getPickUpStartTime() != null) {
            waybillDTO.setPickUpStartTime(new Date(addOrderApiReqData.getPickUpStartTime()));
        }
        if (addOrderApiReqData.getPickUpEndTime() != null) {
            waybillDTO.setPickUpEndTime(new Date(addOrderApiReqData.getPickUpEndTime()));
        }
        //关键配置参数
        //安全认证
        setLopPlugin(request);
        waybillDTO.setOrderId(addOrderApiReqData.getOrderId());
        waybillDTO.setSalePlat(JD_SALE_PLAT);
        waybillDTO.setCustomerCode(jdCustomerCode);
        waybillDTO.setPromiseTimeType(DeliveryTypeEnum.JD_THS.getServiceType());
        return request;
    }

    /**
     * 京东物流生成oauthPlugin
     */
    private void setLopPlugin(DomainAbstractRequest<? extends AbstractResponse> request) {
        LopPlugin oauthPlugin;
        try {
            oauthPlugin = OAuth2PluginFactory.produceLopPlugin(jdServerUrl, jdAppkey, jdAppSecret, getRefreshToken());
        } catch (LopException e) {
            log.error("京东物流生成oauthPlugin出错", e);
            throw new BusinessException(String.format("京东物流生成oauthPlugin出错:%s", e.getMessage()));
        }
        request.addLopPlugin(oauthPlugin);
    }

    /**
     * 京东实时查询物流轨迹
     */
    @Override
    public DeliveryPathResData getLatestDeliveryPath(DeliveryPathReqData deliveryPathReqData) {
        log.info("京东查询实时轨迹，调用参数 = {}", JSON.toJSONString(deliveryPathReqData));
        //构建请求
        Waybill2CTraceApiGetWaybill2CTraceByWaybillCodeLopRequest request = new Waybill2CTraceApiGetWaybill2CTraceByWaybillCodeLopRequest();
        Waybill2CTraceDto waybillTraceDto = new Waybill2CTraceDto();
        waybillTraceDto.setTradeCode(jdCustomerCode);
        waybillTraceDto.setWaybillCode(deliveryPathReqData.getWaybillId());
        request.setWaybill2CTraceDto(waybillTraceDto);
        setLopPlugin(request);
        //构建响应
        log.info("京东查询实时轨迹，api请求参数 = {}", JSON.toJSONString(request));
        Waybill2CTraceApiGetWaybill2CTraceByWaybillCodeLopResponse response = null;
        try {
            response = client.execute(request);
        } catch (Exception e) {
            log.error("京东查询实时轨迹，api请求失败", e);
        }
        log.info("京东查询实时轨迹，api请求结果 = {}", JSON.toJSONString(response));

        //构造返回结果
        DeliveryPathResData deliveryPathResData = Optional.ofNullable(response).map(r -> {
            DeliveryPathResData dpr = new DeliveryPathResData();
            dpr.setCompanyCode(deliveryPathReqData.getCompanyCode());
            dpr.setSourceCompanyCode(getSourceCompanyCode());
            dpr.setWaybillId(deliveryPathReqData.getWaybillId());
            dpr.setPathItemList(Optional.ofNullable(r.getResult())
                    .map(BaseResult::getData).map(pathList -> {
                        return ListUtils.emptyIfNull(pathList)
                                .stream()
                                .map(p -> new DeliveryPathResData.PathItem(p.getOperateTime().getTime(), p.getOperateDesc(), p.getOperateMessage(),String.valueOf(p.getState())))
                                .sorted(Comparator.comparing(DeliveryPathResData.PathItem::getPathTime).reversed())
                                .collect(Collectors.toList());
                    }).orElse(Collections.emptyList()));
            //最新物流状态
            ListUtils.emptyIfNull(dpr.getPathItemList()).stream().findFirst().ifPresent(item -> {
                dpr.setPathState(item.getPathState());
            });
            return dpr;
        }).orElseGet(() -> new DeliveryPathResData());
        log.info("京东查询实时轨迹，请求返回结果 = {}", JSON.toJSONString(deliveryPathResData));
        return deliveryPathResData;
    }

    private String getRefreshToken() {
        return refreshToken;
    }

    @Data
    static class PullDataServiceDTO {
        private String orderType;
        private String pin;
        private Map<String, Object> parameters;
    }

    public static void main(String[] args) {
        System.out.println(PhoneUtil.isPhone("10518471901"));
    }
}

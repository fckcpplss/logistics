package com.longfor.c10.lzyx.logistics.core.service.impl.delivery;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.ImmutableMap;
import com.longfor.c10.lzyx.logistics.core.config.EBillSFConfig;
import com.longfor.c10.lzyx.logistics.core.service.IDeliveryService;
import com.longfor.c10.lzyx.logistics.core.util.DesensitizedUtils;
import com.longfor.c10.lzyx.logistics.core.util.SFClient;
import com.longfor.c10.lzyx.logistics.dao.service.*;
import com.longfor.c10.lzyx.logistics.entity.annotation.DeliveryCompanyType;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.AmUserInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.delivery.*;
import com.longfor.c10.lzyx.logistics.entity.dto.shunfeng.*;
import com.longfor.c10.lzyx.logistics.entity.entity.*;
import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.DeleteStatusEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.SFFeeTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.enums.SfProductTypeEnum;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.sf.csim.express.service.CallExpressServiceTools;
import com.sf.csim.express.service.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 顺丰物流服务
 * @author zhaoyalong
 */
@Slf4j
@Service
@DeliveryCompanyType(code = CompanyCodeEnum.SF)
public class SfDeliveryServiceImpl extends AbstractDeliverService implements IDeliveryService {

    @Autowired
    private IShopLogisticsService shopLogisticsService;

    @Autowired
    private ILogisticsDeliveryService logisticsDeliveryService;

    @Autowired
    private EBillSFConfig sfConfig;

    @Autowired
    private ILogisticsOrderGoodsService logisticsOrderGoodsService;

    @Autowired
    private ILogisticsDeliveryCompanyService logisticsDeliveryCompanyService;

    @Autowired
    private ILogisticsEbillService logisticsEbillService;


    @Value("${sf.delivery.discount:1}")
    private Double discount;

    @Value("${sf.delivery.express-type:231}")
    private String expressType;

    @Value("${sf.delivery.appid}")
    private String appId;

    @Value("${sf.delivery.sk}")
    private String sk;

    @Value("${sf.delivery.url}")
    private String url;

    private SFClient sfClient;

    private static final String SERVICE_CODE = "COM_RECE_CLOUD_PRINT_WAYBILLS";

    private static final String FILE_TYPE_PDF = "pdf";
    /**
     * 统一接入平台校验成功
     */
    public static final String A1000 = "A1000";

    @PostConstruct
    public void init(){
        sfClient = new SFClient(appId,sk,url);
    }


    @Override
    CompanyCodeEnum getSourceCompanyCode() {
        return CompanyCodeEnum.SF;
    }

    @Override
    public AddOrderApiResData addApiOrder(AddOrderApiReqData addOrderApiReqData) {
        log.info("顺丰下单，api调用参数 = {}", JSON.toJSONString(addOrderApiReqData));
        SFAddOrderResBody sfBody = extractAddOrderReqDataToSfBody(addOrderApiReqData);
        // 生成签名并请求
        SFApiResData sfApiResData = sfClient.post(SFConstant.ADD_ORDER_URL,sfBody);
        log.info("顺丰下单，api请求结果 = {}",JSON.toJSONString(sfApiResData));
        SFAddOrderResData sfAddOrderResData = Optional.ofNullable(sfApiResData)
                .map(SFApiResData::getResult)
                .map(JSON::toJSONString)
                .map(result -> JSONObject.parseObject(result,SFAddOrderResData.class))
                .orElseThrow(() -> new BusinessException("顺丰下单失败"));
        AddOrderApiResData addOrderApiResData = new AddOrderApiResData();
        addOrderApiResData.setWaybillId(sfAddOrderResData.getMailno());
        addOrderApiResData.setOrderId(sfAddOrderResData.getOrderid());
        log.info("顺丰下单，返回结果 = {}", JSON.toJSONString(addOrderApiResData));
        return addOrderApiResData;
    }
    @Override
    public CancelOrderResData cancelApiOrder(CancelOrderReqData cancelOrderReqData) {
        log.info("顺丰取消订单，api调用参数 = {}", JSON.toJSONString(cancelOrderReqData));
        SFApiResData sfApiResData = sfClient.post(SFConstant.CANCEL_ORDER_URL, ImmutableMap.of("companyId", appId, "orderId", cancelOrderReqData.getOrderId()));
        log.info("顺丰取消订单，api请求结果 = {}",JSON.toJSONString(sfApiResData));
        SFCancelOrderResData sfCancelOrderResData = Optional.ofNullable(sfApiResData)
                .map(JSON::toJSONString)
                .map(item -> JSONObject.parseObject(item,SFCancelOrderResData.class))
                .orElseThrow(() -> new BusinessException("顺丰取消订单错误"));
        CancelOrderResData cancelOrderResData = new CancelOrderResData();
        cancelOrderResData.setCode("ok".equals(sfCancelOrderResData.getSucc()) ? 200 : 500);
        cancelOrderResData.setMsg(sfCancelOrderResData.getMsg());
        log.info("顺丰取消订单，返回结果 = {}", JSON.toJSONString(cancelOrderResData));
        return cancelOrderResData;
    }

    @Override
    public DeliveryPathResData getLatestDeliveryPath(DeliveryPathReqData deliveryPathReqData) {
        List<SFOrderRouteDTO> sfOrderRouteDTOs = null;
        if(StringUtils.isBlank(deliveryPathReqData.getRoutePathData())){
            log.info("顺丰实时查询物流轨迹，api调用参数 = {}", JSON.toJSONString(deliveryPathReqData));
            LogisticsDelivery logisticsDelivery = Optional.ofNullable(logisticsDeliveryService.getOne(Wrappers.<LogisticsDelivery>lambdaQuery()
                    .eq(LogisticsDelivery::getDeliveryNo, deliveryPathReqData.getWaybillId())
                    .eq(LogisticsDelivery::getDeleteStatus, 0)
                    .last(" limit 1"))).orElseThrow(() -> new BusinessException(StrUtil.format("运单信息不存在")));
            log.info("顺丰实时查询物流轨迹，运单信息 = {}", JSON.toJSONString(logisticsDelivery));

            //查询顺丰接口
            SFApiResData sfApiResData = sfClient.post(SFConstant.LIST_ROUTE_URL,ImmutableMap.of("companyId", appId, "orderId", logisticsDelivery.getId()));
            log.info("顺丰实时查询物流轨迹，api请求结果 = {}",JSON.toJSONString(sfApiResData));
            sfOrderRouteDTOs = Optional.ofNullable(sfApiResData)
                    .map(SFApiResData::getResult)
                    .map(JSON::toJSONString)
                    .map(result -> JSONArray.parseArray(result,SFOrderRouteDTO.class))
                    .orElse(Collections.emptyList());
        }else{
            log.info("顺丰实时查询物流轨迹，以路由推送为准");
            sfOrderRouteDTOs = new ArrayList<>(Arrays.asList(JSONObject.parseObject(deliveryPathReqData.getRoutePathData(),SFOrderRouteDTO.class)));
        }
        //返回
        DeliveryPathResData deliveryPathResData = new DeliveryPathResData();
        deliveryPathResData.setCompanyCode(deliveryPathReqData.getCompanyCode());
        deliveryPathResData.setSourceCompanyCode(getSourceCompanyCode());
        deliveryPathResData.setPathItemList(ListUtils.emptyIfNull(sfOrderRouteDTOs).stream().map(route -> {
            deliveryPathResData.setWaybillId(route.getMailno());
            DeliveryPathResData.PathItem pathItem = new DeliveryPathResData.PathItem();
            pathItem.setPathDes(route.getAcceptAddress());
            pathItem.setPathMsg(route.getRemark());
            pathItem.setPathTime(Optional.ofNullable(route.getAcceptTotaltime()).orElse(DateUtil.parse(route.getAcceptTime())).getTime());
            pathItem.setPathState(route.getOpcode());
            return pathItem;
        }).collect(Collectors.toList()));
        //最新物流状态
        ListUtils.emptyIfNull(deliveryPathResData.getPathItemList()).stream().findFirst().ifPresent(item -> {
            deliveryPathResData.setPathState(item.getPathState());
        });
        log.info("顺丰实时查询物流轨迹，返回信息 = {}", JSON.toJSONString(deliveryPathResData));
        return deliveryPathResData;
    }

    @Override
    public FeeResultResData queryFeeResult(FeeResultReqData feeResultReqData) {
        log.info("顺丰查询费率，api调用参数 = {}",JSON.toJSONString(feeResultReqData));
        SFApiResData sfApiResData = sfClient.post(SFConstant.GET_FREIGHT_URL, ImmutableMap.of("companyId",appId,"orderId",feeResultReqData.getOrderId()));
        log.info("顺丰查询费率，api请求结果 = {}",JSON.toJSONString(sfApiResData));
        SFFreightDTO sFFreightDTO = Optional.ofNullable(sfApiResData)
                .map(SFApiResData::getResult)
                .map(JSON::toJSONString)
                .map(result -> JSONObject.parseObject(result,SFFreightDTO.class))
                .orElseThrow(() -> new BusinessException("顺丰查询费率失败"));
        FeeResultResData feeResultResData = new FeeResultResData();
        feeResultResData.setCustomerAcctCode(sFFreightDTO.getCustomerAcctCode());
        feeResultResData.setBusinessNo(feeResultReqData.getBusinessNo());
        BigDecimal weight = BigDecimal.valueOf(Float.parseFloat(sFFreightDTO.getMeterageWeightQty()));
        feeResultResData.setCalWeight(weight);
        feeResultResData.setWeight(weight);
        feeResultResData.setSenderAddress(sFFreightDTO.getConsignorAddr());
        feeResultResData.setReceiverAddress(sFFreightDTO.getAddresseeAddr());
        feeResultResData.setFeeInfoList(ListUtils.emptyIfNull(sFFreightDTO.getFeeList()).stream()
                .map(fee -> {
                    FeeResultResData.FeeInfo feeInfo = new FeeResultResData.FeeInfo();
                    double standardAmount = Double.parseDouble(fee.getValue());
                    feeInfo.setStandardAmount(BigDecimal.valueOf(standardAmount));
                    feeInfo.setCostNo(fee.getType());
                    feeInfo.setCostName(fee.getName());
                    if (StringUtils.isNotBlank(fee.getPaymentTypeCode())) {
                        feeInfo.setPaymentType(Byte.parseByte(fee.getPaymentTypeCode()));
                    }
                    if (StringUtils.isNotBlank(fee.getSettlementTypeCode())) {
                        feeInfo.setSettlementType(Byte.parseByte(fee.getSettlementTypeCode()));
                    }
                    if(SFFeeTypeEnum.YF.getCode().equals(fee.getType())){
                        // 注意!!! 外部需要判断是否是平台物流，平台物流才有折扣
                        feeInfo.setActualAmount(BigDecimal.valueOf(standardAmount * discount));
                    }else {
                        feeInfo.setActualAmount(BigDecimal.valueOf(standardAmount));
                    }
                    SFFeeTypeEnum typeEnum = ObjectUtils.defaultIfNull(SFFeeTypeEnum.fromCode(fee.getType()), SFFeeTypeEnum.YF);
                    if(typeEnum.equals(SFFeeTypeEnum.YF)){
                        // 运费
                        feeInfo.setCostClassify(SFFeeTypeEnum.YF.name());
                    }else if(typeEnum.equals(SFFeeTypeEnum.BF)){
                        // 保价费
                        feeInfo.setCostClassify(SFFeeTypeEnum.BF.name());
                    }
                    return feeInfo;
                })
                .collect(Collectors.toList()));
        log.info("顺丰查询费率，返回结果 = {}",JSON.toJSONString(feeResultResData));
        return feeResultResData;
    }

    @Override
    public boolean subLogisticsPath(SubLogisticsPathReqData subLogisticsPathReqData) {
        return true;
    }

    @Override
    public String getApiDeliverTime(DeliverTimeReqData deliverTimeReqData) {
        log.info("顺丰查询预计送达时间，调用参数 = {}",JSON.toJSONString(deliverTimeReqData));
        Map<String,Object> req = ImmutableMap.of("companyId",appId,
                "waybillNo",deliverTimeReqData.getWaybillId(),
                "checkType",1,
                "checkNos",deliverTimeReqData.getPhone());
        log.info("顺丰查询预计送达时间，api请求参数 = {}",JSON.toJSONString(req));
        SFApiResData sfApiResData = sfClient.post(SFConstant.GET_ESTIMATED_DELIVERY_TIME_URL,req);
        log.info("顺丰查询预计送达时间，api请求结果 = {}",JSON.toJSONString(sfApiResData));

        SFGetEstimatedDeliveryTimeResData sfGetEstimatedDeliveryTimeResData = Optional.ofNullable(sfApiResData)
                .map(SFApiResData::getResult)
                .map(JSON::toJSONString)
                .map(result -> JSONObject.parseObject(result,SFGetEstimatedDeliveryTimeResData.class))
                .orElseThrow(() -> new BusinessException("顺丰查询预计送达时间错误"));
        String promiseTm = sfGetEstimatedDeliveryTimeResData.getPromiseTm();
        log.info("顺丰查询预计送达时间，返回结果 = {}",promiseTm);
        return promiseTm;
    }

    @Override
    DeliveryPrintResData getApiPrintData(DeliveryPrintReqData deliveryPrintReqData) throws UnsupportedEncodingException {
        log.info("顺丰云打印，api请求打印数据，开始");
        EBillSFReq req = new EBillSFReq();
        req.setTemplateCode(sfConfig.getTemplateCode());
        req.setFileType(FILE_TYPE_PDF);

        String requestId = UUID.randomUUID().toString().replace("-", "");
        deliveryPrintReqData.getDeliveryList().forEach(deliveryEntity -> {
            Document document = assembleDocs(deliveryEntity);
            req.getDocuments().add(document);
        });

        String msgData = JSON.toJSONString(req);
        Map<String, String> params = new HashMap<>(6);
        String timeStamp = String.valueOf(System.currentTimeMillis());
        params.put("partnerID", sfConfig.getClientCode());
        params.put("requestID", requestId);
        params.put("serviceCode", SERVICE_CODE);
        params.put("timestamp", timeStamp);
        params.put("msgData", msgData);
        params.put("msgDigest", CallExpressServiceTools.getMsgDigest(msgData, timeStamp, sfConfig.getCheckWord()));

        log.info("顺丰云打印，api请求打印数据，请求参数，params = {}",JSON.toJSONString(params));
        String result = HttpClientUtil.post(sfConfig.getCallUrl(), params);
        log.info("顺丰云打印，api请求打印数据，请求结果，result = {}",JSON.toJSONString(result));
        EBillSFResp resp = JSON.parseObject(result, EBillSFResp.class);
        boolean success = A1000.equals(resp.getApiResultCode());
        if (success) {
            //成功保存打印信息
            saveEBill(deliveryPrintReqData.getDeliveryList(),deliveryPrintReqData.getAmUserInfo());
        }
        DeliveryPrintResData deliveryPrintResData = new DeliveryPrintResData();
        deliveryPrintResData.setApiErrorMsg(resp.getApiErrorMsg());
        deliveryPrintResData.setApiResponseID(resp.getApiResponseID());
        deliveryPrintResData.setApiResultCode(resp.getApiResultCode());
        deliveryPrintResData.setApiResultData(resp.getApiResultData());
        log.info("顺丰云打印，api请求打印数据，返回信息，deliveryPrintResData = {}",JSON.toJSONString(deliveryPrintResData));
        return deliveryPrintResData;
    }

    /**
     * 顺丰下单请求参数转换
     */
    private SFAddOrderResBody extractAddOrderReqDataToSfBody(AddOrderApiReqData addOrderApiReqData) {
        String senderMobile = addOrderApiReqData.getSender().getMobile();
        String senderTel = addOrderApiReqData.getSender().getTel();
        log.info("顺丰下单，寄件电话 = {}，座机 = {}",senderMobile,senderTel);
        if (StringUtils.isNotBlank(senderMobile) && StringUtils.isBlank(senderTel) && !PhoneUtil.isMobile(senderMobile)) {
            // 如果senderMobile填的是座机号
            log.info("顺丰寄件设置senderTel: {}", senderMobile);
            senderTel = senderMobile;
            senderMobile = null;
        }

        String isDoCall = SfProductTypeEnum.fromCode(StrUtil.emptyToDefault(addOrderApiReqData.getExpressType(), expressType)).getIsDoCall();
        SFAddOrderResBody sfAddOrderResBody = new SFAddOrderResBody();
        sfAddOrderResBody.setCompanyId(appId);
        sfAddOrderResBody.setOrderId(addOrderApiReqData.getOrderId());
        sfAddOrderResBody.setJContact(addOrderApiReqData.getSender().getName());

        sfAddOrderResBody.setJTel(senderTel);
        sfAddOrderResBody.setJMobile(senderMobile);
        sfAddOrderResBody.setJProvince(addOrderApiReqData.getSender().getProvince());
        sfAddOrderResBody.setJCity(addOrderApiReqData.getSender().getCity());
        sfAddOrderResBody.setJAddress(addOrderApiReqData.getSender().getArea() + addOrderApiReqData.getSender().getAddress());
        sfAddOrderResBody.setIsDoCall(isDoCall);
        sfAddOrderResBody.setDContact(addOrderApiReqData.getReceiver().getName());
        sfAddOrderResBody.setDTel(addOrderApiReqData.getReceiver().getTel());
        sfAddOrderResBody.setDMobile(addOrderApiReqData.getReceiver().getMobile());
        sfAddOrderResBody.setDProvince(addOrderApiReqData.getReceiver().getProvince());
        sfAddOrderResBody.setDCity(addOrderApiReqData.getReceiver().getCity());
        sfAddOrderResBody.setDAddress(addOrderApiReqData.getReceiver().getArea() + addOrderApiReqData.getReceiver().getAddress());
        //结算方式默认-0-寄付月结
        sfAddOrderResBody.setPayMethod("0");
        sfAddOrderResBody.setExpressType(StrUtil.emptyToDefault(addOrderApiReqData.getExpressType(), expressType));
        sfAddOrderResBody.setDepositumInfo(addOrderApiReqData.getShop().getGoodsName());
        sfAddOrderResBody.setDepositumNo(addOrderApiReqData.getCargo().getCount() + "");
        sfAddOrderResBody.setCustid(addOrderApiReqData.getAccount());
        log.info("顺丰下单，组装下单请求参数完成 = {}",JSON.toJSONString(sfAddOrderResBody));
        return sfAddOrderResBody;
    }

    /**
     * 组装顺丰面单数据
     * @param delivery
     * @param order
     * @return
     */
    private Document assembleDocs(LogisticsDelivery delivery) {
        log.info("顺丰云打印，组装面单数据");
        Document doc = new Document();
        doc.setMasterWaybillNo(delivery.getDeliveryNo());
        doc.setIsPrintLogo(Boolean.TRUE.toString());
        doc.setSystemSource("scp");
        doc.setPrintNum("1");
        doc.setPrintDateTime(DateUtil.now());

        if (Objects.isNull(delivery.getShopLogisticsId())) {
            doc.setAgingText("陆运");
        } else {
            ShopLogistics shopLogistics = shopLogisticsService.getById(delivery.getShopLogisticsId());
            String expressType = shopLogistics.getExpressType();
            if (org.springframework.util.StringUtils.isEmpty(expressType)) {
                doc.setAgingText("陆运");
            } else {
                doc.setAgingText(SfProductTypeEnum.fromCode(expressType).getDesc());
            }
        }
        doc.setDestRouteLabel("028");

        List<LogisticsOrderGoods> orderGoodsEntities = logisticsOrderGoodsService.list(Wrappers.<LogisticsOrderGoods>lambdaQuery()
                .select(LogisticsOrderGoods::getGoodsName,LogisticsOrderGoods::getSkuSpecs,LogisticsOrderGoods::getGoodsNum)
                .eq(LogisticsOrderGoods::getLogisticsDeliveryId, delivery.getId())
                .eq(LogisticsOrderGoods::getDeleteStatus, DeleteStatusEnum.NO.getCode()));
        if(CollectionUtils.isNotEmpty(orderGoodsEntities)){
            //设置寄托物
            doc.setEntrustedArticles(orderGoodsEntities
                    .stream()
                    .map(goodsInfo -> new StringBuilder(goodsInfo.getGoodsName()).append("/").append(goodsInfo.getSkuSpecs()).append("/").append(goodsInfo.getGoodsNum()))
                    .collect(Collectors.joining("\n")));
        }
        doc.setFromName(delivery.getSendName());
        doc.setFromPhone(DesensitizedUtils.maskMobilePhone(delivery.getSendPhone()));
        Optional.ofNullable(logisticsDeliveryCompanyService.getOne(Wrappers.<LogisticsDeliveryCompany>lambdaQuery()
                .eq(LogisticsDeliveryCompany::getCompanyCode,delivery.getCompanyCode())
                .eq(LogisticsDeliveryCompany::getStatus,1))).ifPresent(companyInfo -> {
            doc.setFromOrgName(companyInfo.getCompanyName());
        });
        String sendAddress = delivery.getSendProvince() + delivery.getSendCity() + delivery.getSendArea() + delivery.getSendAddress();
        doc.setFromAddress(sendAddress);
        doc.setFromPostcode("028");
        doc.setToName(delivery.getReceiptName());
        doc.setToPhone(DesensitizedUtils.maskMobilePhone(delivery.getReceiptPhone()));
        String receiptAddress = delivery.getReceiptProvince() + delivery.getReceiptCity() + delivery.getReceiptArea() + delivery.getReceiptAddress();
        doc.setToAddress(receiptAddress);
        doc.setPayment("寄付月结");
        doc.setTwoDimensionCode("MMM={'k1':'028','k2':'028','k3':'','k4':'T6','k5':'SF1338226194423','k6':'','k7':'f935dc05'}");
        doc.setOrderNo(delivery.getChildOrderId());
        doc.setRealName(Boolean.TRUE.toString());
        log.info("顺丰云打印，组装面单数据，组装结果，doc = {}",JSON.toJSONString(doc));
        return doc;
    }

    /**
     * 保存eBill数据
     *
     * @param deliveries
     */
    private void saveEBill(List<LogisticsDelivery> deliveries, AmUserInfo amUserInfo) {
        log.info("顺丰云打印，保存打印信息");
        deliveries.stream().forEach(delivery -> {
            LogisticsEbill eBillEntity = new LogisticsEbill();
            eBillEntity.setId(IdWorker.getId());
            eBillEntity.setCompanyCode(CompanyCodeEnum.SF.getCode());
            eBillEntity.setDeliveryNo(delivery.getDeliveryNo());
            eBillEntity.setStatus(1);
            eBillEntity.setCreatorAccount(Optional.ofNullable(amUserInfo).map(AmUserInfo::getUserName).orElse("未知"));
            eBillEntity.setCreatorName(Optional.ofNullable(amUserInfo).map(AmUserInfo::getRealName).orElse("未知"));
            eBillEntity.setUpdateAccount(Optional.ofNullable(amUserInfo).map(AmUserInfo::getUserName).orElse("未知"));
            eBillEntity.setUpdateName(Optional.ofNullable(amUserInfo).map(AmUserInfo::getRealName).orElse("未知"));
            eBillEntity.setCreateTime(new Date());
            eBillEntity.setUpdateTime(new Date());
            logisticsEbillService.saveOrUpdate(eBillEntity, Wrappers.<LogisticsEbill>lambdaQuery().eq(LogisticsEbill::getDeliveryNo, eBillEntity.getDeliveryNo()));
        });
    }
}

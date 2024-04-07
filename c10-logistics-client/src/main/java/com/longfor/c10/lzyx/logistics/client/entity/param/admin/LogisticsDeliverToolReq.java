package com.longfor.c10.lzyx.logistics.client.entity.param.admin;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.Data;

import java.util.List;

/**
 * 修改运单号工具 请求实体
 * @Author dongshaopeng
 * @Date 2022/6/14 4:49 下午
 * @Param
 * @return
 **/
@Data
public class LogisticsDeliverToolReq extends BaseReqData {

    /**
     * 订单编号
     */
    private String childOrderId;

    /**
     * 原运单id
     */
    private String logisticsDeliveryId;

    /**
     * 新运单号
     */
    private String newDeliverNo;

    /**
     * 新物流公司编码
     */
    private String newLogisticsCompanyCode;

    /**
     * 新运费承担方
     */
    private Integer logisticsType;

    /**
     * 附件
     */
    private List<AttachmentLinkDto> attachmentList;

    /**
     * 备注
     */
    private String remark;

}

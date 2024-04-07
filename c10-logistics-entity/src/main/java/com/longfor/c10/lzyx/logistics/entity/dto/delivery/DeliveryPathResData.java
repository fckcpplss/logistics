package com.longfor.c10.lzyx.logistics.entity.dto.delivery;


import com.longfor.c10.lzyx.logistics.entity.enums.CompanyCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPathResData implements Serializable {
    /**
     * 运单ID
     */
    private String waybillId;

    /**
     * 快递公司编码
     */
    private String companyCode;

    /**
     * 快递公司类型编码
     */
    private CompanyCodeEnum sourceCompanyCode;

    /**
     * 轨迹节点列表
     */
    private List<PathItem> pathItemList;

    /**
     * 轨迹节点状态
     */
    private String pathState;

    /**
     * 物流状态，系统内部状态
     */
    private Integer logisticsStatus;

    /**
     * 物流订单状态，系统内部状态
     */
    private Integer orderStatus;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PathItem implements Serializable {
        /**
         * 轨迹节点 Unix 时间戳
         */
        private Long pathTime;
        /**
         * 轨迹节点描述
         */
        private String pathDes;
        /**
         * 	轨迹节点详情
         */
        private String pathMsg;
        /**
         * 轨迹节点状态
         */
        private String pathState;
    }
}

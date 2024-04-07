package com.longfor.c10.lzyx.logistics.client.entity.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 物流运单监控列表返回VO
 * @author zhaoyl
 * @date 2022/2/17 下午3:52
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsMonitorListResVO extends LogisticsMonitorListResData {
    /**
     *物流状态展示
     */
    private String logisticsStatusShow;

    /**
     *物流类型展示
     */
    private String logisticsTypeShow;

    /**
     * 运费承担方展示
     */
    private String feeTypeShow;

    /**
     *订单状态展示
     */
    private String orderStatusShow;

    /**
     *是否取消中文展示
     */
    private String ifCancelShow;


    /**
     * 商品信息
     */
    private List<LogisticsMonitorListGodsVO> goodsResList;

    /**
     * 异常附件
     */
    private List<LogisticsMonitorAttachementVO> attachment1s;

}

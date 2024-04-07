package com.longfor.c10.lzyx.logistics.entity.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName : PathItemResData
 * @Description :
 * @Author : chengshangwei
 * @Date: 2020-07-08 10:22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PathItemResData implements Serializable {
    @ApiModelProperty(value = "轨迹节点 yyyy-MM-dd")
    private String pathTime;

    @ApiModelProperty(value = "轨迹节点描述")
    private String pathDes;

    @ApiModelProperty(value = "轨迹节点详情")
    private String pathMsg;

    @ApiModelProperty(value = "轨迹节点状态：0：运输中 1：已揽件 2：待处理 3：已签收 4：已退签 5：派件中 6：已退回 7：转单 8：待清关 9：清关中 10：已清关 11：清关异常 12：收件人拒签 ")
    private String pathState;

    @ApiModelProperty(value = "物流中心状态名称")
    private String logisticsCenterStateName;
}

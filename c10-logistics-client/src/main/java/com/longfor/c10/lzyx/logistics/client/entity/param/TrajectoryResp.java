package com.longfor.c10.lzyx.logistics.client.entity.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 查看物流轨迹Resp
 * @author: jiamingqiang
 * @date: 2021/10/25
 */
@ApiModel(value = "查看物流轨迹Resp")
@Data
public class TrajectoryResp {

    @ApiModelProperty(value = "预计到达时间")
    private String deliverTime;

    @ApiModelProperty(value = "物流轨迹节点信息")
    private List<PathItemResData> pathItemList;
}

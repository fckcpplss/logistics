package com.longfor.c10.lzyx.logistics.entity.dto.merchant;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @description: 查看物流轨迹Req
 * @author: zhaoyalong
 * @date: 2021/11/01
 */
@Data
@ApiModel(value = "打印面单Req")
public class ExpressEBillReqData extends BaseReqData {
    @NotEmpty(message = "快递单号不能为空")
    private List<String> deliveryNos;
}

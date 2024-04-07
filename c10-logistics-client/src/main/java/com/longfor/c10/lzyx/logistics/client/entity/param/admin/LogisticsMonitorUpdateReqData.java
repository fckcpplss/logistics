package com.longfor.c10.lzyx.logistics.client.entity.param.admin;

import com.longfor.c10.lzyx.logistics.client.entity.vo.admin.LogisticsMonitorAttachementVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 监控运单更新请求参数
 * @author zhaoyl
 * @date 2022/2/17 下午5:21
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsMonitorUpdateReqData {
    /**
     * 物流运单id
     */
    @NotBlank(message = "运单id不能为空")
    private String logisticsDeliveryId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 附件列表
     */
    private List<LogisticsMonitorAttachementVO> attachment1s;


}

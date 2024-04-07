package com.longfor.c10.lzyx.logistics.entity.dto.tools;

import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import lombok.Data;

import java.io.Serializable;

/**
 * 运维工具-批量核销
 * @author  renwei03
 **/
@Data
public class LogisticsOrderVerifyRecordReq extends BaseReqData implements Serializable {
    private String childOrderId;
}

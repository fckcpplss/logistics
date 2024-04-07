package com.longfor.c10.lzyx.logistics.client.entity.param.admin;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * 运维工具-批量核销
 * @author  renwei03
 **/
@Data
public class LogisticsOrderVerifyReq extends BaseReqData implements Serializable {
    private String orderFileName;
    /**
     * 订单文件地址
     */
    private String orderFileUrl;

    /**
     * 业务确认截图列表
     */
    private List<FilePair> verifyList;
    private String verifyListString;

    @Data
    public static class FilePair implements Serializable{
        private String name;
        private String url;
    }

}

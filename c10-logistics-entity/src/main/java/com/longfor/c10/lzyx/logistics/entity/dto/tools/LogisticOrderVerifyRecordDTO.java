package com.longfor.c10.lzyx.logistics.entity.dto.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 运维工具-批量核销操作日志
 * @author renwei03
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticOrderVerifyRecordDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 子订单id
     */
    private String childOrderId;

    /**
     * 订单文件名称
     */
    private String orderFileName;

    /**
     * 订单文件url
     */
    private String orderFileUrl;

    /**
     * 业务确认截图
     */
    private List<FilePair> verifyList;

    @Data
    public static class FilePair{
        private String name;
        private String url;
    }

    /**
     * 创建人时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createUser;
}

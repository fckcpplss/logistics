package com.longfor.c10.lzyx.logistics.entity.dto.shunfeng;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 顺丰打印面单req
 * @author: zhaoyalong
 */
@Data
public class EBillSFReq {
    //模板编码
    private String templateCode;
    //业务数据
    private List<Document> documents = new ArrayList<>();
    //版本号(传值为2.0表示选择V2.0版本服务；传值为空表示选择V1.0版本服务)
    private String version;
    //生成面单文件格式
    private String fileType;
}

package com.longfor.c10.lzyx.logistics.dao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longfor.c10.lzyx.logistics.dao.mapper.LogisticsVerifyOrderRecordMapper;
import com.longfor.c10.lzyx.logistics.dao.service.ILogisticsVerifyOrderRecordService;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsVerifyOrderRecord;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 运维工具-批量核销操作日志
 * </p>
 *
 * @author zhaoyl
 * @since 2022-04-12
 */
@Service
public class LogisticsVerifyOrderRecordServiceImpl extends ServiceImpl<LogisticsVerifyOrderRecordMapper, LogisticsVerifyOrderRecord> implements ILogisticsVerifyOrderRecordService {

}

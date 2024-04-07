package com.longfor.c10.lzyx.logistics.dao.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorListReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.LogisticsMonitorListResData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 物流监控dao层
 * @author zhaoyl
 * @date 2022/2/17 上午11:33
 * @since 1.0
 */
@Mapper
public interface LogisticsMonitorMapper {
    /**
     * 待发货列表
     * @param page 分页信息
     * @param pendingQuery 过滤信息
     * @return 发货信息
     */
    IPage<LogisticsMonitorListResData> queryDeliveryList(IPage<LogisticsMonitorListResData> page, @Param("req") LogisticsMonitorListReqData query);
}

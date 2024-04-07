package com.longfor.c10.lzyx.logistics.dao.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longfor.c10.lzyx.logistics.entity.entity.LogisticsFee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longfor.c10.lzyx.logistics.entity.dto.FeeListReq;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.FeeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 运单费用 Mapper 接口
 * </p>
 *
 * @author zhaoyl
 * @since 2022-03-10
 */
public interface LogisticsFeeMapper extends BaseMapper<LogisticsFee> {
    /**
     * 分页查询运费列表
     */
    IPage<FeeVO> getLogisticsFeeList(IPage<FeeVO> page, @Param("req") FeeListReq req);
    /**
     * 不分页查询运费列表
     */
    List<FeeVO> getLogisticsFeeList(@Param("req") FeeListReq req);
}

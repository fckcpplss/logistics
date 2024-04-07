package com.longfor.c10.lzyx.logistics.entity.dto.merchant;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 待发货列表导出实体
 * @Author zhaoyalong
 * @Param
 * @return
 **/
@Data
@AllArgsConstructor
public class LogisticsCompanyInfoExportVO {
    /**
     * 快递公司编码
     */
    @Excel(name = "快递公司编码")
    private String companyCode;

    /**
     * 快递公司名称
     */
    @Excel(name = "快递公司名称")
    private String companyName;

}

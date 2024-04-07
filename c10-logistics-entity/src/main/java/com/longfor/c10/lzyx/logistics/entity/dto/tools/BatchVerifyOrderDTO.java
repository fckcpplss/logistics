package com.longfor.c10.lzyx.logistics.entity.dto.tools;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import cn.afterturn.easypoi.handler.inter.IExcelDataModel;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.longfor.c10.lzyx.logistics.entity.dto.admin.DeliveryNoSendListVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 运维工具-批量核销导入
 * @author zhaoyl
 * @date 2021/12/1 下午7:22
 * @since 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@HeadRowHeight(value = 20)
@ColumnWidth(value = 20)
public class BatchVerifyOrderDTO implements IExcelModel, IExcelDataModel {

    /**
     * 订单号
     */
    @Excel(name = "订单编号",width= 20)
    private String orderNo;

    /**
     * 行号
     */
    @ExcelIgnore
    private int rowNum;
    /**
     * 错误信息
     */
    @ExcelIgnore
    private String errorMsg;

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public void setErrorMsg(String s) {
        this.errorMsg = s;
    }

    @Override
    public int getRowNum() {
        return rowNum;
    }

    @Override
    public void setRowNum(int i) {
        this.rowNum = i;
    }
}

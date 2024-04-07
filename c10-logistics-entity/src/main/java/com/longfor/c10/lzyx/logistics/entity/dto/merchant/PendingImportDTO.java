package com.longfor.c10.lzyx.logistics.entity.dto.merchant;

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

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

/**
 * 待发货列表导入DTO
 * @author zhaoyl
 * @date 2021/12/1 下午7:22
 * @since 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@HeadRowHeight(value = 20)
@ColumnWidth(value = 20)
public class PendingImportDTO extends DeliveryNoSendListVO implements IExcelModel, IExcelDataModel {
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
    public int getRowNum() {
        return this.rowNum;
    }

    @Override
    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean checkAllNull(){
        return StringUtils.isBlank(this.getOrderNo())
                && StringUtils.isBlank(this.getOrgName())
                && StringUtils.isBlank(this.getShopName())
                && StringUtils.isBlank(this.getBizChannelCode())
                && StringUtils.isBlank(this.getGoodsId())
                && StringUtils.isBlank(this.getSkuSpecs())
                && StringUtils.isBlank(this.getGoodsDesc())
                && StringUtils.isBlank(this.getGoodsImgUrl())
                && StringUtils.isBlank(this.getGoodsImgUrl())
                && StringUtils.isBlank(this.getGoodsType())
                && StringUtils.isBlank(this.getSkuId())
                && Objects.isNull(this.getGoodsNum())
                && Objects.isNull(this.getGoodsVos())
                && StringUtils.isBlank(this.getGoodsIds())
                && StringUtils.isBlank(this.getReceiptAddress())
                && StringUtils.isBlank(this.getReceiptName())
                && StringUtils.isBlank(this.getReceiptPhoneNumber())
                && StringUtils.isBlank(this.getLogisticsTypeShow())
                && Objects.isNull(this.getLogisticsType())
                && StringUtils.isBlank(this.getOrderCreateTime())
                && StringUtils.isBlank(this.getSellTypeShow())
                && Objects.isNull(this.getSellType())
                && StringUtils.isBlank(this.getOrderDesc())
                && StringUtils.isBlank(this.getDeliveryNo());
    }
}

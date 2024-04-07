package com.longfor.c10.lzyx.logistics.entity.dto.merchant;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.handler.inter.IExcelDataModel;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.longfor.c10.lzyx.logistics.entity.dto.BaseReqData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 待发货列表导入DTO
 * @author zhaoyl
 * @date 2021/12/1 下午7:22
 * @since 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingImportReqData extends BaseReqData {
    private MultipartFile file;
}

package com.longfor.c10.lzyx.logistics.client.entity.param.merchant;

import com.longfor.c10.lzyx.logistics.client.entity.BaseReqData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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

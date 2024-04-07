package com.longfor.c10.lzyx.logistics.entity.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 更新物流配置请求
 * @author heandong
 */
@Data
public class ShopLogisticsUpdReq extends BaseReqData{

    /**
     * 供应商物流配置id
     */
    @NotNull(message = "id不能为空")
    Integer id;
    /**
     * 账户
     */
    @Length(max = 32, message = "最大长度不能超过32")
    String account;
    /**
     * appKey
     */
    @Length(max = 32, message = "最大长度不能超过32")
    String appKey;
    /**
     * 是否被选中
     */
    @NotNull(message = "choose字段不能为空")
    Boolean choose;
    /**
     * 是否是默认选项（放在下拉列表第一个）
     */
    @NotNull(message = "chooseDefault字段不能为空")
    Boolean chooseDefault;

    /**
     * 物流商品类型
     */
    String expressType;
}

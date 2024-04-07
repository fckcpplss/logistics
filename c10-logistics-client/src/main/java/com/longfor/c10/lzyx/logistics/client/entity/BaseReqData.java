package com.longfor.c10.lzyx.logistics.client.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author zhaoyl
 * @date 2022/2/7 下午7:43
 * @since 1.0
 */
@Data
public class BaseReqData {
    /**
     * 所属运营组织
     */
    @Length(max = 64)
    private String orgId;

    /**
     * 所属运营组织列表
     */
    private List<String> orgIds;

    /**
     * shopId
     */
    private String shopId;

    /**
     * shopIds
     */
    private List<String> shopIds;
    /**
     * B端用户信息
     */
    private AmUserInfo amUserInfo;
    /**
     * C端用户信息
     */
    private UserTokenInfo userTokenInfo;
}

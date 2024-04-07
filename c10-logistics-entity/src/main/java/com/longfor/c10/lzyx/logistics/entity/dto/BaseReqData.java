package com.longfor.c10.lzyx.logistics.entity.dto;

import com.longfor.c10.lzyx.logistics.entity.dto.auth.AmUserInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.UserTokenInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author zhaoyl
 * @date 2022/2/7 下午7:43
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseReqData {
    /**
     * 所属运营组织
     */
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

    /**
     * 获取一个shopId
     * @return
     */
    public String getFirstShopId(){
        return ListUtils.emptyIfNull(getShopIds()).stream().findFirst().orElse(null);
    }
}

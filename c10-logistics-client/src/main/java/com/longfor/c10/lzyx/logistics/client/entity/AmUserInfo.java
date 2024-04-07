package com.longfor.c10.lzyx.logistics.client.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhaoyl
 * @date 2022/2/11 下午3:13
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmUserInfo {
    private String realName;
    private String jobCode;
    private String userType;
    private String userId;
    private String email;
    private String userName;
    private String casUser;
    private String remark;
    private List<String> orgIds;
    private List<String> orgNames;
    private List<SprInfo> sprInfos;
    private List<String> roleCodes;
}

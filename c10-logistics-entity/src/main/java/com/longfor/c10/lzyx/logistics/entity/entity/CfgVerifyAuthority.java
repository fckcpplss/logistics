package com.longfor.c10.lzyx.logistics.entity.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 有权限核销人配置表
 * </p>
 *
 * @author liuxin41
 * @since 2022-04-15
 */
@Getter
@Setter
@TableName("cfg_verify_authority")
public class CfgVerifyAuthority implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
      private Long id;

    /**
     * 核销人oa账号
     */
    private String oaAccount;

    /**
     * 是否禁用：0-否，1-是
     */
    private Integer isForbid;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
      @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 创建人账号
     */
    private String creatorAccount;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 更新人账号
     */
    private String updateAccount;

    /**
     * 更新人姓名
     */
    private String updateName;


}

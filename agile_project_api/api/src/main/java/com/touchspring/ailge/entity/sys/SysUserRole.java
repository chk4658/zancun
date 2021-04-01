package com.touchspring.ailge.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户角色关系表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_USER_ROLE")
public class SysUserRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableField("SYS_ROLE_ID")
    private String sysRoleId;

    /**
     * 用户ID
     */
    @TableField("SYS_USER_ID")
    private String sysUserId;


}

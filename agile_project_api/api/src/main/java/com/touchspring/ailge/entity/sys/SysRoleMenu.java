package com.touchspring.ailge.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 角色菜单关系表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_ROLE_MENU")
public class SysRoleMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableField("SYS_ROLE_ID")
    private String sysRoleId;

    /**
     * 菜单ID
     */
    @TableField("SYS_MENU_ID")
    private String sysMenuId;


}

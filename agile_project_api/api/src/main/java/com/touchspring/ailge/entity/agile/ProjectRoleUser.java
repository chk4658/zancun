package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 项目角色与人员关系表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_ROLE_USER")
public class ProjectRoleUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 项目角色ID
     */
    @TableField("PROJECT_ROLE_ID")
    private String projectRoleId;

    /**
     * 用户ID
     */
    @TableField("SYS_USER_ID")
    private String sysUserId;


    /**
     * 角色绑定人员
     */
    @TableField(exist = false)
    private List<SysUser> sysUsers;


}

package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 项目角色关系表

 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_ROLE")
public class ProjectRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @TableField("ROLE_NAME")
    private String roleName;

    /**
     * 角色描述
     */
    @TableField("ROLE_DESCRIPTION")
    private String roleDescription;

    /**
     * 项目ID
     */
    @TableField("PROJECT_ID")
    private String projectId;

    /**
     * 职责与使命
     */
    @TableField("DUTY")
    private String duty;

    /**
     * 保留字段1
     */
    @TableField("EXT1")
    private String ext1;

    /**
     * 保留字段2
     */
    @TableField("EXT2")
    private String ext2;

    /**
     * 保留字段3
     */
    @TableField("EXT3")
    private String ext3;

    /**
     * 任务所属当量
     */
    @TableField("TASK_EQUIVALENT")
    private String taskEquivalent;

    /**
     * 项目当量（废弃）
     */
    @TableField(exist = false)
    private String roleEquivalent;


    /**
     * 角色绑定人员
     */
    @TableField(exist = false)
    private List<ProjectRoleUser> projectRoleUsers;

}

package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 项目模板里程碑表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_TEMPLATE_MILESTONE")
public class ProjectTemplateMilestone extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 里程碑名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 模板ID
     */
    @TableField("PROJECT_TEMPLATE_ID")
    private String projectTemplateId;

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
     * 排序
     */
    @TableField("SORT")
    private String sort;

    /**
     * 审核项目角色ID
     */
    @TableField("REVIEW_PROJECT_ROLE_ID")
    private String reviewProjectRoleId;

    /**
     * 确认项目角色ID
     */
    @TableField("CONFIRM_PROJECT_ROLE_ID")
    private String confirmProjectRoleId;

    /**
     * 任务
     */
    @TableField(exist = false)
    private List<ProjectTemplateTask> taskList;

    /**
     * 审核项目角色
     */
    @TableField(exist = false)
    public ProjectTemplateRole reviewProjectRole;

    /**
     * 确认项目角色
     */
    @TableField(exist = false)
    public ProjectTemplateRole confirmProjectRole;


}

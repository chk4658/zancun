package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 项目模板任务表

 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_TEMPLATE_TASK")
public class ProjectTemplateTask extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 项目模板ID
     */
    @TableField("PROJECT_TEMPLATE_ID")
    private String projectTemplateId;

    /**
     * 项目模板里程碑ID
     */
    @TableField("PROJECT_TEMPLATE_MILESTONE_ID")
    private String projectTemplateMilestoneId;

    /**
     * 排序
     */
    @TableField("SORT")
    private String sort;

    /**
     * 名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 描述
     */
    @TableField("DESCRIPTION")
    private String description;

    /**
     * 父ID
     */
    @TableField("PARENT_ID")
    private String parentId;

    /**
     * 任务类型
     */
    @TableField("TYPE")
    private String type;

    /**
     * 是否有交付要求
     */
    @TableField("IS_REQUIREMENT")
    private Long isRequirement;

    /**
     * 开阀条件
     */
    @TableField("OPEN_CONDITIONS")
    private String openConditions;

    /**
     * 开阀描述
     */
    @TableField("OPEN_DESCRIPTION")
    private String openDescription;

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
     * 提前期限
     */
    @TableField("AHEAD_DAY")
    private Integer aheadDay;

    /**
     * 子任务
     */
    @TableField(exist = false)
    private List<ProjectTemplateTask> children;

    /**
     * 属性值
     */
    @TableField(exist = false)
    private List<TaskTemplateData> taskDataList;

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

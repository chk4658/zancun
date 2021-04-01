package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.utils.PropertyMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import static org.apache.ibatis.type.JdbcType.VARCHAR;

/**
 * <p>
 * 里程碑信息表

 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_MILESTONE")
public class ProjectMilestone extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 圈子ID
     */
    @TableField("CIRCLE_ID")
    private String circleId;

    /**
     * 项目ID
     */
    @TableField("PROJECT_ID")
    private String projectId;

    /**
     * 排序
     */
    @TableField("SORT")
    private String sort;

    /**
     * 名称
     */
//    @PropertyMsg("名称")
    @TableField("NAME")
    private String name;

    /**
     * 预计截止时间
     */
    @PropertyMsg("预计截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8:00")
    @TableField("EST_END_TIME")
    private LocalDateTime estEndTime;

    /**
     * 实际结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8:00")
    @TableField("ACT_END_TIME")
    private LocalDateTime actEndTime;

    /**
     * 预留字段1
     */
    @TableField("EXT1")
    private String ext1;

    /**
     * 预留字段2
     */
    @TableField("EXT2")
    private String ext2;

    /**
     * 预留字段3
     */
    @TableField("EXT3")
    private String ext3;

    /**
     * 审核项目角色ID
     */
    @TableField(value = "REVIEW_PROJECT_ROLE_ID", jdbcType = VARCHAR)
    private String reviewProjectRoleId;

    /**
     * 确认项目角色ID
     */
    @TableField(value = "CONFIRM_PROJECT_ROLE_ID", jdbcType = VARCHAR)
    private String confirmProjectRoleId;

    /**
     * 状态
     */
    @TableField("STATUS")
    private String status;

    /**
     * 是否启用
     */
//    @PropertyMsg("是否启用")
    @TableField("ENABLED")
    private Integer enabled;

    /**
     * 是否禁用
     */
//    @PropertyMsg("是否禁用")
    @TableField("FORBIDDEN")
    private Integer forbidden;

    /**
     * 模板ID
     */
    @TableField("PROJECT_TEMPLATE_ID")
    private String projectTemplateId;

    /**
     * 模板里程碑ID
     */
    @TableField("PROJECT_TEMPLATE_MILESTONE_ID")
    private String projectTemplateMilestoneId;


    /**
     * 是否展开，1展开,0收起
     */
    @TableField("COLLAPSED")
    private Integer collapsed;

    /**
     * 任务
     */
    @TableField(exist = false)
    private List<Task> taskList;

    /**
     * 审核用户
     */
    @TableField(exist = false)
    public SysUser reviewUser;

    /**
     * 确认用户
     */
    @TableField(exist = false)
    public SysUser confirmUser;

    /**
     * 审核项目角色
     */
    @TableField(exist = false)
    public ProjectRole reviewProjectRole;

    /**
     * 确认项目角色
     */
    @TableField(exist = false)
    public ProjectRole confirmProjectRole;


}

package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.utils.PropertyMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static org.apache.ibatis.type.JdbcType.DATE;
import static org.apache.ibatis.type.JdbcType.VARCHAR;

/**
 * <p>
 * 项目任务信息表
 * </p>
 *
 * @author wangjing
 * @since 2020-07-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("TASK")
public class Task extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public Task() {
    }

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
     * 里程碑ID
     */
    @TableField("MILESTONE_ID")
    private String milestoneId;

    /**
     * 排序
     */
    @TableField("SORT")
    private String sort;

    /**
     * 名称
     */
    @PropertyMsg("名称")
    @TableField("NAME")
    private String name;

    /**
     * 开阀条件
     */
    @PropertyMsg("开阀条件")
    @TableField("OPEN_CONDITIONS")
    private String openConditions;

    /**
     * 开阀描述
     */
    @PropertyMsg("开阀描述")
    @TableField("OPEN_DESCRIPTION")
    private String openDescription;

    /**
     * 开阀状态 1：正常 2：异常
     */
    @PropertyMsg("开阀状态")
    @TableField("OPEN_STATUS")
    private String openStatus;

    /**
     * 父ID
     */
    @TableField("PARENT_ID")
    private String parentId;

    /**
     * 状态
     */
    @PropertyMsg("状态")
    @TableField("STATUS")
    private String status;

    /**
     * 时间状态
     */
    @TableField("DATE_STATUS")
    private String dateStatus;

    /**
     * 任务类型
     */
    @PropertyMsg("任务类型")
    @TableField("TYPE")
    private String type;

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
    @TableField(value = "ACT_END_TIME", updateStrategy = FieldStrategy.IGNORED, jdbcType = DATE)
    private LocalDateTime actEndTime;

    /**
     * 是否有交付要求
     */
    @PropertyMsg("是否有交付要求")
    @TableField("IS_REQUIREMENT")
    private Long isRequirement;

    /**
     * 负责任务角色ID
     */
//    @PropertyMsg("负责项目角色名称")
    @TableField(value = "REVIEW_PROJECT_ROLE_ID", jdbcType = VARCHAR)
    private String reviewProjectRoleId;

    /**
     * 审核任务角色ID
     */
//    @PropertyMsg("审核项目角色名称")
    @TableField(value = "CONFIRM_PROJECT_ROLE_ID", jdbcType = VARCHAR)
    private String confirmProjectRoleId;

    /**
     * 拒绝理由
     */
//    @PropertyMsg("拒绝理由")
    @TableField("REFUSE_REASON")
    private String refuseReason;

    /**
     * 待条件通过理由
     */
//    @PropertyMsg("带条件通过理由")
    @TableField("PASS_REASON")
    private String passReason;

    /**
     * 是否启用 1 是 0 否
     */
    @PropertyMsg("是否启用")
    @TableField("ENABLED")
    private Integer enabled;

    /**
     * 是否禁用 0 否 1 是
     */
    @PropertyMsg("是否禁用")
    @TableField("FORBIDDEN")
    private Integer forbidden;

    /**
     * 提前期限
     */
    @TableField("AHEAD_DAY")
    private Integer aheadDay;

    /**
     * 优先级
     */
    @TableField("PRIORITY")
    private String priority;

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
     * 模板任务ID
     */
    @TableField("PROJECT_TEMPLATE_TASK_ID")
    private String ProjectTemplateTaskId;

    /**
     * 是否为临时任务 0：否，1：是
     */
    @TableField("IS_TEMPORARY")
    private Integer isTemporary;

    /**
     * 任务归档 0 正常 1 归档
     */
    @TableField("ST_MARK")
    private Integer stMark;

    /**
     * 子任务
     */
    @TableField(exist = false)
    private List<Task> children;

    /**
     * 属性值
     */
    @TableField(exist = false)
    private List<TaskData> taskDataList;

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

    /**
     * 交付物
     */
    @TableField(exist = false)
    public List<TaskDelivery> taskDeliveryList;

    /**
     * 里程碑
     */
    @TableField(exist = false)
    private ProjectMilestone projectMilestone;

    /**
     * 是否真实存在的标识 true -> 存在
     */
    @TableField(exist = false)
    private Boolean flag;

    /**
     * 项目
     */
    @TableField(exist = false)
    private Project project;

}

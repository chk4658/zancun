package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysDepartment;
import com.touchspring.ailge.entity.sys.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

import static org.apache.ibatis.type.JdbcType.DATE;
import static org.apache.ibatis.type.JdbcType.VARCHAR;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_ISSUE")
public class ProjectIssue extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 项目id
     */
    @TableField("PROJECT_ID")
    private String projectId;
    /**
     * 里程碑id
     */
    @TableField("PROJECT_MILESTONE_ID")
    private String projectMilestoneId;
    /**
     * 来源
     */
    @TableField(value = "SOURCE",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private String source;
    /**
     * 提出人部门id
     */
    @TableField("CREATE_USER_DEPARTMENT_ID")
    private String createUserDepartmentId;
    /**
     * 问题类别
     */
    @TableField("QUESTION_TYPE_ID")
    private String questionTypeId;
    /**
     * 问题描述
     */
    @TableField(value = "DESCRIPTION",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private String description;
    /**
     * 涉及区域
     */
    @TableField(value = "INVOLVED",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private String involved;
    /**
     * 问题图片
     */
    @TableField(value = "IMG",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private String img;
    /**
     * 问题优先级
     */
    @TableField(value = "PRIORITY",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private String priority;
    /**
     * 是否重复发生
     */
    @TableField(value = "HAS_REPEAT",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private String hasRepeat;
    /**
     * 问题分析人
     */
    @TableField(value = "ANALYSIS_USER_ID",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private String analysisUserId;
    /**
     * 问题分析
     */
    @TableField(value = "CAUSE",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private String  cause;

    /**
     * 短期措施责任人
     */
    @TableField(value = "SHORT_MEASURE_USER_ID",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private String shortMeasureUserId;
    /**
     * 长期措施责任人
     */
    @TableField(value = "LONG_MEASURE_USER_ID",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private String longMeasureUserId;
    /**
     * 短期措施
     */
    @TableField(value = "SHORT_MEASURE",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private String shortMeasure;
    /**
     * 短期措施预计时间
     */
    @TableField(value = "SHORT_MEASURE_EXPECT_TIME",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = DATE)
    private Date shortMeasureExpectTime;
    /**
     * 短期措施实际时间
     */
    @TableField(value = "SHORT_MEASURE_ACTUAL_TIME",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = DATE)
    private Date shortMeasureActualTime;
    /**
     * 长期措施
     */
    @TableField(value = "LONG_MEASURE",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private String longMeasure;
    /**
     * 长期措施预计时间
     */
    @TableField(value = "LONG_MEASURE_EXPECT_TIME",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = DATE)
    private Date longMeasureExpectTime;
    /**
     * 长期措施实际时间
     */
    @TableField(value = "LONG_MEASURE_ACTUAL_TIME",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = DATE)
    private Date longMeasureActualTime;
    /**
     * 是否关闭
     */
    @TableField(value = "HAS_CLOSE",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private Integer hasClose;
    /**
     * 状态
     */
    @TableField("STATUS")
    private Integer status;
    /**
     * 是否输入LL
     */
    @TableField(value = "HAS_LL",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private Integer hasLl;
    /**
     * LL
     */
    @TableField(value = "LL",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private String ll;
    /**
     * 备注
     */
    @TableField(value = "REMARK",
            updateStrategy=FieldStrategy.IGNORED,
            jdbcType = VARCHAR)
    private String remark;

    /**
     * 阶段
     */
    @TableField("STAGE")
    private Integer stage;

    @TableField(exist = false)
    private Project project;

    @TableField(exist = false)
    private ProjectMilestone projectMilestone;

    @TableField(exist = false)
    private SysUser createUser;

    @TableField(exist = false)
    private SysDepartment createUserDepartment;


    @TableField(exist = false)
    private SysUser updateUser;

}

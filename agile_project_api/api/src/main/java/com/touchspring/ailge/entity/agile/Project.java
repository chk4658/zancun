package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.utils.PropertyMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 项目信息表

 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT")
public class Project extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 圈子ID
     */
    @PropertyMsg("隶属圈子")
    @TableField("CIRCLE_ID")
    private String circleId;

    /**
     * 名称
     */
    @PropertyMsg("项目名称")
    @TableField("NAME")
    private String name;

    /**
     * 描述
     */
//    @PropertyMsg("描述")
    @TableField("DESCRIPTION")
    private String description;

    /**
     * 项目状态
0  正常 -> 绿灯
1  预警 -> 黄灯
2  延期 -> 红灯
3  严重延期 -> 红灯
4  完成
     */
//    @PropertyMsg("项目状态")
    @TableField("STATUS")
    private String status;

    /**
     * 项目负责人
     */
    @PropertyMsg("项目负责人")
    @TableField("PROJECT_USER_ID")
    private String projectUserId;

    /**
     * 可见范围  1：项目参与人； 0：所有人 ；2：选定部门  默认0
     */
//    @PropertyMsg("可见范围")
    @TableField("VISIBLE_USER_ID")
    private Integer visibleUserId;

    /**
     * 是否上线 0未上线 1上线 2禁用 3暂停
     */
    @TableField("HAS_ONLINE")
    private Integer hasOnLine;

    /**
     * 预留字段2
     */
    @TableField("EXT2")
    private String ext2;

    /**
     * 项目类型：1:自建2：导入
     */
//    @PropertyMsg("项目类型")
    @TableField("TYPE")
    private String type;

    /**
     * 是否是草稿箱
     */
//    @PropertyMsg("是否是草稿箱")
    @TableField("IS_DRAFT_BOX")
    private Integer isDraftBox;

    /**
     * 预计截止时间
     */
    @PropertyMsg("预计截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8:00")
    @TableField("EST_END_TIME")
    private LocalDateTime estEndTime;

    /**
     * 模板ID
     */
    @TableField("PROJECT_TEMPLATE_ID")
    private String projectTemplateId;

    /**
     * 所属圈子名称目录
     */
    @TableField("CIRCLE_BELONGS")
    private String circleBelongs;

    /**
     * 关键字
     */
    @TableField("KEYWORD")
    private String keyword;

    /**
     * 项目归档 0 正常 1 归档
     */
    @TableField("ST_MARK")
    private Integer stMark;

    /**
     * 项目负责人
     */
    @TableField(exist = false)
    private SysUser projectUser;

    /**
     * 所有的父圈集合
     */
    @TableField(exist = false)
    private List<Circle> circles;

    /**
     * 里程碑
     */
    @TableField(exist = false)
    private List<ProjectMilestone> projectMilestones;

    /**
     * 任务数
     */
    @TableField(exist = false)
    private Integer taskNum;

    /**
     * 项目参与人数
     */
    @TableField(exist = false)
    private Long userNum;


    /**
     * 当前项目所属圈子详情
     */
    @TableField(exist = false)
    private Circle circle;

    /**
     * 归档人
     */
    @TableField(exist = false)
    private SysUser stMarkUser;


}

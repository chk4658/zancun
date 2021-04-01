package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 任务记录表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("TASK_LOG")
public class TaskLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public TaskLog() {
    }

    public TaskLog(String content, String projectId, String taskId) {
        this.content = content;
        this.projectId = projectId;
        this.taskId = taskId;
    }

    /**
     * 日志内容
     */
    @TableField("CONTENT")
    private String content;

    /**
     * 项目ID
     */
    @TableField("PROJECT_ID")
    private String projectId;

    /**
     * 任务ID
     */
    @TableField("TASK_ID")
    private String taskId;

    /**
     * 创建用户
     */
    @TableField(exist = false)
    private SysUser createSysUser;


}

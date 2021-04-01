package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 临时任务-人
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("TASK_USER")
public class TaskUser extends BaseEntity {

    /**
     * 任务ID
     */
    @TableField("TASK_ID")
    private String taskId;

    /**
     * 负责人ID
     */
    @TableField("REVIEW_SYS_USER_ID")
    private String reviewSysUserId;

    /**
     * 审核人ID
     */
    @TableField("CONFIRM_SYS_USER_ID")
    private String confirmSysUserId;

}

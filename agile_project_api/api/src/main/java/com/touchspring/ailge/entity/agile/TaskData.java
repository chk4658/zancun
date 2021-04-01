package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 任务数据表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("TASK_DATA")
public class TaskData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableField("TASK_ID")
    private String taskId;

    /**
     * 项目属性ID
     */
    @TableField("PROJECT_ATTR_ID")
    private String projectAttrId;

    /**
     * 数值
     */
    @TableField("VALUE")
    private String value;

    /**
     * 类型
     */
    @TableField(exist = false)
    private String attrType;


}

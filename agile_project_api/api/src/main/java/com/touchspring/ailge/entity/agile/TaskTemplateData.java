package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 模板任务数据表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("TASK_TEMPLATE_DATA")
public class TaskTemplateData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 模板任务ID
     */
    @TableField("PROJECT_TEMPLATE_TASK_ID")
    private String projectTemplateTaskId;

    /**
     * 项目模板属性ID
     */
    @TableField("PROJECT_TEMPLATE_ATTR_ID")
    private String projectTemplateAttrId;

    /**
     * 数值
     */
    @TableField("VALUE")
    private String value;

    /**
     * 类型
     */
    @TableField(exist = false)
    private String projectTemplateAttrType;


}

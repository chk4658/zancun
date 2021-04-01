package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 项目任务属性字段表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_ATTR")
public class ProjectAttr extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 字段名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 项目ID
     */
    @TableField("PROJECT_ID")
    private String projectId;

    /**
     * 枚举类中 PROJECT_TYPE
     */
    @TableField("TYPE")
    private String type;

    /**
     * 模板属性ID
     */
    @TableField("PROJECT_TEMPLATE_ATTR_ID")
    private String ProjectTemplateAttrId;


}

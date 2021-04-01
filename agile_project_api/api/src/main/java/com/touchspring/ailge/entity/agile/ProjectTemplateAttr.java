package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 模板项目属性字段表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_TEMPLATE_ATTR")
public class ProjectTemplateAttr extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 字段名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 项目模板ID
     */
    @TableField("PROJECT_TEMPLATE_ID")
    private String projectTemplateId;

    /**
     * 枚举类中 PROJECT_TYPE
     */
    @TableField("TYPE")
    private String type;

}

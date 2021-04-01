package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 项目模板类型表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_TEMPLATE_TYPE")
public class ProjectTemplateType extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 类型名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 保留字段1
     */
    @TableField("EXT1")
    private String ext1;

    /**
     * 保留字段2
     */
    @TableField("EXT2")
    private String ext2;

}

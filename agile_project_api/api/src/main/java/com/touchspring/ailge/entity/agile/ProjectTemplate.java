package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 项目模板表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_TEMPLATE")
public class ProjectTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public ProjectTemplate() {
    }

    public ProjectTemplate(String name) {
        this.name = name;
    }

    /**
     * 项目名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 模板类型ID
     */
    @TableField("PROJECT_TEMPLATE_TYPE_ID")
    private String projectTemplateTypeId;

    /**
     * 图片地址
     */
    @TableField("IMG_URL")
    private String imgUrl;

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

    /**
     * 有效
     */
    @TableField("ENABLED")
    private Long enabled;

    /**
     * 项目模板类型名称
     */
    @TableField(exist = false)
    private ProjectTemplateType projectTemplateType;


    /**
     * 项目模板类型名称
     */
    @TableField(exist = false)
    private List<ProjectTemplateMilestone> projectTemplateMilestones;

    /**
     * 项目模板属性名称
     */
    @TableField(exist = false)
    private List<ProjectTemplateAttr> projectTemplateAttrs;


}

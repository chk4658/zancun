package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 项目模板角色关系表

 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_TEMPLATE_ROLE")
public class ProjectTemplateRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @TableField("ROLE_NAME")
    private String roleName;

    /**
     * 角色描述
     */
    @TableField("ROLE_DESCRIPTION")
    private String roleDescription;

    /**
     * 项目模板ID
     */
    @TableField("PROJECT_TEMPLATE_ID")
    private String projectTemplateId;

    /**
     * 职责与使命
     */
    @TableField("DUTY")
    private String duty;

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
     * 保留字段3
     */
    @TableField("EXT3")
    private String ext3;

}

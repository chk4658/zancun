package com.touchspring.ailge.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 系统角色信息表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_ROLE_TEMPLATE")
public class SysRoleTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @TableField("NAME")
    private String name;

    /**
     * CODE
     */
    @TableField("CODE")
    private String code;

    /**
     * 描述
     */
    @TableField("DESCRIPTION")
    private String description;

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
     * 有效
     */
    @TableField("ENABLED")
    private Long enabled;


}

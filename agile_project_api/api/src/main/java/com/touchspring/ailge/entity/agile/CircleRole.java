package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.touchspring.ailge.utils.PropertyMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 圈子角色关系表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("CIRCLE_ROLE")
public class CircleRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @PropertyMsg("角色名称")
    @TableField("ROLE_NAME")
    private String roleName;

    /**
     * 角色描述
     */
    @PropertyMsg("角色描述")
    @TableField("ROLE_DESCRIPTION")
    private String roleDescription;

    /**
     * 圈子ID
     */
    @TableField("CIRCLE_ID")
    private String circleId;

    /**
     * 职责与使命
     */
    @PropertyMsg("职责与使命")
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

    /**
     * 角色绑定人员
     */
    @TableField(exist = false)
    private List<CircleRoleUser> circleRoleUsers;


}

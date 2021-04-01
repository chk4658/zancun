package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目角色当量（项目当量）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_ROLE_EQUIVALENT")
public class ProjectRoleEquivalent extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 项目角色ID
     */
    @TableField("PROJECT_ROLE_ID")
    private String projectRoleId;

    /**
     * 月份
     */
    @TableField("MONTHS")
    private String months;

    /**
     * 项目当量值
     */
    @TableField("VALUE")
    private Double value;

}

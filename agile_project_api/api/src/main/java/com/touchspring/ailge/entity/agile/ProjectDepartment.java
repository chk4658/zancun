package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysDepartment;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 项目与部门关系表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_DEPARTMENT")
public class ProjectDepartment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @TableField("PROJECT_ID")
    private String projectId;

    /**
     * 部门ID
     */
    @TableField("SYS_DEPARTMENT_ID")
    private String sysDepartmentId;

    /**
     * 部门
     */
    @TableField(exist = false)
    private SysDepartment sysDepartment;

}

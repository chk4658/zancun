package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.touchspring.ailge.entity.sys.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 圈子角色与人员关系表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("CIRCLE_ROLE_USER")
public class CircleRoleUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 圈子角色ID
     */
    @TableField("CIRCLE_ROLE_ID")
    private String circleRoleId;

    /**
     * 用户ID
     */
    @TableField("SYS_USER_ID")
    private String sysUserId;

    /**
     * 用户
     */
    @TableField(exist = false)
    private SysUser sysUser;


}

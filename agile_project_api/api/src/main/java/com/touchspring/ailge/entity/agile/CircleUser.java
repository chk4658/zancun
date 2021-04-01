package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("CIRCLE_USER")
public class CircleUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("SYS_USER_ID")
    private String sysUserId;

    /**
     * 圈子ID
     */
    @TableField("CIRCLE_ID")
    private String circleId;

    /**
     * CODE:OPERATE/ADD_CIRCLE/ADD_PROJECT
     */
    @TableField("TYPE")
    private String type;


}

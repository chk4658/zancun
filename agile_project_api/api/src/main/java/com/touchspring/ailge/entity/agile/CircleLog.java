package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.touchspring.ailge.entity.sys.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 圈子记录表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("CIRCLE_LOG")
public class CircleLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 日志内容
     */
    @TableField("CONTENT")
    private String content;

    /**
     * 圈子ID
     */
    @TableField("CIRCLE_ID")
    private String circleId;

    /**
     * 创建人
     */
    @TableField(exist = false)
    private SysUser createSysUser;


}

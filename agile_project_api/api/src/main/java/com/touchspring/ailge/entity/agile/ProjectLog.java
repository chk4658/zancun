package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.touchspring.ailge.entity.sys.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 项目日志表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_LOG")
public class ProjectLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 日志内容
     */
    @TableField("CONTENT")
    private String content;

    /**
     * 项目ID
     */
    @TableField("PROJECT_ID")
    private String projectId;

    /**
     * 创建人
     */
    @TableField(exist = false)
    private SysUser createSysUser;

}

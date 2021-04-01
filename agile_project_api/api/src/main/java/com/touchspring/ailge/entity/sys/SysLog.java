package com.touchspring.ailge.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 系统操作记录表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_LOG")
public class SysLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 日志内容
     */
    @TableField("CONTENT")
    private String content;

    /**
     * 日志类型
     */
    @TableField("M_TYPE")
    private String mType;


}

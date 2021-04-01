package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.utils.PropertyMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.JdbcType;

/**
 * 当量管理
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("EQUIVALENT")
public class Equivalent extends BaseEntity {

    /**
     * 项目当量
     */
    @TableField(value = "PROJECT")
    private Double project;

    /**
     * 任务当量
     */
    @TableField(value = "TASK")
    private Double task;

    /**
     * 其他当量
     */
    @TableField(value = "OTHER",jdbcType = JdbcType.NUMERIC,updateStrategy = FieldStrategy.IGNORED)
    private Double other;

    /**
     * 交付质量
     */
    @TableField(value = "DELIVERY_QUALITY",jdbcType = JdbcType.NUMERIC,updateStrategy = FieldStrategy.IGNORED)
    private Double deliveryQuality;

    /**
     * 知识
     */
    @TableField(value = "KNOWLEDGE",jdbcType = JdbcType.NUMERIC,updateStrategy = FieldStrategy.IGNORED)
    private Double knowledge;

    /**
     * 工作当量
     */
    @TableField(value = "WORK")
    private Double work;

    /**
     * 用户id
     */
    @TableField("SYS_USER_ID")
    private String sysUserId;

    /**
     * 月份：2020-10
     */
    @TableField("MONTHS")
    private String months;

    /**
     * 创建人
     */
    @TableField(exist = false)
    private SysUser sysUser;

}

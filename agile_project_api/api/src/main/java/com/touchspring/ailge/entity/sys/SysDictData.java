package com.touchspring.ailge.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 字典数据表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_DICT_DATA")
public class SysDictData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 字典表ID
     */
    @TableField("SYS_DICT_ID")
    private String sysDictId;

    /**
     * 颜色
     */
    @TableField("COLOR")
    private String color;


}

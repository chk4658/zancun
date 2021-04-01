package com.touchspring.ailge.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_DICT")
public class SysDict extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 有效
     */
    @TableField("ENABLED")
    private Long enabled;

    /**
     * 字典数据表
     */
    @TableField(exist = false)
    private List<SysDictData> sysDictDataList;


}

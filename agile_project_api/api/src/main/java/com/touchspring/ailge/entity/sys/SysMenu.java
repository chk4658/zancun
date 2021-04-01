package com.touchspring.ailge.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_MENU")
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 父节点ID
     */
    @TableField("PARENT_ID")
    private String parentId;

    /**
     * 路径
     */
    @TableField("PATH")
    private String path;

    /**
     * 图标
     */
    @TableField("ICON")
    private String icon;

    /**
     * 分类
     */
    @TableField("TYPE")
    private String type;

    /**
     * 排序
     */
    @TableField("SORT")
    private String sort;

    /**
     * 类别
     */
    @TableField("GROUPS")
    private String groups;

    /**
     * 编码
     */
    @TableField("CODE")
    private String code;

    @TableField(exist = false)
    private List<SysMenu> children;


}

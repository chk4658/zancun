package com.touchspring.ailge.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 部门管理表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_DEPARTMENT")
public class SysDepartment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 公司主键
     */
    @TableField("COMPANY_ID")
    private String companyId;

    /**
     * 父级主键
     */
    @TableField("PARENT_ID")
    private String parentId;

    /**
     * 编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 名称
     */
    @TableField("FULL_NAME")
    private String fullName;

    /**
     * 简称
     */
    @TableField("SHORT_NAME")
    private String shortName;

    /**
     * 性质
     */
    @TableField("NATURE")
    private String nature;

    /**
     * 负责人
     */
    @TableField("MANAGER")
    private String manager;

    /**
     * 联系电话
     */
    @TableField("PHONE")
    private String phone;

    /**
     * 传真
     */
    @TableField("FAX")
    private String fax;

    /**
     * 电子邮件
     */
    @TableField("EMAIL")
    private String email;

    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 有效
     */
    @TableField("ENABLED")
    private Long enabled;

//    /**
//     * 所属公司名称
//     */
//    @TableField(exist = false)
//    private String companyName;


}

package com.touchspring.ailge.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 公司信息表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_COMPANY")
public class SysCompany extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父主键
     */
    @TableField("PARENT_ID")
    private String parentId;

    /**
     * 公司类型
     */
    @TableField("CATEGORY")
    private String category;

    /**
     * 公司编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 公司全名
     */
    @TableField("FULL_NAME")
    private String fullName;

    /**
     * 公司简称
     */
    @TableField("SHORT_NAME")
    private String shortName;

    /**
     * 公司性质
     */
    @TableField("NATURE")
    private String nature;

    /**
     * 法定代表人
     */
    @TableField("MANAGER")
    private String manager;

    /**
     * 联系人
     */
    @TableField("CONTACT")
    private String contact;

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

    @TableField(exist = false)
    private List<SysDepartment> departments;


}

package com.touchspring.ailge.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.touchspring.ailge.entity.ldap.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_USER")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 公司主键
     */
    @TableField("COMPANY_ID")
    private String companyId;

    /**
     * 部门主键
     */
    @TableField("DEPARTMENT_ID")
    private String departmentId;

    /**
     * 内部用户
     */
    @TableField("IS_INNER_USER")
    private Long isInnerUser;

    /**
     * 用户编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 登录账户
     */
    @TableField("ACCOUNT")
    private String account;

    /**
     * 登录密码
     */
    @TableField("PASSWORD")
    private String password;

    /**
     * 密码秘钥
     */
    @TableField("SECRETKEY")
    private String secretkey;

    /**
     * 姓名
     */
    @TableField("REAL_NAME")
    private String realName;

    /**
     * 姓名拼音
     */
    @TableField("SPELL")
    private String spell;

    /**
     * 性别
     */
    @TableField("SEX")
    private String sex;

    /**
     * 出生日期
     */
    @TableField("BIRTHDAY")
    private LocalDateTime birthday;

    /**
     * 手机
     */
    @TableField("MOBILE")
    private String mobile;

    /**
     * 电话
     */
    @TableField("TELEPHONE")
    private String telephone;

    /**
     * QQ号码
     */
    @TableField("QQ")
    private String qq;

    /**
     * 电子邮件
     */
    @TableField("EMAIL")
    private String email;

    /**
     * 最后修改密码日期
     */
    @TableField("CHANGE_PASSWORD_TIME")
    private LocalDateTime changePasswordTime;

    /**
     * 单点登录标识
     */
    @TableField("OPEN_ID")
    private Long openId;

    /**
     * 登录次数
     */
    @TableField("LOGIN_COUNT")
    private Long loginCount;

    /**
     * 第一次访问时间
     */
    @TableField("FIRST_VISIT")
    private LocalDateTime firstVisit;

    /**
     * 上一次访问时间
     */
    @TableField("PREVIOUS_VISIT")
    private LocalDateTime previousVisit;

    /**
     * 最后访问时间
     */
    @TableField("LAST_VISIT")
    private LocalDateTime lastVisit;

    /**
     * 审核状态
     */
    @TableField("AUDIT_STATUS")
    private String auditStatus;

    /**
     * 审核员主键
     */
    @TableField("AUDIT_USER_ID")
    private String auditUserId;

    /**
     * 审核时间
     */
    @TableField("AUDIT_TIME")
    private LocalDateTime auditTime;

//    /**
//     * 是否在线
//     */
//    @TableField("ONLINE")
//    private Long online;

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

    /**
     * 排序码
     */
    @TableField("SORT")
    private Long sort;

    @TableField(exist = false)
    private String departmentName;


    public SysUser(User user) {
        this.account = user.getAccount();
        this.email = user.getEmail();
        this.telephone = user.getTelephone();
        this.mobile = user.getMobile();
        this.loginCount = user.getLoginCount();
    }

    public SysUser() {
    }
}

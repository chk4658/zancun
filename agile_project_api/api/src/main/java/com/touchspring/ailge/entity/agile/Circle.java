package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.CircleOperationType;
import com.touchspring.ailge.utils.PropertyMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import static org.apache.ibatis.type.JdbcType.VARCHAR;

/**
 * <p>
 *
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("CIRCLE")
public class Circle extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 圈主ID
     */
    @PropertyMsg("圈主姓名")
    @TableField("OWNER_UID")
    private String ownerUid;

    /**
     * 圈子名称
     */
    @PropertyMsg("圈子名称")
    @TableField("NAME")
    private String name;

    /**
     * 父圈ID：最上层：0 其他保存圈子ID
     */
//    @PropertyMsg("父圈ID")
    @TableField(value = "PARENT_ID", updateStrategy = FieldStrategy.IGNORED, jdbcType = VARCHAR)
    private String parentId;

    /**
     * 描述
     */
    @PropertyMsg("描述")
    @TableField("DESCRIPTION")
    private String description;

    /**
     * 是否允许添加子圈， 0 否；默认 0 1：是所有人 2：部分授权
     */
//    @PropertyMsg("是否允许添加子圈")
    @TableField("IS_ADD_CIRCLE")
    private Integer isAddCircle;

    /**
     * 是否允许添加项目 0 否；默认 0 1：是所有人 2：部分授权
     */
//    @PropertyMsg("是否允许添加项目")
    @TableField("IS_ADD_PROJECT")
    private Integer isAddProject;

    /**
     * 是否允许操作圈子，0 否；默认 0 1：是所有人 2：部分授权
     */
//    @PropertyMsg("是否允许操作圈子")
    @TableField("IS_OPERATE")
    private Integer isOperate;

    /**
     * 保留字段1
     */
    @TableField("EXT1")
    private String ext1;

    /**
     * 保留字段2
     */
    @TableField("EXT2")
    private String ext2;

    /**
     * 保留字段3
     */
    @TableField("EXT3")
    private String ext3;

    /**
     * 保留字段4
     */
    @TableField("EXT4")
    private String ext4;

    /**
     * 头像地址
     */
    @PropertyMsg("头像地址")
    @TableField("PROFILE_URL")
    private String profileUrl;

    /**
     * 本圈人数
     */
    @TableField("USER_NUM")
    private Long userNum;

    /**
     * 总人数
     */
    @TableField("ALL_USER_NUM")
    private Long allUserNum;

    /**
     * 本圈项目数
     */
    @TableField("PROJECT_NUM")
    private Long projectNum;

    /**
     * 总项目数
     */
    @TableField("ALL_PROJECT_NUM")
    private Integer allProjectNum;

    /**
     * 图片地址
     */
    @TableField("IMG_URL")
    private String imgUrl;

    /**
     * 子圈
     */
    @TableField(exist = false)
    private List<Circle> children;

    /**
     * 人员
     */
    @TableField(exist = false)
    private SysUser ownerUser;

    /**
     * 本圈的总圈子个数
     */
    @TableField(exist = false)
    private Integer allChildrenCircleNum;

    /**
     * 允许操作圈子
     */
    @TableField(exist = false)
    private List<CircleUser> operateUsers;

    /**
     * 允许添加子圈
     */
    @TableField(exist = false)
    private List<CircleUser> addCircleUsers;

    /**
     * 允许添加项目
     */
    @TableField(exist = false)
    private List<CircleUser> addProjectUsers;


    /**
     * 允许添加项目
     */
    @TableField(exist = false)
    private CircleOperationType.UserOperation.Operation operation;


    /**
     * 任务数
     */
    @TableField(exist = false)
    private Integer taskNum;

}

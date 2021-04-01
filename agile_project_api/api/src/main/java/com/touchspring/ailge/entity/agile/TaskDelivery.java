package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 任务交付物关系表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("TASK_DELIVERY")
public class TaskDelivery extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableField("TASK_ID")
    private String taskId;

    /**
     * 交付物ID
     */
    @TableField("DELIVERY_ID")
    private String deliveryId;

    /**
     * 提交人ID
     */
    @TableField("SUBMIT_USER_ID")
    private String submitUserId;

    /**
     * 提交人
     */
    @TableField(exist = false)
    private SysUser submitUser;

    /**
     * 交付物名称
     */
    @TableField("DELIVERY_NAME")
    private String deliveryName;

    /**
     * 交付物大小
     */
    @TableField("DELIVERY_SIZE")
    private String deliverySize;

    /**
     * 上传时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8:00")
    @TableField("SUBMIT_AT")
    private LocalDateTime submitAt;

    /**
     * 确认人Id
     */
    @TableField("CONFIRM_USER_ID")
    private String confirmUserId;

    /**
     * 确认人
     */
    @TableField(exist = false)
    private SysUser confirmUser;


    /**
     * 审核状态 0：未审核；1：通过；2：拒绝
     */
    @TableField("AUDIT_STATUS")
    private Integer auditStatus;

    /**
     * 审核时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8:00")
    @TableField("AUDIT_AT")
    private LocalDateTime auditAt;

    /**
     * 项目ID
     */
    @TableField("PROJECT_ID")
    private String projectId;

    /**
     * 拒绝理由
     */
    @TableField("REFUSE_REASON")
    private String refuseReason;

    /**
     * 是否已经重新上传；1表示已重新上传，其他表示需要重新上传
     */
    @TableField("IS_REUPLOAD")
    private Integer isReupload;

    /**
     * 重新上传的交付物id，以‘||’拼接
     */
    @TableField("REUPLOAD_LIST")
    private String reuploadList;

    /**
     * 交付物列表
     */
    @TableField(exist = false)
    private List<Delivery> deliveryList;

    /**
     * 任务
     */
    @TableField(exist = false)
    private Task task;

    /**
     * 项目
     */
    @TableField(exist = false)
    private Project project;

    /**
     * 交付物路径
     */
    @TableField(exist = false)
    private String deliveryPath;

}

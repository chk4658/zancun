package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 项目与人员收藏关系表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("PROJECT_COLLECTION")
public class ProjectCollection extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @TableField("PROJECT_ID")
    private String projectId;

    /**
     * 用户ID
     */
    @TableField("SYS_USER_ID")
    private String sysUserId;


}

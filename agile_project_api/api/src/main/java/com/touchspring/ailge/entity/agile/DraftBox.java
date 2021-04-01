package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 草稿箱
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("DRAFT_BOX")
public class DraftBox extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 项目名称
     */
    @TableField("NAME")
    private String name;

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

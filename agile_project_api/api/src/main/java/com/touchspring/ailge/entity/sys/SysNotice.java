package com.touchspring.ailge.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 公告表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_NOTICE")
public class SysNotice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    @TableField("TITLE")
    private String title;

    /**
     * 详情
     */
    @TableField("DETAIL")
    private String detail;

    /**
     * 类型
     */
    @TableField("TYPE")
    private Long type;

    /**
     * 是否置顶0：是1：否
     */
    @TableField("IS_TOP")
    private Long isTop;

    /**
     * 有效期
     */
    @TableField("VALIDITY_TIME")
    private LocalDateTime validityTime;


}

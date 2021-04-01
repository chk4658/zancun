package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 消息用户关系表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("MESSAGE_USER")
public class MessageUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableField("MESSAGE_ID")
    private String messageId;

    /**
     * 是否已读 0：未读； 1 ：已读  默认0
     */
    @TableField("IS_READ")
    private Integer isRead;

    /**
     * 接收人ID
     */
    @TableField("SYS_USER_ID")
    private String sysUserId;


}

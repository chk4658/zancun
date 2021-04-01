package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("CHAT_USER")
public class ChatUser extends BaseEntity {

    /**
     * 交流ID
     */
    @TableField("CHAT_ID")
    private String chatId;

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

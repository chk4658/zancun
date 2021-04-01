package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.utils.PropertyMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("CHAT")
public class Chat extends BaseEntity {

    /**
     * 关联表的id
     */
    @TableField("FOREIGN_ID")
    private String foreignId;

    /**
     * 类型 CIRCLE圈子 PROJECT任务 TASK
     */
    @TableField("TYPE")
    private String type;

    /**
     * 父类id
     */
    @TableField("PARENT_ID")
    private String parentId;


    /**
     * 回复id
     */
    @TableField("REPLY_ID")
    private String replyId;


    /**
     * 内容
     */
    @TableField("CONTENT")
    private String content;


    /**
     * 预留字段1
     */
    @TableField("EXT2")
    private String ext2;

    /**
     * 创建人
     */
    @TableField(exist = false)
    private SysUser createUser;

    /**
     * 子模块
     */
    @TableField(exist = false)
    private List<Chat> children;

    /**
     * 子模块
     */
    @TableField(exist = false)
    private Chat reply;




}

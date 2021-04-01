package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 消息表
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("MESSAGE")
public class Message extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 消息类型：PROJECT，CIRCLE，QA，TASK
     */
    @TableField("TYPE")
    private String type;

    /**
     * 关联ID
     */
    @TableField("RELATION_ID")
    private String relationId;

    /**
     * 消息内容
     */
    @TableField("CONTENT")
    private String content;

    /**
     * 项目Id，只有type为TASK时  作为前端方便取数据使用
     */
    @TableField("PROJECT_ID")
    private String projectId;


}

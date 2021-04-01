package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("QUESTION_TYPE")
public class QuestionType extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @TableField("name")
    private String name;
    /**
     * 父id
     */
    @TableField("PARENT_ID")
    private String parentId;

    @TableField(exist = false)
    private List<QuestionType> children;


}

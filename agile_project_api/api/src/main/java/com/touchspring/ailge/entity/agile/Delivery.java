package com.touchspring.ailge.entity.agile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchspring.ailge.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 交付物
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("DELIVERY")
public class Delivery extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 交付物名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 路径
     */
    @TableField("PATH")
    private String path;

    /**
     * 任务交付物关系
     */
    @TableField(exist = false)
    private List<TaskDelivery> taskDeliveryList;


}

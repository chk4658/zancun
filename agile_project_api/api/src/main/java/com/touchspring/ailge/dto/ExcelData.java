package com.touchspring.ailge.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @program: agile
 * @description:
 * @author: zdj
 * @create: 2020-10-27 15:55
 **/
@Data
public class ExcelData extends BaseRowModel{
    /**
     * 名称
     */
    @ExcelProperty(value = "名称", index = 0)
    private String name;
    /**
     * 优先级
     */
    @ExcelProperty(value = "优先级", index = 1)
    private String priority;
    /**
     * 负责人角色
     */
    @ExcelProperty(value = "负责人角色", index = 2)
    private String reviewProjectRoleId;
    /**
     * 审核人角色
     */
    @ExcelProperty(value = "审核人角色", index = 3)
    private String confirmProjectRoleId;
    /**
     * 截止时间
     */
    @ExcelProperty(value = "截止时间", index = 4)
    private Date endTime;
    /**
     * 交付要求
     */
    @ExcelProperty(value = "交付要求", index = 5)
    private String isRequirement;
    /**
     * 任务类型
     */
    @ExcelProperty(value = "任务类型", index = 6)
    private String type;
    /**
     * 开阀条件
     */
    @ExcelProperty(value = "开阀条件", index = 7)
    private String openConditions;
    /**
     * 开阀描述
     */
    @ExcelProperty(value = "开阀描述", index = 8)
    private String openDescription;



}

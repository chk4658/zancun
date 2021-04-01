package com.touchspring.ailge.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * 当量Excel
 */
@Data
public class EquivalentExcelDTO extends BaseRowModel {

    /**
     * 名称
     */
    @ExcelProperty(value = "员工账号", index = 0)
    private String account;
    /**
     * 优先级
     */
    @ExcelProperty(value = "其他当量", index = 1)
    private String other;
    /**
     * 负责人角色
     */
    @ExcelProperty(value = "交付质量", index = 2)
    private String deliveryQuality;
    /**
     * 审核人角色
     */
    @ExcelProperty(value = "知识", index = 3)
    private String knowledge;

}

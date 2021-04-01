package com.touchspring.ailge.dto;


import lombok.Data;

import java.util.List;

/**
 * 级联选择器
 */
@Data
public class CascaderResultDTO {

    /**
     * 值
     */
    private String value;
    /**
     * 显示
     */
    private String label;
    /**
     * 子类
     */
    private List<CascaderResultDTO> children;

}

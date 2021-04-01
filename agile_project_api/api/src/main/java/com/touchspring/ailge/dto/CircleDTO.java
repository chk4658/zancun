package com.touchspring.ailge.dto;

import com.touchspring.ailge.entity.agile.Circle;
import lombok.Data;

import java.util.List;

@Data
public class CircleDTO extends Circle {

    /**
     * 角色权限
     */
    private List<String> roleNames;

    /**
     * 允许操作圈子的人员
     */
    private List<String> operateIds;

    /**
     * 允许添加子圈的人员
     */
    private List<String> addCircleIds;

    /**
     * 允许添加项目的人员
     */
    private List<String> addProjectIds;

}

package com.touchspring.ailge.dto;

import com.touchspring.ailge.entity.agile.Task;
import lombok.Data;

@Data
public class TaskDTO extends Task {

    /**
     * 审核人角色名称
     */
    private String reviewRoleName;

    /**
     * 确认人角色名称
     */
    private String confirmRoleName;
    /**
     * 是否为重新开启任务
     */
    private Boolean restartTaskFlag;
}

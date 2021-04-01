package com.touchspring.ailge.dto;

import com.touchspring.ailge.entity.agile.ProjectTemplateTask;
import lombok.Data;

@Data
public class ProjectTemplateTaskDTO extends ProjectTemplateTask {

    /**
     * 审核人角色名称
     */
    private String reviewRoleName;

    /**
     * 确认人角色名称
     */
    private String confirmRoleName;
}

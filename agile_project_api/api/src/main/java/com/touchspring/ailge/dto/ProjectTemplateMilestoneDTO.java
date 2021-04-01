package com.touchspring.ailge.dto;

import com.touchspring.ailge.entity.agile.ProjectTemplateMilestone;
import lombok.Data;

@Data
public class ProjectTemplateMilestoneDTO extends ProjectTemplateMilestone {

    /**
     * 审核人角色名称
     */
    private String reviewRoleName;

    /**
     * 确认人角色名称
     */
    private String confirmRoleName;

}

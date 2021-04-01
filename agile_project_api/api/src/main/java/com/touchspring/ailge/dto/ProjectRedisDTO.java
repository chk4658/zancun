package com.touchspring.ailge.dto;

import com.touchspring.ailge.entity.agile.ProjectMilestone;
import com.touchspring.ailge.entity.agile.Task;
import lombok.Data;

import java.util.List;

/**
 * 项目的Redis工具类
 */
@Data
public class ProjectRedisDTO {

    public ProjectRedisDTO(List<ProjectMilestone> projectMilestoneList, List<Task> parentTaskList, List<Task> childTaskList) {
        this.projectMilestoneList = projectMilestoneList;
        this.parentTaskList = parentTaskList;
        this.childTaskList = childTaskList;
    }

    public ProjectRedisDTO() {
    }

    /**
     * 里程碑
     */
    private List<ProjectMilestone> projectMilestoneList;

    /**
     * 父任务
     */
    private List<Task> parentTaskList;

    /**
     * 子任务
     */
    private List<Task> childTaskList;

}

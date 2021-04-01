package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.ProjectMilestoneLog;
import com.touchspring.ailge.entity.agile.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectMilestoneLogService extends IService<ProjectMilestoneLog> {

    void saveEstEndTimeChange(LocalDateTime lastTime, LocalDateTime curTime, String userId, String milestoneId, String projectId);

    void saveTaskChange(String type, String userId, List<Task> taskList);

    void saveMilestoneRoleChange(String userId, String milestoneId, String projectId, String content);
}

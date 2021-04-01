package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.ProjectMilestoneLogDao;
import com.touchspring.ailge.entity.agile.ProjectMilestoneLog;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.service.agile.ProjectMilestoneLogService;
import com.touchspring.ailge.utils.PropertyMsg;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectMilestoneLogServiceImpl extends ServiceImpl<ProjectMilestoneLogDao, ProjectMilestoneLog> implements ProjectMilestoneLogService {

    @Autowired
    private ProjectMilestoneLogDao projectMilestoneLogDao;

    @Override
    public void saveEstEndTimeChange(LocalDateTime lastTime, LocalDateTime curTime, String userId, String milestoneId, String projectId) {
        ProjectMilestoneLog projectMilestoneLog = new ProjectMilestoneLog();
        projectMilestoneLog.setContent("Changed 里程碑时间节点, From \"" + lastTime + "\", To \"" + curTime + "\"");
        projectMilestoneLog.setCreateUserId(userId);
        projectMilestoneLog.setProjectMilestoneId(milestoneId);
        projectMilestoneLog.setProjectId(projectId);
        projectMilestoneLogDao.insert(projectMilestoneLog);
    }

    @Override
    public void saveTaskChange(String type, String userId, List<Task> taskList) {
        if (CollectionUtils.isEmpty(taskList)) return;
        Map<String, List<Task>> milestoneMap = taskList.stream().collect(Collectors.groupingBy(Task::getMilestoneId));
        List<ProjectMilestoneLog> milestoneLogs = new ArrayList<>();
        milestoneMap.forEach((k, v) -> {

            List<String> taskNames = v.stream().map(Task::getName).collect(Collectors.toList());
            ProjectMilestoneLog projectMilestoneLog = new ProjectMilestoneLog();
            projectMilestoneLog.setContent(type + "任务: " + StringUtils.join(taskNames.toArray(), ","));
            projectMilestoneLog.setCreateUserId(userId);
            projectMilestoneLog.setProjectMilestoneId(k);
            projectMilestoneLog.setProjectId(v.get(0).getProjectId());
            milestoneLogs.add(projectMilestoneLog);
        });
        this.saveBatch(milestoneLogs);
    }

    @Override
    public void saveMilestoneRoleChange(String userId, String milestoneId, String projectId, String content) {
        ProjectMilestoneLog projectMilestoneLog = new ProjectMilestoneLog();
        projectMilestoneLog.setContent(content);
        projectMilestoneLog.setCreateUserId(userId);
        projectMilestoneLog.setProjectMilestoneId(milestoneId);
        projectMilestoneLog.setProjectId(projectId);
        projectMilestoneLogDao.insert(projectMilestoneLog);
    }

}

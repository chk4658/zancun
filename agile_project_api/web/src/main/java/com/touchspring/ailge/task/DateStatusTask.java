package com.touchspring.ailge.task;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.agile.ProjectDao;
import com.touchspring.ailge.dao.agile.ProjectMilestoneDao;
import com.touchspring.ailge.dao.agile.TaskDao;
import com.touchspring.ailge.entity.agile.Project;
import com.touchspring.ailge.entity.agile.ProjectMilestone;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.enums.*;
import com.touchspring.ailge.service.agile.TaskService;
import com.touchspring.ailge.utils.PlaceholderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 定时触发，任务的时间状态
 */
@Service
public class DateStatusTask {

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProjectMilestoneDao projectMilestoneDao;
    @Autowired
    private ProjectDao projectDao;

    /**
     * 定时检查未完成任务的期限
     */
    @Scheduled(cron = "0 5 0 * * ?")
    public void checkNotCompleteTask(){
        //查询状态为未完成，且状态、截止日期不为空的任务
        List<Task> taskList = new LambdaQueryChainWrapper<Task>(taskDao).isNotNull(Task::getStatus)
                .ne(Task::getStatus, TaskStatusEnum.COMPLETED.getCode())
                .isNotNull(Task::getEstEndTime)
                .ne(Task::getStMark, BaseEnums.YES.getCode()).list();
        if (CollectionUtils.isEmpty(taskList)) return;

        //任务状态更新
        List<Task> differentTaskList = taskList.stream().
                filter(task -> !StringUtils.equals(task.getDateStatus(), taskService.getDateStatus(task.getStatus(), task.getEstEndTime()))).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(differentTaskList)) return;
        differentTaskList = differentTaskList.stream().peek(task -> task.setDateStatus(taskService.getDateStatus(task.getStatus(), task.getEstEndTime()))).collect(Collectors.toList());
        taskService.updateBatchById(differentTaskList);

        // 更新Redis--->只更新启用状态
        List<Task> enableTasks = differentTaskList.stream().filter(task -> task.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode()))).collect(Collectors.toList());
        taskService.batchUpdateTaskInRedis(enableTasks);

        // 任务即将超期，消息给（任务负责人、项目负责人）
        List<Task> willExpireTasks = enableTasks.stream().filter(task -> task.getDateStatus().equals(DateStatusEnum.ADJACENT_NODE.getCode())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(willExpireTasks))
            willExpireTasks.forEach(task -> {
                Project project = projectDao.selectById(task.getProjectId());
                if (project.getHasOnLine() != null && project.getHasOnLine().equals(BaseEnums.YES.getCode())) {
                    Map<String,String> messageExt = new HashMap<>();
                    messageExt.put("projectName",project.getName());
                    messageExt.put("taskName",task.getName());
                    int day = task.getEstEndTime() == null ? 0 : Period.between(LocalDate.now(), task.getEstEndTime().toLocalDate()).getDays();
                    messageExt.put("day",day + "");
                    if(day > 0) {
                        taskService.taskMessage(task.getReviewProjectRoleId(), null, task.getId(),
                                true, task.getProjectId(), MessageModuleEnum.TASK_WILL_DELAY.getType(), PlaceholderUtil.resolvePlaceholders(MessageModuleEnum.TASK_WILL_DELAY.getMsg(),messageExt));

                    }
                }
              });

        //里程碑状态 --> 找到所属任务最大状态 更新Redis--->只更新启用状态
        Set<String> milestoneIds = differentTaskList.stream().filter(task -> StringUtils.isNotBlank(task.getMilestoneId())).map(Task::getMilestoneId).collect(Collectors.toSet());
        milestoneIds.forEach(milestoneId -> taskService.setMilestoneStatusByTask(milestoneId, true));

        // 项目状态 --> 找到所属里程碑最大的状态
        Set<String> projectIds = differentTaskList.stream().filter(task -> StringUtils.isNotBlank(task.getProjectId())).map(Task::getProjectId).collect(Collectors.toSet());
        projectIds.forEach(projectId -> taskService.setProjectStatusByMilestone(projectId));
    }
}

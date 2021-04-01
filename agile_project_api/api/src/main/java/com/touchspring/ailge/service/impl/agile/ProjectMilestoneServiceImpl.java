package com.touchspring.ailge.service.impl.agile;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.agile.*;
import com.touchspring.ailge.dao.sys.SysRoleTemplateDao;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.dto.EnableOrForbiddenDTO;
import com.touchspring.ailge.dto.ProjectMilestoneDTO;
import com.touchspring.ailge.dto.ProjectRedisDTO;
import com.touchspring.ailge.dto.TreeResultDTO;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.agile.*;
import com.touchspring.ailge.enums.*;
import com.touchspring.ailge.service.agile.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.utils.BeanChangeUtilService;
import com.touchspring.ailge.utils.IdWorker;
import com.touchspring.ailge.utils.PlaceholderUtil;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 里程碑信息表
 * 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class ProjectMilestoneServiceImpl extends ServiceImpl<ProjectMilestoneDao, ProjectMilestone> implements ProjectMilestoneService {

    @Autowired
    private ProjectMilestoneDao projectMilestoneDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskDataDao taskDataDao;
    @Autowired
    private ProjectRoleDao projectRoleDao;
    @Autowired
    private SysRoleTemplateDao sysRoleTemplateDao;
    @Autowired
    private ProjectRoleService projectRoleService;
    @Autowired
    private ProjectRoleUserServiceImpl projectRoleUserService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProjectTemplateMilestoneDao projectTemplateMilestoneDao;
    @Autowired
    private ProjectTemplateRoleDao projectTemplateRoleDao;
    @Autowired
    private ProjectTemplateTaskDao projectTemplateTaskDao;
    @Autowired
    private ProjectTemplateAttrDao projectTemplateAttrDao;
    @Autowired
    private ProjectAttrDao projectAttrDao;
    @Autowired
    private TaskTemplateDataDao taskTemplateDataDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BeanChangeUtilService<ProjectMilestone> beanChangeUtilService;
    @Autowired
    private ProjectMilestoneLogDao projectMilestoneLogDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private TaskDeliveryService taskDeliveryService;
    @Autowired
    private TaskDataService taskDataService;
    @Autowired
    private ProjectLogService projectLogService;
    @Autowired
    private ProjectMilestoneLogService projectMilestoneLogService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRoleUserDao projectRoleUserDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private TaskLogService taskLogService;

    /**
     * 里程碑列表及关联任务
     */
    @Override
    public List<ProjectMilestone> findByProjectId(String projectId, String searchName) {

        List<ProjectMilestone> projectMilestoneList = new LambdaQueryChainWrapper<ProjectMilestone>(projectMilestoneDao)
                .eq(ProjectMilestone::getProjectId, projectId).orderByAsc(ProjectMilestone::getSort).list();

        projectMilestoneList = projectMilestoneList.stream().peek(projectMilestone -> {
            //获取父任务
            List<Task> parentTask = new LambdaQueryChainWrapper<Task>(taskDao).isNull(Task::getParentId).eq(Task::getMilestoneId, projectMilestone.getId()).orderByAsc(Task::getSort).list();
            for (Task parent : parentTask) {
                this.getAttribute(parent);
            }
            projectMilestone.setTaskList(parentTask);
            if (StringUtils.isNotBlank(projectMilestone.getReviewProjectRoleId()))
                projectMilestone.setReviewProjectRole(projectRoleDao.selectById(projectMilestone.getReviewProjectRoleId()));
            if (StringUtils.isNotBlank(projectMilestone.getConfirmProjectRoleId()))
                projectMilestone.setConfirmProjectRole(projectRoleDao.selectById(projectMilestone.getConfirmProjectRoleId()));
        }).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(projectMilestoneList) && StringUtils.isNotBlank(searchName)) {
            List<ProjectMilestone> results = new ArrayList<>();
            Pattern pattern = Pattern.compile(searchName);
            for (ProjectMilestone projectMilestone : projectMilestoneList) {
                Matcher matcher = pattern.matcher(projectMilestone.getName());
                // 根据里程碑名称查找
                if (matcher.find()) results.add(projectMilestone);
            }
            return results;
        }
//        projectMilestoneDao.findAllByProjectId(projectId, searchName)

        return projectMilestoneList;
    }

    @Override
    public List<ProjectMilestone> findByProjectIdAndStatusName(String projectId, String statusName) {
        if (StringUtils.isBlank(statusName)) return null;

        List<Task> forbiddenTaskList = new ArrayList<>();
        if (statusName.equals(TreeResultStatus.FORBIDDEN.getMessage())) {
            forbiddenTaskList = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getProjectId, projectId)
                    .eq(Task::getForbidden, Integer.valueOf(TreeResultStatus.FORBIDDEN.getCode())).eq(Task::getStMark, BaseEnums.NO.getCode()).orderByAsc(Task::getSort).list();
        } else {
            forbiddenTaskList = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getProjectId, projectId)
                    .eq(Task::getEnabled, Integer.valueOf(TreeResultStatus.NOT_ENABLE.getCode())).eq(Task::getStMark, BaseEnums.NO.getCode()).orderByAsc(Task::getSort).list();
        }

        List<Task> taskTreeList = new ArrayList<>();
        //获取父任务
        Iterator<Task> it = forbiddenTaskList.iterator();
        while (it.hasNext()) {
            Task task = it.next();
            if (com.touchspring.core.utils.StringUtils.isBlank(task.getParentId())) {
                taskTreeList.add(task);
                it.remove();
            }
        }

        // tasks 存放无父任务的子任务
        ArrayList<Task> tasks = new ArrayList<>(forbiddenTaskList);
        if (!CollectionUtils.isEmpty(forbiddenTaskList)) {
            for (Task task : forbiddenTaskList) {
                for (Task c : taskTreeList) {
                    if (StringUtils.equals(c.getId(), task.getParentId())) {
                        if (c.getChildren() == null)
                            c.setChildren(new ArrayList<>());
                        c.getChildren().add(task);
                        tasks.remove(task);
                        break;
                    }
                }
            }
        }

        //查找父任务
        if (!CollectionUtils.isEmpty(tasks)) {
            //根据父任务分组
            Map<String, List<Task>> sameParentIdTasks = tasks.stream().collect(Collectors.groupingBy(Task::getParentId));
            sameParentIdTasks.forEach((k, v) -> {
                Task parentTask = this.getParentTask(k, v);
                taskTreeList.add(parentTask);
            });
        }
        taskTreeList.sort(Comparator.comparing(Task::getSort));
        //根据里程碑分组
        Map<String, List<Task>> projectMilestoneMap = taskTreeList.stream().collect(Collectors.groupingBy(Task::getMilestoneId));

        List<ProjectMilestone> results = new ArrayList<>();
        List<ProjectMilestone> projectMilestones = new ArrayList<>();
        if (statusName.equals(TreeResultStatus.FORBIDDEN.getMessage())) {
            projectMilestones = new LambdaQueryChainWrapper<ProjectMilestone>(projectMilestoneDao).eq(ProjectMilestone::getProjectId, projectId).eq(ProjectMilestone::getForbidden, Integer.valueOf(TreeResultStatus.FORBIDDEN.getCode())).list();
        } else {
            projectMilestones = new LambdaQueryChainWrapper<ProjectMilestone>(projectMilestoneDao).eq(ProjectMilestone::getProjectId, projectId).eq(ProjectMilestone::getEnabled, Integer.valueOf(TreeResultStatus.NOT_ENABLE.getCode())).list();
        }
        if (!CollectionUtils.isEmpty(projectMilestones)) {
            projectMilestones.forEach(projectMilestone -> {
                if (CollectionUtils.isEmpty(projectMilestoneMap) || CollectionUtils.isEmpty(projectMilestoneMap.get(projectMilestone.getId())))
                    results.add(projectMilestone);
            });
        }

        for (Map.Entry<String, List<Task>> map : projectMilestoneMap.entrySet()) {
            ProjectMilestone projectMilestone = projectMilestoneDao.selectById(map.getKey());
            projectMilestone.setTaskList(map.getValue());
            results.add(projectMilestone);
        }
        results.sort(Comparator.comparing(ProjectMilestone::getSort));
        return results;
    }

    public Task getParentTask(String parentId, List<Task> tasks) {
        Task parentTask = taskDao.selectById(parentId);
        parentTask.setChildren(tasks);
        return parentTask;
    }

    private void putToMap(Map<String, List<Task>> projectMilestoneMap, Task task) {
        if (CollectionUtils.isEmpty(projectMilestoneMap) || CollectionUtils.isEmpty(projectMilestoneMap.get(task.getMilestoneId()))) {
            List<Task> taskList = new ArrayList<>();
            taskList.add(task);
            projectMilestoneMap.put(task.getMilestoneId(), taskList);
        } else {
            List<Task> lastTasks = projectMilestoneMap.get(task.getMilestoneId());
            lastTasks.add(task);
            projectMilestoneMap.put(task.getMilestoneId(), lastTasks);
        }
    }

    /**
     * 从redis中查找项目下的所有内容
     *
     * @param projectId  项目ID
     * @param searchName 查询的名称
     * @return .
     */
    @Override
    public ProjectRedisDTO findAllInRedisByProjectId(String projectId, String searchName) {
        HashOperations<String, String, List<String>> hash = redisTemplate.opsForHash();
        //获取所有里程碑
        List<ProjectMilestone> projectMilestoneList = new ArrayList<>();
        Cursor<Map.Entry<String, String>> milestoneCursor = this.findInRedis(projectId, RedisTableName.PROJECT_MILESTONE.getCode());
        while (milestoneCursor.hasNext()) {
            Map.Entry<String, String> entry = milestoneCursor.next();
            projectMilestoneList.add(JSON.parseObject(entry.getValue(), ProjectMilestone.class));
        }
        //获取所有父任务
        List<Task> parentTaskList = new ArrayList<>();
        Cursor<Map.Entry<String, String>> parentTaskCursor = this.findInRedis(projectId, RedisTableName.PARENT_TASK.getCode());
        while (parentTaskCursor.hasNext()) {
            Map.Entry<String, String> entry = parentTaskCursor.next();
            parentTaskList.add(JSON.parseObject(entry.getValue(), Task.class));
        }
        //获取所有子任务
        List<Task> childTaskList = new ArrayList<>();
        Cursor<Map.Entry<String, String>> childTaskCursor = this.findInRedis(projectId, RedisTableName.CHILD_TASK.getCode());
        while (childTaskCursor.hasNext()) {
            Map.Entry<String, String> entry = childTaskCursor.next();
            childTaskList.add(JSON.parseObject(entry.getValue(), Task.class));
        }

        if (StringUtils.isBlank(searchName))
            return new ProjectRedisDTO(projectMilestoneList, parentTaskList, childTaskList);

        // 根据名称搜索
        List<ProjectMilestone> nameLikeMilestoneList = projectMilestoneList.stream()
                .filter(projectMilestone -> StringUtils.isNotBlank(projectMilestone.getName()) && Pattern.compile(searchName).matcher(projectMilestone.getName()).find()).collect(Collectors.toList());
        List<String> milestoneIdList = nameLikeMilestoneList.stream().map(BaseEntity::getId).collect(Collectors.toList());
        List<Task> parentTaskListByMilestoneId = parentTaskList.stream().filter(task -> milestoneIdList.contains(task.getMilestoneId())).collect(Collectors.toList());
        List<Task> childTaskListByMilestoneId = childTaskList.stream().filter(task -> milestoneIdList.contains(task.getMilestoneId())).collect(Collectors.toList());

        return new ProjectRedisDTO(nameLikeMilestoneList, parentTaskListByMilestoneId, childTaskListByMilestoneId);
    }

    /**
     * 在Redis中根据指定前缀查找
     *
     * @param projectId 项目ID
     * @param pattern   前缀名称
     * @return redis中对应数据
     */
    private Cursor<Map.Entry<String, String>> findInRedis(String projectId, String pattern) {
        Cursor<Map.Entry<String, String>> cursor = redisTemplate.opsForHash().scan(
                RedisTableName.PROJECT.getCode() + projectId,
                ScanOptions.scanOptions().match(pattern + "*").build());
        return cursor;
    }

    /**
     * 获取子任务
     */
    private List<Task> findChildren(String parentId) {
        List<Task> tasks = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getParentId, parentId)
                .eq(Task::getEnabled, Integer.valueOf(TreeResultStatus.ENABLE.getCode()))
                .eq(Task::getForbidden, Integer.valueOf(TreeResultStatus.NOT_FORBIDDEN.getCode())).orderByAsc(Task::getSort).list();
        for (Task task : tasks) {
            this.getAttribute(task);
        }
        return tasks;
    }

    /**
     * 获取其余属性
     *
     * @param task 任务
     */
    public void getAttribute(Task task) {
        List<Task> children = this.findChildren(task.getId());
        task.setChildren(children);
        task.setTaskDataList(taskDataDao.findByTaskId(task.getId()));
        task.setReviewUser(projectRoleUserService.findByProjectRoleId(task.getReviewProjectRoleId()));
        task.setConfirmUser(projectRoleUserService.findByProjectRoleId(task.getConfirmProjectRoleId()));
        if (StringUtils.isNotBlank(task.getReviewProjectRoleId()))
            task.setReviewProjectRole(projectRoleDao.selectById(task.getReviewProjectRoleId()));
        if (StringUtils.isNotBlank(task.getConfirmProjectRoleId()))
            task.setConfirmProjectRole(projectRoleDao.selectById(task.getConfirmProjectRoleId()));
    }

    /**
     * 保存/更新
     */
    @Override
    public ResultData save(ProjectMilestoneDTO projectMilestoneDTO, String userId, boolean flag, boolean batchRorC) {
        ProjectMilestone target;
        //审核人
        String reviewProjectRoleId = projectRoleService.getProjectRoleIdByRoleName(projectMilestoneDTO.getReviewRoleName(), projectMilestoneDTO.getProjectId(), userId);
        //确认人
        String confirmProjectRoleId = projectRoleService.getProjectRoleIdByRoleName(projectMilestoneDTO.getConfirmRoleName(), projectMilestoneDTO.getProjectId(), userId);

        if (StringUtils.isBlank(projectMilestoneDTO.getId())) {
            target = projectMilestoneDTO;
            target.setReviewProjectRoleId(reviewProjectRoleId);
            target.setConfirmProjectRoleId(confirmProjectRoleId);
            target.setSort(IdWorker.nextId());
            target.setEnabled(Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
            target.setForbidden(Integer.valueOf(TreeResultStatus.NOT_FORBIDDEN.getCode()));
            target.setCreateUserId(userId);
            projectMilestoneDao.insert(target);

            //项目日志：里程碑创建
            projectLogService.saveMilestoneChange(LogTypeEnum.INSERT.getMsg(), target.getName(), target.getProjectId(), userId);

        } else {
            target = projectMilestoneDao.selectById(projectMilestoneDTO.getId());

            if (target == null) return ResultData.notFound();
            // 项目为上线状态，里程碑截止时间不能早于任务截止时间
            if (projectMilestoneDTO.getEstEndTime() != null && !projectMilestoneDTO.getEstEndTime().equals(target.getEstEndTime()) && StringUtils.isNotBlank(target.getProjectId())) {
                Project project = new LambdaQueryChainWrapper<Project>(projectDao).eq(BaseEntity::getId, target.getProjectId()).eq(Project::getHasOnLine, BaseEnums.YES.getCode()).one();
                if (project != null) {
                    LocalDateTime maxTaskEstEndTime = taskDao.getMaxEstEndTimeByMilestoneIdAndEnable(target.getId(), BaseEnums.YES.getCode());
                    if (maxTaskEstEndTime != null && projectMilestoneDTO.getEstEndTime().toLocalDate().isBefore(maxTaskEstEndTime.toLocalDate()))
                        return new ResultData(ResultStatus.MILESTONE_EST_END_TIME.getCode(), ResultStatus.MILESTONE_EST_END_TIME.getMessage());
                }
            }

            projectMilestoneDTO.setReviewProjectRoleId(StringUtils.isBlank(projectMilestoneDTO.getReviewRoleName()) ? "" : reviewProjectRoleId);
            projectMilestoneDTO.setConfirmProjectRoleId(StringUtils.isBlank(projectMilestoneDTO.getConfirmRoleName()) ? "" : confirmProjectRoleId);

            projectMilestoneDTO.setUpdateUserId(userId);
            //判断是否变更角色
            projectMilestoneDao.updateById(projectMilestoneDTO);

            //Log 里程碑时间节点的变更
            if (projectMilestoneDTO.getEstEndTime() != null && target.getEstEndTime() != null && !projectMilestoneDTO.getEstEndTime().equals(target.getEstEndTime()))
                projectMilestoneLogService.saveEstEndTimeChange(target.getEstEndTime(), projectMilestoneDTO.getEstEndTime(), userId, target.getId(), target.getProjectId());

            //Log 负责人/审核人角色的变更
            StringBuilder str = new StringBuilder();
            if (!StringUtils.equals(target.getReviewProjectRoleId(), reviewProjectRoleId))
                str.append(PlaceholderUtil.resolvePlaceholders(ChangeEnum.userChange,
                        this.getChangeLog("里程碑负责人角色",
                                StringUtils.isBlank(target.getReviewProjectRoleId()) ? null : projectRoleDao.selectById(target.getReviewProjectRoleId()).getRoleName(),
                                StringUtils.isBlank(reviewProjectRoleId) ? null : projectMilestoneDTO.getReviewRoleName()))).append("\";");

            if (!StringUtils.equals(target.getConfirmProjectRoleId(), confirmProjectRoleId))
                str.append(PlaceholderUtil.resolvePlaceholders(ChangeEnum.userChange,
                        this.getChangeLog("里程碑审核人角色",
                                StringUtils.isBlank(target.getConfirmProjectRoleId()) ? null : projectRoleDao.selectById(target.getConfirmProjectRoleId()).getRoleName(),
                                StringUtils.isBlank(confirmProjectRoleId) ? null : projectMilestoneDTO.getConfirmRoleName()))).append("\";");
            if (StringUtils.isNotBlank(str.toString()))
                projectMilestoneLogService.saveMilestoneRoleChange(userId, target.getId(), target.getProjectId(), str.replace(str.length() - 1, str.length(), "").toString());

            //Log 名称修改
            if (!StringUtils.equals(target.getName(), projectMilestoneDTO.getName()))
                projectMilestoneLogService.saveMilestoneRoleChange(userId, target.getId(), target.getProjectId(),
                        PlaceholderUtil.resolvePlaceholders(ChangeEnum.userChange, this.getChangeLog("名称", target.getName(), projectMilestoneDTO.getName())));
        }

        //更新Redis中的里程碑
        this.updateMilestoneInRedisByMilestone(target);

        if (flag)
            if (batchRorC) {
                this.batchUpdateTaskRole(target.getId(), reviewProjectRoleId, null, userId);
            } else {
                this.batchUpdateTaskRole(target.getId(), null, confirmProjectRoleId, userId);
            }


        // 项目截止日期
        if (projectMilestoneDTO.getEstEndTime() != null && StringUtils.isNotBlank(target.getProjectId())) {
            Project project = projectDao.selectById(target.getProjectId());
            if (project != null) {
                // 取最大的启用状态里程碑截止时间
                LocalDateTime maxProjectEstEndTime = projectMilestoneDao.getMaxEstEndTimeByProjectIdAndEnable(target.getProjectId(), Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
                project.setEstEndTime(maxProjectEstEndTime);
                projectDao.updateById(project);
            }
        }

        return ResultData.ok();
    }

    /**
     * 批量修改任务下的角色 记录更改后的对应人员的log
     *
     * @param projectMilestoneId   里程碑ID
     * @param reviewProjectRoleId  确认人角色ID
     * @param confirmProjectRoleId 审核人角色ID
     */
    private void batchUpdateTaskRole(String projectMilestoneId, String reviewProjectRoleId, String confirmProjectRoleId, String createUserId) {
        List<Task> taskList = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getMilestoneId, projectMilestoneId).list();
        if (CollectionUtils.isEmpty(taskList)) return;

        Project project = taskList.get(0).getProjectId() == null ? null : projectDao.selectById(taskList.get(0).getProjectId());

        //查询之前角色对应的人员与当前修改后的角色对应的人员是否相同
        ProjectRoleUser curReviewProjectRoleUser = null;
        if (StringUtils.isNotBlank(reviewProjectRoleId))
            curReviewProjectRoleUser = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).eq(ProjectRoleUser::getProjectRoleId, reviewProjectRoleId).one();
        ProjectRoleUser curConfirmProjectRoleUser = null;
        if (StringUtils.isNotBlank(confirmProjectRoleId))
            curConfirmProjectRoleUser = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).eq(ProjectRoleUser::getProjectRoleId, confirmProjectRoleId).one();

        List<TaskLog> taskLogs = new ArrayList<>();

        String curReviewUserName = StringUtils.isBlank(reviewProjectRoleId) || curReviewProjectRoleUser == null ? null : sysUserDao.selectById(curReviewProjectRoleUser.getSysUserId()).getRealName();
        String curConfirmUserName = StringUtils.isBlank(confirmProjectRoleId) || curConfirmProjectRoleUser == null ? null : sysUserDao.selectById(curConfirmProjectRoleUser.getSysUserId()).getRealName();
        taskList.forEach(task -> {
            if (StringUtils.isNotBlank(reviewProjectRoleId)) {
                this.comparePastAndPresentValues(task.getReviewProjectRoleId(), curReviewUserName, createUserId, task.getProjectId(), task.getId(), taskLogs, true, project == null ? null : project.getHasOnLine(), reviewProjectRoleId);
                task.setReviewProjectRoleId(reviewProjectRoleId);
            } else if (StringUtils.isNotBlank(confirmProjectRoleId)) {

                this.comparePastAndPresentValues(task.getConfirmProjectRoleId(), curConfirmUserName, createUserId, task.getProjectId(), task.getId(), taskLogs, false, project == null ? null : project.getHasOnLine(), confirmProjectRoleId);
                task.setConfirmProjectRoleId(confirmProjectRoleId);

            }
        });
        taskService.updateBatchById(taskList);
        //更新Redis中的任务 只更新启用状态
        List<Task> enableTasks = taskList.stream().filter(task -> task.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode()))
                && BaseEnums.NO.getCode().equals(task.getStMark())).collect(Collectors.toList());
        taskService.batchUpdateTaskInRedis(enableTasks);

        if (!CollectionUtils.isEmpty(taskLogs))
            taskLogService.saveBatch(taskLogs);
    }

    /**
     * 比较之前的用户与当前修改后的用户是否相同
     *
     * @param lastRoleId   历史角色ID
     * @param curUserName  当前指定的人员姓名
     * @param createUserId 创建用户ID
     * @param projectId    项目ID
     * @param taskId       任务ID
     * @param taskLogs     任务日志
     * @param flag         true ：负责人 false：审核人
     * @param hasOnLine    是否上线：1上线
     */
    public void comparePastAndPresentValues(String lastRoleId, String curUserName, String createUserId, String projectId, String taskId, List<TaskLog> taskLogs, boolean flag, Integer hasOnLine, String curRoleId) {
        String lastUserName = null;
        if (StringUtils.isNotBlank(lastRoleId)) {
            ProjectRoleUser lastProjectRoleUser = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).eq(ProjectRoleUser::getProjectRoleId, lastRoleId).one();
            lastUserName = lastProjectRoleUser == null ? null : sysUserDao.selectById(lastProjectRoleUser.getSysUserId()).getRealName();
        }
        if (!StringUtils.equals(lastUserName, curUserName)) {
            TaskLog taskLog = new TaskLog();
            taskLog.setCreateUserId(createUserId);
            taskLog.setProjectId(projectId);
            taskLog.setTaskId(taskId);
            StringBuilder str = new StringBuilder();
            str.append(PlaceholderUtil.resolvePlaceholders(ChangeEnum.userChange, this.getChangeLog(flag ? "任务负责人" : "任务审核人", lastUserName, curUserName))).append(";");
            str.replace(str.length() - 1, str.length(), "");
            taskLog.setContent(str.toString());
            taskLogs.add(taskLog);

            // 邮件通知 --- 项目必须为上线状态
            if (StringUtils.isNotBlank(curUserName) && BaseEnums.YES.getCode().equals(hasOnLine))
                taskService.roleUserChaneOfTask(flag, createUserId, curUserName, flag ? curRoleId : null, flag ? null : curRoleId, taskId, projectId);
        }
    }

    /**
     * 组装变更Log
     */
    public Map<String, String> getChangeLog(String type, String lastValue, String curValue) {
        Map<String, String> messageExt = new HashMap<>();
        messageExt.put("type", type);
        messageExt.put("lastValue", lastValue);
        messageExt.put("curValue", curValue);
        return messageExt;
    }

    /**
     * 根据模板角色ID，生成项目对应的角色
     * @param templateRoleId 模板角色ID
     * @param projectId      项目ID
     * @return .
     */
    private String getProjectRoleIdByTemplate(String templateRoleId, String projectId, String createUserId) {
        if (StringUtils.isBlank(templateRoleId)) return null;
        String roleName = projectTemplateRoleDao.selectById(templateRoleId).getRoleName();
        return projectRoleService.getProjectRoleIdByRoleName(roleName, projectId, createUserId);
    }

    /**
     * 删除里程碑及关联表
     *
     * @param id ID
     */
    @Override
    public boolean delete(String id, boolean flag, String userId) {
        //删除任务及关联表
        taskService.deleteByProjectMilestoneId(id, userId);
        ProjectMilestone projectMilestone = projectMilestoneDao.selectById(id);

        //删除Log
        projectMilestoneLogDao.delete(new LambdaQueryWrapper<ProjectMilestoneLog>().eq(ProjectMilestoneLog::getProjectMilestoneId, id));

        //删除Redis
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        hash.delete(RedisTableName.PROJECT.getCode() + projectMilestone.getProjectId(),
                RedisTableName.PROJECT_MILESTONE.getCode() + id);
        projectMilestoneDao.deleteById(id);

        // 更新项目状态和时间 如果为删除项目操作, 则不更新
        if (flag) {
            Project project = projectDao.selectById(projectMilestone.getProjectId());
            if (project != null) {
                List<Project> projectList = new ArrayList<>();
                projectList.add(project);
                taskService.updateProjectEstEndTimeAndStatus(projectList);
            }
            //项目日志：里程碑删除
            projectLogService.saveMilestoneChange(LogTypeEnum.DELETE.getMsg(), projectMilestone.getName(), projectMilestone.getProjectId(), userId);
        }

        return true;
    }

    /**
     * 删除Redis中的里程碑
     */
    public void deleteFromRedisByProjectIdAndMilestoneId(String projectId, String projectMilestoneId) {
        redisTemplate.opsForHash().delete(RedisTableName.PROJECT.getCode() + projectId,
                RedisTableName.PROJECT_MILESTONE.getCode() + projectMilestoneId);
    }

    /**
     * 里程碑上移
     *
     * @param id 里程碑ID
     * @return .
     */
    @Override
    public ResultData upMilestone(String id) {
        ProjectMilestone projectMilestone = projectMilestoneDao.selectById(id);
        if (projectMilestone == null || StringUtils.isBlank(projectMilestone.getProjectId()))
            return ResultData.notFound();
        // 启用状态
        List<ProjectMilestone> projectMilestones = new LambdaQueryChainWrapper<ProjectMilestone>(projectMilestoneDao).eq(ProjectMilestone::getProjectId, projectMilestone.getProjectId())
                .eq(ProjectMilestone::getEnabled, Integer.valueOf(TreeResultStatus.ENABLE.getCode())).list();
        projectMilestones.sort((a, b) -> -a.getSort().compareTo(b.getSort()));
        Optional<ProjectMilestone> lessOptional = projectMilestones.stream().filter(x -> x.getSort().compareTo(projectMilestone.getSort()) < 0).findFirst();
        if (!lessOptional.isPresent()) {
            ResultData resultData = ResultData.errorRequest();
            resultData.setMessage("里程碑无法上移");
            return resultData;
        }
        ProjectMilestone less = lessOptional.get();
        this.swapMilestoneSort(projectMilestone, less);
        return ResultData.ok();
    }

    /**
     * 里程碑下移
     *
     * @param id 里程碑ID
     */
    @Override
    public ResultData downMilestone(String id) {
        ProjectMilestone projectMilestone = projectMilestoneDao.selectById(id);
        if (projectMilestone == null || StringUtils.isBlank(projectMilestone.getProjectId()))
            return ResultData.notFound();
        // 启用状态
        List<ProjectMilestone> projectMilestones = new LambdaQueryChainWrapper<ProjectMilestone>(projectMilestoneDao).eq(ProjectMilestone::getProjectId, projectMilestone.getProjectId())
                .eq(ProjectMilestone::getEnabled, Integer.valueOf(TreeResultStatus.ENABLE.getCode())).list();
        projectMilestones.sort(Comparator.comparing(ProjectMilestone::getSort));
        Optional<ProjectMilestone> greaterOptional = projectMilestones.stream().filter(x -> x.getSort().compareTo(projectMilestone.getSort()) > 0).findFirst();
        if (!greaterOptional.isPresent()) {
            ResultData resultData = ResultData.errorRequest();
            resultData.setMessage("里程碑无法下移");
            return resultData;
        }
        ProjectMilestone greater = greaterOptional.get();
        this.swapMilestoneSort(projectMilestone, greater);
        return ResultData.ok();
    }

    /**
     * 根据项目ID删除相关表
     *
     * @param projectId 项目ID
     * @return .
     */
    @Override
    public boolean deleteByProjectId(String projectId, String userId) {
        List<ProjectMilestone> projectMilestones = new LambdaQueryChainWrapper<>(projectMilestoneDao).eq(ProjectMilestone::getProjectId, projectId).list();
        if (CollectionUtils.isEmpty(projectMilestones)) return true;
        for (ProjectMilestone projectMilestone : projectMilestones) {
            this.delete(projectMilestone.getId(), false, userId);
        }
        return true;
    }

    /**
     * 导入项目相关表信息
     */
    @Override
    public void importByTemplate(TreeResultDTO treeResult, Project project, String userId) {
        if (treeResult == null) return;
        //除了ID、创建时间，其他相同字段拷贝
        String[] excludeArr = this.getCopyExcludeArr();

        String projectTemplateId = treeResult.getId();
        //绑定项目与模板关系
        project.setProjectTemplateId(StringUtils.isBlank(project.getProjectTemplateId()) ? projectTemplateId : project.getProjectTemplateId() + "," + projectTemplateId);
        projectDao.updateById(project);

        //key：模板属性ID value：项目属性ID
        Map<String, String> templateToProjectAttrIdMap = new HashMap<>();
        //同步模板属性表 -----> 项目属性表
        List<ProjectTemplateAttr> templateAttrList = new LambdaQueryChainWrapper<ProjectTemplateAttr>(projectTemplateAttrDao)
                .eq(ProjectTemplateAttr::getProjectTemplateId, projectTemplateId).list();
        if (!CollectionUtils.isEmpty(templateAttrList)) {
            templateAttrList.forEach(projectTemplateAttr -> {
                //当前项目下是否存在该模板属性
                ProjectAttr projectAttr = new LambdaQueryChainWrapper<ProjectAttr>(projectAttrDao).eq(ProjectAttr::getProjectId, project.getId())
                        .eq(ProjectAttr::getProjectTemplateAttrId, projectTemplateAttr.getId()).one();
                if (projectAttr == null) {
                    ProjectAttr curProjectAttr = new ProjectAttr();
                    BeanUtils.copyProperties(projectTemplateAttr, curProjectAttr, excludeArr);
                    curProjectAttr.setProjectId(project.getId());
                    curProjectAttr.setProjectTemplateAttrId(projectTemplateAttr.getId());
                    projectAttrDao.insert(curProjectAttr);
                    templateToProjectAttrIdMap.put(projectTemplateAttr.getId(), curProjectAttr.getId());
                } else {
                    if (!StringUtils.equals(projectAttr.getName(), projectTemplateAttr.getName()) || !StringUtils.equals(projectAttr.getType(), projectTemplateAttr.getType())) {
                        projectAttr.setName(projectTemplateAttr.getName());
                        projectAttr.setType(projectTemplateAttr.getType());
                        projectAttrDao.updateById(projectAttr);
                        templateToProjectAttrIdMap.put(projectTemplateAttr.getId(), projectAttr.getId());
                    }
                }
            });
        }

        List<TreeResultDTO> children = treeResult.getChildren();
        if (CollectionUtils.isEmpty(children)) return;

        List<String> milestoneNameList = new ArrayList<>();
        children.forEach(templateMilestoneTreeResult -> {

            //根据ID获取里程碑模板其他数据
            ProjectTemplateMilestone curProjectTemplateMilestone = projectTemplateMilestoneDao.selectById(templateMilestoneTreeResult.getId());

            //生成里程碑
            ProjectMilestone curProjectMilestone = new ProjectMilestone();
            BeanUtils.copyProperties(curProjectTemplateMilestone, curProjectMilestone, excludeArr);
            //生成角色 是否有审核、确认角色
            curProjectMilestone.setReviewProjectRoleId(this.getProjectRoleIdByTemplate(curProjectTemplateMilestone.getReviewProjectRoleId(), project.getId(), userId));
            curProjectMilestone.setConfirmProjectRoleId(this.getProjectRoleIdByTemplate(curProjectTemplateMilestone.getConfirmProjectRoleId(), project.getId(), userId));
            //圈子ID 项目ID
            curProjectMilestone.setCircleId(project.getCircleId());
            curProjectMilestone.setProjectId(project.getId());
            curProjectMilestone.setEnabled(Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
            curProjectMilestone.setForbidden(Integer.valueOf(TreeResultStatus.NOT_FORBIDDEN.getCode()));
            curProjectMilestone.setSort(IdWorker.nextId());
            //模板相关
            curProjectMilestone.setProjectTemplateId(projectTemplateId);
            curProjectMilestone.setProjectTemplateMilestoneId(templateMilestoneTreeResult.getId());
            projectMilestoneDao.insert(curProjectMilestone);
            milestoneNameList.add(curProjectMilestone.getName());

            // 更新Redis中的里程碑 只存启用的
            this.updateMilestoneInRedisByMilestone(curProjectMilestone);

            //生成里程碑下的任务及相关属性
            if (!CollectionUtils.isEmpty(templateMilestoneTreeResult.getChildren())) {
                this.saveTask(templateMilestoneTreeResult.getChildren(), project, excludeArr, curProjectMilestone.getId(), templateToProjectAttrIdMap,
                        null, templateMilestoneTreeResult.getId(), projectTemplateId, userId);
            }
        });

        //项目日志：里程碑创建
        projectLogService.batchSaveMilestoneChange(LogTypeEnum.INSERT.getMsg(), milestoneNameList, project.getId(), userId);
    }

    /**
     * 已有项目导入
     */
    @Override
    public String importByExistProject(TreeResultDTO treeResult, String userId, String curProjectId) {
        //只导入选中的内容，未选中不处理
        if (treeResult == null) return null;
        String[] excludeArr = this.getCopyExcludeArr();

        //项目
        String lastProjectId = treeResult.getId();
        Project curProject = projectDao.selectById(curProjectId);

        //项目属性
        Map<String, String> projectAttrIdMap = new HashMap<>();
        List<ProjectAttr> lastAttrList = new LambdaQueryChainWrapper<ProjectAttr>(projectAttrDao).eq(ProjectAttr::getProjectId, lastProjectId).list();
        if (!CollectionUtils.isEmpty(lastAttrList)) {
            lastAttrList.forEach(projectAttr -> {
                ProjectAttr curProjectAttr = new ProjectAttr();
                BeanUtils.copyProperties(projectAttr, curProjectAttr, excludeArr);
                curProjectAttr.setProjectId(curProjectId);
                projectAttrDao.insert(curProjectAttr);
                projectAttrIdMap.put(projectAttr.getId(), curProjectAttr.getId());
            });
        }

        List<TreeResultDTO> children = treeResult.getChildren();
        if (CollectionUtils.isEmpty(children)) return curProjectId;

        List<Task> saveToRedisList = new ArrayList<>();
        //项目角色ID
        Map<String, String> projectProjectIdMap = new HashMap<>();

        //项目里程碑
        children.forEach(lastMilestoneTreeResult -> {
            ProjectMilestone lastProjectMilestone = projectMilestoneDao.selectById(lastMilestoneTreeResult.getId());

            //生成里程碑
            ProjectMilestone curProjectMilestone = new ProjectMilestone();
            BeanUtils.copyProperties(lastProjectMilestone, curProjectMilestone, excludeArr);
            //生成角色 是否有审核、确认角色
            curProjectMilestone.setReviewProjectRoleId(this.getProjectRoleId(curProjectMilestone.getReviewProjectRoleId(), curProjectId, userId));
            curProjectMilestone.setConfirmProjectRoleId(this.getProjectRoleId(curProjectMilestone.getConfirmProjectRoleId(), curProjectId, userId));
            curProjectMilestone.setProjectId(curProjectId);
            curProjectMilestone.setCircleId(curProject.getCircleId());
            curProjectMilestone.setSort(IdWorker.nextId());
            curProjectMilestone.setStatus(null);
            curProjectMilestone.setEstEndTime(null);
            curProjectMilestone.setActEndTime(null);
            projectMilestoneDao.insert(curProjectMilestone);

            // 更新Redis中的里程碑
            if (lastProjectMilestone.getEnabled().equals(BaseEnums.YES.getCode())) this.updateMilestoneInRedisByMilestone(curProjectMilestone);

            //生成里程碑下的任务及相关属性
            this.copyTaskByLast(lastMilestoneTreeResult.getChildren(), curProject, excludeArr, curProjectMilestone.getId(), projectAttrIdMap, null, saveToRedisList, projectProjectIdMap, userId);

        });

        //角色对应的人员
        if (!CollectionUtils.isEmpty(projectProjectIdMap)) {
            projectProjectIdMap.forEach((lastProjectRoleId, curProjectRoleId) -> {
                ProjectRoleUser lastProjectRoleUser = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).eq(ProjectRoleUser::getProjectRoleId, lastProjectRoleId)
                        .select(ProjectRoleUser::getSysUserId).one();
                if (lastProjectRoleUser != null && StringUtils.isNotBlank(lastProjectRoleUser.getSysUserId())) {
                    ProjectRoleUser curProjectRoleUser = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).eq(ProjectRoleUser::getProjectRoleId, curProjectRoleId).one();
                    if (curProjectRoleUser == null) {
                        //保存项目角色与人员
                        curProjectRoleUser = new ProjectRoleUser();
                        curProjectRoleUser.setProjectRoleId(curProjectRoleId);
                        curProjectRoleUser.setSysUserId(lastProjectRoleUser.getSysUserId());
                        projectRoleUserDao.insert(curProjectRoleUser);
                    }
                }
            });
        }

        if (!CollectionUtils.isEmpty(saveToRedisList)) // redis
            taskService.batchUpdateTaskInRedis(saveToRedisList);

        return curProjectId;
    }

    /**
     * 生成里程碑下的任务及相关属性
     */
    private void copyTaskByLast(List<TreeResultDTO> children, Project project, String[] excludeArr, String curProjectMilestoneId, Map<String, String> projectAttrIdMap, String parentTaskId, List<Task> saveToRedisList,
                                Map<String, String> projectProjectIdMap, String createUserId) {
//        List<TreeResultDTO> tasks = children.stream().filter(tree -> tree.getStatus().equals(TreeResultStatus.ENABLE.getCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(children)) return;
        children.forEach(taskTreeResult -> {
            Task lastTask = taskDao.selectById(taskTreeResult.getId());
            //不是归档任务
            if (lastTask.getStMark() != null && lastTask.getStMark().equals(BaseEnums.NO.getCode())) {
                Task parentTask = new Task();
                BeanUtils.copyProperties(lastTask, parentTask, excludeArr);
                parentTask.setReviewProjectRoleId(this.getProjectRoleId(parentTask.getReviewProjectRoleId(), project.getId(), createUserId));
                parentTask.setConfirmProjectRoleId(this.getProjectRoleId(parentTask.getConfirmProjectRoleId(), project.getId(), createUserId));
                parentTask.setProjectId(project.getId());
                parentTask.setMilestoneId(curProjectMilestoneId);
                parentTask.setParentId(parentTaskId);
                parentTask.setCircleId(project.getCircleId());
                parentTask.setStatus(TaskStatusEnum.NOT_STARTED.getCode());
                parentTask.setSort(IdWorker.nextId());
                parentTask.setEstEndTime(null);
                parentTask.setActEndTime(null);
                parentTask.setDateStatus(null);
                if (StringUtils.isBlank(parentTask.getPriority()))
                    parentTask.setPriority(PriorityEnum.LOW.getCode());
                taskDao.insert(parentTask);

                //添加到Redis存储
                if (parentTask.getEnabled().equals(BaseEnums.YES.getCode())) saveToRedisList.add(parentTask);

                //任务属性值
                List<TaskData> taskDataList = new LambdaQueryChainWrapper<TaskData>(taskDataDao)
                        .eq(TaskData::getTaskId, lastTask.getId()).list();
                if (!CollectionUtils.isEmpty(taskDataList)) {
                    taskDataList.forEach(lastTaskData -> {
                        TaskData taskData = new TaskData();
                        BeanUtils.copyProperties(lastTaskData, taskData, excludeArr);
                        taskData.setTaskId(parentTask.getId());
                        taskData.setProjectAttrId(projectAttrIdMap.get(lastTaskData.getProjectAttrId()));
                        taskDataDao.insert(taskData);
                    });
                }

                //角色对应的人员
                if (StringUtils.isNotBlank(parentTask.getReviewProjectRoleId()))
                    projectProjectIdMap.put(lastTask.getReviewProjectRoleId(), parentTask.getReviewProjectRoleId());
                if (StringUtils.isNotBlank(parentTask.getConfirmProjectRoleId()))
                    projectProjectIdMap.put(lastTask.getConfirmProjectRoleId(), parentTask.getConfirmProjectRoleId());


                // 更新Redis里的任务列表
//                taskService.updateTaskInRedisByTaskId(parentTask.getId());

                //查找对应的子任务
                this.copyTaskByLast(taskTreeResult.getChildren(), project, excludeArr, curProjectMilestoneId, projectAttrIdMap, parentTask.getId(), saveToRedisList, projectProjectIdMap, createUserId);
            }
        });
    }

    /**
     * 根据上一个项目角色ID，生成新的项目对应的角色
     *
     * @param lastRoleId 模板角色ID
     * @param projectId  项目ID
     * @return .
     */
    private String getProjectRoleId(String lastRoleId, String projectId, String createUserId) {
        if (StringUtils.isBlank(lastRoleId)) return null;
        String roleName = projectRoleDao.selectById(lastRoleId).getRoleName();
        return projectRoleService.getProjectRoleIdByRoleName(roleName, projectId, createUserId);
    }

    /**
     * 根据角色修改里程碑
     *
     * @param projectRoleIds 项目角色ID
     */
    @Override
    public void deleteRoleId(List<String> projectRoleIds) {
        List<ProjectMilestone> projectMilestones = new LambdaQueryChainWrapper<ProjectMilestone>(projectMilestoneDao).in(ProjectMilestone::getReviewProjectRoleId, projectRoleIds)
                .or().in(ProjectMilestone::getConfirmProjectRoleId, projectRoleIds).list();
        if (CollectionUtils.isEmpty(projectMilestones)) return;
        projectMilestones.forEach(projectMilestone -> {
            String reviewProjectRoleId = projectRoleIds.contains(projectMilestone.getReviewProjectRoleId()) ? null : projectMilestone.getReviewProjectRoleId();
            String confirmProjectRoleId = projectRoleIds.contains(projectMilestone.getConfirmProjectRoleId()) ? null : projectMilestone.getConfirmProjectRoleId();
            projectMilestoneDao.updateByRoleId(projectMilestone.getId(), reviewProjectRoleId, confirmProjectRoleId);
            //更新Redis中启用的里程碑
            if (projectMilestone.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode())))
                this.updateMilestoneInRedisByMilestone(projectMilestone);
        });
    }

    /**
     * 解除禁用（启用）/禁用 操作Redis
     * @param enableOrForbiddenDTOList
     * @param isForbidden 0 -> 解除禁用===》直接设置为启用 (1 -> 禁用)
     * @return
     */
    @Override
    public boolean updateToNotForbidden(List<EnableOrForbiddenDTO.EnableOrForbiddens> enableOrForbiddenDTOList, Integer isForbidden) {
        if (CollectionUtils.isEmpty(enableOrForbiddenDTOList) || isForbidden == null) return true;

        if (isForbidden.equals(BaseEnums.NO.getCode())) return this.updateEnabled(enableOrForbiddenDTOList, BaseEnums.YES.getCode());

        Integer enabled = Integer.valueOf(TreeResultStatus.NOT_ENABLE.getCode());

        Map<String, List<EnableOrForbiddenDTO.EnableOrForbiddens>> enableOrForbiddenMap = enableOrForbiddenDTOList.stream().collect(Collectors.groupingBy(EnableOrForbiddenDTO.EnableOrForbiddens::getType));
        enableOrForbiddenMap.forEach((type, enableOrForbiddenList) -> {
            if (type.equals(EnableOrForbiddenDTO.PROJECT_MILESTONE)) {
                List<ProjectMilestone> projectMilestones = projectMilestoneDao.selectBatchIds(enableOrForbiddenList.stream().map(EnableOrForbiddenDTO.EnableOrForbiddens::getId).collect(Collectors.toList()));

                projectMilestones.forEach(projectMilestone -> {
                    // 判断里程碑下是否包含启用状态任务，包含则不更改
                    List<Task> taskEnableList = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getMilestoneId, projectMilestone.getId())
                            .eq(Task::getEnabled, TreeResultStatus.ENABLE.getCode())
                            .ne(Task::getStMark, BaseEnums.YES.getCode()).list();
                    if (CollectionUtils.isEmpty(taskEnableList)) {
                        //如果上一次状态为启用，则需要移出Redis
                        if (projectMilestone.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode())))
                            this.deleteFromRedisByProjectIdAndMilestoneId(projectMilestone.getProjectId(), projectMilestone.getId());
                        projectMilestone.setEnabled(enabled);
                        projectMilestone.setForbidden(isForbidden);
                        projectMilestoneDao.updateById(projectMilestone);
                    }
                });
            } else {
                List<Task> tasks = taskDao.selectBatchIds(enableOrForbiddenList.stream().map(EnableOrForbiddenDTO.EnableOrForbiddens::getId).collect(Collectors.toList()));
                tasks.forEach(task -> {
                    //任务下是否包含启用状态子任务，包含，则不更改
                    List<Task> taskEnableList = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getParentId, task.getId())
                            .eq(Task::getEnabled, TreeResultStatus.ENABLE.getCode())
                            .ne(Task::getStMark, BaseEnums.YES.getCode())
                            .list();
                    if (CollectionUtils.isEmpty(taskEnableList)) {
                        //如果上一次状态为启用，则需要移出Redis
                        if (task.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode())))
                            redisTemplate.opsForHash().delete(RedisTableName.PROJECT.getCode() + task.getProjectId(),
                                    StringUtils.isBlank(task.getParentId()) ? RedisTableName.PARENT_TASK.getCode() + task.getId() : RedisTableName.CHILD_TASK.getCode() + task.getId());
                        task.setEnabled(enabled);
                        task.setForbidden(isForbidden);
                        taskDao.updateById(task);
                    }
                });
            }
        });
        return true;
    }

    @Override
    public boolean updateEnabled(List<EnableOrForbiddenDTO.EnableOrForbiddens> enableOrForbiddenDTOList, Integer isEnabled) {
        if (CollectionUtils.isEmpty(enableOrForbiddenDTOList) || isEnabled == null) return true;
        Integer forbidden = Integer.valueOf(TreeResultStatus.NOT_FORBIDDEN.getCode());

        Map<String, List<EnableOrForbiddenDTO.EnableOrForbiddens>> enableOrForbiddenMap = enableOrForbiddenDTOList.stream().collect(Collectors.groupingBy(EnableOrForbiddenDTO.EnableOrForbiddens::getType));
        enableOrForbiddenMap.forEach((type, enableOrForbiddenList) -> {
            if (type.equals(EnableOrForbiddenDTO.PROJECT_MILESTONE)) {
                List<ProjectMilestone> projectMilestones = projectMilestoneDao.selectBatchIds(enableOrForbiddenList.stream().map(EnableOrForbiddenDTO.EnableOrForbiddens::getId).collect(Collectors.toList()));
                projectMilestones = projectMilestones.stream().peek(projectMilestone -> {
                    projectMilestone.setEnabled(isEnabled);
                    projectMilestone.setForbidden(forbidden);
                }).collect(Collectors.toList());
                this.updateBatchById(projectMilestones);
                //未启用 -> 移出Redis
                if (isEnabled.equals(Integer.valueOf(TreeResultStatus.NOT_ENABLE.getCode())))
                    projectMilestones.forEach(projectMilestone -> this.deleteFromRedisByProjectIdAndMilestoneId(projectMilestone.getProjectId(), projectMilestone.getId()));
                    //启用 -> 插入Redis
                else
                    this.batchUpdateMilestoneInRedis(projectMilestones);
            } else {
                List<Task> tasks = taskDao.selectBatchIds(enableOrForbiddenList.stream().map(EnableOrForbiddenDTO.EnableOrForbiddens::getId).collect(Collectors.toList()));
                tasks = tasks.stream().peek(task -> {
                    task.setEnabled(isEnabled);
                    task.setForbidden(forbidden);
                }).collect(Collectors.toList());
                taskService.updateBatchById(tasks);
                //未启用 -> 移出Redis
                if (isEnabled.equals(Integer.valueOf(TreeResultStatus.NOT_ENABLE.getCode())))
                    tasks.forEach(task -> redisTemplate.opsForHash().delete(RedisTableName.PROJECT.getCode() + task.getProjectId(),
                            StringUtils.isBlank(task.getParentId()) ? RedisTableName.PARENT_TASK.getCode() + task.getId() : RedisTableName.CHILD_TASK.getCode() + task.getId()));
                    //启用 -> 插入Redis
                else
                    taskService.batchUpdateTaskInRedis(tasks);
            }
        });

        return true;
    }

    /**
     * 设为禁用状态
     */
    @Override
    public void updateToForbidden(EnableOrForbiddenDTO.EnableOrForbiddens enableOrForbiddens) {
        Integer forbidden = Integer.valueOf(TreeResultStatus.FORBIDDEN.getCode());
        Integer enabled = Integer.valueOf(TreeResultStatus.NOT_ENABLE.getCode());
        this.updateEnableOrForbidden(enableOrForbiddens, forbidden, enabled);
    }

    /**
     * 设为未启用状态
     */
    @Override
    public void updateToNotEnabled(EnableOrForbiddenDTO.EnableOrForbiddens enableOrForbiddens) {
        Integer forbidden = Integer.valueOf(TreeResultStatus.NOT_FORBIDDEN.getCode());
        Integer enabled = Integer.valueOf(TreeResultStatus.NOT_ENABLE.getCode());
        this.updateEnableOrForbidden(enableOrForbiddens, forbidden, enabled);
    }

    /**
     * 复制里程碑
     *
     * @param milestoneId 里程碑ID
     */
    @Override
    public void copyMilestone(String milestoneId) {

        //除了ID、创建时间，其他相同字段拷贝
        String[] excludeArr = this.getCopyExcludeArr();

        //获取选中的里程碑及任务关联数据
        ProjectMilestone lastProjectMilestone = projectMilestoneDao.findContainsTaskById(milestoneId);
        if (lastProjectMilestone == null) return;
        ProjectMilestone copyMilestone = new ProjectMilestone();
        BeanUtils.copyProperties(lastProjectMilestone, copyMilestone, excludeArr);
        //copy 里程碑
        copyMilestone.setSort(IdWorker.nextId());
        copyMilestone.setStatus(null);
        copyMilestone.setActEndTime(null);
        copyMilestone.setEstEndTime(null);
        projectMilestoneDao.insert(copyMilestone);
        // redis
        this.updateMilestoneInRedisByMilestone(copyMilestone);

        //copy 任务及相关表数据
        List<Task> saveToRedisList = new ArrayList<>();
        this.copyTask(copyMilestone.getTaskList(), excludeArr, copyMilestone.getId(), null, saveToRedisList);
        if (CollectionUtils.isEmpty(saveToRedisList)) return;
        // redis
        taskService.batchUpdateTaskInRedis(saveToRedisList);
    }

    /**
     * 除了ID、创建时间，其他相同字段拷贝
     */
    public String[] getCopyExcludeArr() {
        ArrayList<String> excludes = new ArrayList<>();
        excludes.add("id");
        excludes.add("createAt");
        excludes.add("updateAt");
        excludes.add("parentId");
        excludes.add("createUserId");
        excludes.add("updateUserId");
        excludes.add("sort");
        return excludes.toArray(new String[0]);
    }


    @Override
    public void copyTask(List<Task> taskList, String[] excludeArr, String milestoneId, String parentTaskId, List<Task> saveToRedisList) {
        if (CollectionUtils.isEmpty(taskList)) return;
        taskList.forEach(task -> {
            // 任务
            Task copyTask = new Task();
            BeanUtils.copyProperties(task, copyTask, excludeArr);
            copyTask.setSort(this.getSort(task));
            copyTask.setParentId(parentTaskId);
            copyTask.setMilestoneId(milestoneId);
            //不要复制时间、状态
            copyTask.setStatus(TaskStatusEnum.NOT_STARTED.getCode());
            copyTask.setEstEndTime(null);
            copyTask.setActEndTime(null);
            copyTask.setDateStatus(null);
            taskDao.insert(copyTask);

            //添加到存储
            saveToRedisList.add(copyTask);

            // 任务属性
            List<TaskData> taskDataList = copyTask.getTaskDataList();
            taskDataList = taskDataList.stream().peek(taskData -> {
                taskData.setId(null);
                taskData.setTaskId(copyTask.getId());
                taskData.setCreateAt(new Date());
                taskData.setUpdateAt(new Date());
            }).collect(Collectors.toList());
            taskDataService.saveBatch(taskDataList);

            // 任务交付物
//            List<TaskDelivery> taskDeliveryList = copyTask.getTaskDeliveryList();
//            taskDeliveryList = taskDeliveryList.stream().peek(taskDelivery -> {
//                taskDelivery.setId(null);
//                taskDelivery.setTaskId(copyTask.getId());
//                taskDelivery.setCreateAt(new Date());
//                taskDelivery.setUpdateAt(new Date());
//            }).collect(Collectors.toList());
//            taskDeliveryService.saveBatch(taskDeliveryList);

            this.copyTask(copyTask.getChildren(), excludeArr, milestoneId, copyTask.getId(), saveToRedisList);
        });
    }

    private String getSort(Task task) {
        List<Task> tasks = null;
        if (StringUtils.isBlank(task.getParentId())) {
            tasks = taskDao.findByMilestoneIdAndLtSort(task.getMilestoneId(), task.getSort());
        } else {
            tasks = taskDao.findByParentIdAndLtSort(task.getParentId(), task.getSort());
        }
        if (CollectionUtils.isEmpty(tasks)) return IdWorker.nextId();
        Task task1 = tasks.get(0);

        if (task1.getSort().length() > task.getSort().length()) {
            return task.getSort() + "0" + task1.getSort().substring(task.getSort().length(), task1.getSort().length());
        }
        BigDecimal sort = new BigDecimal(task.getSort());
        BigDecimal sort1 = new BigDecimal(task1.getSort());
        if (Math.abs(sort.subtract(sort1).doubleValue()) > 1) {
            return (sort.add(new BigDecimal(1))) + "";
        }
        return task.getSort() + "1";
    }

    /**
     * 更新为未启用、禁用状态
     */
    private void updateEnableOrForbidden(EnableOrForbiddenDTO.EnableOrForbiddens enableOrForbiddens, Integer forbidden, Integer enabled) {
        if (enableOrForbiddens.getType().equals(EnableOrForbiddenDTO.PROJECT_MILESTONE)) {
            ProjectMilestone projectMilestone = projectMilestoneDao.selectById(enableOrForbiddens.getId());
            //查找所有状态为启用状态的任务
            List<Task> taskList = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getMilestoneId, enableOrForbiddens.getId())
                    .eq(Task::getEnabled, Integer.valueOf(TreeResultStatus.ENABLE.getCode()))
                    .ne(Task::getStMark, BaseEnums.YES.getCode()).list();
            this.batchUpdateTask(taskList, forbidden, enabled);
            projectMilestone.setEnabled(enabled);
            projectMilestone.setForbidden(forbidden);
            projectMilestoneDao.updateById(projectMilestone);
            //移出Redis
            this.deleteFromRedisByProjectIdAndMilestoneId(projectMilestone.getProjectId(), projectMilestone.getId());
        } else {
            List<Task> allTaskList = new ArrayList<>();
            allTaskList.add(taskDao.selectById(enableOrForbiddens.getId()));
            //查找所有状态为启用的子任务
            List<Task> childTaskList = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getParentId, enableOrForbiddens.getId())
                    .eq(Task::getEnabled, Integer.valueOf(TreeResultStatus.ENABLE.getCode()))
                    .ne(Task::getStMark, BaseEnums.YES.getCode()).list();
            if (!CollectionUtils.isEmpty(childTaskList))
                allTaskList.addAll(childTaskList);
            this.batchUpdateTask(allTaskList, forbidden, enabled);
        }
    }


    /**
     * 更新展开收起状态
     */
    @Override
    public ResultData updateReferCollapsed(String id, String single, Integer collapsed) {
        if ("ONE".equals(single)) {
            ProjectMilestone projectMilestone = projectMilestoneDao.selectById(id);
            projectMilestone.setCollapsed(collapsed);
            projectMilestoneDao.updateById(projectMilestone);
            this.updateMilestoneInRedisByMilestone(projectMilestone);
        } else if ("ALL".equals(single)) {

            List<ProjectMilestone> list = new LambdaQueryChainWrapper<ProjectMilestone>(projectMilestoneDao).eq(ProjectMilestone::getProjectId, id).list();
            list.forEach(projectMilestone -> {
                projectMilestone.setCollapsed(collapsed);
                projectMilestoneDao.updateById(projectMilestone);
                this.updateMilestoneInRedisByMilestone(projectMilestone);
            });
        }
        return ResultData.ok();
    }

    /**
     * 批量修改Task
     */
    private void batchUpdateTask(List<Task> taskList, Integer forbidden, Integer enabled) {
        if (CollectionUtils.isEmpty(taskList)) return;
        taskList = taskList.stream().peek(task -> {
            task.setEnabled(enabled);
            task.setForbidden(forbidden);
        }).collect(Collectors.toList());
        //批量修改
        taskService.updateBatchById(taskList);
        //移出Redis
        taskList.forEach(task ->
                redisTemplate.opsForHash().delete(RedisTableName.PROJECT.getCode() + task.getProjectId(),
                        StringUtils.isBlank(task.getParentId()) ? RedisTableName.PARENT_TASK.getCode() + task.getId() : RedisTableName.CHILD_TASK.getCode() + task.getId()));
    }

    /**
     * 生成里程碑下的任务及相关属性
     */
    private void saveTask(List<TreeResultDTO> children, Project project, String[] excludeArr, String curProjectMilestoneId, Map<String, String> templateToProjectAttrIdMap, String parentTaskId,
                          String templateMilestoneId, String projectTemplateId, String createUserId) {
        children.forEach(taskTreeResult -> {
            //获取任务模板完整信息
            ProjectTemplateTask curParentProjectTemplateTask = projectTemplateTaskDao.selectById(taskTreeResult.getId());

            //生成父任务 再生成子任务
            Task parentTask = new Task();
            BeanUtils.copyProperties(curParentProjectTemplateTask, parentTask, excludeArr);
            //生成角色 是否有审核、确认角色
            parentTask.setReviewProjectRoleId(this.getProjectRoleIdByTemplate(curParentProjectTemplateTask.getReviewProjectRoleId(), project.getId(), createUserId));
            parentTask.setConfirmProjectRoleId(this.getProjectRoleIdByTemplate(curParentProjectTemplateTask.getConfirmProjectRoleId(), project.getId(), createUserId));
            //圈子ID 项目ID 里程碑ID
            parentTask.setCircleId(project.getCircleId());
            parentTask.setProjectId(project.getId());
            parentTask.setMilestoneId(curProjectMilestoneId);
            parentTask.setParentId(parentTaskId);
            parentTask.setStatus(TaskStatusEnum.NOT_STARTED.getCode());
            parentTask.setEnabled(Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
            parentTask.setForbidden(Integer.valueOf(TreeResultStatus.NOT_FORBIDDEN.getCode()));
            parentTask.setSort(IdWorker.nextId());
            //模板相关
            parentTask.setProjectTemplateId(projectTemplateId);
            parentTask.setProjectTemplateMilestoneId(templateMilestoneId);
            parentTask.setProjectTemplateTaskId(taskTreeResult.getId());
            parentTask.setStMark(BaseEnums.NO.getCode());
            parentTask.setPriority(PriorityEnum.LOW.getCode());
            parentTask.setIsTemporary(BaseEnums.NO.getCode());
            taskDao.insert(parentTask);

            //任务属性值
            List<TaskTemplateData> taskTemplateDataList = new LambdaQueryChainWrapper<TaskTemplateData>(taskTemplateDataDao)
                    .eq(TaskTemplateData::getProjectTemplateTaskId, curParentProjectTemplateTask.getId()).list();
            if (!CollectionUtils.isEmpty(taskTemplateDataList)) {
                List<TaskData> taskDataList = new ArrayList<>();
                taskTemplateDataList.forEach(taskTemplateData -> {
                    TaskData taskData = new TaskData();
                    BeanUtils.copyProperties(taskTemplateData, taskData, excludeArr);
                    taskData.setTaskId(parentTask.getId());
                    taskData.setProjectAttrId(templateToProjectAttrIdMap.get(taskTemplateData.getProjectTemplateAttrId()));
//                    taskDataDao.insert(taskData);
                    taskDataList.add(taskData);
                });
                taskDataService.saveBatch(taskDataList);
            }

            // 更新Redis里的任务列表 只存启用的
            taskService.updateTaskInRedisByTaskId(parentTask.getId());

            //查找对应的子任务
            if (!CollectionUtils.isEmpty(taskTreeResult.getChildren()))
                this.saveTask(taskTreeResult.getChildren(), project, excludeArr, curProjectMilestoneId, templateToProjectAttrIdMap, parentTask.getId(), templateMilestoneId, projectTemplateId, createUserId);

        });
    }

    /**
     * 交换顺序
     *
     * @param a 当前的里程碑
     * @param b 要交换的里程碑
     */
    private void swapMilestoneSort(ProjectMilestone a, ProjectMilestone b) {
        String tempSort = b.getSort();
        b.setSort(a.getSort());
        a.setSort(tempSort);
        projectMilestoneDao.updateById(b);
        projectMilestoneDao.updateById(a);
        //更新Redis中的里程碑
        this.updateMilestoneInRedisByMilestone(a);
        this.updateMilestoneInRedisByMilestone(b);
    }

    /**
     * 刷新里程碑Redis
     *
     * @param projectMilestone 里程碑
     */
    public void updateMilestoneInRedisByMilestone(ProjectMilestone projectMilestone) {
        redisTemplate.opsForHash().put(RedisTableName.PROJECT.getCode() + projectMilestone.getProjectId(),
                RedisTableName.PROJECT_MILESTONE.getCode() + projectMilestone.getId(),
                JSON.toJSONString(projectMilestoneDao.findById(projectMilestone.getId())));
    }

    /**
     * 批量更新里程碑Redis
     *
     * @param projectMilestoneList 仅有里程碑
     */
    public void batchUpdateMilestoneInRedis(List<ProjectMilestone> projectMilestoneList) {
        redisTemplate.executePipelined(new RedisCallback<List<Task>>() {
            @Override
            public List<Task> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                for (ProjectMilestone projectMilestone : projectMilestoneList) {
                    redisConnection.hSet((RedisTableName.PROJECT.getCode() + projectMilestone.getProjectId()).getBytes(),
                            (RedisTableName.PROJECT_MILESTONE.getCode() + projectMilestone.getId()).getBytes(),
                            JSON.toJSONString(projectMilestoneDao.findById(projectMilestone.getId())).getBytes());
                }
                return null;
            }
        });
    }

    /**
     * 根据项目ID更新圈子ID
     *
     * @param circleId  圈子ID
     * @param projectId 项目ID
     */
    @Override
    public void updateMilestoneAndTaskCircleIdByProjectId(String circleId, String projectId) {
        //里程碑
        LambdaUpdateWrapper<ProjectMilestone> milestoneLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        milestoneLambdaUpdateWrapper.eq(ProjectMilestone::getProjectId, projectId);
        milestoneLambdaUpdateWrapper.set(ProjectMilestone::getCircleId, circleId);
        this.update(milestoneLambdaUpdateWrapper);
        //任务
        LambdaUpdateWrapper<Task> taskLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        taskLambdaUpdateWrapper.eq(Task::getProjectId, projectId);
        taskLambdaUpdateWrapper.set(Task::getCircleId, circleId);
        taskService.update(taskLambdaUpdateWrapper);
    }


}

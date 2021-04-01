package com.touchspring.ailge.service.impl.agile;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.*;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.dto.TaskDTO;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.agile.*;
import com.touchspring.ailge.entity.ldap.User;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.*;
import com.touchspring.ailge.service.agile.*;
import com.touchspring.ailge.utils.BeanChangeUtilService;
import com.touchspring.ailge.utils.IdWorker;
import com.touchspring.ailge.utils.PlaceholderUtil;
import com.touchspring.ailge.utils.PropertyMsg;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目任务信息表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-07-30
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskDao, Task> implements TaskService {

    private final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskDataDao taskDataDao;
    @Autowired
    private TaskDeliveryDao taskDeliveryDao;
    @Autowired
    private ProjectMilestoneServiceImpl projectMilestoneService;
    @Autowired
    private ProjectRoleDao projectRoleDao;
    @Autowired
    private ProjectRoleService projectRoleService;
    @Autowired
    private ProjectMilestoneDao projectMilestoneDao;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private BeanChangeUtilService<Task> beanChangeUtilService;
    @Autowired
    private TaskLogDao taskLogDao;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private ProjectMilestoneLogService projectMilestoneLogService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private MessageUserService messageUserService;
    @Autowired
    private ProjectRoleUserDao projectRoleUserDao;
    @Autowired
    private TaskUserDao taskUserDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private ProjectRoleUserService projectRoleUserService;

    @Override
    public ResultData save(TaskDTO taskDTO, String userId, String reuploadListIds) {
        Task target;
        // 拒绝/带条件通过理由
        String refuseReason = taskDTO.getRefuseReason();
        String passReason = taskDTO.getPassReason();
        if (StringUtils.isBlank(taskDTO.getId())) {
            target = taskDTO;
            target.setReviewProjectRoleId(
                    projectRoleService.getProjectRoleIdByRoleName(taskDTO.getReviewRoleName(), taskDTO.getProjectId(), userId));
            target.setConfirmProjectRoleId(
                    projectRoleService.getProjectRoleIdByRoleName(taskDTO.getConfirmRoleName(), taskDTO.getProjectId(), userId));
            target.setSort(IdWorker.nextId());
            //默认状态为未开始
            target.setStatus(TaskStatusEnum.NOT_STARTED.getCode());
            target.setEnabled(Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
            target.setForbidden(Integer.valueOf(TreeResultStatus.NOT_FORBIDDEN.getCode()));
            if (taskDTO.getPriority() == null) {
                target.setPriority(PriorityEnum.LOW.getCode());
            }
            target.setCreateUserId(userId);
            target.setStMark(BaseEnums.NO.getCode());
            taskDao.insert(target);

            // 主任务已完成，添加子任务后变成进行中
            if (StringUtils.isNotBlank(target.getParentId())) {
                Task parentTask = taskDao.selectById(target.getParentId());
                if (parentTask != null && StringUtils.equals(parentTask.getStatus(), TaskStatusEnum.COMPLETED.getCode()))
                    this.updateParentTask(parentTask, TaskStatusEnum.IN_PROGRESS.getCode(), userId);
            }

            //临时任务不保存Log
            if (target.getIsTemporary() == null) {
                //Log 里程碑日志:任务创建
                List<Task> taskList = new ArrayList<>();
                taskList.add(target);
                projectMilestoneLogService.saveTaskChange(LogTypeEnum.INSERT.getMsg(), userId, taskList);
            }


            //任务日志：子任务的创建
            if (StringUtils.isNotBlank(target.getParentId()) && !target.getParentId().equals("0")) {
                List<String> childNames = new ArrayList<>();
                childNames.add(target.getName());
                taskLogService.saveChildTaskChange(userId, childNames, target.getParentId(), LogTypeEnum.INSERT.getMsg(), target.getProjectId());
            }

        } else {
            target = taskDao.selectById(taskDTO.getId());
            if (target == null) return ResultData.errorRequest();

            if (taskDTO.getEstEndTime() != null && !taskDTO.getEstEndTime().equals(target.getEstEndTime())) {
                //子任务的截止时间，不能定义在主任务之后
                if (StringUtils.isNotBlank(target.getParentId())) {
                    Task parentTask = taskDao.selectById(target.getParentId());
                    if (parentTask != null && parentTask.getEstEndTime() != null && parentTask.getEstEndTime().isBefore(taskDTO.getEstEndTime()))
                        return new ResultData(ResultStatus.CHILD_EST_END_TIME.getCode(), ResultStatus.CHILD_EST_END_TIME.getMessage());
                } else {
                    LocalDateTime maxChildTaskEstEndTime = taskDao.getMaxEstEndTimeByParentTaskIdAndEnable(target.getId(), Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
                    if (maxChildTaskEstEndTime != null && taskDTO.getEstEndTime().isBefore(maxChildTaskEstEndTime))
                        return new ResultData(ResultStatus.PARENT_EST_END_TIME.getCode(), ResultStatus.PARENT_EST_END_TIME.getMessage());
                }

                //更新任务状态或者更新截止日期，都需要更新日期状态
                //更新截止日期
                if (StringUtils.isNotBlank(target.getStatus())) {
                    String curDateStatus = this.getDateStatus(target.getStatus(), taskDTO.getEstEndTime());
                    if (!StringUtils.equals(target.getDateStatus(), curDateStatus))
                        taskDTO.setDateStatus(curDateStatus);
                }
            }

            //更新任务状态
            if (StringUtils.isNotBlank(taskDTO.getStatus())) {
                if (!StringUtils.equals(taskDTO.getStatus(), target.getStatus())) {

                    //子任务重新开启任务，主任务自动重新开启
                    if (StringUtils.isNotBlank(target.getParentId()) && taskDTO.getRestartTaskFlag()) {
                        Task parentTask = taskDao.selectById(target.getParentId());
                        if (parentTask != null && !StringUtils.equals(parentTask.getStatus(), taskDTO.getStatus()))
                            this.updateParentTask(parentTask, taskDTO.getStatus(), userId);
                    }

                    // 子任务未完成（未开始、待审核、拒绝等），主任务不能通过
                    if (StringUtils.isBlank(target.getParentId()) &&
                            (taskDTO.getStatus().equals(TaskStatusEnum.COMPLETED.getCode()) || taskDTO.getStatus().equals(TaskStatusEnum.CONDITIONAL_PASS.getCode()))) {
                        List<Task> notPassChildTasks = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getParentId, target.getId())
                                .eq(Task::getEnabled, BaseEnums.YES.getCode()).eq(Task::getStMark, BaseEnums.NO.getCode())
                                .notIn(Task::getStatus, Arrays.asList(TaskStatusEnum.COMPLETED.getCode(), TaskStatusEnum.CONDITIONAL_PASS.getCode())).list();
                        if (!CollectionUtils.isEmpty(notPassChildTasks))
                            return new ResultData(ResultStatus.EXIT_CHILD_TASK_NOT_END.getCode(), ResultStatus.EXIT_CHILD_TASK_NOT_END.getMessage());

                        // 子任务带条件通过时，主任务不能完全通过，可以带条件通过或拒绝
                        if (taskDTO.getStatus().equals(TaskStatusEnum.COMPLETED.getCode())) {
                            List<Task> conditionalPassChildTasks = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getParentId, target.getId())
                                    .eq(Task::getEnabled, BaseEnums.YES.getCode()).eq(Task::getStMark, BaseEnums.NO.getCode())
                                    .in(Task::getStatus, TaskStatusEnum.CONDITIONAL_PASS.getCode()).list();
                            if (!CollectionUtils.isEmpty(conditionalPassChildTasks))
                                return new ResultData(ResultStatus.EXIT_CHILD_TASK_IN_PASS.getCode(), ResultStatus.EXIT_CHILD_TASK_IN_PASS.getMessage());
                        }
                    }

//                    已拒绝并且已重新上传交付物的任务将isReupload置为1
                    if (StringUtils.isNotBlank(reuploadListIds)) {
                        String[] strings = reuploadListIds.split(",");
                        logger.info("已重新上传交付物" + Arrays.toString(strings));
                        for (String reuploadId : strings) {
                            TaskDelivery taskDelivery = taskDeliveryDao.selectById(reuploadId);
                            taskDelivery.setIsReupload(1);
                            taskDeliveryDao.updateById(taskDelivery);
                        }
                    }

                    //任务状态为结束
                    if (taskDTO.getStatus().equals(TaskStatusEnum.COMPLETED.getCode()))
                        taskDTO.setActEndTime(LocalDateTime.now());
                    else
                        taskDTO.setActEndTime(null);

                    if (target.getEstEndTime() != null) {
                        String curDateStatus = this.getDateStatus(taskDTO.getStatus(), target.getEstEndTime());
                        if (!StringUtils.equals(target.getDateStatus(), curDateStatus))
                            taskDTO.setDateStatus(curDateStatus);
                    }
                }
                //状态没有改变，并且状态为“已完成”
                else if (taskDTO.getStatus().equals(TaskStatusEnum.COMPLETED.getCode()))
                    taskDTO.setActEndTime(target.getActEndTime());
            }

            taskDTO.setReviewProjectRoleId(StringUtils.isBlank(taskDTO.getReviewRoleName()) ? "" :
                    projectRoleService.getProjectRoleIdByRoleName(taskDTO.getReviewRoleName(), taskDTO.getProjectId(), userId));
            taskDTO.setConfirmProjectRoleId(StringUtils.isBlank(taskDTO.getConfirmRoleName()) ? "" :
                    projectRoleService.getProjectRoleIdByRoleName(taskDTO.getConfirmRoleName(), taskDTO.getProjectId(), userId));

            //拒绝/带条件通过理由
            if (StringUtils.isNotBlank(taskDTO.getRefuseReason()))
                taskDTO.setRefuseReason(StringUtils.isBlank(target.getRefuseReason()) ? taskDTO.getRefuseReason() : target.getRefuseReason() + "||" + taskDTO.getRefuseReason());
            if (StringUtils.isNotBlank(taskDTO.getPassReason()))
                taskDTO.setPassReason(StringUtils.isBlank(target.getPassReason()) ? taskDTO.getPassReason() : target.getPassReason() + "||" + taskDTO.getPassReason());

            taskDTO.setUpdateUserId(userId);
            taskDao.updateById(taskDTO);
        }

        // 更新Redis中的任务
        this.updateTaskInRedisByTaskId(target.getId());

        //临时任务，无所属项目
        Project project = null;
        if (StringUtils.isNotBlank(target.getProjectId()))
            project = projectDao.selectById(target.getProjectId());

        //里程碑截止日期 -----> 取最大的启用状态任务截止时间
        if (taskDTO.getEstEndTime() != null && StringUtils.isNotBlank(target.getMilestoneId()) && !taskDTO.getEstEndTime().equals(target.getEstEndTime())) {
            ProjectMilestone projectMilestone = projectMilestoneDao.selectById(target.getMilestoneId());
            if (projectMilestone != null && StringUtils.isNotBlank(projectMilestone.getProjectId())) {
                // 取最大的启用状态任务截止时间
                LocalDateTime maxTaskEstEndTime = taskDao.getMaxEstEndTimeByMilestoneIdAndEnable(target.getMilestoneId(), Integer.valueOf(TreeResultStatus.ENABLE.getCode()));

                //里程碑下属任务更改时间，若任务时间早于里程碑时间，里程碑时间不变，若任务时间晚于里程碑时间，里程碑调整为该任务时间
                if (projectMilestone.getEstEndTime() == null || !projectMilestone.getEstEndTime().equals(maxTaskEstEndTime)) {
                    projectMilestone.setEstEndTime(maxTaskEstEndTime);
                    projectMilestoneDao.updateById(projectMilestone);
                    // 更新Redis中的里程碑
                    if (projectMilestone.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode())))
                        redisTemplate.opsForHash().put(RedisTableName.PROJECT.getCode() + target.getProjectId(),
                                RedisTableName.PROJECT_MILESTONE.getCode() + target.getMilestoneId(),
                                JSON.toJSONString(projectMilestoneDao.findById(projectMilestone.getId())));
                    //Log 里程碑时间节点的变更
                    projectMilestoneLogService.saveEstEndTimeChange(projectMilestone.getEstEndTime(), maxTaskEstEndTime, userId, projectMilestone.getId(), projectMilestone.getProjectId());

                    // 项目截止日期 -----> 取最大的启用状态里程碑截止时间
                    if (project != null) {
                        // 取最大的启用状态里程碑截止时间
                        LocalDateTime maxProjectEstEndTime = projectMilestoneDao.getMaxEstEndTimeByProjectIdAndEnable(projectMilestone.getProjectId(), Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
                        project.setEstEndTime(maxProjectEstEndTime);
                        projectDao.updateById(project);
                    }
                }
            }
            // 排除未上线任务 任务时间节点变更（任务负责人、任务审核人）
            if (project != null && BaseEnums.YES.getCode().equals(project.getHasOnLine())) {
                Map<String, String> messageExt = new HashMap<>();
                messageExt.put("userName", sysUserDao.selectById(userId).getRealName());
                this.taskMessage(taskDTO.getReviewProjectRoleId(), taskDTO.getConfirmProjectRoleId(), target.getId(), false, target.getProjectId(),
                        MessageModuleEnum.TASK_TIME_CHANGE.getType(), PlaceholderUtil.resolvePlaceholders(MessageModuleEnum.TASK_TIME_CHANGE.getMsg(), messageExt));
            }
        }

        //任务时间状态有值，找到最大状态
        if (StringUtils.isNotBlank(taskDTO.getDateStatus()) && StringUtils.isNotBlank(target.getMilestoneId())) {
            this.setMilestoneStatusByTask(target.getMilestoneId(), false);
            // 任务即将超期，消息给（任务负责人、项目负责人）
            if (project != null && BaseEnums.YES.getCode().equals(project.getHasOnLine())
                    && target.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode())) && taskDTO.getDateStatus().equals(DateStatusEnum.ADJACENT_NODE.getCode()))
                this.taskWillDelay(target.getProjectId(), target.getName(), taskDTO.getEstEndTime(), taskDTO.getReviewProjectRoleId(), target.getId());
        }

        //Log
        if (StringUtils.isNotBlank(taskDTO.getId()))
            this.saveTaskLog(taskDTO, target, refuseReason, passReason, userId);

        // 任务审核/已拒绝，消息给 任务审核人/任务负责人
        if (!StringUtils.equals(taskDTO.getStatus(), target.getStatus())
                && (StringUtils.equals(taskDTO.getStatus(), TaskStatusEnum.CHECK_PENDING.getCode()) || StringUtils.equals(taskDTO.getStatus(), TaskStatusEnum.REFUSED.getCode()))
                && (project == null || BaseEnums.YES.getCode().equals(project.getHasOnLine()))) {
            Map<String, String> messageExt = new HashMap<>();
            String reviewProjectRoleId = null;
            String confirmProjectRoleId = null;
            if (StringUtils.equals(taskDTO.getStatus(), TaskStatusEnum.CHECK_PENDING.getCode())) {
                confirmProjectRoleId = taskDTO.getConfirmProjectRoleId();
                messageExt.put("userName", sysUserDao.selectById(userId).getRealName());
            } else {
                reviewProjectRoleId = taskDTO.getReviewProjectRoleId();
                messageExt.put("refuseReason", refuseReason);
                messageExt.put("userName", "您");
            }
            this.taskMessage(reviewProjectRoleId, confirmProjectRoleId, target.getId(), false, target.getProjectId(),
                    StringUtils.equals(taskDTO.getStatus(), TaskStatusEnum.CHECK_PENDING.getCode()) ? MessageModuleEnum.TASK_CHECK.getType() : MessageModuleEnum.TASK_REFUSED.getType(),
                    PlaceholderUtil.resolvePlaceholders(StringUtils.equals(taskDTO.getStatus(), TaskStatusEnum.CHECK_PENDING.getCode()) ? MessageModuleEnum.TASK_CHECK.getMsg() : MessageModuleEnum.TASK_REFUSED.getMsg(),
                            messageExt));
        }

        //任务负责人、审核人变更，消息 给新的任务负责人或新的任务审核人
        if (StringUtils.isNotBlank(taskDTO.getReviewProjectRoleId()) && !StringUtils.equals(target.getReviewProjectRoleId(), taskDTO.getReviewProjectRoleId()))
            this.taskResponsiblePersonChange(userId, taskDTO.getReviewProjectRoleId(), true, target.getId(), target.getProjectId(),
                    target.getReviewProjectRoleId(), project == null ? null : project.getHasOnLine());
        if (StringUtils.isNotBlank(taskDTO.getConfirmProjectRoleId()) && !StringUtils.equals(target.getConfirmProjectRoleId(), taskDTO.getConfirmProjectRoleId()))
            this.taskResponsiblePersonChange(userId, taskDTO.getConfirmProjectRoleId(), false, target.getId(), target.getProjectId(),
                    target.getConfirmProjectRoleId(), project == null ? null : project.getHasOnLine());

        return ResultData.ok();
    }

    /**
     * 更新父任务状态 并记录日志
     */
    private void updateParentTask(Task parentTask, String status, String userId) {
        String lastStatus = parentTask.getStatus();
        parentTask.setStatus(status);
        parentTask.setActEndTime(null);
        //时间状态
        if (parentTask.getEstEndTime() != null) {
            String curDateStatus = this.getDateStatus(status, parentTask.getEstEndTime());
            if (!StringUtils.equals(parentTask.getDateStatus(), curDateStatus))
                parentTask.setDateStatus(curDateStatus);
        }
        taskDao.updateById(parentTask);
        //更新到Redis
        this.updateTaskInRedisByTaskId(parentTask.getId());

        //状态修改log
        TaskLog taskLog = this.getTaskLog(userId, parentTask.getProjectId(), parentTask.getId(), TaskStatusEnum.getMessage(lastStatus),
                TaskStatusEnum.getMessage(status), "状态");
        taskLogDao.insert(taskLog);
    }

    /**
     * 项目负责人、审核人变更
     * @param userId 创建人ID
     * @param curProjectRoleId 当前选择的新角色
     * @param flag true ：负责人 false：审核人
     * @param taskId 任务ID
     * @param projectId 项目ID
     * @param lastProjectRoleId 历史角色ID
     * @param hasOnLine 是否上线
     */
    private void taskResponsiblePersonChange(String userId, String curProjectRoleId, boolean flag, String taskId, String projectId, String lastProjectRoleId, Integer hasOnLine) {
        ProjectRoleUser curProjectRoleUser = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).eq(ProjectRoleUser::getProjectRoleId, curProjectRoleId).one();

        // 变更log 发送邮件
        List<TaskLog> taskLogs = new ArrayList<>();
        projectMilestoneService.comparePastAndPresentValues(lastProjectRoleId,
                curProjectRoleUser == null ? null : sysUserDao.selectById(curProjectRoleUser.getSysUserId()).getRealName(), userId, projectId, taskId, taskLogs, flag, hasOnLine, curProjectRoleId);
        if (!CollectionUtils.isEmpty(taskLogs))
            taskLogService.saveBatch(taskLogs);

    }

    /**
     * 任务相关联的两个角色的人变更，发送邮件
     */
    public void roleUserChaneOfTask(boolean flag, String userId, String toUserName, String reviewProjectRoleId, String confirmProjectRoleId, String taskId, String projectId) {
        String roleType = "负责人";
        if (!flag) roleType = "审核人";
        Map<String, String> messageExt = new HashMap<>();
        messageExt.put("userName", sysUserDao.selectById(userId).getRealName());
        messageExt.put("roleType", roleType);
//        messageExt.put("toUserName", toUserName);
        this.taskMessage(reviewProjectRoleId, confirmProjectRoleId, taskId,
                false, projectId, MessageModuleEnum.TASK_RESPONSIBLE_PERSON_CHANGE.getType()
                , PlaceholderUtil.resolvePlaceholders(MessageModuleEnum.TASK_RESPONSIBLE_PERSON_CHANGE.getMsg(), messageExt));
    }

    /**
     * 任务相关消息通知
     *
     * @param reviewProjectRoleId  任务负责人
     * @param confirmProjectRoleId 任务审核人
     * @param taskId               任务ID
     * @param flag                 是否需要通知项目负责人
     * @param projectId            项目ID
     */
    public void taskMessage(String reviewProjectRoleId, String confirmProjectRoleId, String taskId, Boolean flag, String projectId, String type, String msg) {
        List<String> receiveUserIds = new ArrayList<>();
        if (StringUtils.isNotBlank(reviewProjectRoleId)) {
            ProjectRoleUser reviewProjectRoleUser = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).eq(ProjectRoleUser::getProjectRoleId, reviewProjectRoleId).one();
            if (reviewProjectRoleUser != null) receiveUserIds.add(reviewProjectRoleUser.getSysUserId());
        }
        if (StringUtils.isNotBlank(confirmProjectRoleId)) {
            ProjectRoleUser confirmProjectRoleUser = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).eq(ProjectRoleUser::getProjectRoleId, confirmProjectRoleId).one();
            if (confirmProjectRoleUser != null) receiveUserIds.add(confirmProjectRoleUser.getSysUserId());
        }
        if (flag && StringUtils.isNotBlank(projectId)) {
            Project project = projectDao.selectById(projectId);
            if (project != null) receiveUserIds.add(project.getProjectUserId());
        }
        messageUserService.saveMessageToUser(type, taskId,
                msg, receiveUserIds.toArray(new String[0]), projectId, null, null, null);
    }

    /**
     * 任务归档
     */
    @Override
    public boolean letTaskToStMark(String taskId) {
        if (StringUtils.isBlank(taskId)) return false;
        List<String> taskIds = new ArrayList<>();
        taskIds.add(taskId);
        this.getChildTaskIds(taskId, taskIds);
        List<Task> notStMarkTasks = new LambdaQueryChainWrapper<Task>(taskDao).in(BaseEntity::getId, taskIds).ne(Task::getStMark, BaseEnums.YES.getCode()).list();
        if (CollectionUtils.isEmpty(notStMarkTasks)) return false;
        notStMarkTasks = notStMarkTasks.stream().peek(notStMarkTask -> notStMarkTask.setStMark(BaseEnums.YES.getCode())).collect(Collectors.toList());
        this.updateBatchById(notStMarkTasks);
        //移出Redis
        this.deleteTaskInRedis(notStMarkTasks);
        return true;
    }

    /**
     * 我的任务
     *
     * @param userId 当前用户ID
     * @return .
     */
    @Override
    public Page<Task> getMyTasks(String userId, Integer page, Integer size) {
        //任务名称 项目名称 里程碑名称 状态 责任人 确认人 截止时间
        Page<Task> taskPage = new Page<>(page, size);
        //负责人：未完成 2 4 6
        //审核人：除去已完成 2 4 6
        return taskPage.setRecords(taskDao.findReviewOrConfirmNotCompleteTaskByUserId(taskPage, userId));
    }

    /**
     * Log
     */
    private void saveTaskLog(TaskDTO taskDTO, Task target, String refuseReason, String passReason, String userId) {
        StringBuilder str = new StringBuilder();
        String changeLog = beanChangeUtilService.contrastObj(target, taskDTO);
        if (StringUtils.isNotBlank(changeLog)) {
            if (StringUtils.isNotBlank(str))
                str.append(";");
            str.append(changeLog);
        }
        // 拒绝/带条件通过理由
        if (StringUtils.isNotBlank(refuseReason)) {
            if (StringUtils.isNotBlank(str))
                str.append(";");
            str.append("Add 拒绝理由" + ", With \"" + refuseReason + "\"");
        }
        if (StringUtils.isNotBlank(passReason)) {
            if (StringUtils.isNotBlank(str))
                str.append(";");
            str.append("Add 带条件通过理由" + ", With \"" + passReason + "\"");
        }

        if (StringUtils.isNotBlank(str)) {
            TaskLog taskLog = new TaskLog(str.toString(), target.getProjectId(), target.getId());
            taskLog.setCreateUserId(userId);
            taskLogDao.insert(taskLog);
        }
    }

    /**
     * 根据启用任务的时间状态，设置里程碑状态
     *
     * @param milestoneId 里程碑ID
     * @param batchFlag   true -> 定时任务修改 false -> 单独修改任务状态时
     */
    @Override
    public void setMilestoneStatusByTask(String milestoneId, boolean batchFlag) {
        ProjectMilestone projectMilestone = projectMilestoneDao.selectById(milestoneId);
        if (projectMilestone == null) return;
        String maxDateStatus = taskDao.getMaxDateStatusByMilestoneIdAndEnable(milestoneId, Integer.valueOf(TreeResultStatus.ENABLE.getCode()));

        // 状态紫色、蓝色 均显示绿色
        if (StringUtils.equals(maxDateStatus, DateStatusEnum.DELAY_COMPLETION.getCode())
                || StringUtils.equals(maxDateStatus, DateStatusEnum.NOT_TO_THE_NODE.getCode()))
            maxDateStatus = DateStatusEnum.FINISH_ON_TIME.getCode();

        if (!StringUtils.equals(projectMilestone.getStatus(), maxDateStatus) && StringUtils.isNotBlank(projectMilestone.getProjectId())) {
            LambdaUpdateWrapper<ProjectMilestone> milestoneLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            milestoneLambdaUpdateWrapper.eq(BaseEntity::getId, milestoneId);
            milestoneLambdaUpdateWrapper.set(ProjectMilestone::getStatus, maxDateStatus);
            projectMilestoneService.update(milestoneLambdaUpdateWrapper);
            // 更新Redis--->只更新启用状态
            if (projectMilestone.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode())))
                redisTemplate.opsForHash().put(RedisTableName.PROJECT.getCode() + projectMilestone.getProjectId(),
                        RedisTableName.PROJECT_MILESTONE.getCode() + milestoneId,
                        JSON.toJSONString(projectMilestoneDao.findById(milestoneId)));

            if (batchFlag) return;
            // 修改项目状态 所属里程碑最大的状态
            this.setProjectStatusByMilestone(projectMilestone.getProjectId());
        }
    }

    /**
     * 根据里程碑的状态，设置项目状态
     *
     * @param projectId 项目ID
     */
    @Override
    public void setProjectStatusByMilestone(String projectId) {
        String maxMilestoneStatus = projectMilestoneDao.getMaxStatusByProjectIdAndEnable(projectId, Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
        Project project = projectDao.selectById(projectId);
        if (project != null && !StringUtils.equals(project.getStatus(), maxMilestoneStatus)) {
            LambdaUpdateWrapper<Project> projectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            projectLambdaUpdateWrapper.eq(BaseEntity::getId, project.getId());
            projectLambdaUpdateWrapper.set(Project::getStatus, maxMilestoneStatus);
            projectService.update(projectLambdaUpdateWrapper);
            // todo 项目即将超期，消息给（项目负责人）项目已上线
            if (maxMilestoneStatus.equals(DateStatusEnum.ADJACENT_NODE.getCode()) && BaseEnums.YES.getCode().equals(project.getHasOnLine())) {
                messageUserService.saveMessageToUser(MessageModuleEnum.PROJECT_WILL_DELAY.getType(),
                        projectId, MessageModuleEnum.PROJECT_WILL_DELAY.getMsg(), new String[]{project.getProjectUserId()}, null,
                        null, null, null);
            }
        }
    }

    /**
     * 更新Redis里的任务列表 ----》 变更任务字段、属性值、交付物
     *
     * @param taskId 任务
     */
    public void updateTaskInRedisByTaskId(String taskId) {
        Task task = taskDao.findAllById(taskId);
        if (task == null) return;
        redisTemplate.opsForHash().put(RedisTableName.PROJECT.getCode() + task.getProjectId(),
                StringUtils.isBlank(task.getParentId()) ? RedisTableName.PARENT_TASK.getCode() + task.getId() : RedisTableName.CHILD_TASK.getCode() + task.getId(),
                JSON.toJSONString(task));
    }

    /**
     * 批量更新Redis里的任务列表
     *
     * @param taskList 仅有任务内容
     */
    public void batchUpdateTaskInRedis(List<Task> taskList) {
        redisTemplate.executePipelined(new RedisCallback<List<Task>>() {
            @Override
            public List<Task> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                for (Task task : taskList) {
                    if (task.getStMark().equals(BaseEnums.YES.getCode())) continue;
                    redisConnection.hSet((RedisTableName.PROJECT.getCode() + task.getProjectId()).getBytes(),
                            (StringUtils.isBlank(task.getParentId()) ? RedisTableName.PARENT_TASK.getCode() + task.getId() : RedisTableName.CHILD_TASK.getCode() + task.getId()).getBytes(),
                            JSON.toJSONString(taskDao.findAllById(task.getId())).getBytes());
                }
                return null;
            }
        });
    }

    /**
     * 根据任务状态和截止日期，获取日期状态
     *
     * @param taskStatus 任务状态
     * @param estEndTime 预计截止日期
     * @return 日期状态
     */
    public String getDateStatus(String taskStatus, LocalDateTime estEndTime) {
        //状态为待条件通过，则时间状态为默认
        if (StringUtils.equals(taskStatus, TaskStatusEnum.CONDITIONAL_PASS.getCode()))
            return DateStatusEnum.CONDITIONAL_PASS.getCode();
        String dateStatus = null;
        LocalDateTime now = LocalDateTime.now();
        if (!taskStatus.equals(TaskStatusEnum.COMPLETED.getCode())) {
            //往前7天的零点 -- 当天的最后时间
            LocalDateTime lastSevenDay = LocalDateTime.of(estEndTime.plusDays(-6).toLocalDate(), LocalTime.MIN);
            if (now.isBefore(lastSevenDay))
                dateStatus = DateStatusEnum.NOT_TO_THE_NODE.getCode();
            else if (now.isAfter(LocalDateTime.of(estEndTime.toLocalDate(), LocalTime.MAX)))
                dateStatus = DateStatusEnum.DELAY.getCode();
            else
                dateStatus = DateStatusEnum.ADJACENT_NODE.getCode();
        } else {
            //比截止日期的最后时间大
            if (now.isAfter(LocalDateTime.of(estEndTime.toLocalDate(), LocalTime.MAX)))
                dateStatus = DateStatusEnum.DELAY_COMPLETION.getCode();
            else
                dateStatus = DateStatusEnum.FINISH_ON_TIME.getCode();
        }
        return dateStatus;
    }

    /**
     * 批量删除 并删除关联表
     */
    @Override
    public boolean batchDelete(String[] ids, boolean flag, String userId) {
        List<String> idList = Arrays.asList(ids);
        //删除任务
        List<String> taskIds = new ArrayList<>(idList);
        if (flag)
            idList.forEach(taskId -> this.getChildTaskIds(taskId, taskIds));
        HashSet<String> taskIdSet = new HashSet<>(taskIds);

        //删除关联表
        taskDataDao.delete(new LambdaQueryWrapper<TaskData>().in(TaskData::getTaskId, taskIdSet));
        taskDeliveryDao.delete(new LambdaQueryWrapper<TaskDelivery>().in(TaskDelivery::getTaskId, taskIdSet));
        //删除Log
        taskLogDao.delete(new LambdaQueryWrapper<TaskLog>().in(TaskLog::getTaskId, taskIdSet));

        //删除临时任务关系表
        taskUserDao.delete(new LambdaQueryWrapper<TaskUser>().in(TaskUser::getTaskId, taskIdSet));

        //删除redis
        List<Task> tasks = taskDao.selectBatchIds(taskIdSet);
        this.deleteTaskInRedis(tasks);
        //删除任务
        taskDao.deleteBatchIds(taskIdSet);
        // 更新里程碑、项目状态和时间
        if (flag) {
            this.updateMilestoneAndProjectByTasks(tasks);
            //Log 里程碑日志：任务删除
            projectMilestoneLogService.saveTaskChange(LogTypeEnum.DELETE.getMsg(), userId, tasks);

            //Log 任务日志：子任务的删除
            List<Task> hasParentTasks = tasks.stream().filter(task -> StringUtils.isNotBlank(task.getParentId()) && !task.getParentId().equals("0")).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(hasParentTasks)) {
                Map<String, List<Task>> groupByParentTasks = hasParentTasks.stream().collect(Collectors.groupingBy(Task::getParentId));
                groupByParentTasks.forEach((parentId, childTasks) -> {
                    if (!taskIdSet.contains(parentId)) {
                        taskLogService.saveChildTaskChange(userId, childTasks.stream().map(Task::getName).collect(Collectors.toList()), parentId, LogTypeEnum.DELETE.getMsg(), childTasks.get(0).getProjectId());
                    }
                });
            }
        }

        return true;
    }

    /**
     * 更新里程碑、项目状态和时间
     */
    private void updateMilestoneAndProjectByTasks(List<Task> tasks) {
        Set<String> milestoneIds = tasks.stream().filter(task -> StringUtils.isNotBlank(task.getMilestoneId())).map(Task::getMilestoneId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(milestoneIds)) return;
        List<ProjectMilestone> projectMilestones = projectMilestoneDao.selectBatchIds(milestoneIds);
        if (!CollectionUtils.isEmpty(projectMilestones)) this.updateMilestoneEstEndTimeAndStatus(projectMilestones);
        Set<String> projectIds = projectMilestones.stream().filter(projectMilestone -> StringUtils.isNotBlank(projectMilestone.getProjectId())).map(ProjectMilestone::getProjectId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(projectIds)) return;
        List<Project> projects = projectDao.selectBatchIds(projectIds);
        this.updateProjectEstEndTimeAndStatus(projects);
    }

    @Override
    public void deleteRoleId(List<String> projectRoleIds) {
        List<Task> tasks = new LambdaQueryChainWrapper<Task>(taskDao).in(Task::getReviewProjectRoleId, projectRoleIds)
                .or().in(Task::getConfirmProjectRoleId, projectRoleIds).list();
        if (CollectionUtils.isEmpty(tasks)) return;
        tasks.forEach(task -> {
            String reviewProjectRoleId = projectRoleIds.contains(task.getReviewProjectRoleId()) ? null : task.getReviewProjectRoleId();
            String confirmProjectRoleId = projectRoleIds.contains(task.getConfirmProjectRoleId()) ? null : task.getConfirmProjectRoleId();
            taskDao.updateByRoleId(task.getId(), reviewProjectRoleId, confirmProjectRoleId);

        });

        // 更新Redis中 启用状态的任务
        List<Task> enableTasks = tasks.stream().filter(task -> task.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode()))).collect(Collectors.toList());
        this.batchUpdateTaskInRedis(enableTasks);
    }

    /**
     * 根据角色ID刷新TASK
     *
     * @param projectRoleId 角色ID
     */
    @Override
    public void updateTaskInRedisByProjectRoleId(String projectRoleId) {
        List<Task> tasks = new LambdaQueryChainWrapper<Task>(taskDao).and(i -> i.nested(j -> j.eq(Task::getReviewProjectRoleId, projectRoleId)).or(j -> j.eq(Task::getConfirmProjectRoleId, projectRoleId)))
                .eq(Task::getEnabled, TreeResultStatus.ENABLE.getCode()).eq(Task::getForbidden, TreeResultStatus.NOT_FORBIDDEN.getCode()).eq(Task::getStMark, BaseEnums.NO.getCode()).list();
        if (CollectionUtils.isEmpty(tasks)) return;
        // 更新Redis中的任务
        this.batchUpdateTaskInRedis(tasks);
    }

    /**
     * 批量修改任务时间 && 任务时间状态
     *
     * @param taskList 任务列表
     */
    @Override
    public void batchUpdateTaskTime(List<Task> taskList, SysUser user, boolean isUpdateMilestoneFlag) {
        if (CollectionUtils.isEmpty(taskList)) return;
        Map<String, Task> taskMap = taskList.stream().filter(task -> task.getEstEndTime() != null).collect(Collectors.toMap(Task::getId, a -> a, (k1, k2) -> k1));
        if (CollectionUtils.isEmpty(taskMap)) return;
        List<Task> allTaskList = taskDao.selectBatchIds(taskMap.keySet());
        if (StringUtils.isNotBlank(allTaskList.get(0).getProjectId())) {
            Project project = projectDao.selectById(allTaskList.get(0).getProjectId());

            List<Task> timeChangeTasks = allTaskList.stream().filter(task -> task.getEstEndTime() == null || !task.getEstEndTime().equals(taskMap.get(task.getId()).getEstEndTime())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(timeChangeTasks)) {
                List<TaskLog> taskLogs = new ArrayList<>();
                timeChangeTasks.forEach(task -> {
                    //时间变更日志log
                    this.getTaskLogs(taskLogs, user.getId(), task.getProjectId(), task.getId(), task.getEstEndTime() == null ? null : task.getEstEndTime().toString(),
                            taskMap.get(task.getId()).getEstEndTime().toString(), "预计截止时间");

                    // 任务时间节点变更（任务负责人、任务审核人）
                    if (project != null && BaseEnums.YES.getCode().equals(project.getHasOnLine())) {
                        Map<String, String> messageExt = new HashMap<>();
                        messageExt.put("userName", user.getRealName());
                        this.taskMessage(task.getReviewProjectRoleId(), task.getConfirmProjectRoleId(), task.getId(), false, task.getProjectId(),
                                MessageModuleEnum.TASK_TIME_CHANGE.getType(), PlaceholderUtil.resolvePlaceholders(MessageModuleEnum.TASK_TIME_CHANGE.getMsg(), messageExt));
                    }
                });
                taskLogService.saveBatch(taskLogs);
            }

            List<Task> curTaskList = new ArrayList<>();
            for (Task task : allTaskList) {
                LocalDateTime estEndTime = taskMap.get(task.getId()).getEstEndTime();

                // 过滤掉批量修改时间时，子任务时间不能大于父任务时间
                if (isUpdateMilestoneFlag) {
                    //父任务是否在选择中
                    if (StringUtils.isNotBlank(task.getParentId()) && !taskMap.keySet().contains(task.getParentId())) {
                        Task parentTask = taskDao.selectById(task.getParentId());
                        if (parentTask != null && estEndTime.isAfter(parentTask.getEstEndTime()))
                            continue;
                    }
//                    //子任务是否在选择中
//                    else if (StringUtils.isBlank(task.getParentId())) {
//
//                        List<Task> childTaskList = new LambdaQueryChainWrapper<>(taskDao).eq(Task::getParentId, task.getId())
//                                .eq(Task::getEnabled, BaseEnums.YES.getCode()).eq(Task::getStMark, BaseEnums.NO.getCode()).list();
//                        if (!CollectionUtils.isEmpty(childTaskList)) {
//                            List<Task> reduceTasks = childTaskList.stream().filter(item -> !taskMap.keySet().contains(item.getId()))
//                                    .filter(task1 -> estEndTime.isBefore(task1.getEstEndTime())).collect(Collectors.toList());
//                            if (!CollectionUtils.isEmpty(reduceTasks)) {
//                                continue;
//                            }
//                        }
//                    }
                }
                task.setEstEndTime(estEndTime);
                //任务时间状态
                if (StringUtils.isNotBlank(task.getStatus()) && estEndTime != null)
                    task.setDateStatus(this.getDateStatus(task.getStatus(), estEndTime));
                curTaskList.add(task);
            }
            if (CollectionUtils.isEmpty(curTaskList)) return;
            this.updateBatchById(curTaskList);
            // 更新Redis--->只更新启用状态
            List<Task> enableTasks = curTaskList.stream().filter(task -> task.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode()))
                    && BaseEnums.NO.getCode().equals(task.getStMark())).collect(Collectors.toList());
            this.batchUpdateTaskInRedis(enableTasks);

            // 任务即将超期，消息给（任务负责人、项目负责人）
            if (project != null && BaseEnums.YES.getCode().equals(project.getHasOnLine())) {
                List<Task> willExpireTasks = enableTasks.stream().filter(task -> StringUtils.equals(task.getDateStatus(), DateStatusEnum.ADJACENT_NODE.getCode())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(willExpireTasks))
                    willExpireTasks.forEach(task -> this.taskWillDelay(task.getProjectId(), task.getName(), task.getEstEndTime(), task.getReviewProjectRoleId(), task.getId()));
            }

            // 更新里程碑状态 更新项目状态
            Set<String> milestoneIds = curTaskList.stream().filter(task -> org.apache.commons.lang3.StringUtils.isNotBlank(task.getMilestoneId())).map(Task::getMilestoneId).collect(Collectors.toSet());
            milestoneIds.forEach(milestoneId -> this.setMilestoneStatusByTask(milestoneId, true));
            this.setProjectStatusByMilestone(allTaskList.get(0).getProjectId());

            //是否需要更新里程碑时间
            if (isUpdateMilestoneFlag) {
                milestoneIds.forEach(milestoneId -> {
                    ProjectMilestone projectMilestone = projectMilestoneDao.selectById(milestoneId);
                    if (projectMilestone != null) this.updateMilestoneAndProjectTime(projectMilestone, user.getId());
                });

                //更新项目时间
                if (project != null) {
                    LocalDateTime maxProjectEstEndTime = projectMilestoneDao.getMaxEstEndTimeByProjectIdAndEnable(project.getId(), Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
                    LambdaUpdateWrapper<Project> projectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    projectLambdaUpdateWrapper.eq(BaseEntity::getId, project.getId());
                    projectLambdaUpdateWrapper.set(Project::getEstEndTime, maxProjectEstEndTime);
                    projectService.update(projectLambdaUpdateWrapper);
                }
            }


        } else {
//            临时任务 临时任务没有状态，不用判断项目状态，没有里程碑，
            // 任务时间节点变更（任务负责人、任务审核人）项目上线
            List<Task> timeChangeTasks = allTaskList.stream().filter(task -> task.getEstEndTime() == null || !task.getEstEndTime().equals(taskMap.get(task.getId()).getEstEndTime())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(timeChangeTasks))
                timeChangeTasks.forEach(task -> {
                    Map<String, String> messageExt = new HashMap<>();
                    messageExt.put("userName", user.getRealName());
                    this.taskMessage(task.getReviewProjectRoleId(), task.getConfirmProjectRoleId(), task.getId(), false, task.getProjectId(),
                            MessageModuleEnum.TASK_TIME_CHANGE.getType(), PlaceholderUtil.resolvePlaceholders(MessageModuleEnum.TASK_TIME_CHANGE.getMsg(), messageExt));
                });

            allTaskList = allTaskList.stream().peek(task -> {
                LocalDateTime estEndTime = taskMap.get(task.getId()).getEstEndTime();
                task.setEstEndTime(estEndTime);
                //任务时间状态
                if (StringUtils.isNotBlank(task.getStatus()) && estEndTime != null)
                    task.setDateStatus(this.getDateStatus(task.getStatus(), estEndTime));
            }).collect(Collectors.toList());
            this.updateBatchById(allTaskList);
        }
    }

    private void updateMilestoneAndProjectTime(ProjectMilestone projectMilestone, String createUserId) {
        // 取最大的启用状态任务截止时间
        LocalDateTime maxTaskEstEndTime = taskDao.getMaxEstEndTimeByMilestoneIdAndEnable(projectMilestone.getId(), Integer.valueOf(TreeResultStatus.ENABLE.getCode()));

        //里程碑下属任务更改时间，若任务时间早于里程碑时间，里程碑时间不变，若任务时间晚于里程碑时间，里程碑调整为该任务时间
        if (projectMilestone.getEstEndTime() == null || !projectMilestone.getEstEndTime().equals(maxTaskEstEndTime)) {
            LambdaUpdateWrapper<ProjectMilestone> milestoneLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            milestoneLambdaUpdateWrapper.eq(BaseEntity::getId, projectMilestone.getId());
            milestoneLambdaUpdateWrapper.set(ProjectMilestone::getEstEndTime, maxTaskEstEndTime);
            projectMilestoneService.update(milestoneLambdaUpdateWrapper);
            // 更新Redis中的里程碑
            if (projectMilestone.getEnabled().equals(Integer.valueOf(TreeResultStatus.ENABLE.getCode())))
                redisTemplate.opsForHash().put(RedisTableName.PROJECT.getCode() + projectMilestone.getProjectId(),
                        RedisTableName.PROJECT_MILESTONE.getCode() + projectMilestone.getId(),
                        JSON.toJSONString(projectMilestoneDao.findById(projectMilestone.getId())));
            //Log 里程碑时间节点的变更
            projectMilestoneLogService.saveEstEndTimeChange(projectMilestone.getEstEndTime(), maxTaskEstEndTime, createUserId, projectMilestone.getId(), projectMilestone.getProjectId());
        }
    }

    /**
     * 组装task日志List
     */
    private void getTaskLogs(List<TaskLog> taskLogs, String userId, String projectId, String taskId, String lastValue, String curValue, String type) {
        TaskLog taskLog = this.getTaskLog(userId, projectId, taskId, lastValue, curValue, type);
        taskLogs.add(taskLog);
    }

    /**
     * 获取Task单挑日志
     */
    private TaskLog getTaskLog(String userId, String projectId, String taskId, String lastValue, String curValue, String type) {
        TaskLog taskLog = new TaskLog();
        taskLog.setCreateUserId(userId);
        taskLog.setProjectId(projectId);
        taskLog.setTaskId(taskId);
        StringBuilder str = new StringBuilder();
        str.append(PlaceholderUtil.resolvePlaceholders(ChangeEnum.userChange, projectMilestoneService.getChangeLog(type, lastValue, curValue))).append(";");
        str.replace(str.length() - 1, str.length(), "");
        taskLog.setContent(str.toString());
        return taskLog;
    }

    /**
     * 任务即将超期，计算天数
     */
    private void taskWillDelay(String projectId, String taskName, LocalDateTime estEndTime, String reviewProjectRoleId, String taskId) {
        // 任务即将超期，消息给（任务负责人、项目负责人）
        Project project = projectDao.selectById(projectId);
        Map<String, String> messageExt = new HashMap<>();
        messageExt.put("projectName", project.getName());
        messageExt.put("taskName", taskName);
        int day = estEndTime == null ? 0 : Period.between(LocalDate.now(), estEndTime.toLocalDate()).getDays();
        messageExt.put("day", day + "");
        if (day > 0) {
            this.taskMessage(reviewProjectRoleId, null, taskId, true,
                    projectId, MessageModuleEnum.TASK_WILL_DELAY.getType(), PlaceholderUtil.resolvePlaceholders(MessageModuleEnum.TASK_WILL_DELAY.getMsg(), messageExt));
        }
    }

    /**
     * 复制任务关联信息
     *
     * @param taskId
     */
    @Override
    public void copyTask(String taskId) {
        List<Task> lastTaskList = taskDao.findContainsChildListById(taskId);
        if (CollectionUtils.isEmpty(lastTaskList)) return;
        List<Task> saveToRedisList = new ArrayList<>();
        projectMilestoneService.copyTask(lastTaskList, projectMilestoneService.getCopyExcludeArr(), lastTaskList.get(0).getMilestoneId(), lastTaskList.get(0).getParentId(), saveToRedisList);
        if (CollectionUtils.isEmpty(saveToRedisList)) return;
        // redis
        this.batchUpdateTaskInRedis(saveToRedisList);
    }

    /**
     * 删除任务、关联任务及关联表
     */
    @Override
    public boolean delete(String id, String userId) {
        //删除任务
        List<String> taskIds = new ArrayList<>();
        taskIds.add(id);
        this.getChildTaskIds(id, taskIds);
        //删除关联表
        taskDataDao.delete(new LambdaQueryWrapper<TaskData>().in(TaskData::getTaskId, taskIds));
        taskDeliveryDao.delete(new LambdaQueryWrapper<TaskDelivery>().in(TaskDelivery::getTaskId, taskIds));
        //删除Log
        taskLogDao.delete(new LambdaQueryWrapper<TaskLog>().in(TaskLog::getTaskId, taskIds));

        //删除临时任务关系表
        taskUserDao.delete(new LambdaQueryWrapper<TaskUser>().in(TaskUser::getTaskId, taskIds));

        //删除redis
        List<Task> tasks = taskDao.selectBatchIds(taskIds);
        this.deleteTaskInRedis(tasks);
        //删除任务
        taskDao.deleteBatchIds(taskIds);
        // 更新里程碑、项目状态和时间
        this.updateMilestoneAndProjectByTasks(tasks);

        //Log 里程碑日志：任务删除
        projectMilestoneLogService.saveTaskChange(LogTypeEnum.DELETE.getMsg(), userId, tasks);

        //Log 任务日志：子任务的删除
        List<Task> curTasks = tasks.stream().filter(task -> task.getId().equals(id)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(curTasks)) {
            if (StringUtils.isNotBlank(curTasks.get(0).getParentId()) && !curTasks.get(0).getParentId().equals("0")) {
                List<String> childNames = new ArrayList<>();
                childNames.add(curTasks.get(0).getName());
                taskLogService.saveChildTaskChange(userId, childNames, curTasks.get(0).getParentId(), LogTypeEnum.DELETE.getMsg(), curTasks.get(0).getProjectId());
            }
        }

        return true;
    }

    /**
     * 更新里程碑状态和时间
     */
    private void updateMilestoneEstEndTimeAndStatus(List<ProjectMilestone> projectMilestoneList) {
        projectMilestoneList.forEach(projectMilestone -> {
            // 更新里程碑时间
            LocalDateTime maxTaskEstEndTime = taskDao.getMaxEstEndTimeByMilestoneIdAndEnable(projectMilestone.getId(), Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
            // 更新里程碑状态
            String maxDateStatus = taskDao.getMaxDateStatusByMilestoneIdAndEnable(projectMilestone.getId(), Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
            if (StringUtils.equals(maxDateStatus, DateStatusEnum.DELAY_COMPLETION.getCode())
                    || StringUtils.equals(maxDateStatus, DateStatusEnum.NOT_TO_THE_NODE.getCode()))
                maxDateStatus = DateStatusEnum.FINISH_ON_TIME.getCode();
            projectMilestoneDao.updateEstEndTimeAndStatus(projectMilestone.getId(), maxTaskEstEndTime, maxDateStatus);
            //更新Redis
//            projectMilestoneService.updateMilestoneInRedisByMilestone(projectMilestone);
            if (projectMilestone.getEnabled().equals(BaseEnums.YES.getCode())) {
                projectMilestone.setEstEndTime(maxTaskEstEndTime);
                projectMilestone.setStatus(maxDateStatus);
                redisTemplate.opsForHash().put(RedisTableName.PROJECT.getCode() + projectMilestone.getProjectId(),
                        RedisTableName.PROJECT_MILESTONE.getCode() + projectMilestone.getId(),
                        JSON.toJSONString(projectMilestone));
            }
        });
    }

    /**
     * 更新项目状态和时间
     */
    @Override
    public void updateProjectEstEndTimeAndStatus(List<Project> projectList) {
        projectList.forEach(project -> {
            LocalDateTime maxMilestoneEstEndTime = projectMilestoneDao.getMaxEstEndTimeByProjectIdAndEnable(project.getId(), Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
            String maxMilestoneStatus = projectMilestoneDao.getMaxStatusByProjectIdAndEnable(project.getId(), Integer.valueOf(TreeResultStatus.ENABLE.getCode()));
            projectDao.updateEstEndTimeAndStatus(project.getId(), maxMilestoneEstEndTime, maxMilestoneStatus);
        });
    }

    @Override
    public List<Task> getByUserId(String userId, Date startDate, Date endDate) {
        List<Project> allVisibleProject = projectService.findAllVisibleProject(userId, null, BaseEnums.NO.getCode());
        if (CollectionUtils.isEmpty(allVisibleProject)) return new ArrayList<Task>();
        List<String> projectIds = allVisibleProject.stream().map(BaseEntity::getId).collect(Collectors.toList());
        return taskDao.findContainsRoleBetweenDate(projectIds, startDate, endDate);
//        return new LambdaQueryChainWrapper<Task>(taskDao)
//                .in(Task::getProjectId, allVisibleProject.stream().map(BaseEntity::getId).collect(Collectors.toList()))
//                .ge(Task::getEstEndTime, startDate)
//                .le(Task::getEstEndTime, endDate).list();
    }

    /**
     * 根据任务ID删除Redis里的数据
     *
     * @param taskList 任务
     */
    public void deleteTaskInRedis(List<Task> taskList) {
        for (Task task : taskList)
            redisTemplate.opsForHash().delete(RedisTableName.PROJECT.getCode() + task.getProjectId(),
                    StringUtils.isBlank(task.getParentId()) ? RedisTableName.PARENT_TASK.getCode() + task.getId() : RedisTableName.CHILD_TASK.getCode() + task.getId());
    }

    /**
     * 根据项目里程碑删除任务及相关表
     *
     * @param projectMilestoneId 里程碑ID
     * @return .
     */
    @Override
    public boolean deleteByProjectMilestoneId(String projectMilestoneId, String userId) {
        //里程碑下第一级任务
        List<Task> taskList = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getMilestoneId, projectMilestoneId).list();
        if (CollectionUtils.isEmpty(taskList)) return true;
        String[] taskIds = taskList.stream().map(BaseEntity::getId).toArray(String[]::new);
        this.batchDelete(taskIds, false, userId);
        return true;
    }

    /**
     * 上移任务
     *
     * @param id 任务ID
     * @return .
     */
    @Override
    public ResultData upTask(String id) {
        Task task = taskDao.selectById(id);
        if (task == null || StringUtils.isBlank(task.getMilestoneId())) return ResultData.notFound();

        List<Task> tasks = new ArrayList<>();
        //首先是否为父任务，不是父任务就与子任务作比较
        if (StringUtils.isBlank(task.getParentId()))
            tasks = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getMilestoneId, task.getMilestoneId()).eq(Task::getEnabled, Integer.valueOf(TreeResultStatus.ENABLE.getCode()))
                    .eq(Task::getStMark, BaseEnums.NO.getCode()).isNull(Task::getParentId).list();
        else
            // 启用状态
            tasks = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getMilestoneId, task.getMilestoneId()).eq(Task::getEnabled, Integer.valueOf(TreeResultStatus.ENABLE.getCode()))
                    .eq(Task::getStMark, BaseEnums.NO.getCode()).eq(Task::getParentId, task.getParentId()).list();
        tasks.sort((a, b) -> -a.getSort().compareTo(b.getSort()));
        Optional<Task> lessOptional = tasks.stream().filter(x -> x.getSort().compareTo(task.getSort()) < 0).findFirst();
        if (!lessOptional.isPresent()) {
            ResultData resultData = ResultData.errorRequest();
            resultData.setMessage("无法上移");
            return resultData;
        }
        Task less = lessOptional.get();
        this.swapTaskSort(task, less);

        return ResultData.ok();
    }

    /**
     * 下移任务
     *
     * @param id 任务ID
     * @return .
     */
    @Override
    public ResultData downTask(String id) {
        Task task = taskDao.selectById(id);
        if (task == null || StringUtils.isBlank(task.getMilestoneId())) return ResultData.notFound();

        List<Task> tasks = new ArrayList<>();
        //首先是否为父任务，不是父任务就与子任务作比较
        if (StringUtils.isBlank(task.getParentId()))
            tasks = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getMilestoneId, task.getMilestoneId()).eq(Task::getEnabled, Integer.valueOf(TreeResultStatus.ENABLE.getCode()))
                    .eq(Task::getStMark, BaseEnums.NO.getCode()).isNull(Task::getParentId).list();
        else
            // 启用状态
            tasks = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getMilestoneId, task.getMilestoneId()).eq(Task::getEnabled, Integer.valueOf(TreeResultStatus.ENABLE.getCode()))
                    .eq(Task::getStMark, BaseEnums.NO.getCode()).eq(Task::getParentId, task.getParentId()).list();
        tasks.sort(Comparator.comparing(Task::getSort));
        Optional<Task> greaterOptional = tasks.stream().filter(x -> x.getSort().compareTo(task.getSort()) > 0).findFirst();
        if (!greaterOptional.isPresent()) {
            ResultData resultData = ResultData.errorRequest();
            resultData.setMessage("无法下移");
            return resultData;
        }
        Task greater = greaterOptional.get();
        this.swapTaskSort(task, greater);
        return ResultData.ok();
    }

    /**
     * 根据项目ID查找任务
     *
     * @param projectId 项目ID
     * @return .
     */
    @Override
    public List<Task> getByProjectId(String projectId) {
        List<Task> parentTask = new LambdaQueryChainWrapper<Task>(taskDao).isNull(Task::getParentId).eq(Task::getProjectId, projectId)
                .eq(Task::getEnabled, Integer.valueOf(TreeResultStatus.ENABLE.getCode()))
                .eq(Task::getForbidden, Integer.valueOf(TreeResultStatus.NOT_FORBIDDEN.getCode()))
                .eq(Task::getStMark, BaseEnums.NO.getCode())
                .orderByAsc(Task::getSort).list();
        for (Task parent : parentTask) {
            projectMilestoneService.getAttribute(parent);
        }
        return parentTask;
    }

    /**
     * 交换顺序
     */
    private void swapTaskSort(Task a, Task b) {
        String tempSort = b.getSort();
        b.setSort(a.getSort());
        a.setSort(tempSort);
        taskDao.updateById(b);
        taskDao.updateById(a);
        this.updateTaskInRedisByTaskId(a.getId());
        this.updateTaskInRedisByTaskId(b.getId());
    }

    /**
     * 获取关联子菜单
     */
    private void getChildTaskIds(String id, List<String> taskIds) {
        List<Task> tasks = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getParentId, id).list();
        for (Task task : tasks) {
            taskIds.add(task.getId());
            List<Task> tasks1 = new LambdaQueryChainWrapper<Task>(taskDao).eq(Task::getParentId, task.getId()).list();
            if (CollectionUtils.isEmpty(tasks1)) continue;
            this.getChildTaskIds(task.getId(), taskIds);
        }
    }
}

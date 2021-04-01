package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.*;
import com.touchspring.ailge.dao.sys.SysRoleTemplateDao;
import com.touchspring.ailge.dao.sys.SysUserDao;
import com.touchspring.ailge.dto.ProjectRoleUserDTO;
import com.touchspring.ailge.entity.BaseEntity;
import com.touchspring.ailge.entity.agile.*;
import com.touchspring.ailge.entity.sys.SysRoleTemplate;
import com.touchspring.ailge.entity.sys.SysUser;
import com.touchspring.ailge.enums.BaseEnums;
import com.touchspring.ailge.enums.MessageModuleEnum;
import com.touchspring.ailge.enums.TreeResultStatus;
import com.touchspring.ailge.service.agile.ProjectRoleService;
import com.touchspring.ailge.service.agile.ProjectRoleUserService;
import com.touchspring.ailge.service.agile.TaskLogService;
import com.touchspring.ailge.service.agile.TaskService;
import com.touchspring.ailge.utils.PlaceholderUtil;
import com.touchspring.core.utils.StringUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目角色与人员关系表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-17
 */
@Service
public class ProjectRoleUserServiceImpl extends ServiceImpl<ProjectRoleUserDao, ProjectRoleUser> implements ProjectRoleUserService {

    @Autowired
    private SysRoleTemplateDao sysRoleTemplateDao;
    @Autowired
    private ProjectRoleDao projectRoleDao;
    @Autowired
    private ProjectRoleUserDao projectRoleUserDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private ProjectRoleService projectRoleService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProjectLogDao projectLogDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private ProjectDao projectDao;

    /**
     * 变更指定角色的对应成员 一对一
     * @param projectId 项目ID
     * @param roleName 角色名称
     * @param newUserId 新的用户ID
     */
    @Override
    public void saveOrUpdateRoleUser(String projectId, String roleName, String newUserId, String createUerId) {
        //保存角色
        String projectRoleId = projectRoleService.getProjectRoleIdByRoleName(roleName, projectId, createUerId);
        if (StringUtils.isBlank(projectRoleId)) return;
        //保存角色用户
        ProjectRoleUser projectRoleUser = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).eq(ProjectRoleUser::getProjectRoleId, projectRoleId).one();
        if (projectRoleUser == null){
            //保存项目角色与人员
            projectRoleUser = new ProjectRoleUser();
            projectRoleUser.setProjectRoleId(projectRoleId);
            projectRoleUser.setSysUserId(newUserId);
            projectRoleUserDao.insert(projectRoleUser);
        } else {
            projectRoleUser.setSysUserId(newUserId);
            projectRoleUserDao.updateById(projectRoleUser);
        }
    }

    @Override
    public Page<SysUser> findByProjectRoleId(Integer page, Integer size, String projectRoleId) {
        if (StringUtils.isBlank(projectRoleId)) return new Page<SysUser>();
        List<ProjectRoleUser> projectRoleUsers = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).eq(ProjectRoleUser::getProjectRoleId, projectRoleId).select(ProjectRoleUser::getSysUserId).list();
        if (CollectionUtils.isEmpty(projectRoleUsers)) return new Page<SysUser>();
        List<String> userIds = projectRoleUsers.stream().map(ProjectRoleUser::getSysUserId).collect(Collectors.toList());
        Page<SysUser> sysUserPage = new Page<>(page, size);
        return sysUserPage.setRecords(sysUserDao.findByUserIds(sysUserPage, userIds));
    }

    /**
     *  查找当前项目下的人员数量
     * @param projectId 项目ID
     * @return .
     */
    @Override
    public Integer findCurProjectUserNumByProjectId(String projectId) {
        Set<String> sysUserIds = new HashSet<>();
        List<ProjectRole> projectRoleList = new LambdaQueryChainWrapper<ProjectRole>(projectRoleDao).eq(ProjectRole::getProjectId, projectId).select(BaseEntity::getId).list();
        if (!CollectionUtils.isEmpty(projectRoleList)) {
            List<String> projectRoleIds = projectRoleList.stream().map(BaseEntity::getId).collect(Collectors.toList());
            List<ProjectRoleUser> projectRoleUsers = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).in(ProjectRoleUser::getProjectRoleId, projectRoleIds).select(ProjectRoleUser::getSysUserId).list();
            if (!CollectionUtils.isEmpty(projectRoleUsers))
                sysUserIds = projectRoleUsers.stream().map(ProjectRoleUser::getSysUserId).collect(Collectors.toSet());
        }
        return sysUserIds.size();
    }

    @Override
    public boolean save(String projectRoleId, String userId, String createUserId) {

        //获取上次的用户
        ProjectRoleUser lastProjectRoleUser = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).eq(ProjectRoleUser::getProjectRoleId, projectRoleId).one();
        //若用户未变更，则不需要更新
        if ((lastProjectRoleUser == null && StringUtils.isBlank(userId)) || (lastProjectRoleUser != null && StringUtils.equals(lastProjectRoleUser.getSysUserId(), userId))) return true;
        //当前角色是负责人或审核人
        List<Task> lastTaskList = new LambdaQueryChainWrapper<Task>(taskDao).and(i -> i.nested(j -> j.eq(Task::getReviewProjectRoleId, projectRoleId)).or(j -> j.eq(Task::getConfirmProjectRoleId, projectRoleId)))
                .eq(Task::getEnabled, TreeResultStatus.ENABLE.getCode()).eq(Task::getForbidden, TreeResultStatus.NOT_FORBIDDEN.getCode()).eq(Task::getStMark, BaseEnums.NO.getCode()).list();

        projectRoleUserDao.deleteByProjectRoleId(projectRoleId);
        if (StringUtils.isNotBlank(userId)){
            ProjectRoleUser projectRoleUser = new ProjectRoleUser();
            projectRoleUser.setProjectRoleId(projectRoleId);
            projectRoleUser.setSysUserId(userId);
            projectRoleUserDao.insert(projectRoleUser);
        }
        // 更新Redis中的任务
        taskService.updateTaskInRedisByProjectRoleId(projectRoleId);

        if (!CollectionUtils.isEmpty(lastTaskList)){
            //Log
            String lastUserName = lastProjectRoleUser == null? null : sysUserDao.selectById(lastProjectRoleUser.getSysUserId()).getRealName();
            String curUserName = StringUtils.isBlank(userId)? null: sysUserDao.selectById(userId).getRealName();
            List<TaskLog> taskLogs = new ArrayList<>();
            lastTaskList.forEach(task -> {
                TaskLog taskLog = new TaskLog();
                taskLog.setCreateUserId(createUserId);
                taskLog.setProjectId(task.getProjectId());
                taskLog.setTaskId(task.getId());
                StringBuilder str = new StringBuilder();
                if (StringUtils.equals(task.getReviewProjectRoleId(), projectRoleId))
                    str.append("Changed 任务负责人, From \"").append(lastUserName).append("\", To \"").append(curUserName).append("\";");
                if (StringUtils.equals(task.getConfirmProjectRoleId(), projectRoleId))
                    str.append("Changed 任务审核人, From \"").append(lastUserName).append("\", To \"").append(curUserName).append("\";");
                str.replace(str.length()-1, str.length(), "");
                taskLog.setContent(str.toString());
                taskLogs.add(taskLog);
            });
            taskLogService.saveBatch(taskLogs);

            //项目上线，消息通知
            ProjectRole projectRole = projectRoleDao.selectById(projectRoleId);
            if (projectRole == null) return true;
            Project project = projectDao.selectById(projectRole.getProjectId());
            if (project != null && BaseEnums.YES.getCode().equals(project.getHasOnLine()))
                // 任务负责人、审核人变更，消息给（新的任务负责人、新的任务审核人、项目负责人）
                lastTaskList.forEach(task -> this.taskResponsiblePersonChange(task, projectRoleId, createUserId, curUserName, lastProjectRoleUser));
        }

        return true;
    }

    /**
     * 项目负责人、审核人变更
     */
    public void taskResponsiblePersonChange(Task task, String projectRoleId, String createUserId, String curUserName, ProjectRoleUser lastProjectRoleUser){

        Map<String,String> messageExt = new HashMap<>();
        messageExt.put("userName",sysUserDao.selectById(createUserId).getRealName());
        messageExt.put("toUserName", curUserName);
        String reviewProjectRoleId = task.getReviewProjectRoleId();
        String confirmProjectRoleId = task.getConfirmProjectRoleId();
        if (StringUtils.equals(task.getReviewProjectRoleId(), projectRoleId) && StringUtils.equals(task.getConfirmProjectRoleId(), projectRoleId))
            messageExt.put("roleType", "负责人&审核人");
        else if (StringUtils.equals(task.getReviewProjectRoleId(), projectRoleId)){
            messageExt.put("roleType", "负责人");
            confirmProjectRoleId = null;
        }
        else {
            messageExt.put("roleType", "审核人");
            reviewProjectRoleId = null;
        }

        taskService.taskMessage(reviewProjectRoleId, confirmProjectRoleId, task.getId(),
                false, task.getProjectId(), MessageModuleEnum.TASK_RESPONSIBLE_PERSON_CHANGE.getType()
                , PlaceholderUtil.resolvePlaceholders(MessageModuleEnum.TASK_RESPONSIBLE_PERSON_CHANGE.getMsg(), messageExt));
    }

    @Override
    public boolean delete(String projectRoleId, String userId) {
        projectRoleUserDao.deleteByProjectRoleIdAndUserId(projectRoleId, userId);
        // 更新Redis中的任务
        taskService.updateTaskInRedisByProjectRoleId(projectRoleId);
        return true;
    }

    /**
     * 根据项目角色ID查找用户
     * @param projectRoleId 项目角色ID
     * @return 用户
     */
    public SysUser findByProjectRoleId(String projectRoleId){
        if (StringUtils.isBlank(projectRoleId)) return null;
        ProjectRoleUser projectRoleUser = new LambdaQueryChainWrapper<ProjectRoleUser>(projectRoleUserDao).eq(ProjectRoleUser::getProjectRoleId, projectRoleId).select(ProjectRoleUser::getSysUserId).one();
        if (projectRoleUser == null) return null;
        return sysUserDao.selectById(projectRoleUser.getSysUserId());
    }
}

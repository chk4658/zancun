package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.agile.TaskTemplateDataDao;
import com.touchspring.ailge.dto.ProjectTemplateTaskDTO;
import com.touchspring.ailge.entity.agile.ProjectTemplateTask;
import com.touchspring.ailge.dao.agile.ProjectTemplateTaskDao;
import com.touchspring.ailge.entity.agile.TaskTemplateData;
import com.touchspring.ailge.service.agile.ProjectTemplateRoleService;
import com.touchspring.ailge.service.agile.ProjectTemplateTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.utils.IdWorker;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <p>
 * 项目模板任务表
 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class ProjectTemplateTaskServiceImpl extends ServiceImpl<ProjectTemplateTaskDao, ProjectTemplateTask> implements ProjectTemplateTaskService {

    @Autowired
    private ProjectTemplateRoleService projectTemplateRoleService;
    @Autowired
    private ProjectTemplateTaskDao projectTemplateTaskDao;
    @Autowired
    private TaskTemplateDataDao taskTemplateDataDao;

    /**
     * 保存/修改
     */
    @Override
    public boolean save(ProjectTemplateTaskDTO projectTemplateTaskDTO) {
        ProjectTemplateTask target;
        if (StringUtils.isBlank(projectTemplateTaskDTO.getId())) {
            target = projectTemplateTaskDTO;
            target.setReviewProjectRoleId(projectTemplateRoleService.getProjectTemplateRoleIdAndSave(projectTemplateTaskDTO.getReviewRoleName(), projectTemplateTaskDTO.getProjectTemplateId()));
            target.setConfirmProjectRoleId(projectTemplateRoleService.getProjectTemplateRoleIdAndSave(projectTemplateTaskDTO.getConfirmRoleName(), projectTemplateTaskDTO.getProjectTemplateId()));
            target.setSort(IdWorker.nextId());
            projectTemplateTaskDao.insert(target);
        } else {
            projectTemplateTaskDTO.setReviewProjectRoleId( StringUtils.isBlank(projectTemplateTaskDTO.getReviewRoleName()) ? "" :
                    projectTemplateRoleService.getProjectTemplateRoleIdAndSave(projectTemplateTaskDTO.getReviewRoleName(), projectTemplateTaskDTO.getProjectTemplateId()));
            projectTemplateTaskDTO.setConfirmProjectRoleId(
                    projectTemplateRoleService.getProjectTemplateRoleIdAndSave( StringUtils.isBlank(projectTemplateTaskDTO.getProjectTemplateId()) ? "" :
                            projectTemplateTaskDTO.getConfirmRoleName(), projectTemplateTaskDTO.getProjectTemplateId()));
            projectTemplateTaskDao.updateById(projectTemplateTaskDTO);
        }
        return true;
    }

    /**
     * 删除任务、关联任务及关联表
     */
    @Override
    public boolean delete(String id) {
        List<String> taskIds = new ArrayList<>();
        taskIds.add(id);
        this.getChildTaskIds(id, taskIds);

        //删除数据表
        taskTemplateDataDao.delete(new LambdaQueryWrapper<TaskTemplateData>().in(TaskTemplateData::getProjectTemplateTaskId, taskIds));
        //删除子任务
        projectTemplateTaskDao.deleteBatchIds(taskIds);

        return true;
    }

    @Override
    public boolean batchDelete(String[] ids) {
        //删除数据表
        taskTemplateDataDao.delete(new LambdaQueryWrapper<TaskTemplateData>().in(TaskTemplateData::getProjectTemplateTaskId, Arrays.asList(ids)));
        //删除子任务
        projectTemplateTaskDao.deleteBatchIds(Arrays.asList(ids));
        return true;
    }

    /**
     * 根据模板里程碑删除
     * @param templateMilestoneId 模板里程碑ID
     * @return .
     */
    @Override
    public boolean deleteByProjectTemplateMilestoneId(String templateMilestoneId) {
        List<ProjectTemplateTask> projectTemplateTasks = new LambdaQueryChainWrapper<ProjectTemplateTask>(projectTemplateTaskDao)
                .eq(ProjectTemplateTask::getProjectTemplateMilestoneId, templateMilestoneId).isNull(ProjectTemplateTask::getParentId).list();
        if (CollectionUtils.isEmpty(projectTemplateTasks)) return true;
        for (ProjectTemplateTask projectTemplateTask: projectTemplateTasks)
            this.delete(projectTemplateTask.getId());

        return true;
    }

    /**
     * 上移
     */
    @Override
    public ResultData upTask(String id) {
        ProjectTemplateTask projectTemplateTask = projectTemplateTaskDao.selectById(id);
        if (projectTemplateTask == null || StringUtils.isBlank(projectTemplateTask.getProjectTemplateMilestoneId())) return ResultData.notFound();
        List<ProjectTemplateTask> tasks = new ArrayList<>();
        if (StringUtils.isBlank(projectTemplateTask.getParentId()))
            tasks = new LambdaQueryChainWrapper<ProjectTemplateTask>(projectTemplateTaskDao)
                    .eq(ProjectTemplateTask::getProjectTemplateMilestoneId, projectTemplateTask.getProjectTemplateMilestoneId()).isNull(ProjectTemplateTask::getParentId).list();
        else
            tasks = new LambdaQueryChainWrapper<ProjectTemplateTask>(projectTemplateTaskDao)
                    .eq(ProjectTemplateTask::getProjectTemplateMilestoneId, projectTemplateTask.getProjectTemplateMilestoneId())
                    .eq(ProjectTemplateTask::getParentId, projectTemplateTask.getParentId()).list();
        tasks.sort((a, b) -> -a.getSort().compareTo(b.getSort()));
        Optional<ProjectTemplateTask> lessOptional = tasks.stream().filter(x -> x.getSort().compareTo(projectTemplateTask.getSort()) < 0).findFirst();
        if (!lessOptional.isPresent()) {
            ResultData resultData = ResultData.errorRequest();
            resultData.setMessage("无法上移");
            return resultData;
        }
        ProjectTemplateTask less = lessOptional.get();
        this.swapTaskSort(projectTemplateTask, less);

        return ResultData.ok();
    }

    /**
     * 下移
     */
    @Override
    public ResultData downTask(String id) {
        ProjectTemplateTask projectTemplateTask = projectTemplateTaskDao.selectById(id);
        if (projectTemplateTask == null || StringUtils.isBlank(projectTemplateTask.getProjectTemplateMilestoneId())) return ResultData.notFound();

        List<ProjectTemplateTask> tasks = new ArrayList<>();
        if (StringUtils.isBlank(projectTemplateTask.getParentId()))
            tasks = new LambdaQueryChainWrapper<ProjectTemplateTask>(projectTemplateTaskDao)
                    .eq(ProjectTemplateTask::getProjectTemplateMilestoneId, projectTemplateTask.getProjectTemplateMilestoneId()).isNull(ProjectTemplateTask::getParentId).list();
        else
            tasks = new LambdaQueryChainWrapper<ProjectTemplateTask>(projectTemplateTaskDao)
                    .eq(ProjectTemplateTask::getProjectTemplateMilestoneId, projectTemplateTask.getProjectTemplateMilestoneId())
                    .eq(ProjectTemplateTask::getParentId, projectTemplateTask.getParentId()).list();
        tasks.sort(Comparator.comparing(ProjectTemplateTask::getSort));
        Optional<ProjectTemplateTask> greaterOptional = tasks.stream().filter(x -> x.getSort().compareTo(projectTemplateTask.getSort()) > 0).findFirst();
        if (!greaterOptional.isPresent()) {
            ResultData resultData = ResultData.errorRequest();
            resultData.setMessage("无法下移");
            return resultData;
        }
        ProjectTemplateTask greater = greaterOptional.get();
        this.swapTaskSort(projectTemplateTask, greater);
        return ResultData.ok();
    }

    /**
     * 获取关联子菜单
     */
    private void getChildTaskIds(String id,List<String> taskIds){
        List<ProjectTemplateTask> tasks = new LambdaQueryChainWrapper<ProjectTemplateTask>(projectTemplateTaskDao).eq(ProjectTemplateTask::getParentId, id).list();
        for (ProjectTemplateTask task: tasks) {
            taskIds.add(task.getId());
            List<ProjectTemplateTask> tasks1 = new LambdaQueryChainWrapper<ProjectTemplateTask>(projectTemplateTaskDao).eq(ProjectTemplateTask::getParentId, task.getId()).list();
            if(CollectionUtils.isEmpty(tasks1)) continue;
            this.getChildTaskIds(task.getId(),taskIds);
        }
    }

    /**
     * 交换顺序
     */
    private void swapTaskSort(ProjectTemplateTask a, ProjectTemplateTask b){
        String tempSort = b.getSort();
        b.setSort(a.getSort());
        a.setSort(tempSort);
        projectTemplateTaskDao.updateById(b);
        projectTemplateTaskDao.updateById(a);
    }
}

package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.agile.ProjectTemplateRoleDao;
import com.touchspring.ailge.dao.agile.ProjectTemplateTaskDao;
import com.touchspring.ailge.dao.agile.TaskTemplateDataDao;
import com.touchspring.ailge.dto.ProjectTemplateMilestoneDTO;
import com.touchspring.ailge.entity.agile.ProjectTemplateMilestone;
import com.touchspring.ailge.dao.agile.ProjectTemplateMilestoneDao;
import com.touchspring.ailge.entity.agile.ProjectTemplateTask;
import com.touchspring.ailge.service.agile.ProjectTemplateMilestoneService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.service.agile.ProjectTemplateRoleService;
import com.touchspring.ailge.service.agile.ProjectTemplateTaskService;
import com.touchspring.ailge.utils.IdWorker;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目模板里程碑表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class ProjectTemplateMilestoneServiceImpl extends ServiceImpl<ProjectTemplateMilestoneDao, ProjectTemplateMilestone> implements ProjectTemplateMilestoneService {

    @Autowired
    private ProjectTemplateMilestoneDao projectTemplateMilestoneDao;
    @Autowired
    private ProjectTemplateTaskDao projectTemplateTaskDao;
    @Autowired
    private ProjectTemplateRoleDao projectTemplateRoleDao;
    @Autowired
    private TaskTemplateDataDao taskTemplateDataDao;
    @Autowired
    private ProjectTemplateRoleService projectTemplateRoleService;
    @Autowired
    private ProjectTemplateTaskService projectTemplateTaskService;

    /**
     * 查询
     */
    @Override
    public List<ProjectTemplateMilestone> findByProjectTemplateId(Page<ProjectTemplateMilestone> templateMilestonePage, String projectTemplateId, String searchName) {

        List<ProjectTemplateMilestone> projectMilestoneList = projectTemplateMilestoneDao.listByProjectTemplateId(templateMilestonePage, projectTemplateId, searchName);

        projectMilestoneList = projectMilestoneList.stream().peek(projectMilestone -> {
            //获取父任务
            List<ProjectTemplateTask> parentTask = new LambdaQueryChainWrapper<ProjectTemplateTask>(projectTemplateTaskDao).isNull(ProjectTemplateTask::getParentId)
                    .eq(ProjectTemplateTask::getProjectTemplateMilestoneId, projectMilestone.getId()).orderByAsc(ProjectTemplateTask::getSort).list();
            for (ProjectTemplateTask parent: parentTask) {
                this.getAttribute(parent);
            }
            projectMilestone.setTaskList(parentTask);
            if (StringUtils.isNotBlank(projectMilestone.getReviewProjectRoleId())) projectMilestone.setReviewProjectRole(projectTemplateRoleDao.selectById(projectMilestone.getReviewProjectRoleId()));
            if (StringUtils.isNotBlank(projectMilestone.getConfirmProjectRoleId())) projectMilestone.setConfirmProjectRole(projectTemplateRoleDao.selectById(projectMilestone.getConfirmProjectRoleId()));
        }).collect(Collectors.toList());

        return projectMilestoneList;
    }

    /**
     * 保存
     */
    @Override
    public boolean save(ProjectTemplateMilestoneDTO projectTemplateMilestoneDTO) {
        ProjectTemplateMilestone target;
        //审核人
        String reviewProjectRoleId = StringUtils.isBlank(projectTemplateMilestoneDTO.getReviewRoleName()) ? "" : projectTemplateRoleService.getProjectTemplateRoleIdAndSave(
                projectTemplateMilestoneDTO.getReviewRoleName(), projectTemplateMilestoneDTO.getProjectTemplateId());
        //确认人
        String confirmProjectRoleId =  StringUtils.isBlank(projectTemplateMilestoneDTO.getConfirmRoleName()) ? "" : projectTemplateRoleService.getProjectTemplateRoleIdAndSave(
                projectTemplateMilestoneDTO.getConfirmRoleName(), projectTemplateMilestoneDTO.getProjectTemplateId());
        target = projectTemplateMilestoneDTO;
        if (StringUtils.isBlank(projectTemplateMilestoneDTO.getId())){
            target.setReviewProjectRoleId(reviewProjectRoleId);
            target.setConfirmProjectRoleId(confirmProjectRoleId);
            target.setSort(IdWorker.nextId());
            projectTemplateMilestoneDao.insert(target);
        } else {
            //审核人
            projectTemplateMilestoneDTO.setReviewProjectRoleId(reviewProjectRoleId);
            //确认人
            projectTemplateMilestoneDTO.setConfirmProjectRoleId(confirmProjectRoleId);
            //判断是否变更角色
            projectTemplateMilestoneDao.updateById(projectTemplateMilestoneDTO);
        }
        //里程碑选择了角色，则把里程碑下的审核人、确认人角色全部修改
        if (StringUtils.isNotBlank(reviewProjectRoleId) || StringUtils.isNotBlank(confirmProjectRoleId)) {
            List<ProjectTemplateTask> taskList = new LambdaQueryChainWrapper<ProjectTemplateTask>(projectTemplateTaskDao)
                    .eq(ProjectTemplateTask::getProjectTemplateMilestoneId, target.getId()).list();
            for (ProjectTemplateTask task: taskList){
                task.setReviewProjectRoleId(StringUtils.isNotBlank(reviewProjectRoleId)? reviewProjectRoleId: task.getReviewProjectRoleId());
                task.setConfirmProjectRoleId(StringUtils.isNotBlank(confirmProjectRoleId)? confirmProjectRoleId: task.getConfirmProjectRoleId());
                projectTemplateTaskDao.updateById(task);
            }
        }
        return true;
    }

    /**
     * 删除
     * @param id 里程碑ID
     * @return .
     */
    @Override
    public boolean delete(String id) {
        //删除任务及相关联表
        projectTemplateTaskService.deleteByProjectTemplateMilestoneId(id);

        //删除里程碑
        projectTemplateMilestoneDao.deleteById(id);
        return true;
    }

    /**
     * 根据项目模板ID删除
     * @param projectTemplateId 项目模板ID
     * @return .
     */
    @Override
    public boolean deleteByProjectTemplateId(String projectTemplateId) {
        List<ProjectTemplateMilestone> templateMilestones = new LambdaQueryChainWrapper<ProjectTemplateMilestone>(projectTemplateMilestoneDao)
                .eq(ProjectTemplateMilestone::getProjectTemplateId, projectTemplateId).list();
        if (CollectionUtils.isEmpty(templateMilestones)) return true;
        for (ProjectTemplateMilestone projectTemplateMilestone: templateMilestones)
            this.delete(projectTemplateMilestone.getId());
        return true;
    }

    /**
     * 上移
     */
    @Override
    public ResultData upMilestone(String id) {
        ProjectTemplateMilestone projectMilestone = projectTemplateMilestoneDao.selectById(id);
        if (projectMilestone == null || StringUtils.isBlank(projectMilestone.getProjectTemplateId())) return ResultData.notFound();
        List<ProjectTemplateMilestone> projectMilestones = new LambdaQueryChainWrapper<ProjectTemplateMilestone>(projectTemplateMilestoneDao)
                .eq(ProjectTemplateMilestone::getProjectTemplateId, projectMilestone.getProjectTemplateId()).list();
        projectMilestones.sort((a, b) -> -a.getSort().compareTo(b.getSort()));
        Optional<ProjectTemplateMilestone> lessOptional = projectMilestones.stream().filter(x -> x.getSort().compareTo(projectMilestone.getSort()) < 0).findFirst();
        if (!lessOptional.isPresent()) {
            ResultData resultData = ResultData.errorRequest();
            resultData.setMessage("里程碑无法上移");
            return resultData;
        }
        ProjectTemplateMilestone less = lessOptional.get();
        this.swapMilestoneSort(projectMilestone, less);
        return ResultData.ok();
    }

    /**
     * 下移
     */
    @Override
    public ResultData downMilestone(String id) {
        ProjectTemplateMilestone projectMilestone = projectTemplateMilestoneDao.selectById(id);
        if (projectMilestone == null || StringUtils.isBlank(projectMilestone.getProjectTemplateId())) return ResultData.notFound();
        List<ProjectTemplateMilestone> projectMilestones = new LambdaQueryChainWrapper<ProjectTemplateMilestone>(projectTemplateMilestoneDao)
                .eq(ProjectTemplateMilestone::getProjectTemplateId, projectMilestone.getProjectTemplateId()).list();
        projectMilestones.sort(Comparator.comparing(ProjectTemplateMilestone::getSort));
        Optional<ProjectTemplateMilestone> greaterOptional = projectMilestones.stream().filter(x -> x.getSort().compareTo(projectMilestone.getSort()) > 0).findFirst();
        if (!greaterOptional.isPresent()) {
            ResultData resultData = ResultData.errorRequest();
            resultData.setMessage("里程碑无法下移");
            return resultData;
        }
        ProjectTemplateMilestone greater = greaterOptional.get();
        this.swapMilestoneSort(projectMilestone, greater);
        return ResultData.ok();
    }

    @Override
    public ProjectTemplateMilestone getId(String id) {
        ProjectTemplateMilestone projectMilestone = projectTemplateMilestoneDao.getById(id);
        List<ProjectTemplateTask> parentTask = new LambdaQueryChainWrapper<ProjectTemplateTask>(projectTemplateTaskDao).isNull(ProjectTemplateTask::getParentId)
                .eq(ProjectTemplateTask::getProjectTemplateMilestoneId, projectMilestone.getId()).orderByAsc(ProjectTemplateTask::getSort).list();
        for (ProjectTemplateTask parent: parentTask) {
            this.getAttribute(parent);
        }
        projectMilestone.setTaskList(parentTask);
        if (StringUtils.isNotBlank(projectMilestone.getReviewProjectRoleId())) projectMilestone.setReviewProjectRole(projectTemplateRoleDao.selectById(projectMilestone.getReviewProjectRoleId()));
        if (StringUtils.isNotBlank(projectMilestone.getConfirmProjectRoleId())) projectMilestone.setConfirmProjectRole(projectTemplateRoleDao.selectById(projectMilestone.getConfirmProjectRoleId()));
        return projectMilestone;
    }

    /**
     * 获取其余属性
     * @param task 任务
     */
    private void getAttribute(ProjectTemplateTask task) {
        List<ProjectTemplateTask> children = this.findChildren(task.getId());
        task.setChildren(children);
        task.setTaskDataList(taskTemplateDataDao.findByTemplateTaskId(task.getId()));
        if (StringUtils.isNotBlank(task.getReviewProjectRoleId())) task.setReviewProjectRole(projectTemplateRoleDao.selectById(task.getReviewProjectRoleId()));
        if (StringUtils.isNotBlank(task.getConfirmProjectRoleId())) task.setConfirmProjectRole(projectTemplateRoleDao.selectById(task.getConfirmProjectRoleId()));
    }

    /**
     * 获取子任务
     */
    private List<ProjectTemplateTask> findChildren(String parentId) {
        List<ProjectTemplateTask> tasks = new LambdaQueryChainWrapper<ProjectTemplateTask>(projectTemplateTaskDao)
                .eq(ProjectTemplateTask::getParentId, parentId).orderByAsc(ProjectTemplateTask::getSort).list();
        for (ProjectTemplateTask task : tasks) {
            this.getAttribute(task);
        }
        return tasks;
    }

    /**
     * 交换顺序
     * @param a 当前的里程碑
     * @param b 要交换的里程碑
     */
    private void swapMilestoneSort(ProjectTemplateMilestone a, ProjectTemplateMilestone b){
        String tempSort = b.getSort();
        b.setSort(a.getSort());
        a.setSort(tempSort);
        projectTemplateMilestoneDao.updateById(b);
        projectTemplateMilestoneDao.updateById(a);
    }
}

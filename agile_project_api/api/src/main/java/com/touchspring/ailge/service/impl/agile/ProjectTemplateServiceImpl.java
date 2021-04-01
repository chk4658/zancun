package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.ProjectTemplateAttrDao;
import com.touchspring.ailge.dao.agile.ProjectTemplateDao;
import com.touchspring.ailge.dao.agile.ProjectTemplateRoleDao;
import com.touchspring.ailge.dto.TreeResultDTO;
import com.touchspring.ailge.entity.agile.ProjectTemplate;
import com.touchspring.ailge.entity.agile.ProjectTemplateAttr;
import com.touchspring.ailge.entity.agile.ProjectTemplateRole;
import com.touchspring.ailge.enums.IsEnabled;
import com.touchspring.ailge.enums.TreeResultStatus;
import com.touchspring.ailge.service.agile.ProjectTemplateMilestoneService;
import com.touchspring.ailge.service.agile.ProjectTemplateService;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目模板表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class ProjectTemplateServiceImpl extends ServiceImpl<ProjectTemplateDao, ProjectTemplate> implements ProjectTemplateService {

    @Autowired
    private ProjectTemplateDao projectTemplateDao;
    @Autowired
    private ProjectTemplateMilestoneService projectTemplateMilestoneService;
    @Autowired
    private ProjectTemplateAttrDao projectTemplateAttrDao;
    @Autowired
    private ProjectTemplateRoleDao projectTemplateRoleDao;

    @Override
    public boolean save(ProjectTemplate projectTemplate, String userId) {
        ProjectTemplate target;
        if (StringUtils.isBlank(projectTemplate.getId())){
            target = projectTemplate;
            if (StringUtils.isNotBlank(target.getName())) {
                List<ProjectTemplate> projectTemplates = new LambdaQueryChainWrapper<>(projectTemplateDao).eq(ProjectTemplate::getName, target.getName()).list();
                if (!CollectionUtils.isEmpty(projectTemplates)) return false;
            }
//            target.setEnabled(IsEnabled.NOT_DISABLED.getCode());
            target.setCreateUserId(userId);
            projectTemplateDao.insert(target);
        } else {
            target = projectTemplateDao.selectById(projectTemplate.getId());
            if (!StringUtils.equals(target.getName(), projectTemplate.getName()) && StringUtils.isNotBlank(projectTemplate.getName())){
                List<ProjectTemplate> projectTemplates = new LambdaQueryChainWrapper<>(projectTemplateDao).eq(ProjectTemplate::getName, projectTemplate.getName()).list();
                if (!CollectionUtils.isEmpty(projectTemplates)) return false;
            }
            projectTemplate.setUpdateUserId(userId);
            projectTemplateDao.updateById(projectTemplate);
        }
        return true;
    }

    /**
     * 禁用或恢复
     * @param id 项目模板ID
     * @param flag true -> 禁用 false -> 恢复
     * @return .
     */
    @Override
    public boolean disableOrRestore(String id, boolean flag) {
        ProjectTemplate projectTemplate = projectTemplateDao.selectById(id);
        if (flag) projectTemplate.setEnabled(IsEnabled.DISABLE.getCode());
        else projectTemplate.setEnabled(IsEnabled.NOT_DISABLED.getCode());
        projectTemplateDao.updateById(projectTemplate);
        return true;
    }

    @Override
    public List<ProjectTemplate> findByProjectTemplateTypeId(String projectTemplateTypeId, String searchName,Long enabled) {
        ProjectTemplate projectTemplate = new ProjectTemplate();
        projectTemplate.setProjectTemplateTypeId(projectTemplateTypeId);
        projectTemplate.setEnabled(enabled);
        if (StringUtils.isNotBlank(searchName)) projectTemplate.setName(searchName);
        return projectTemplateDao.findByProjectTemplate(projectTemplate);
    }

    /**
     * 删除
     * @param id 项目模板ID
     * @return .
     */
    @Override
    public boolean delete(String id) {
        //删除属性
        projectTemplateAttrDao.delete(new LambdaQueryWrapper<ProjectTemplateAttr>().eq(ProjectTemplateAttr::getProjectTemplateId, id));
        //删除里程碑
        projectTemplateMilestoneService.deleteByProjectTemplateId(id);
        //删除角色
        projectTemplateRoleDao.delete(new LambdaQueryWrapper<ProjectTemplateRole>().eq(ProjectTemplateRole::getProjectTemplateId, id));

        projectTemplateDao.deleteById(id);
        return true;
    }


    /**
     * 返回树状结构
     * @param projectTemplateId
     * @return
     */
    @Override
    public List<TreeResultDTO> findTreeResult(String projectTemplateId) {
        // 查询所有项目模板
//        projectTemplate.setEnabled(IsEnabled.NOT_DISABLED.getCode());
        List<ProjectTemplate> projectTemplates = projectTemplateDao.findAllByProjectTemplateId(projectTemplateId);


        List<TreeResultDTO> treeResultDTOS = projectTemplates.stream().map(p ->{
            TreeResultDTO templatesTreeResult = new TreeResultDTO();
            templatesTreeResult.setId(p.getId());
            templatesTreeResult.setName(p.getName());
            templatesTreeResult.setType(TreeResultDTO.PROJECT_TEMPLATE);
            templatesTreeResult.setStatus(TreeResultStatus.NOT_ENABLE.getCode());
            templatesTreeResult.setChildren(p.getProjectTemplateMilestones().stream().map(m -> {
                TreeResultDTO milestoneTreeResult = new TreeResultDTO();
                milestoneTreeResult.setId(m.getId());
                milestoneTreeResult.setName(m.getName());
                milestoneTreeResult.setType(TreeResultDTO.PROJECT_TEMPLATE_MILESTONE);
                milestoneTreeResult.setStatus(TreeResultStatus.NOT_ENABLE.getCode());
                milestoneTreeResult.setChildren(m.getTaskList().stream().map(t -> {
                    TreeResultDTO TaskTreeResult = new TreeResultDTO();
                    TaskTreeResult.setId(t.getId());
                    TaskTreeResult.setName(t.getName());
                    TaskTreeResult.setType(TreeResultDTO.PROJECT_TEMPLATE_TASK);
                    TaskTreeResult.setStatus(TreeResultStatus.NOT_ENABLE.getCode());
                    TaskTreeResult.setChildren(t.getChildren().stream().map(child -> {
                        TreeResultDTO childrenTreeResult = new TreeResultDTO();
                        childrenTreeResult.setId(child.getId());
                        childrenTreeResult.setName(child.getName());
                        childrenTreeResult.setType(TreeResultDTO.PROJECT_TEMPLATE_TASK_CHILD);
                        childrenTreeResult.setStatus(TreeResultStatus.NOT_ENABLE.getCode());
                        childrenTreeResult.setChildren(new ArrayList<TreeResultDTO>());
                        return childrenTreeResult;
                    }).collect(Collectors.toList()) );
                    return TaskTreeResult;
                }).collect(Collectors.toList()));
                return milestoneTreeResult;
            }).collect(Collectors.toList()));
            return templatesTreeResult;
        }).collect(Collectors.toList());
        return treeResultDTOS;
    }

}

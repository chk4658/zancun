package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchspring.ailge.dao.agile.ProjectTemplateDao;
import com.touchspring.ailge.entity.agile.ProjectTemplate;
import com.touchspring.ailge.entity.agile.ProjectTemplateType;
import com.touchspring.ailge.dao.agile.ProjectTemplateTypeDao;
import com.touchspring.ailge.service.agile.ProjectTemplateService;
import com.touchspring.ailge.service.agile.ProjectTemplateTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 项目模板类型表 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class ProjectTemplateTypeServiceImpl extends ServiceImpl<ProjectTemplateTypeDao, ProjectTemplateType> implements ProjectTemplateTypeService {

    @Autowired
    private ProjectTemplateTypeDao projectTemplateTypeDao;
    @Autowired
    private ProjectTemplateDao projectTemplateDao;
    @Autowired
    private ProjectTemplateService projectTemplateService;

    /**
     * 保存或更新
     * @param projectTemplateType
     * @return
     */
    @Override
    public boolean save(ProjectTemplateType projectTemplateType) {

        ProjectTemplateType target;
        if (StringUtils.isBlank(projectTemplateType.getId())) {
            target = projectTemplateType;
            List<ProjectTemplateType> projectTemplateTypes = new LambdaQueryChainWrapper<ProjectTemplateType>(projectTemplateTypeDao).eq(ProjectTemplateType::getName, target.getName()).list();
            if (!CollectionUtils.isEmpty(projectTemplateTypes)) return false;
            projectTemplateTypeDao.insert(target);
        } else {
            target = projectTemplateTypeDao.selectById(projectTemplateType.getId());
            if (!target.getName().equals(projectTemplateType.getName())) {
                List<ProjectTemplateType> projectTemplateTypes = new LambdaQueryChainWrapper<ProjectTemplateType>(projectTemplateTypeDao).eq(ProjectTemplateType::getName, projectTemplateType.getName()).list();
                if (!CollectionUtils.isEmpty(projectTemplateTypes)) return false;
                projectTemplateTypeDao.updateById(projectTemplateType);
            }
        }
        return true;
    }

    /**
     * 根据模板ID查找对应的模板类型
     * @param projectTemplateId 项目模板ID
     * @return .
     */
    @Override
    public ProjectTemplateType findByProjectTemplateId(String projectTemplateId) {
        String projectTemplateTypeId = projectTemplateDao.selectById(projectTemplateId).getProjectTemplateTypeId();
        return projectTemplateTypeDao.selectById(projectTemplateTypeId);
    }

    /**
     * 删除模板类型和关联表
     * @param id 模板类型ID
     * @return .
     */
    @Override
    public boolean delete(String id) {
        List<ProjectTemplate> projectTemplates = new LambdaQueryChainWrapper<ProjectTemplate>(projectTemplateDao).eq(ProjectTemplate::getProjectTemplateTypeId, id).list();
        if (!CollectionUtils.isEmpty(projectTemplates))
            projectTemplates.forEach(projectTemplate -> projectTemplateService.delete(projectTemplate.getId()));
        projectTemplateTypeDao.deleteById(id);
        return true;
    }
}

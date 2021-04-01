package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.ProjectTemplateRoleDao;
import com.touchspring.ailge.dao.sys.SysRoleTemplateDao;
import com.touchspring.ailge.entity.agile.ProjectTemplateRole;
import com.touchspring.ailge.entity.sys.SysRoleTemplate;
import com.touchspring.ailge.service.agile.ProjectTemplateRoleService;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 项目角色关系表
 服务实现类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
@Service
public class ProjectTemplateRoleServiceImpl extends ServiceImpl<ProjectTemplateRoleDao, ProjectTemplateRole> implements ProjectTemplateRoleService {

    @Autowired
    private SysRoleTemplateDao sysRoleTemplateDao;
    @Autowired
    private ProjectTemplateRoleDao projectTemplateRoleDao;

    @Override
    public String saveByRoleNameAndProjectTemplateId(String roleName, String projectTemplateId) {
        ProjectTemplateRole projectRole = new ProjectTemplateRole();
        SysRoleTemplate sysRoleTemplate = new LambdaQueryChainWrapper<SysRoleTemplate>(sysRoleTemplateDao).eq(SysRoleTemplate::getName, roleName).one();
        projectRole.setRoleName(roleName);
        projectRole.setProjectTemplateId(projectTemplateId);
        if (sysRoleTemplate != null){
            projectRole.setRoleDescription(sysRoleTemplate.getDescription());
            projectRole.setDuty(sysRoleTemplate.getDuty());
        }
        projectTemplateRoleDao.insert(projectRole);
        return projectRole.getId();
    }

    /**
     * 保存项目模板下的角色
     * @param roleName 角色名称
     * @param projectTemplateId 项目模板ID
     * @return 项目模板角色ID
     */
    @Override
    public String getProjectTemplateRoleIdAndSave(String roleName, String projectTemplateId) {
        if (StringUtils.isBlank(roleName) || StringUtils.isBlank(projectTemplateId)) return null;
        ProjectTemplateRole projectRole = new LambdaQueryChainWrapper<>(projectTemplateRoleDao)
                .eq(ProjectTemplateRole::getRoleName, roleName).eq(ProjectTemplateRole::getProjectTemplateId, projectTemplateId).one();
        if (projectRole != null) return projectRole.getId();
        return this.saveByRoleNameAndProjectTemplateId(roleName, projectTemplateId);
    }

    /**
     * 保存
     */
    @Override
    public boolean save(ProjectTemplateRole projectTemplateRole) {
        ProjectTemplateRole target;
        if (StringUtils.isBlank(projectTemplateRole.getId())) {
            target = projectTemplateRole;
            List<ProjectTemplateRole> projectRoles = new LambdaQueryChainWrapper<ProjectTemplateRole>(projectTemplateRoleDao)
                    .eq(ProjectTemplateRole::getRoleName, target.getRoleName())
                    .eq(ProjectTemplateRole::getProjectTemplateId, projectTemplateRole.getProjectTemplateId()).list();
            if (!CollectionUtils.isEmpty(projectRoles)) return false;
            projectTemplateRoleDao.insert(target);
        } else {
            target = projectTemplateRoleDao.selectById(projectTemplateRole.getId());
            if (!StringUtils.equals(target.getRoleName(), projectTemplateRole.getRoleName())) {
                List<ProjectTemplateRole> projectRoles = new LambdaQueryChainWrapper<ProjectTemplateRole>(projectTemplateRoleDao)
                        .eq(ProjectTemplateRole::getRoleName, projectTemplateRole.getRoleName())
                        .eq(ProjectTemplateRole::getProjectTemplateId, projectTemplateRole.getProjectTemplateId()).list();
                if (!CollectionUtils.isEmpty(projectRoles)) return false;
            }
            projectTemplateRoleDao.updateById(projectTemplateRole);
        }
        return true;
    }

}

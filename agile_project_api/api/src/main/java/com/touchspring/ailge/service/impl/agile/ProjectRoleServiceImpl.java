package com.touchspring.ailge.service.impl.agile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.dao.agile.ProjectLogDao;
import com.touchspring.ailge.dao.agile.ProjectRoleEquivalentDao;
import com.touchspring.ailge.dao.agile.ProjectRoleUserDao;
import com.touchspring.ailge.dao.sys.SysRoleTemplateDao;
import com.touchspring.ailge.dto.RoleEquivalentDTO;
import com.touchspring.ailge.entity.agile.ProjectRole;
import com.touchspring.ailge.dao.agile.ProjectRoleDao;
import com.touchspring.ailge.entity.agile.ProjectRoleEquivalent;
import com.touchspring.ailge.entity.agile.ProjectRoleUser;
import com.touchspring.ailge.entity.sys.SysRoleTemplate;
import com.touchspring.ailge.enums.ChangeEnum;
import com.touchspring.ailge.enums.LogTypeEnum;
import com.touchspring.ailge.enums.ResultStatus;
import com.touchspring.ailge.service.agile.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.utils.PlaceholderUtil;
import com.touchspring.core.mvc.ui.ResultData;
import com.touchspring.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
public class ProjectRoleServiceImpl extends ServiceImpl<ProjectRoleDao, ProjectRole> implements ProjectRoleService {

    @Autowired
    private ProjectRoleUserDao projectRoleUserDao;
    @Autowired
    private ProjectRoleDao projectRoleDao;
    @Autowired
    private SysRoleTemplateDao sysRoleTemplateDao;
    @Autowired
    private ProjectMilestoneService projectMilestoneService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProjectRoleEquivalentDao projectRoleEquivalentDao;
    @Autowired
    private ProjectRoleEquivalentService projectRoleEquivalentService;
    @Autowired
    private ProjectLogService projectLogService;

    /**
     * 删除项目角色和关联表
     */
    @Override
    public boolean batchDelete(String[] ids, String userId) {
        Arrays.asList(ids).forEach(projectRoleId -> {
            LambdaQueryWrapper<ProjectRoleUser> queryWrapper = new LambdaQueryWrapper<ProjectRoleUser>().eq(ProjectRoleUser::getProjectRoleId, projectRoleId);
            projectRoleUserDao.delete(queryWrapper);
        });

        //删除里程碑、任务里的角色ID
        projectMilestoneService.deleteRoleId(Arrays.asList(ids));
        taskService.deleteRoleId(Arrays.asList(ids));

        //删除项目角色当量
        projectRoleEquivalentDao.delete(new LambdaQueryWrapper<ProjectRoleEquivalent>().in(ProjectRoleEquivalent::getProjectRoleId, Arrays.asList(ids)));

        //log
        List<ProjectRole> projectRoleList = projectRoleDao.selectBatchIds(Arrays.asList(ids));
        List<String> roleNameList = projectRoleList.stream().map(ProjectRole::getRoleName).collect(Collectors.toList());
        projectLogService.save(LogTypeEnum.DELETE.getMsg() + "角色: " + StringUtils.join(roleNameList, ","), projectRoleList.get(0).getProjectId(), userId);

        projectRoleDao.deleteBatchIds(Arrays.asList(ids));
        return true;
    }

    /**
     * 项目添加角色
     * @param projectRole 项目角色
     * @return .
     */
    @Override
    public ResultData insert(ProjectRole projectRole, String createUserId) {
        if (StringUtils.isBlank(projectRole.getRoleName())) return new ResultData(ResultStatus.ROLE_NAME_IS_NOT_EMPTY.getCode(), ResultStatus.ROLE_NAME_IS_NOT_EMPTY.getMessage());
        ProjectRole target;
        if (StringUtils.isBlank(projectRole.getId())) {
            target = projectRole;
            List<ProjectRole> projectRoles = new LambdaQueryChainWrapper<ProjectRole>(projectRoleDao)
                    .eq(ProjectRole::getRoleName, target.getRoleName())
                    .eq(ProjectRole::getProjectId, projectRole.getProjectId()).list();
            if (!CollectionUtils.isEmpty(projectRoles)) return new ResultData(ResultStatus.ROLE_IS_EXIST.getCode(), ResultStatus.ROLE_IS_EXIST.getMessage());
            projectRoleDao.insert(target);
            //log
            projectLogService.save(LogTypeEnum.INSERT.getMsg() + "角色: " + target.getRoleName(), projectRole.getProjectId(), createUserId);
        } else {
            target = projectRoleDao.selectById(projectRole.getId());
            if (!StringUtils.equals(target.getRoleName(), projectRole.getRoleName())) {
                List<ProjectRole> projectRoles = new LambdaQueryChainWrapper<ProjectRole>(projectRoleDao)
                        .eq(ProjectRole::getRoleName, projectRole.getRoleName())
                        .eq(ProjectRole::getProjectId, projectRole.getProjectId()).list();
                if (!CollectionUtils.isEmpty(projectRoles)) return new ResultData(ResultStatus.ROLE_IS_EXIST.getCode(), ResultStatus.ROLE_IS_EXIST.getMessage());
                //log
                projectLogService.save(PlaceholderUtil.resolvePlaceholders(ChangeEnum.userChange, projectMilestoneService.getChangeLog("角色", target.getRoleName(), projectRole.getRoleName())),
                        projectRole.getProjectId(), createUserId);
            }
            projectRoleDao.updateById(projectRole);
        }

        //项目当量相关处理
        projectRoleEquivalentService.updateAndSave(target.getId(), projectRole.getRoleEquivalent());

        return ResultData.ok();
    }

    /**
     * 根据角色名称保存项目对应的角色关系表
     * @param roleName
     * @param projectId
     * @return
     */
    @Override
    public String saveByRoleName(String roleName, String projectId, String createUserId) {
        ProjectRole projectRole = new ProjectRole();
        SysRoleTemplate sysRoleTemplate = new LambdaQueryChainWrapper<SysRoleTemplate>(sysRoleTemplateDao).eq(SysRoleTemplate::getName, roleName).one();
        if (sysRoleTemplate == null){
            projectRole.setRoleName(roleName);
            projectRole.setProjectId(projectId);
            projectRoleDao.insert(projectRole);
        } else {
            projectRole.setRoleName(roleName);
            projectRole.setProjectId(projectId);
            projectRole.setRoleDescription(sysRoleTemplate.getDescription());
            projectRole.setDuty(sysRoleTemplate.getDuty());
            projectRoleDao.insert(projectRole);
        }

        //log
        projectLogService.save(LogTypeEnum.INSERT.getMsg() + "角色: " + roleName, projectId, createUserId);

        return projectRole.getId();
    }

    @Override
    public String getProjectRoleIdByRoleName(String roleName, String projectId, String createUserId) {
        if (StringUtils.isBlank(roleName) || StringUtils.isBlank(projectId)) return null;
        ProjectRole projectRole = new LambdaQueryChainWrapper<ProjectRole>(projectRoleDao).eq(ProjectRole::getRoleName, roleName).eq(ProjectRole::getProjectId, projectId).one();
        if (projectRole != null) return projectRole.getId();
        return this.saveByRoleName(roleName, projectId, createUserId);
    }

    /**
     * 获取角色及项目当量
     * @param id 项目角色ID
     * @return
     */
    @Override
    public ProjectRole getById(String id) {
        ProjectRole projectRole = projectRoleDao.selectById(id);
        //项目当量
        projectRole.setRoleEquivalent(projectRoleEquivalentService.getByProjectRoleId(id));
        return projectRole;
    }
}

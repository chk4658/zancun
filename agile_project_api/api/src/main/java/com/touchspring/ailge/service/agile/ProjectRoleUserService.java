package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.ProjectRoleUser;
import com.touchspring.ailge.entity.agile.Task;
import com.touchspring.ailge.entity.sys.SysUser;

import java.util.List;

/**
 * <p>
 * 项目角色与人员关系表 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-17
 */
public interface ProjectRoleUserService extends IService<ProjectRoleUser> {

    void saveOrUpdateRoleUser(String projectId, String roleName, String newUserId, String createUerId);

    Page<SysUser> findByProjectRoleId(Integer page, Integer size, String projectRoleId);

    Integer findCurProjectUserNumByProjectId(String projectId);

    boolean save(String projectRoleId, String userId, String createUserId);

    boolean delete(String projectRoleId, String userId);

    void taskResponsiblePersonChange(Task task, String projectRoleId, String createUserId, String curUserName, ProjectRoleUser lastProjectRoleUser);

}

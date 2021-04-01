package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchspring.ailge.entity.agile.ProjectRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.core.mvc.ui.ResultData;

import java.util.List;

/**
 * <p>
 * 项目角色关系表
 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectRoleService extends IService<ProjectRole> {

    boolean batchDelete(String[] ids, String userId);

    ResultData insert(ProjectRole projectRole, String createUserId);

    String saveByRoleName(String roleName, String projectId, String createUserId);

    String getProjectRoleIdByRoleName(String roleName, String projectId, String createUserId);

    ProjectRole getById(String id);
}

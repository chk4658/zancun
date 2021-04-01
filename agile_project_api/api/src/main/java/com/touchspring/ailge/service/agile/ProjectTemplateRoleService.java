package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.ProjectTemplateRole;

/**
 * <p>
 * 项目角色关系表
 服务类
 * </p>
 *
 * @author wangjing
 * @since 2020-08-04
 */
public interface ProjectTemplateRoleService extends IService<ProjectTemplateRole> {

    String saveByRoleNameAndProjectTemplateId(String roleName, String projectTemplateId);

    String getProjectTemplateRoleIdAndSave(String roleName, String projectTemplateId);

    boolean save(ProjectTemplateRole projectTemplateRole);

}

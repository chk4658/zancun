package com.touchspring.ailge.service.agile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchspring.ailge.entity.agile.ProjectRoleEquivalent;

import java.util.List;

/**
 * 项目角色当量（项目当量）
 */
public interface ProjectRoleEquivalentService extends IService<ProjectRoleEquivalent> {

    boolean updateAndSave(String projectRoleId, String roleEquivalent);

    String getByProjectRoleId(String projectRoleId);

}
